package com.perl5.lang.perl.debugger;

import com.perl5.PerlBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.ui.notification.NotificationGroup;
import consulo.project.ui.notification.NotificationGroupContributor;

import java.util.function.Consumer;

/**
 * Consulo registration of the debugger notification group, upstream
 * {@code <notificationGroup displayType="BALLOON" id="PERL_DEBUGGER" key="perl.debugger.notification.group"/>}
 * in perl5.plugin.debugger.main.xml. Consulo requires a registered {@link NotificationGroup} instance instead of the group id.
 */
@ExtensionImpl
public class PerlDebuggerNotificationGroupContributor implements NotificationGroupContributor {
  public static final NotificationGroup PERL_DEBUGGER = NotificationGroup.balloonGroup(
    PerlDebugProcess.PERL_DEBUGGER_NOTIFICATION_GROUP_ID,
    LocalizeValue.localizeTODO(PerlBundle.message("perl.debugger.notification.group"))
  );

  @Override
  public void contribute(Consumer<NotificationGroup> registrator) {
    registrator.accept(PERL_DEBUGGER);
  }
}
