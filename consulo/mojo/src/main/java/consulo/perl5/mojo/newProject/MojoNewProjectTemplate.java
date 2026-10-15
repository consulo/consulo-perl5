package consulo.perl5.mojo.newProject;

import com.perl5.lang.mojolicious.MojoBundle;
import com.perl5.lang.mojolicious.MojoIcons;
import com.perl5.lang.mojolicious.MojoUtil;
import com.perl5.lang.mojolicious.model.MojoApp;
import com.perl5.lang.mojolicious.model.MojoPlugin;
import consulo.ui.ex.InputValidator;
import consulo.ui.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Mojolicious templates of the "New Project/Module" wizard: {@code mojo generate} commands of upstream {@code MojoGenerateAction}s
 */
public enum MojoNewProjectTemplate {
  APP("app", "MyApp", "mojo.action.generate.app", "mojo.action.generate.app.prompt.message", MojoIcons.MOJO_APP_ICON,
      new MojoApp.NameValidator()) {
    @Override
    @NotNull List<String> getMainFilePaths(@NotNull String name) {
      return List.of("lib/" + toPath(name) + ".pm");
    }

    @Override
    @Nullable String getTemplatesDirectoryName() {
      return MojoUtil.DEFAULT_TEMPLATES_DIR_NAME;
    }
  },
  LITE_APP("lite_app", "myapp.pl", "mojo.action.generate.lite.app", "mojo.action.generate.lite.app.prompt.message",
           MojoIcons.MOJO_LITE_APP_ICON, null) {
    @Override
    @NotNull List<String> getMainFilePaths(@NotNull String name) {
      return List.of(name);
    }

    @Override
    boolean isGeneratingDirectory() {
      return false;
    }
  },
  PLUGIN("plugin", "MyPlugin", "mojo.action.generate.plugin", "mojo.action.generate.plugin.prompt.message", MojoIcons.pluginIcon(),
         new MojoPlugin.NameValidator()) {
    @Override
    @NotNull List<String> getMainFilePaths(@NotNull String name) {
      // a plugin name without a namespace is prefixed with Mojolicious::Plugin::
      return List.of("lib/Mojolicious/Plugin/" + toPath(name) + ".pm", "lib/" + toPath(name) + ".pm");
    }
  };

  private final String myCommand;
  private final String myDefaultName;
  private final String myTitleKey;
  private final String myNamePromptKey;
  private final Image myIcon;
  private final @Nullable InputValidator myNameValidator;

  MojoNewProjectTemplate(@NotNull String command,
                         @NotNull String defaultName,
                         @NotNull String titleKey,
                         @NotNull String namePromptKey,
                         @NotNull Image icon,
                         @Nullable InputValidator nameValidator) {
    myCommand = command;
    myDefaultName = defaultName;
    myTitleKey = titleKey;
    myNamePromptKey = namePromptKey;
    myIcon = icon;
    myNameValidator = nameValidator;
  }

  /**
   * @return the {@code mojo generate} command
   */
  @NotNull String getCommand() {
    return myCommand;
  }

  @NotNull String getDefaultName() {
    return myDefaultName;
  }

  @NotNull String getTitle() {
    return MojoBundle.message(myTitleKey);
  }

  @NotNull String getNamePrompt() {
    return MojoBundle.message(myNamePromptKey);
  }

  @NotNull Image getIcon() {
    return myIcon;
  }

  boolean isValidName(@NotNull String name) {
    if (myNameValidator != null) {
      return myNameValidator.checkInput(name);
    }
    return !name.isBlank() && name.indexOf('/') < 0 && name.indexOf('\\') < 0;
  }

  /**
   * @return true iff the command generates the files into a new directory (named after the generated entity) of the working directory
   */
  boolean isGeneratingDirectory() {
    return true;
  }

  /**
   * @return the generated templates directory, relative to the generated root
   */
  @Nullable String getTemplatesDirectoryName() {
    return null;
  }

  /**
   * @return candidate paths of the main generated file, relative to the generated root
   */
  abstract @NotNull List<String> getMainFilePaths(@NotNull String name);

  private static @NotNull String toPath(@NotNull String packageName) {
    return packageName.replace("::", "/");
  }
}
