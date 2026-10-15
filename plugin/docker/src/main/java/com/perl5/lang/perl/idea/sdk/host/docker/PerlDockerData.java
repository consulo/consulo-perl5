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

package com.perl5.lang.perl.idea.sdk.host.docker;

import consulo.process.ExecutionException;
import consulo.process.util.ProcessOutput;
import consulo.disposer.Disposable;
import consulo.application.ApplicationManager;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.disposer.Disposer;
import consulo.application.util.SystemInfo;
import consulo.util.io.FileUtil;
import consulo.util.xml.serializer.annotation.Tag;
import consulo.util.xml.serializer.annotation.Transient;
import com.perl5.lang.perl.idea.execution.PerlCommandLine;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.idea.sdk.host.PerlHostFileTransfer;
import com.perl5.lang.perl.idea.sdk.host.PerlHostVirtualFileSystem;
import com.perl5.lang.perl.idea.sdk.host.os.PerlOsHandler;
import com.perl5.lang.perl.idea.sdk.host.os.PerlOsHandlers;
import com.perl5.lang.perl.util.PerlFileUtil;
import com.perl5.lang.perl.util.PerlPluginUtil;
import com.sun.security.auth.module.UnixSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Objects;

class PerlDockerData extends PerlHostData<PerlDockerData, PerlDockerHandler> {
  static final String CONTAINER_ROOT = "/intellijperl";
  static final File CONTAINER_ROOT_FILE = new File(CONTAINER_ROOT);
  static final String HELPERS_ROOT = CONTAINER_ROOT + PerlFileUtil.linuxisePath(PerlPluginUtil.getPluginHelpersRoot());
  private static final Logger LOG = Logger.getInstance(PerlDockerData.class);

  @Transient private @Nullable PerlDockerFileSystem myFileSystem;

  @Tag("image-name") private @Nullable String myImageName;

  public PerlDockerData(@NotNull PerlDockerHandler handler) {
    super(handler);
  }

  @Override
  public @NotNull PerlOsHandler getOsHandler() {
    return PerlOsHandlers.LINUX;
  }

  @Override
  public @Nullable String getSecondaryShortName() {
    return "[" + myImageName + "]";
  }

  public @NotNull String getImageName() {
    return Objects.requireNonNull(myImageName);
  }

  public String getSafeImageName() {
    return getImageName().replaceAll("[:/]", "_");
  }

  public @NotNull PerlDockerData withImageName(@NotNull String imageName) {
    assertWritable();
    myImageName = imageName;
    return this;
  }

  @Override
  public synchronized @NotNull PerlHostVirtualFileSystem getFileSystem(@NotNull Disposable disposable) {
    if (myFileSystem == null) {
      myFileSystem = PerlDockerFileSystem.create(this);
      Disposer.register(disposable, () -> {
        PerlDockerFileSystem fileSystem = myFileSystem;
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
          if (fileSystem != null) {
            fileSystem.clean();
          }
        });
        myFileSystem = null;
      });
    }
    PerlHostVirtualFileSystem hostSystem = PerlHostVirtualFileSystem.getInstance();
    hostSystem.setDelegate(myFileSystem);
    return hostSystem;
  }

  @Override
  public @Nullable File findFileByName(@NotNull String fileName) {
    try {
      ProcessOutput output = execAndGetOutput(new PerlCommandLine("bash", "-cl", "\\which " + fileName).withHostData(this));
      int exitCode = output.getExitCode();
      if (exitCode != 0 && exitCode != 1) {
        LOG.warn("Got non-zero code from script " + exitCode + "; stderr: " + output.getStderr());
        return null;
      }
      List<String> lines = output.getStdoutLines();
      return lines.isEmpty() ? null : new File(lines.getFirst());
    }
    catch (ExecutionException e) {
      LOG.warn("Error seeking for " + fileName, e);
    }
    return null;
  }

  @Override
  public @NotNull String expandUserHome(@NotNull String remotePath) {
    var remoteFile = findFileByName(remotePath);
    return remoteFile != null ? remoteFile.getAbsolutePath() : remotePath;
  }

  @Override
  protected @NotNull Process createProcess(@NotNull PerlCommandLine commandLine) throws ExecutionException {
    return new PerlDockerAdapter(this).createProcess(commandLine);
  }

  @Override
  protected @Nullable String doGetLocalPath(@NotNull String remotePath) {
    File remoteFile = new File(remotePath);
    if (FileUtil.isAncestor(CONTAINER_ROOT_FILE, remoteFile, false)) {
      return PerlFileUtil.unLinuxisePath('/' + FileUtil.getRelativePath(CONTAINER_ROOT_FILE, remoteFile));
    }
    return FileUtil.toSystemIndependentName(FileUtil.join(getLocalCacheRoot(), remotePath));
  }

  @Override
  protected @Nullable String doGetRemotePath(@NotNull String localPath) {
    return FileUtil.toSystemIndependentName(new File(CONTAINER_ROOT_FILE, PerlFileUtil.linuxisePath(localPath)).getPath());
  }

  @Override
  public @NotNull String getLocalCacheRoot() {
    String cachesPath = PerlPluginUtil.getRemotesCachePath();
    File cacheRoot = new File(cachesPath, "docker_" + getSafeImageName());
    FileUtil.createDirectory(cacheRoot);
    return cacheRoot.getAbsolutePath();
  }

  @Override
  public @NotNull PerlHostFileTransfer<PerlDockerData> getFileTransfer() {
    return new PerlDockerFileTransfer(this);
  }

  @Override
  public @NotNull String getHelpersRootPath() {
    return HELPERS_ROOT;
  }

  @Override
  protected @NotNull PerlDockerData self() {
    return this;
  }

  @Override
  public void fixPermissionsRecursively(@NotNull String localPath, @Nullable Project project) throws ExecutionException {
    if (!SystemInfo.isUnix) {
      LOG.debug("Can fix permissions only on unix systems");
      return;
    }

    var remotePath = getRemotePath(localPath);

    UnixSystem system = new UnixSystem();
    long gid = system.getGid();
    long uid = system.getUid();
    var chownOutput = execAndGetOutput(new PerlCommandLine("chown", "-R", uid + ":" + gid, remotePath)
                                         .withProject(project)
                                         .withHostData(this));
    LOG.debug("Executed permission change: ", chownOutput);
  }
}
