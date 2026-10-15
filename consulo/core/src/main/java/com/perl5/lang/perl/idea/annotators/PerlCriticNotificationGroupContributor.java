package com.perl5.lang.perl.idea.annotators;

import com.perl5.PerlBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.ui.notification.NotificationGroup;
import consulo.project.ui.notification.NotificationGroupContributor;

import java.util.function.Consumer;

/**
 * Consulo registration of the Perl::Critic notification group. Upstream used an unregistered group id
 * ({@code perl.critic.notification.group}) directly in {@link PerlCriticAnnotator}, which IntelliJ shows as a balloon.
 */
@ExtensionImpl
public class PerlCriticNotificationGroupContributor implements NotificationGroupContributor {
  public static final NotificationGroup PERL_CRITIC = NotificationGroup.balloonGroup(
    PerlBundle.message("perl.critic.notification.group"),
    LocalizeValue.localizeTODO(PerlBundle.message("perl.critic.notification.group"))
  );

  @Override
  public void contribute(Consumer<NotificationGroup> registrator) {
    registrator.accept(PERL_CRITIC);
  }
}
