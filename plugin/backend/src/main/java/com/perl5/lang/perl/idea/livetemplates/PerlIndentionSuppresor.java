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

package com.perl5.lang.perl.idea.livetemplates;

import consulo.language.editor.template.Template;
import consulo.language.editor.template.context.TemplateContext;
import consulo.language.editor.template.TemplateOptionalProcessor;
import consulo.document.Document;
import consulo.codeEditor.Editor;
import consulo.document.RangeMarker;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.util.dataholder.KeyWithDefaultValue;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;


public class PerlIndentionSuppresor implements TemplateOptionalProcessor {
  // pattern for getting marker
  public static final Pattern HEREDOC_OPENER_PATTERN = Pattern.compile("<<(.+?)");
  public static final Pattern HEREDOC_OPENER_PATTERN_DQ = Pattern.compile("<<(\\s*)(\")(.*?)\"");
  public static final Pattern HEREDOC_OPENER_PATTERN_SQ = Pattern.compile("<<(\\s*)(')(.*?)'");
  public static final Pattern HEREDOC_OPENER_PATTERN_XQ = Pattern.compile("<<(\\s*)(`)(.*?)`");

  // consulo: Consulo template options are keyed; this processor overrides isEnabled() and is never visible
  private static final KeyWithDefaultValue<Boolean> KEY = KeyWithDefaultValue.create("perl5.indention.suppressor", true);

  @Override
  public KeyWithDefaultValue<Boolean> getKey() {
    return KEY;
  }


  @Override
  public void processText(Project project, Template template, Document document, RangeMarker templateRange, Editor editor) {
    if (isEnabled(template)) {
      String templateText = template.getTemplateText();

      if (HEREDOC_OPENER_PATTERN.matcher(templateText).find()
          || HEREDOC_OPENER_PATTERN_SQ.matcher(templateText).find()
          || HEREDOC_OPENER_PATTERN_DQ.matcher(templateText).find()
          || HEREDOC_OPENER_PATTERN_XQ.matcher(templateText).find()
        ) {
        template.setToIndent(false);
      }
    }
  }

  @Override
  public @NotNull LocalizeValue getOptionText() {
    return LocalizeValue.empty();
  }

  @Override
  public boolean isEnabled(Template template) {
    // fixme this is probably bad. Doesn't work if it's custom group
    String groupName = template.getGroupName();
    return groupName != null && groupName.startsWith("Perl5");
  }

  @Override
  public boolean isVisible(@NotNull Template template) {
    return false;
  }
}
