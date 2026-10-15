package com.perl5.lang.perl.idea.actions;

import com.perl5.PerlBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.ui.notification.NotificationGroup;
import consulo.project.ui.notification.NotificationGroupContributor;

import java.util.function.Consumer;

/**
 * Consulo registration of the notification groups used by Perl actions. Upstream used unregistered group ids
 * ({@code perl.action.deparse.notification.group}, {@code perl.action.perl.tidy.notification.group}) directly in
 * {@link PerlDeparseFileAction} and {@link PerlFormatWithPerlTidyAction}, which IntelliJ shows as balloons.
 */
@ExtensionImpl
public class PerlActionsNotificationGroupContributor implements NotificationGroupContributor {
  public static final NotificationGroup PERL_DEPARSE = NotificationGroup.balloonGroup(
    PerlBundle.message("perl.action.deparse.notification.group"),
    LocalizeValue.localizeTODO(PerlBundle.message("perl.action.deparse.notification.group"))
  );

  public static final NotificationGroup PERL_TIDY = NotificationGroup.balloonGroup(
    PerlBundle.message("perl.action.perl.tidy.notification.group"),
    LocalizeValue.localizeTODO(PerlBundle.message("perl.action.perl.tidy.notification.group"))
  );

  @Override
  public void contribute(Consumer<NotificationGroup> registrator) {
    registrator.accept(PERL_DEPARSE);
    registrator.accept(PERL_TIDY);
  }
}
