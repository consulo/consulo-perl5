// This is a generated file. Not intended for manual editing.
package com.perl5.lang.tt2.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.perl5.lang.tt2.parser.TemplateToolkitElementTypesGenerated.*;
import com.perl5.lang.tt2.psi.*;

public class PsiBlockDirectiveImpl extends TemplateToolkitCompositeElementImpl implements PsiBlockDirective {

  public PsiBlockDirectiveImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiTemplateToolkitVisitorGenerated visitor) {
    visitor.visitBlockDirective(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PsiTemplateToolkitVisitorGenerated) accept((PsiTemplateToolkitVisitorGenerated)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiBlockName getBlockName() {
    return findChildByClass(PsiBlockName.class);
  }

}
