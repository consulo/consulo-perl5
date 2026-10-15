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

import consulo.language.editor.template.context.BaseTemplateContextType;
import consulo.language.editor.template.context.EverywhereContextType;
import consulo.language.editor.template.context.TemplateActionContext;
import consulo.language.editor.template.context.TemplateContextType;
import consulo.localize.LocalizeValue;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiComment;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiWhiteSpace;
import consulo.language.ast.IElementType;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.psi.PsiUtilCore;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.fileTypes.PerlFileTypeTest;
import com.perl5.lang.perl.psi.PerlIfUnlessCompound;
import com.perl5.lang.perl.psi.PsiPerlStatement;
import com.perl5.lang.perl.psi.impl.PerlStringContentElementImpl;
import com.perl5.lang.perl.psi.properties.PerlLoop;
import com.perl5.lang.perl.psi.utils.PerlPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.REGEX_TOKEN;


public abstract class PerlTemplateContextType extends BaseTemplateContextType {
  // TODO consulo: upstream takes context ids from xml (<liveTemplateContext contextId=...>), subclasses using this constructor
  //  (TryCatchTemplateContextType, PerlSwitchTemplateContextType, PerlInSwitchAfterCaseTemplateContextType) should pass their id,
  //  until then the class name is used as id (see getContextId())
  protected PerlTemplateContextType(@NotNull String presentableName) {
    this(null, presentableName);
  }

  // consulo: context id and base context (upstream baseContextId="PERL5") are declared in upstream xml
  protected PerlTemplateContextType(@Nullable String contextId, @NotNull String presentableName) {
    this(contextId, presentableName, Generic.class);
  }

  protected PerlTemplateContextType(@Nullable String contextId,
                                    @NotNull String presentableName,
                                    @Nullable Class<? extends TemplateContextType> baseContextType) {
    super(StringUtil.notNullize(contextId), LocalizeValue.localizeTODO(presentableName), baseContextType);
  }

  @Override
  public String getContextId() {
    String contextId = super.getContextId();
    return contextId.isEmpty() ? getClass().getName() : contextId;
  }

  @Override
  public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
    var psiFile = templateActionContext.getFile();
    var startOffset = templateActionContext.getStartOffset();
    if (!PsiUtilCore.getLanguageAtOffset(psiFile, startOffset).isKindOf(PerlLanguage.INSTANCE)) {
      return false;
    }
    PsiElement element = psiFile.findElementAt(startOffset);

    if (element == null) {
      element = psiFile.findElementAt(startOffset - 1);
    }

    if (element == null) {
      return false;
    }

    IElementType tokenType = PsiUtilCore.getElementType(element);

    return !(element instanceof PsiWhiteSpace ||
             element instanceof PerlStringContentElementImpl ||
             element instanceof PsiComment ||
             tokenType == REGEX_TOKEN) &&
           isInContext(element);
  }

  protected abstract boolean isInContext(PsiElement element);

  public static class Generic extends PerlTemplateContextType {
    public Generic() {
      super("PERL5", PerlLanguage.NAME, EverywhereContextType.class);
    }

    @Override
    public boolean isInContext(PsiElement element) {
      return true;
    }
  }

  public static class Postfix extends PerlTemplateContextType {
    public Postfix() {
      super("PERL5_POSTFIX", PerlBundle.message("perl.template.context.postfix"));
    }

    @Override
    public boolean isInContext(PsiElement element) {
      PsiPerlStatement statement = PsiTreeUtil.getParentOfType(element, PsiPerlStatement.class);

      return statement != null && statement.getTextOffset() < element.getTextOffset();
    }
  }

  public static class Prefix extends PerlTemplateContextType {
    public Prefix() {
      this("PERL5_PREFIX", PerlBundle.message("perl.template.context.prefix"));
    }

    public Prefix(@NotNull String presentableName) {
      super(presentableName);
    }

    public Prefix(@NotNull String contextId, @NotNull String presentableName) {
      super(contextId, presentableName);
    }

    @Override
    public boolean isInContext(PsiElement element) {
      PsiPerlStatement statement = PsiTreeUtil.getParentOfType(element, PsiPerlStatement.class);

      return statement != null && statement.getTextOffset() == element.getTextOffset();
    }
  }

  public static class UnfinishedIf extends PerlTemplateContextType.Prefix {
    public UnfinishedIf() {
      super("PERL5_UNFINISHED_IF", PerlBundle.message("perl.template.context.incomplete.if"));
    }

    @Override
    public boolean isInContext(PsiElement element) {
      if (!super.isInContext(element)) {
        return false;
      }
      PsiPerlStatement statement = PsiTreeUtil.getParentOfType(element, PsiPerlStatement.class);
      if (statement == null) {
        return false;
      }

      PsiElement ifStatement = PerlPsiUtil.getPrevSignificantSibling(statement);
      return ifStatement instanceof PerlIfUnlessCompound compound && compound.getUnconditionalBlock() == null;
    }
  }

  public static class Continue extends PerlTemplateContextType.Prefix {
    public Continue() {
      super("PERL5_CONTINUE", PerlBundle.message("perl.template.context.continue"));
    }

    @Override
    public boolean isInContext(PsiElement element) {
      if (!super.isInContext(element)) {
        return false;
      }

      PsiPerlStatement statement = PsiTreeUtil.getParentOfType(element, PsiPerlStatement.class);
      if (statement == null) {
        return false;
      }

      PsiElement possibleLoop = PerlPsiUtil.getPrevSignificantSibling(statement);
      return possibleLoop instanceof PerlLoop perlLoop &&
             perlLoop.canHaveContinueBlock() &&
             perlLoop.getContinueBlock() == null;
    }
  }

  public static class TestFile extends PerlTemplateContextType.Prefix {
    public TestFile() {
      super("PERL5_TEST_FILE", PerlBundle.message("perl.template.context.test.file"));
    }

    @Override
    public boolean isInContext(PsiElement element) {
      return PerlFileTypeTest.isMy(element) && super.isInContext(element);
    }
  }
}
