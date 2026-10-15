package com.perl5.lang.perl.idea.sdk;

import com.perl5.PerlBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.ui.notification.NotificationDisplayType;
import consulo.project.ui.notification.NotificationGroup;
import consulo.project.ui.notification.NotificationGroupContributor;

import java.util.function.Consumer;

/**
 * Consulo notification groups for the interpreters/hosts/version managers/project area.
 * Upstream passes group display names as strings to the {@code Notification} constructor (or registers them in plugin.xml),
 * Consulo requires registered {@link NotificationGroup} instances.
 */
@ExtensionImpl
public final class PerlSdkNotificationGroups implements NotificationGroupContributor {
  public static final NotificationGroup SYNC =
    NotificationGroup.balloonGroup("perl.sync.notification.group", LocalizeValue.of(PerlBundle.message("perl.sync.notification.group")));

  public static final NotificationGroup SELECT_SDK =
    NotificationGroup.balloonGroup("perl.select.sdk.notification", LocalizeValue.of(PerlBundle.message("perl.select.sdk.notification")));

  public static final NotificationGroup MISSING_LIBRARY =
    NotificationGroup.balloonGroup("perl.missing.library.notification",
                                   LocalizeValue.of(PerlBundle.message("perl.missing.library.notification")));

  public static final NotificationGroup VERSION_MANAGERS =
    NotificationGroup.balloonGroup("perl.vm.notification.group", LocalizeValue.of(PerlBundle.message("perl.vm.notification.group")));

  public static final NotificationGroup CACHE_CLEANER =
    NotificationGroup.balloonGroup("perl.cache.cleaner.notification.group",
                                   LocalizeValue.of(PerlBundle.message("perl.cache.cleaner.notification.group")));

  /**
   * upstream: {@code <notificationGroup id="perl5.plugin.update.notification.group" displayType="STICKY_BALLOON" isLogByDefault="true"/>}
   */
  public static final NotificationGroup PLUGIN_UPDATE =
    new NotificationGroup("perl5.plugin.update.notification.group",
                          LocalizeValue.of(PerlBundle.message("plugin.update.baloon.group")),
                          NotificationDisplayType.STICKY_BALLOON,
                          true);

  @Override
  public void contribute(Consumer<NotificationGroup> registrator) {
    registrator.accept(SYNC);
    registrator.accept(SELECT_SDK);
    registrator.accept(MISSING_LIBRARY);
    registrator.accept(VERSION_MANAGERS);
    registrator.accept(CACHE_CLEANER);
    registrator.accept(PLUGIN_UPDATE);
  }
}
