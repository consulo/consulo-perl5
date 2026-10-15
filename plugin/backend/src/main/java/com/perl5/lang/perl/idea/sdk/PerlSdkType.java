/*
 * Copyright 2015-2025 Alexandr Evstigneev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.perl5.lang.perl.idea.sdk;

import consulo.project.ui.notification.Notification;
import consulo.project.ui.notification.NotificationType;
import consulo.project.ui.notification.Notifications;
import consulo.application.ApplicationManager;
import consulo.application.WriteAction;
import consulo.logging.Logger;
import consulo.project.Project;

import com.intellij.openapi.projectRoots.impl.PerlSdkTable;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.Application;
import consulo.content.base.BinariesOrderRootType;
import consulo.content.bundle.SdkUtil;
import consulo.localize.LocalizeValue;
import consulo.ui.MessageBoxes;
import consulo.application.util.SystemInfo;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.util.lang.ObjectUtil;
import consulo.util.collection.ContainerUtil;
import com.perl5.PerlBundle;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.idea.sdk.host.PerlHostFileTransfer;
import com.perl5.lang.perl.idea.sdk.implementation.PerlImplementationData;
import com.perl5.lang.perl.idea.sdk.implementation.PerlImplementationHandler;
import com.perl5.lang.perl.idea.sdk.versionManager.PerlVersionManagerData;
import com.perl5.lang.perl.util.PerlRunUtil;
import org.jdom.Element;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import consulo.content.bundle.AdditionalDataConfigurable;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkAdditionalData;
import consulo.content.bundle.SdkModel;
import consulo.content.bundle.SdkModificator;
import consulo.content.bundle.SdkType;


@ExtensionImpl
public class PerlSdkType extends SdkType {
  private static final Logger LOG = Logger.getInstance(PerlSdkType.class);
  public static final String PERL_SDK_TYPE_ID = "Perl5 Interpreter";

  /**
   * TODO consulo: upstream {@code INSTANCE} singleton field; in Consulo sdk type is an extension instantiated by the platform
   */
  public static @NotNull PerlSdkType getInstance() {
    return Application.get().getExtensionPoint(SdkType.class).findExtensionOrFail(PerlSdkType.class);
  }

  public PerlSdkType() {
    // upstream: getPresentableName() and getIcon() overrides
    super(PERL_SDK_TYPE_ID, LocalizeValue.of(PerlBundle.message("perl.config.interpreter.title")), PerlIcons.PERL_LANGUAGE_ICON);
  }

  @Override
  public void saveAdditionalData(@NotNull SdkAdditionalData sdkAdditionalData, @NotNull Element element) {
    ((SaveAwareSdkAdditionalData)sdkAdditionalData).save(element);
  }

  @Override
  public @NotNull SdkAdditionalData loadAdditionalData(@NotNull Sdk currentSdk, @NotNull Element additional) {
    return PerlSdkAdditionalData.load(additional);
  }

  @Override
  public void setupSdkPaths(@NotNull Sdk sdk) {
    if (ApplicationManager.getApplication().isDispatchThread() && !ApplicationManager.getApplication().isHeadlessEnvironment()) {
      throw new RuntimeException("Do not call from EDT, refreshes FS");
    }
    String oldText = PerlRunUtil.setProgressText(PerlBundle.message("perl.progress.refreshing.inc", sdk.getName()));
    LOG.info("Refreshing @INC for " + sdk);
    PerlHostData<?, ?> hostData = PerlHostData.notNullFrom(sdk);
    List<String> pathsToRefresh = new ArrayList<>();
    // syncing data if necessary
    List<String> incPaths = computeIncPaths(sdk);
    List<Exception> exceptions = new ArrayList<>();

    try (PerlHostFileTransfer<?> fileTransfer = hostData.getFileTransfer()) {
      Consumer<File> downloader = it -> syncAndCollectException(fileTransfer, it, pathsToRefresh, exceptions, false);
      Consumer<File> binStubber = it -> syncAndCollectException(fileTransfer, it, pathsToRefresh, exceptions, true);
      for (String hostPath : incPaths) {
        downloader.accept(new File(hostPath));
        binStubber.accept(PerlRunUtil.findLibsBin(new File(hostPath)));
      }
      // additional bin dirs from version manager
      PerlVersionManagerData.notNullFrom(sdk).getBinDirsPath().forEach(binStubber);

      // sdk home path
      File interpreterPath = new File(Objects.requireNonNull(PerlProjectManager.getInterpreterPath(sdk)));
      binStubber.accept(interpreterPath.getParentFile());

      List<VirtualFile> filesToRefresh = pathsToRefresh.stream()
        .map(it -> VirtualFileUtil.findFileByIoFile(new File(it), true))
        .filter(Objects::nonNull)
        .toList();

      if (!filesToRefresh.isEmpty()) {
        PerlRunUtil.setProgressText(PerlBundle.message("perl.progress.refreshing.filesystem"));
        VirtualFileUtil.markDirtyAndRefresh(false, true, true, filesToRefresh.toArray(VirtualFile.EMPTY_ARRAY));
      }

      try {
        fileTransfer.syncHelpers();
      }
      catch (IOException e) {
        exceptions.add(e);
      }

      if (!exceptions.isEmpty()) {
        int copiedFiles = filesToRefresh.size();
        int errorsNumber = exceptions.size();
        Notifications.Bus.notify(new Notification(
          PerlSdkNotificationGroups.SYNC,
          PerlBundle.message("perl.sync.notification.title"),
          PerlBundle.message("perl.sync.notification.body",
                             copiedFiles, copiedFiles + errorsNumber, errorsNumber,
                             StringUtil.pluralize(PerlBundle.message("perl.sync.notification.pluralize"), errorsNumber)),
          NotificationType.ERROR));
        exceptions.forEach(LOG::warn);
      }
    }
    catch (IOException e) {
      LOG.warn("Error closing transfer for " + sdk, e);
    }

    // updating sdk
    SdkModificator sdkModificator = sdk.getSdkModificator();
    sdkModificator.removeAllRoots();
    for (String hostPath : incPaths) {
      String localPath = hostData.getLocalPath(hostPath);
      if (localPath == null) {
        continue;
      }
      File libDir = new File(localPath);

      if (libDir.exists() && libDir.isDirectory()) {
        VirtualFile virtualDir = VirtualFileUtil.findFileByIoFile(libDir, true);
        if (virtualDir != null) {
          sdkModificator.addRoot(virtualDir, BinariesOrderRootType.ID);
        }
      }
    }

    ApplicationManager.getApplication().invokeAndWait(() -> WriteAction.run(sdkModificator::commitChanges));
    PerlRunUtil.setProgressText(oldText);
  }

  /**
   * Copying {@code fileToCopy} using the {@code fileTransfer} and collecting local path of copied file to the {@code pathsToRefresh}. In
   * case exception been thrown by the {@code fileTransfer}, it's collected to the {@code exceptionsThrown}
   *
   * @param if set, only empty stubs with same names going to be created, instead of actual copying the file.
   */
  private static void syncAndCollectException(@NotNull PerlHostFileTransfer<?> fileTransfer,
                                              @Nullable File fileToCopy,
                                              @NotNull List<? super String> pathsToRefresh,
                                              @NotNull List<? super Exception> exceptionsThrown,
                                              boolean stubOnly) {
    if (fileToCopy == null) {
      return;
    }
    try {
      if (stubOnly) {
        pathsToRefresh.add(fileTransfer.stubFiles(fileToCopy).getPath());
      }
      else {
        pathsToRefresh.add(fileTransfer.syncFile(fileToCopy).getPath());
      }
    }
    catch (IOException e) {
      exceptionsThrown.add(e);
    }
  }


  // TODO consulo: upstream overrides ProjectJdkTable SdkType#suggestHomePath(), Consulo has #suggestHomePaths()
  @SuppressWarnings("deprecation")
  public @Nullable String suggestHomePath() {
    throw new RuntimeException("unsupported");
  }

  /**
   * TODO consulo: perl interpreters require host/version manager data, they can't be created from a home path by the generic
   *  Consulo bundles UI. Interpreters are added in the Perl5 settings ({@code Perl5SdkConfigurable}).
   */
  @Override
  public boolean supportsUserAdd() {
    return false;
  }

  @Override
  public @Nullable AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel,
                                                                               @NotNull SdkModificator sdkModificator) {
    return null;
  }

  // TODO consulo: SdkType#getPresentableName() is replaced with the display name passed to the constructor
  public @NotNull String getPresentableName() {
    return PerlBundle.message("perl.config.interpreter.title");
  }

  @Override
  public @NotNull String suggestSdkName(String currentSdkName, @NotNull String sdkHome) {
    throw new RuntimeException("Should not be invoked");
  }

  /**
   * TODO consulo: SdkType#getVersionString(Sdk) is final in Consulo and delegates to {@link #getVersionString(String)} with the sdk home.
   *  Upstream override is kept under this name, perl version is computed with sdk host and version manager data.
   */
  public @Nullable String getPerlVersionString(@NotNull Sdk sdk) {
    return ObjectUtil.doIfNotNull(
      getPerlVersionDescriptor(PerlProjectManager.getInterpreterPath(sdk),
                               PerlHostData.notNullFrom(sdk),
                               PerlVersionManagerData.notNullFrom(sdk)),
      VersionDescriptor::getVersionString);
  }

  private static @NotNull List<String> computeIncPaths(@NotNull Sdk sdk) {
    return ContainerUtil.filter(PerlRunUtil.getOutputFromPerl(sdk, PerlRunUtil.PERL_LE, "print for @INC"), it -> !".".equals(it));
  }

  @Override
  public @Nullable String getVersionString(@NotNull String sdkHome) {
    for (Sdk sdk : PerlSdkTable.getInstance().getInterpreters()) {
      if (sdkHome.equals(sdk.getHomePath())) {
        return getPerlVersionString(sdk);
      }
    }
    return null;
  }

  @Override
  public boolean isValidSdkHome(@NotNull String sdkHome) {
    throw new RuntimeException("Unsupported");
  }

  @Override
  public @NotNull String adjustSelectedSdkHome(@NotNull String homePath) {
    File file = new File(homePath);
    if (file.isDirectory()) {
      return homePath;
    }
    File parentFile = file.getParentFile();
    return parentFile != null && parentFile.isDirectory() ? parentFile.getPath() : homePath;
  }

  public @NotNull String getPerlExecutableName() {
    return SystemInfo.isWindows ? "perl.exe" : "perl";
  }

  private static @NotNull String suggestSdkName(@Nullable VersionDescriptor descriptor,
                                                @NotNull PerlHostData<?, ?> hostData,
                                                @NotNull PerlVersionManagerData<?, ?> versionManagerData) {
    return StringUtil.capitalize(hostData.getShortName()) + ", " +
           StringUtil.capitalize(versionManagerData.getShortName()) + ": " +
           "Perl" + (descriptor == null ? "" : " " + descriptor.version);
  }

  @Contract("null, _,_->null")
  public static @Nullable VersionDescriptor getPerlVersionDescriptor(@Nullable String interpreterPath,
                                                                     @NotNull PerlHostData<?, ?> hostData,
                                                                     @NotNull PerlVersionManagerData<?, ?> versionManagerData) {
    if (StringUtil.isEmpty(interpreterPath)) {
      return null;
    }

    return VersionDescriptor.create(PerlRunUtil.getOutputFromProgram(
      hostData, versionManagerData,
      interpreterPath, "-MConfig", "-e", "print q{perl}.chr(10);do {print;print chr(10)} for @Config{qw/version archname/}"));
  }

  public static void createAndAddSdk(@NotNull String interpreterPath,
                                     @NotNull PerlHostData<?, ?> hostData,
                                     @NotNull PerlVersionManagerData<?, ?> versionManagerData,
                                     @Nullable Consumer<? super Sdk> sdkConsumer,
                                     @Nullable Project project) {
    createSdk(interpreterPath, hostData, versionManagerData, sdk -> {
      PerlSdkTable.getInstance().addJdk(sdk);
      if (sdkConsumer != null) {
        sdkConsumer.accept(sdk);
      }
      PerlRunUtil.refreshSdkDirs(sdk, project);
    });
  }

  /**
   * Creates and adds new Perl SDK
   *
   * @param interpreterPath interpreter path
   * @param sdkConsumer     created sdk consumer
   */
  public static void createSdk(@NotNull String interpreterPath,
                               @NotNull PerlHostData<?, ?> hostData,
                               @NotNull PerlVersionManagerData<?, ?> versionManagerData,
                               @NotNull Consumer<? super Sdk> sdkConsumer) {
    VersionDescriptor perlVersionDescriptor = PerlSdkType.getPerlVersionDescriptor(interpreterPath, hostData, versionManagerData);
    if (perlVersionDescriptor == null) {
      // consulo: Messages are Swing dialogs (not available in web), the unified message box is shown asynchronously
      ApplicationManager.getApplication().invokeLater(
        () -> MessageBoxes.okError(LocalizeValue.of(PerlBundle.message("dialog.message.failed.to.fetch.perl.version.see.logs.for.more.details")))
          .title(LocalizeValue.of(PerlBundle.message("dialog.title.failed.to.create.interpreter")))
          .showAsync());
      return;
    }
    String newSdkName = SdkUtil.createUniqueSdkName(
      suggestSdkName(perlVersionDescriptor, hostData, versionManagerData), PerlSdkTable.getInstance().getInterpreters().toArray(Sdk.EMPTY_ARRAY));

    final Sdk newSdk = PerlSdkTable.getInstance().createSdk(newSdkName);
    var sdkModificator = newSdk.getSdkModificator();
    sdkModificator.setHomePath(interpreterPath);

    PerlImplementationData<?, ?> implementationData = PerlImplementationHandler.createData(
      interpreterPath, hostData, versionManagerData);

    if (implementationData == null) {
      return;
    }

    sdkModificator.setVersionString(perlVersionDescriptor.getVersionString());
    sdkModificator.setSdkAdditionalData(new PerlSdkAdditionalData(hostData, versionManagerData, implementationData, PerlConfig.empty()));
    ApplicationManager.getApplication().invokeAndWait(() -> WriteAction.run(sdkModificator::commitChanges));
    PerlConfig.init(newSdk);
    sdkConsumer.accept(newSdk);
  }

  public static class VersionDescriptor {
    private String version;
    private String platform;

    private VersionDescriptor() {
    }

    private static @Nullable VersionDescriptor create(@NotNull List<String> perlResponse) {
      if (perlResponse.size() != 3 || !StringUtil.equals("perl", perlResponse.get(0))) {
        LOG.warn("Got inappropriate response from perl: " + StringUtil.join(perlResponse, "\n"));
        return null;
      }

      VersionDescriptor result = new VersionDescriptor();
      result.version = perlResponse.get(1);
      result.platform = perlResponse.get(2);
      return result;
    }

    public @NotNull String getVersionString() {
      return PerlBundle.message("perl.version.string", version, platform);
    }
  }
}

