/*
 * Copyright 2015-2026 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.project;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import jakarta.inject.Singleton;
import jakarta.inject.Inject;
import consulo.project.ui.notification.Notification;
import consulo.project.ui.notification.NotificationType;
import consulo.project.ui.notification.Notifications;
import consulo.disposer.Disposable;
import consulo.ui.ex.action.AnActionEvent;
import consulo.language.editor.CommonDataKeys;
import consulo.dataContext.DataContext;
import consulo.application.ReadAction;
import consulo.application.WriteAction;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.language.util.ModuleUtilCore;
import consulo.project.Project;
import consulo.module.event.ModuleListener;
import consulo.project.RootsChangeRescanningInfo;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.event.SdkTableListener;
import consulo.content.bundle.SdkModificator;
import com.intellij.openapi.projectRoots.impl.PerlModuleExtension;
import com.intellij.openapi.projectRoots.impl.PerlSdkTable;
import consulo.module.content.layer.event.ModuleRootEvent;
import consulo.module.content.layer.event.ModuleRootListener;
import consulo.content.base.BinariesOrderRootType;
import consulo.perl5.core.module.PerlModuleExtensionUtil;
import consulo.perl5.core.module.PerlModuleSettingsUtil;
import consulo.perl5.core.roots.RootsChangeUtil;
import consulo.application.util.AtomicNotNullLazyValue;
import consulo.disposer.Disposer;
import consulo.util.io.FileUtil;
import consulo.util.lang.StringUtil;

import consulo.util.collection.FactoryMap;
import consulo.component.messagebus.MessageBusConnection;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.actions.PerlDumbAwareAction;
import com.perl5.lang.perl.idea.configuration.settings.PerlLocalSettings;
import com.perl5.lang.perl.idea.configuration.settings.sdk.PerlSdkLibrary;
import com.perl5.lang.perl.idea.modules.PerlLibrarySourceRootType;
import com.perl5.lang.perl.idea.modules.PerlSourceRootType;
import com.perl5.lang.perl.idea.sdk.PerlConfig;
import com.perl5.lang.perl.idea.sdk.PerlSdkNotificationGroups;
import com.perl5.lang.perl.idea.sdk.PerlSdkType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.event.VirtualFileEvent;
import consulo.virtualFileSystem.event.VirtualFileListener;
import consulo.virtualFileSystem.event.VirtualFileMoveEvent;
import consulo.virtualFileSystem.event.VirtualFilePropertyEvent;

@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
@Singleton
public class PerlProjectManager implements Disposable {
  private final @NotNull Project myProject;
  private final PerlLocalSettings myPerlSettings;
  private final Map<PerlSourceRootType, List<VirtualFile>> myModulesRootsProvider;
  private volatile AtomicNotNullLazyValue<Map<VirtualFile, PerlSourceRootType>> myAllModulesMapProvider;
  private volatile AtomicNotNullLazyValue<List<VirtualFile>> myExternalLibraryRootsProvider;
  private volatile AtomicNotNullLazyValue<List<VirtualFile>> mySdkLibraryRootsProvider;
  private volatile AtomicNotNullLazyValue<List<VirtualFile>> myLibraryRootsProvider;
  private volatile AtomicNotNullLazyValue<List<PerlSdkLibrary>> myLibrariesProvider;

  @Inject

  public PerlProjectManager(@NotNull Project project) {
    myProject = project;
    myPerlSettings = PerlLocalSettings.getInstance(project);
    myModulesRootsProvider = FactoryMap.create(type -> type.getRoots(myProject));
    resetProjectCaches();
    MessageBusConnection connection = myProject.getMessageBus().connect(this);
    // TODO consulo: interpreters are taken from Perl module extensions: they keep the name of a removed interpreter (it becomes invalid)
    //  and follow renames (platform sdk pointers), upstream resets/renames the project interpreter in its settings
    connection.subscribe(PerlSdkTable.PERL_TABLE_TOPIC, new SdkTableListener() {
      @Override
      public void sdkAdded(@NotNull Sdk sdk) {
        if (sdk.getSdkType() instanceof PerlSdkType) {
          resetProjectCaches();
        }
      }

      @Override
      public void sdkRemoved(@NotNull Sdk sdk) {
        if (!(sdk.getSdkType() instanceof PerlSdkType)) {
          return;
        }
        if (PerlModuleExtensionUtil.isUsed(myProject, sdk.getName())) {
          WriteAction.run(
            () -> RootsChangeUtil.makeRootsChange(myProject, () -> resetProjectCaches(), RootsChangeRescanningInfo.TOTAL_RESCAN));
        }
        else {
          resetProjectCaches();
        }
      }

      @Override
      public void sdkNameChanged(@NotNull Sdk jdk, @NotNull String previousName) {
        if (jdk.getSdkType() instanceof PerlSdkType) {
          resetProjectCaches();
        }
      }
    });
    connection.subscribe(ModuleRootListener.class, new ModuleRootListener() {
      @Override
      public void rootsChanged(@NotNull ModuleRootEvent event) {
        resetProjectCaches();
      }
    });
    // consulo: caches computed before the modules (and their Perl module extensions) are loaded must be dropped
    connection.subscribe(ModuleListener.class, new ModuleListener() {
      @Override
      public void moduleAdded(@NotNull Project project, @NotNull Module module) {
        resetProjectCaches();
      }

      @Override
      public void moduleRemoved(@NotNull Project project, @NotNull Module module) {
        resetProjectCaches();
      }
    });
    VirtualFileListener listener = new VirtualFileListener() {
      @Override
      public void fileCreated(@NotNull VirtualFileEvent event) {
        resetProjectCaches();
      }

      @Override
      public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        resetProjectCaches();
      }

      @Override
      public void fileDeleted(@NotNull VirtualFileEvent event) {
        resetProjectCaches();
      }

      @Override
      public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        resetProjectCaches();
      }
    };
    LocalFileSystem.getInstance().addVirtualFileListener(listener);
    Disposer.register(this, () -> LocalFileSystem.getInstance().removeVirtualFileListener(listener));
  }

  @Override
  public void dispose() {
    // nothing to dispose
  }

  private void resetProjectCaches() {
    myModulesRootsProvider.clear();
    myAllModulesMapProvider = AtomicNotNullLazyValue.createValue(() -> {
      Map<VirtualFile, PerlSourceRootType> map = new HashMap<>();
      for (Module module : ModuleManager.getInstance(myProject).getModules()) {
        PerlModuleExtension.getInstance(module).getRoots().forEach(map::put);
      }
      return Collections.unmodifiableMap(map);
    });
    // TODO consulo: upstream project interpreter is stored in PerlLocalSettings, here it is the interpreter of the first module with
    //  enabled Perl module extension
    myExternalLibraryRootsProvider = AtomicNotNullLazyValue.createValue(() -> {
      var result = new ArrayList<VirtualFile>();

      var perlConfig = PerlConfig.from(getSdk(myProject));
      var versionString = perlConfig  == null ? null: perlConfig.getVersion();
      var archname = perlConfig == null ? null: perlConfig.getArchname();

      for (String externalPath : myPerlSettings.getExternalLibrariesPaths()) {
        VirtualFile externalLibDir = VirtualFileUtil.findFileByIoFile(new File(externalPath), false);
        if (isAcceptableLibDir(externalLibDir)) {
          result.add(externalLibDir);

          var versionDir = versionString == null ? null : externalLibDir.findChild(versionString);
          if( isAcceptableLibDir(versionDir)){
            var versionArchDir = archname == null ? null: versionDir.findChild(archname);
            if (isAcceptableLibDir(versionArchDir)) {
              result.add(versionArchDir);
            }
            result.add(versionDir);
          }

          if (archname != null) {
            var archDir = externalLibDir.findChild(archname);
            if( isAcceptableLibDir(archDir)){
              result.add(archDir);
            }
          }
        }
      }
      return Collections.unmodifiableList(result);
    });
    mySdkLibraryRootsProvider = AtomicNotNullLazyValue.createValue(() -> {
      var result = new ArrayList<>(getExternalLibraryRoots());
      Sdk projectSdk = getProjectSdk();
      if (projectSdk != null) {
        result.addAll(Arrays.asList(projectSdk.getRootProvider().getFiles(BinariesOrderRootType.ID)));
      }
      return Collections.unmodifiableList(result);
    });
    myLibraryRootsProvider = AtomicNotNullLazyValue.createValue(() -> {
      var result = new ArrayList<>(getModulesLibraryRoots());
      result.addAll(getProjectSdkLibraryRoots());
      return Collections.unmodifiableList(result);
    });
    myLibrariesProvider = AtomicNotNullLazyValue.createValue(() -> {
      var sdkLibs = getProjectSdkLibraryRoots();
      if (sdkLibs.isEmpty()) {
        return Collections.emptyList();
      }

      PerlSdkLibrary sdkLibrary;
      Sdk sdk = getProjectSdk();
      if (sdk != null) {
        sdkLibrary = new PerlSdkLibrary(sdk, sdkLibs);
      }
      else {
        // TODO consulo: SyntheticLibrary.newImmutableLibrary is missing in Consulo
        sdkLibrary = new PerlSdkLibrary(null, sdkLibs);
      }
      return Collections.singletonList(sdkLibrary);
    });
  }

  @Contract("null->false")
  private static boolean isAcceptableLibDir(@Nullable VirtualFile externalLibDir) {
    return externalLibDir != null && externalLibDir.isValid() && externalLibDir.isDirectory();
  }

  public Map<VirtualFile, PerlSourceRootType> getAllModulesRoots() {
    return myAllModulesMapProvider.getValue();
  }

  public List<VirtualFile> getExternalLibraryRoots() {
    return myExternalLibraryRootsProvider.getValue();
  }

  /**
   * @return list of in-project perl library roots, that should be included into modules lookup and {@code -I} argument when starting the
   * perl process
   */
  public List<VirtualFile> getModulesLibraryRoots() {
    return getModulesRootsOfType(PerlLibrarySourceRootType.INSTANCE);
  }

  /**
   * @return list of user-configured roots of {@code type}
   */
  public List<VirtualFile> getModulesRootsOfType(@NotNull PerlSourceRootType type) {
    return myModulesRootsProvider.get(type);
  }

  /**
   * @return list of library roots for the project, including:<ul>
   *   <li>default interpreter roots</li>
   *   <li>configured project external library roots</li>
   *   <li>in-project perl library roots, that should be included into modules lookup and {@code -I} argument when starting the process</li>
   * </ul>
   */
  public List<VirtualFile> getAllLibraryRoots() {
    return myLibraryRootsProvider.getValue();
  }

  /**
   * @return list of library roots including:<ul>
   *   <li>default interpreter roots</li>
   *   <li>configured project external library roots</li>
   * </ul>
   */
  public List<VirtualFile> getProjectSdkLibraryRoots() {
    return mySdkLibraryRootsProvider.getValue();
  }

  public List<PerlSdkLibrary> getProjectLibraries() { return myLibrariesProvider.getValue();}

  public @Nullable Sdk getProjectSdk() {
    // consulo: not cached - the interpreter is the one of the first Perl module, which is available only after modules are loaded
    return PerlModuleExtensionUtil.findFirstSdk(myProject);
  }

  /**
   * TODO consulo: sets the interpreter of all modules with enabled Perl module extension (enables the extension for all modules if there
   *  are no such modules), upstream sets the project interpreter in PerlLocalSettings. Commit of module models fires a roots change.
   */
  public void setProjectSdk(@Nullable Sdk sdk) {
    PerlModuleExtensionUtil.setSdk(myProject, sdk);
  }

  public void addExternalLibrary(@NotNull VirtualFile root) {
    addExternalLibraries(Collections.singletonList(root));
  }

  public boolean isPerlEnabled() {
    return !myProject.isDefault() && getProjectSdk() != null;
  }

  public void setExternalLibraries(@NotNull List<? extends VirtualFile> roots) {
    WriteAction.run(() -> {
      myPerlSettings.setExternalLibrariesPaths(Collections.emptyList());
      addExternalLibraries(roots);
    });
  }

  public void addExternalLibraries(@NotNull List<? extends VirtualFile> roots) {
    WriteAction.run(
      () -> {
        List<String> paths = new ArrayList<>(myPerlSettings.getExternalLibrariesPaths());
        boolean doChange = false;
        for (VirtualFile root : roots) {
          if (!root.isValid() || !root.isDirectory()) {
            return;
          }

          String canonicalPath = root.getCanonicalPath();
          if (!paths.contains(canonicalPath)) {
            doChange = true;
            paths.add(canonicalPath);
          }
        }
        if (!doChange && !paths.isEmpty()) {
          return;
        }

        RootsChangeUtil.makeRootsChange(myProject,
          () -> {
            myPerlSettings.setExternalLibrariesPaths(paths);
            resetProjectCaches();
          }, RootsChangeRescanningInfo.RESCAN_DEPENDENCIES_IF_NEEDED);
      }
    );
  }

  public static PerlProjectManager getInstance(@NotNull Project project) {
    return project.getService(PerlProjectManager.class);
  }


  /**
   * TODO consulo: the interpreter of the enabled Perl module extension of the {@code module}, upstream returns the project interpreter
   */
  @Contract("null->null")
  public static @Nullable Sdk getSdk(@Nullable Module module) {
    return module == null ? null : PerlModuleExtensionUtil.getSdk(module);
  }

  /**
   * @return sdk for project. If not configured - suggests to configure
   */
  public static @Nullable Sdk getSdkWithNotification(@NotNull Project project) {
    Sdk sdk = getSdk(project);
    if (sdk != null) {
      return sdk;
    }
    showUnconfiguredInterpreterNotification(project);
    return null;
  }

  public static void showUnconfiguredInterpreterNotification(@NotNull Project project) {
    Notification notification = new Notification(
      PerlSdkNotificationGroups.SELECT_SDK,
      PerlBundle.message("perl.select.sdk.notification.title"),
      PerlBundle.message("perl.select.sdk.notification.message"),
      NotificationType.ERROR
    );
    notification.addAction(new PerlDumbAwareAction(PerlBundle.message("perl.configure.interpreter.action")) {
      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        // consulo: the interpreter is configured in the Perl module extension
        PerlModuleSettingsUtil.openInterpreterSettings(project, PerlModuleSettingsUtil.findModuleToConfigure(project));
        notification.expire();
      }
    });
    Notifications.Bus.notify(notification, project);
  }

  @Contract("null->null")
  public static @Nullable Sdk getSdk(@Nullable Project project) {
    return project == null ? null : getInstance(project).getProjectSdk();
  }

  /**
   * Migration method for 2018.3. Updates sdk path to interpreter path, not bin dir
   *
   * @param sdk in question
   * @return path to interpreter
   */
  @Contract("null->null")
  public static @Nullable String getInterpreterPath(@Nullable Sdk sdk) {
    if (sdk == null) {
      return null;
    }
    String homePath = sdk.getHomePath();
    if (homePath != null && !StringUtil.contains(new File(homePath).getName(), "perl")) {
      homePath = FileUtil.join(homePath, PerlSdkType.getInstance().getPerlExecutableName());
      SdkModificator modificator = sdk.getSdkModificator();
      modificator.setHomePath(homePath);
      modificator.commitChanges();
    }
    return homePath;
  }

  public static @Nullable Sdk getSdk(@NotNull Project project, @Nullable VirtualFile virtualFile) {
    if (virtualFile == null) {
      return getSdk(project);
    }
    Module module = ReadAction.compute(() -> ModuleUtilCore.findModuleForFile(virtualFile, project));
    return module == null ? getSdk(project) : getSdk(module);
  }

  @Contract("null->false")
  public static boolean isPerlEnabled(@Nullable Project project) {
    return project != null && !project.isDisposed() && getInstance(project).isPerlEnabled();
  }

  public static boolean isPerlEnabled(@NotNull DataContext dataContext) {
    return isPerlEnabled(dataContext.getData(Project.KEY));
  }

  /**
   * TODO consulo: Perl module extension of the {@code module} is enabled and has an interpreter, upstream checks the project interpreter
   */
  public static boolean isPerlEnabled(@Nullable Module module) {
    return module != null && !module.getProject().isDisposed() && getSdk(module) != null;
  }

  @Contract("null->null")
  public static @Nullable Sdk getSdk(@Nullable AnActionEvent event) {
    return event == null ? null : getSdk(event.getData(Project.KEY));
  }
}
