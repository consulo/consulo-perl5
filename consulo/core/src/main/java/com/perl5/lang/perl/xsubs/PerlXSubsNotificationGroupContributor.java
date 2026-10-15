package com.perl5.lang.perl.xsubs;

import com.perl5.PerlBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.ui.notification.NotificationGroup;
import consulo.project.ui.notification.NotificationGroupContributor;

import java.util.function.Consumer;

/**
 * Consulo notification group of {@link PerlXSubsState}.
 * Upstream passes the group display name ({@code perl.deparsing.notification}) as a string to the {@code Notification} constructor,
 * Consulo requires a registered {@link NotificationGroup} instance.
 */
@ExtensionImpl
public final class PerlXSubsNotificationGroupContributor implements NotificationGroupContributor {
  public static final NotificationGroup DEPARSING =
    NotificationGroup.balloonGroup("perl.deparsing.notification", LocalizeValue.of(PerlBundle.message("perl.deparsing.notification")));

  @Override
  public void contribute(Consumer<NotificationGroup> registrator) {
    registrator.accept(DEPARSING);
  }
}
