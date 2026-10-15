package com.perl5.lang.perl.coverage;

import com.perl5.PerlBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.ui.notification.NotificationGroup;
import consulo.project.ui.notification.NotificationGroupContributor;

import java.util.function.Consumer;

/**
 * Consulo registration of the notification group used by {@link PerlCoverageRunner}. Upstream passes the unregistered group id
 * ({@code perl.coverage.loading.error} message) directly to the {@code Notification} constructor, which IntelliJ shows as balloon.
 */
@ExtensionImpl
public class PerlCoverageNotificationGroupContributor implements NotificationGroupContributor {
  public static final NotificationGroup COVERAGE_LOADING_ERROR = NotificationGroup.balloonGroup(
    PerlBundle.message("perl.coverage.loading.error"),
    LocalizeValue.localizeTODO(PerlBundle.message("perl.coverage.loading.error"))
  );

  @Override
  public void contribute(Consumer<NotificationGroup> registrator) {
    registrator.accept(COVERAGE_LOADING_ERROR);
  }
}
