// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
// Consulo port of com.intellij.xml.template.formatter (IntelliJ xml-ui-common): missing in Consulo, used by Perl5 template languages.
package consulo.perl5.xml.template.formatter;

import consulo.language.ast.ASTNode;
import consulo.language.codeStyle.ASTBlock;
import consulo.language.codeStyle.Block;
import consulo.language.codeStyle.Indent;
import consulo.language.codeStyle.Spacing;
import consulo.xml.language.XMLLanguage;
import consulo.xml.language.psi.XmlTag;
import consulo.xml.psi.formatter.xml.SyntheticBlock;
import consulo.xml.psi.formatter.xml.XmlFormattingPolicy;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TemplateSyntheticBlock extends SyntheticBlock implements IndentInheritingBlock {
  private Indent myInheritedIndent;

  public TemplateSyntheticBlock(final List<Block> subBlocks,
                                final Block parent,
                                final Indent indent,
                                XmlFormattingPolicy policy,
                                final Indent childIndent) {
    super(subBlocks, parent, indent, policy, childIndent);
  }

  @Override
  public void setIndent(Indent indent) {
    myInheritedIndent = indent;
  }

  @Override
  public Indent getIndent() {
    return myInheritedIndent != null ? myInheritedIndent : super.getIndent();
  }

  @Override
  public Spacing getSpacing(Block child1, @NotNull Block child2) {
    if (child1 != null && isXmlBlock(child1) != isXmlBlock(child2)) {
      if (shouldKeepWhiteSpacesInside()) return Spacing.getReadOnlySpacing();
      return Spacing.createSpacing(0, 1, 0, true, myXmlFormattingPolicy.getKeepBlankLines());
    }
    return super.getSpacing(child1, child2);
  }

  // consulo: Consulo AbstractSyntheticBlock has no shouldKeepWhiteSpacesInside(), same logic as in IntelliJ
  private boolean shouldKeepWhiteSpacesInside() {
    XmlTag tag = getTag();
    return tag != null && myXmlFormattingPolicy.keepWhiteSpacesInsideTag(tag);
  }

  private static boolean isXmlBlock(@NotNull Block block) {
    if (block instanceof TemplateXmlTagBlock || block instanceof TemplateXmlBlock) return true;
    if (block instanceof ASTBlock) {
      ASTNode node = ((ASTBlock)block).getNode();
      return node != null && node.getPsi().getLanguage().isKindOf(XMLLanguage.INSTANCE);
    }
    return false;
  }
}
