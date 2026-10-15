/*
 * Copyright 2015-2026 Alexandr Evstigneev
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
package com.perl5.lang.perl.idea.editor.smartkeys.options;

import com.intellij.openapi.options.UiDslUnnamedConfigurable;
import com.intellij.ui.dsl.builder.Panel;
import com.perl5.lang.perl.idea.codeInsight.Perl5CodeInsightSettings;
import org.jetbrains.annotations.NotNull;

import static com.perl5.PerlBundle.message;

public final class PerlSmartKeyOptions extends UiDslUnnamedConfigurable.Simple {

  // TODO consulo: upstream uses the Kotlin UI DSL (`fun Panel.createContent()` receiver, group/row builder lambdas with
  //  default arguments, `bindSelected` extension from com.intellij.ui.dsl.builder); it has no Java API, the calls below
  //  mirror the upstream structure and need a Consulo UI implementation
  @Override
  public void createContent(@NotNull Panel panel) {
    var settings = Perl5CodeInsightSettings.getInstance();
    panel.group(message("border.title.perl5"), group -> {
      group.row(row -> {
        row.checkBox(message("perl.options.auto.heredoc"))
          .bindSelected(
            () -> settings.HEREDOC_AUTO_INSERTION,
            (Boolean value) -> settings.HEREDOC_AUTO_INSERTION = value);
      });
      group.row(row -> {
        row.checkBox(message("perl.options.auto.colon"))
          .bindSelected(
            () -> settings.AUTO_INSERT_COLON,
            (Boolean value) -> settings.AUTO_INSERT_COLON = value);
      });
      group.row(row -> {
        row.checkBox(message("perl.options.auto.brace.substitution.hex"))
          .bindSelected(
            () -> settings.AUTO_BRACE_HEX_SUBSTITUTION,
            (Boolean value) -> settings.AUTO_BRACE_HEX_SUBSTITUTION = value);
      });
      group.row(row -> {
        row.checkBox(message("perl.options.auto.brace.substitution.oct"))
          .bindSelected(
            () -> settings.AUTO_BRACE_OCT_SUBSTITUTION,
            (Boolean value) -> settings.AUTO_BRACE_OCT_SUBSTITUTION = value);
      });
      group.row(row -> {
        row.checkBox(message("perl.options.smart.comma.sequence"))
          .bindSelected(
            () -> settings.SMART_COMMA_SEQUENCE_TYPING,
            (Boolean value) -> settings.SMART_COMMA_SEQUENCE_TYPING = value);
      });
    });
  }
}
