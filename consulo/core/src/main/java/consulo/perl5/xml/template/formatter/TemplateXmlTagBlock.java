// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
// Consulo port of com.intellij.xml.template.formatter (IntelliJ xml-ui-common): missing in Consulo, used by Perl5 template languages.
package consulo.perl5.xml.template.formatter;

import com.intellij.xml.util.HtmlUtil;
import consulo.language.ast.ASTNode;
import consulo.language.codeStyle.Alignment;
import consulo.language.codeStyle.Block;
import consulo.language.codeStyle.Indent;
import consulo.language.codeStyle.Wrap;
import consulo.xml.psi.formatter.xml.XmlBlock;
import consulo.xml.psi.formatter.xml.XmlFormattingPolicy;
import consulo.xml.psi.formatter.xml.XmlTagBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TemplateXmlTagBlock extends XmlTagBlock implements IndentInheritingBlock {
  private final AbstractXmlTemplateFormattingModelBuilder myBuilder;
  private Indent myInheritedIndent;

  public TemplateXmlTagBlock(final AbstractXmlTemplateFormattingModelBuilder builder,
                             final ASTNode node,
                             final Wrap wrap,
                             final Alignment alignment,
                             final XmlFormattingPolicy policy,
                             final Indent indent) {
    super(node, wrap, alignment, policy, indent);
    myBuilder = builder;
  }

  @Override
  protected XmlTagBlock createTagBlock(@NotNull ASTNode child, Indent indent, Wrap wrap, Alignment alignment) {
    return myBuilder.createXmlTagBlock(child, wrap, alignment, myXmlFormattingPolicy, indent);
  }

  @Override
  protected final Block createSyntheticBlock(@NotNull ArrayList<Block> localResult, Indent childrenIndent) {
    try {
      List<Block> merged =
        myBuilder.mergeWithTemplateBlocks(localResult, myXmlFormattingPolicy.getSettings(), myXmlFormattingPolicy, childrenIndent);
      return myBuilder.createSyntheticBlock(merged, this, Indent.getNoneIndent(), myXmlFormattingPolicy, childrenIndent);
    }
    catch (FragmentedTemplateException e) {
      return myBuilder.createSyntheticBlock(localResult, this, Indent.getNoneIndent(), myXmlFormattingPolicy, childrenIndent);
    }
  }

  // consulo: Consulo AbstractXmlBlock.createSimpleChild() has no text range parameter (IntelliJ passes null here as well)
  @Override
  protected @NotNull XmlBlock createSimpleChild(@NotNull ASTNode child, @Nullable Indent indent,
                                                @Nullable Wrap wrap, @Nullable Alignment alignment) {
    return myBuilder.createXmlBlock(child, wrap, alignment, myXmlFormattingPolicy, indent, null);
  }

  @Override
  public void setIndent(Indent indent) {
    myInheritedIndent = indent;
  }

  @Override
  public Indent getIndent() {
    return myInheritedIndent == null ? super.getIndent() : myInheritedIndent;
  }

  @Override
  protected Indent getChildrenIndent() {
    return Indent.getIndent(myXmlFormattingPolicy.indentChildrenOf(getTag()) ? Indent.Type.NORMAL : Indent.Type.NONE, false, true);
  }

  boolean isScriptBlock() {
    var tag = getTag();
    return tag != null && tag.getLocalName().equalsIgnoreCase(HtmlUtil.SCRIPT_TAG_NAME);
  }
}
