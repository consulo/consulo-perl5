package com.perl5.lang.perl.moduleBuild.run;

import com.perl5.lang.perl.moduleBuild.ModuleBuildBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.ui.notification.NotificationGroup;
import consulo.project.ui.notification.NotificationGroupContributor;

import java.util.function.Consumer;

/**
 * Consulo registration of {@code <notificationGroup id="PERL_MODULE_BUILD_ERROR"/>} from the upstream perl5.plugin.moduleBuild.main.xml
 */
@ExtensionImpl
public final class PerlModuleBuildNotificationGroups implements NotificationGroupContributor {
  public static final NotificationGroup BUILD_ERROR =
    NotificationGroup.balloonGroup("PERL_MODULE_BUILD_ERROR",
                                   LocalizeValue.of(ModuleBuildBundle.message("perl.module.build.notification.group.id.build")));

  @Override
  public void contribute(Consumer<NotificationGroup> registrator) {
    registrator.accept(BUILD_ERROR);
  }
}
