package consulo.perl5.mojo.newProject;

import com.perl5.lang.mojolicious.MojoBundle;
import com.perl5.lang.mojolicious.MojoIcons;
import com.perl5.lang.mojolicious.MojoUtil;
import com.perl5.lang.mojolicious.idea.modules.MojoTemplateMarkSourceRootAction;
import com.perl5.lang.perl.adapters.PackageManagerAdapterFactory;
import com.perl5.lang.perl.idea.execution.PerlCommandLine;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.util.PerlRunUtil;
import consulo.application.ReadAction;
import consulo.application.progress.Task;
import consulo.application.util.concurrent.AppExecutorUtil;
import consulo.content.bundle.Sdk;
import consulo.fileEditor.FileEditorManager;
import consulo.localize.LocalizeValue;
import consulo.logging.Logger;
import consulo.process.ExecutionException;
import consulo.process.event.ProcessEvent;
import consulo.process.event.ProcessListener;
import consulo.project.Project;
import consulo.ui.MessageBoxes;
import consulo.ui.UIAccess;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates a Mojolicious application or plugin into the root of a new module with {@code mojo generate}, as upstream
 * {@code MojoGenerateAction} does, offering to install Mojolicious if the interpreter does not have it. {@code mojo generate app} and
 * {@code plugin} create a directory named after the generated entity: its content is moved to the root.
 */
final class MojoNewProjectGenerator {
  private static final Logger LOG = Logger.getInstance(MojoNewProjectGenerator.class);

  private final Project myProject;
  private final UIAccess myUIAccess;
  private final MojoNewProjectTemplate myTemplate;
  private final String myName;
  private final Path myRoot;

  MojoNewProjectGenerator(@NotNull Project project,
                          @NotNull UIAccess uiAccess,
                          @NotNull MojoNewProjectTemplate template,
                          @NotNull String name,
                          @NotNull Path root) {
    myProject = project;
    myUIAccess = uiAccess;
    myTemplate = template;
    myName = name;
    myRoot = root;
  }

  /**
   * @param afterInstallation true iff Mojolicious has just been installed: it is not offered again
   */
  void generate(boolean afterInstallation) {
    if (!myTemplate.isValidName(myName)) {
      myUIAccess.give(() -> MessageBoxes.okError(LocalizeValue.localizeTODO(
          "Invalid " + myTemplate.getNamePrompt().toLowerCase() + ": " + myName + ". The " + myTemplate.getTitle() + " is not generated."))
        .showAsync(myProject));
      return;
    }
    Task.Backgroundable.queue(myProject, LocalizeValue.localizeTODO("Looking for Mojolicious"), indicator -> {
      refreshBinDirectories();
      VirtualFile mojoScript = MojoUtil.getMojoScript(myProject);
      Sdk sdk = PerlProjectManager.getSdk(myProject);
      myUIAccess.give(() -> {
        if (myProject.isDisposed()) {
          return;
        }
        if (sdk == null) {
          LOG.warn("No perl sdk for " + myProject);
        }
        else if (mojoScript != null) {
          runMojoGenerate(sdk, mojoScript);
        }
        else if (afterInstallation) {
          MessageBoxes.okError(LocalizeValue.localizeTODO(
              "Mojolicious is installed, but the mojo script is not found in the bin directories of the " + sdk.getName() +
              " interpreter."))
            .showAsync(myProject);
        }
        else {
          offerInstallation(sdk);
        }
      });
    });
  }

  /**
   * The mojo script is looked up in the virtual file system: a script installed after the bin directories were loaded (just installed
   * Mojolicious, an installation outside of the IDE) is not there until they are refreshed. Bin directories next to the library roots
   * (e.g. a local::lib) may be not loaded at all.
   */
  private void refreshBinDirectories() {
    List<File> libraryBinPaths = ReadAction.compute(() -> PerlProjectManager.getInstance(myProject).getAllLibraryRoots().stream()
      .map(root -> PerlRunUtil.findLibsBin(new File(root.getPath())))
      .filter(Objects::nonNull)
      .toList());
    List<VirtualFile> binDirectories = new ArrayList<>(ReadAction.compute(() -> PerlRunUtil.getBinDirectories(myProject).toList()));
    LocalFileSystem fileSystem = LocalFileSystem.getInstance();
    for (File binPath : libraryBinPaths) {
      VirtualFile binDirectory = fileSystem.refreshAndFindFileByIoFile(binPath);
      if (binDirectory != null) {
        binDirectories.add(binDirectory);
      }
    }
    VirtualFileUtil.markDirtyAndRefresh(false, false, true, binDirectories.toArray(VirtualFile[]::new));
  }

  @RequiredUIAccess
  private void offerInstallation(@NotNull Sdk sdk) {
    MessageBoxes.yesNo()
      .title(LocalizeValue.of(MojoBundle.message("mojo.action.install.action.title")))
      .text(LocalizeValue.localizeTODO("Mojolicious is not installed for the " + sdk.getName() + " interpreter. " +
                                       "Install it and generate the " + myTemplate.getTitle() + "?"))
      .showAsync(myProject)
      .whenComplete((install, error) -> {
        if (!Boolean.TRUE.equals(install)) {
          return;
        }
        // as GenericPerlProgramRunner installs missing modules
        AppExecutorUtil.getAppExecutorService().execute(() -> {
          var packageManagerAdapter = PackageManagerAdapterFactory.create(sdk, myProject);
          myUIAccess.give(() -> packageManagerAdapter.install(List.of(MojoUtil.MOJO_PACKAGE_NAME), () -> myUIAccess.give(() -> {
            if (!myProject.isDisposed()) {
              generate(true);
            }
          }), true));
        });
      });
  }

  @RequiredUIAccess
  private void runMojoGenerate(@NotNull Sdk sdk, @NotNull VirtualFile mojoScript) {
    PerlHostData<?, ?> hostData = PerlHostData.notNullFrom(sdk);
    Set<Path> rootChildren = listChildren(myRoot);
    PerlCommandLine commandLine = new PerlCommandLine(hostData.getRemotePath(mojoScript.getPath()))
      .withParameters("generate", myTemplate.getCommand(), myName)
      .withProject(myProject)
      // consulo: PerlCommandLine keeps a Swing icon for the console
      .withConsoleIcon(TargetAWT.to(MojoIcons.MOJO_LOGO))
      .withWorkDirectory(myRoot.toString())
      .withProcessListener(new ProcessListener() {
        @Override
        public void processTerminated(@NotNull ProcessEvent event) {
          if (event.getExitCode() == 0) {
            onGenerated(hostData, rootChildren);
          }
        }
      });
    PerlRunUtil.updatePerl5Opt(commandLine.getEnvironment(), PerlRunUtil.getPerlRunIncludeArguments(hostData, myProject));
    PerlRunUtil.runInConsole(commandLine);
  }

  private void onGenerated(@NotNull PerlHostData<?, ?> hostData, @NotNull Set<Path> rootChildren) {
    try {
      hostData.fixPermissionsRecursively(myRoot.toString(), myProject);
    }
    catch (ExecutionException e) {
      LOG.warn("Error fixing permissions for " + myRoot + ": " + e.getMessage());
    }

    if (myTemplate.isGeneratingDirectory()) {
      List<Path> generatedDirectories = new ArrayList<>(listChildren(myRoot));
      generatedDirectories.removeAll(rootChildren);
      generatedDirectories.removeIf(it -> !Files.isDirectory(it));
      if (generatedDirectories.size() == 1) {
        try {
          moveContent(generatedDirectories.get(0), myRoot);
        }
        catch (IOException e) {
          LOG.warn("Error moving generated files from " + generatedDirectories.get(0) + " to " + myRoot, e);
        }
      }
      else {
        LOG.warn("Expected one generated directory in " + myRoot + ", got: " + generatedDirectories);
      }
    }

    LocalFileSystem fileSystem = LocalFileSystem.getInstance();
    VirtualFile root = fileSystem.refreshAndFindFileByNioFile(myRoot);
    if (root != null) {
      root.refresh(false, true);
      // MojoProjectManager marks the roots of the applications it finds, the generated one may be found after indexing only
      String templatesDirectoryName = myTemplate.getTemplatesDirectoryName();
      VirtualFile templatesDirectory = templatesDirectoryName == null ? null : root.findChild(templatesDirectoryName);
      if (templatesDirectory != null) {
        myUIAccess.give(() -> {
          if (!myProject.isDisposed()) {
            new MojoTemplateMarkSourceRootAction().markRoot(myProject, templatesDirectory);
          }
        });
      }
    }
    VirtualFile mainFile = myTemplate.getMainFilePaths(myName).stream()
      .map(it -> fileSystem.refreshAndFindFileByNioFile(myRoot.resolve(it)))
      .filter(it -> it != null && !it.isDirectory())
      .findFirst().orElse(null);
    if (mainFile != null) {
      myUIAccess.give(() -> {
        if (!myProject.isDisposed() && mainFile.isValid()) {
          FileEditorManager.getInstance(myProject).openFile(mainFile, true);
        }
      });
    }
  }

  private static @NotNull Set<Path> listChildren(@NotNull Path directory) {
    try (Stream<Path> children = Files.list(directory)) {
      return children.collect(Collectors.toSet());
    }
    catch (IOException e) {
      LOG.warn("Error listing " + directory, e);
      return Collections.emptySet();
    }
  }

  /**
   * Moves the content of {@code source} into {@code target}, merging directories, and removes {@code source} if nothing is left in it.
   * Existing files are kept.
   */
  private static void moveContent(@NotNull Path source, @NotNull Path target) throws IOException {
    for (Path child : listChildren(source)) {
      Path targetChild = target.resolve(child.getFileName().toString());
      if (Files.isDirectory(child) && Files.isDirectory(targetChild)) {
        moveContent(child, targetChild);
      }
      else if (Files.exists(targetChild)) {
        LOG.warn("Generated " + child + " is not moved: " + targetChild + " exists");
      }
      else {
        Files.move(child, targetChild);
      }
    }
    try (Stream<Path> children = Files.list(source)) {
      if (children.findAny().isEmpty()) {
        Files.delete(source);
      }
    }
  }
}
