/*
 * Copyright 2015-2020 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.configuration.settings;

import com.perl5.lang.perl.idea.PerlPathMacros;
import com.perl5.lang.perl.util.PerlPluginUtil;
import consulo.application.ApplicationManager;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@State(
  name = "Perl5ApplicationSettings",
  storages = @Storage(PerlPathMacros.PERL5_APP_SETTINGS_FILE)
)
public class PerlApplicationSettings implements PersistentStateComponent<PerlApplicationSettings> {
  public String pluginVersion = "";
  public boolean popupShown = false;

  @Override
  public @Nullable PerlApplicationSettings getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull PerlApplicationSettings state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  private boolean isVersionChanged() {
    return !StringUtil.equals(pluginVersion, PerlPluginUtil.getPluginVersion());
  }

  private void updateVersion() {
    String newVersion = PerlPluginUtil.getPluginVersion();
    pluginVersion = newVersion;
    popupShown = false;
  }

  public void setAnnounceShown() {
    updateVersion();
    popupShown = true;
  }

  public boolean shouldShowAnnounce() {
    return isVersionChanged() || !popupShown;
  }

  public static PerlApplicationSettings getInstance() {
    return ApplicationManager.getApplication().getService(PerlApplicationSettings.class);
  }
}
