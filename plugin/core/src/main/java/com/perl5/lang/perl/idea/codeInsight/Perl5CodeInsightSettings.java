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

package com.perl5.lang.perl.idea.codeInsight;

import com.perl5.lang.perl.idea.PerlPathMacros;
import consulo.application.ApplicationManager;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.util.xml.serializer.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;


@State(
  name = "Perl5CodeInsightSettings",
  storages = @Storage(PerlPathMacros.APP_OTHER_SETTINGS_FILE)

)
public class Perl5CodeInsightSettings implements PersistentStateComponent<Perl5CodeInsightSettings>, Cloneable {
  public boolean HEREDOC_AUTO_INSERTION = true;
  public boolean AUTO_INSERT_COLON = true;
  public boolean SMART_COMMA_SEQUENCE_TYPING = true;
  public boolean AUTO_BRACE_HEX_SUBSTITUTION = true;
  public boolean AUTO_BRACE_OCT_SUBSTITUTION = true;

  @Override
  public @Nullable Perl5CodeInsightSettings getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull Perl5CodeInsightSettings state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public static Perl5CodeInsightSettings getInstance() {
    return ApplicationManager.getApplication().getInstance(Perl5CodeInsightSettings.class);
  }

  @TestOnly
  public Perl5CodeInsightSettings copy() throws CloneNotSupportedException {
    return (Perl5CodeInsightSettings)super.clone();
  }
}
