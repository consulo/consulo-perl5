// This is a generated file. Not intended for manual editing.
package com.perl5.lang.perl.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.perl5.lang.perl.lexer.PerlElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.perl5.lang.perl.psi.*;

public class PerlBlockImpl extends ASTWrapperPsiElement implements PerlBlock {

  public PerlBlockImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PerlVisitor) ((PerlVisitor)visitor).visitBlock(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<PerlBlockCompound> getBlockCompoundList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PerlBlockCompound.class);
  }

  @Override
  @NotNull
  public List<PerlForCompound> getForCompoundList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PerlForCompound.class);
  }

  @Override
  @NotNull
  public List<PerlForeachCompound> getForeachCompoundList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PerlForeachCompound.class);
  }

  @Override
  @NotNull
  public List<PerlGivenCompound> getGivenCompoundList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PerlGivenCompound.class);
  }

  @Override
  @NotNull
  public List<PerlIfCompound> getIfCompoundList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PerlIfCompound.class);
  }

  @Override
  @Nullable
  public PerlLabelDeclaration getLabelDeclaration() {
    return findChildByClass(PerlLabelDeclaration.class);
  }

  @Override
  @NotNull
  public List<PerlPackageNamespace> getPackageNamespaceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PerlPackageNamespace.class);
  }

  @Override
  @NotNull
  public List<PerlStatement> getStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PerlStatement.class);
  }

  @Override
  @NotNull
  public List<PerlSubDefinition> getSubDefinitionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PerlSubDefinition.class);
  }

  @Override
  @NotNull
  public List<PerlUnlessCompound> getUnlessCompoundList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PerlUnlessCompound.class);
  }

  @Override
  @NotNull
  public List<PerlUntilCompound> getUntilCompoundList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PerlUntilCompound.class);
  }

  @Override
  @NotNull
  public List<PerlWhileCompound> getWhileCompoundList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PerlWhileCompound.class);
  }

}
