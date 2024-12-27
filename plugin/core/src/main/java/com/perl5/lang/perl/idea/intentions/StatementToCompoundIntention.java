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

package com.perl5.lang.perl.idea.intentions;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.psi.PsiPerlExpr;
import com.perl5.lang.perl.psi.PsiPerlParenthesisedExpr;
import com.perl5.lang.perl.psi.PsiPerlStatementModifier;
import com.perl5.lang.perl.psi.impl.PerlFileImpl;
import com.perl5.lang.perl.psi.impl.PsiPerlStatementImpl;
import com.perl5.lang.perl.psi.utils.PerlElementFactory;
import consulo.codeEditor.Editor;
import consulo.document.util.TextRange;
import consulo.language.editor.intention.PsiElementBaseIntentionAction;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiErrorElement;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatementToCompoundIntention extends PsiElementBaseIntentionAction {
  @Override
  public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
    PsiPerlStatementImpl statement = getStatement(element);
    if (statement == null) {
      return;
    }
    PsiPerlStatementModifier modifier = statement.getModifier();
    if (modifier == null) {
      return;
    }
    PsiElement keyword = modifier.getFirstChild();
    if (keyword == null) {
      return;
    }

    PsiPerlExpr modifierExpression = PsiTreeUtil.getChildOfType(modifier, PsiPerlExpr.class);
    PsiPerlExpr valueExpression = modifierExpression instanceof PsiPerlParenthesisedExpr ?
                                  ((PsiPerlParenthesisedExpr)modifierExpression).getExpr() :
                                  modifierExpression;
    String conditionText = valueExpression == null ? "" : valueExpression.getText();

    TextRange modifierRangeInStatement = TextRange.from(modifier.getStartOffsetInParent(), modifier.getTextLength());
    String statementText = modifierRangeInStatement.replace(statement.getText(), "");

    StringBuilder newCode = new StringBuilder();
    newCode.append(keyword.getText())
      .append("(")
      .append(conditionText)
      .append("){\n")
      .append(statementText)
      .append("\n}");

    PerlFileImpl fakeFile = PerlElementFactory.createFile(project, newCode.toString());
    PsiElement[] children = fakeFile.getChildren();
    assert children.length == 1;
    statement.replace(children[0]);
  }

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
    return getStatement(element) != null;
  }

  @Override
  public @NotNull String getText() {
    return PerlBundle.message("perl.intention.convert.to.compound");
  }

  @Override
  public @Nls @NotNull String getFamilyName() {
    return getText();
  }

  /**
   * @return statement with modifier under cursor, or null
   */
  private static @Nullable PsiPerlStatementImpl getStatement(@NotNull PsiElement elementAtCaret) {
    PsiPerlStatementImpl statement = PsiTreeUtil.getParentOfType(elementAtCaret, PsiPerlStatementImpl.class);
    if (PsiTreeUtil.findChildOfType(statement, PsiErrorElement.class) != null) {
      return null;
    }
    return statement != null && statement.getModifier() != null ? statement : null;
  }
}
