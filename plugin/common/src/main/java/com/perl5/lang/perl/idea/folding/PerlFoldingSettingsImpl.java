/*
 * Copyright 2015-2025 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.folding;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import jakarta.inject.Singleton;
import consulo.application.ApplicationManager;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.util.xml.serializer.XmlSerializerUtil;
import com.perl5.lang.perl.idea.PerlPathMacros;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@State(
  name = "PerlCodeFoldingSettings",
  storages = @Storage(PerlPathMacros.APP_CODEINSIGHT_SETTINGS_FILE)
)
@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
@Singleton
public class PerlFoldingSettingsImpl implements PersistentStateComponent<PerlFoldingSettingsImpl> {
  public boolean COLLAPSE_COMMENTS = true;
  public boolean COLLAPSE_CONSTANT_BLOCKS = false;
  public boolean COLLAPSE_ANON_ARRAYS = false;
  public boolean COLLAPSE_ANON_HASHES = false;
  public boolean COLLAPSE_PARENTHESISED = false;
  public boolean COLLAPSE_HEREDOCS = false;
  public boolean COLLAPSE_TEMPLATES = false;
  public boolean COLLAPSE_QW = false;
  public boolean COLLAPSE_CHAR_SUBSTITUTIONS = true;

  @Override
  public @Nullable PerlFoldingSettingsImpl getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull PerlFoldingSettingsImpl state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public static PerlFoldingSettingsImpl getInstance() {
    return ApplicationManager.getApplication().getService(PerlFoldingSettingsImpl.class);
  }

  // TODO consulo: PersistentStateComponent has no noStateLoaded() callback, so this is never called
  public void noStateLoaded() {
    loadState(new PerlFoldingSettingsImpl());
  }
}
