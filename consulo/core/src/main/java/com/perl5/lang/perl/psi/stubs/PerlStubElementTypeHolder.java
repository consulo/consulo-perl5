package com.perl5.lang.perl.psi.stubs;

import com.perl5.lang.perl.parser.Class.Accessor.ClassAccessorElementTypes;
import com.perl5.lang.perl.parser.PerlElementTypesGenerated;
import com.perl5.lang.perl.parser.moose.MooseElementTypes;
import com.perl5.lang.pod.elementTypes.PodStubElementTypes;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.ast.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Consulo registration of perl and pod stub element types (upstream: {@code stubElementTypeHolder} for {@link PerlStubElementTypes},
 * {@link PodStubElementTypes}, {@link MooseElementTypes} and {@link ClassAccessorElementTypes} in plugin xml). The pod file element type
 * uses the default external id and serialization, so it is not listed.
 */
@ExtensionImpl
public class PerlStubElementTypeHolder extends PerlStubElementTypeHolderBase<PerlStubElementTypes> {
  @Override
  protected @NotNull List<IElementType> getStubElementTypes() {
    return List.of(
      PerlStubElementTypes.FILE,
      PerlStubElementTypes.SUB_DECLARATION,
      PerlStubElementTypes.SUB_DEFINITION,
      PerlStubElementTypes.FUNC_DEFINITION,
      PerlStubElementTypes.METHOD_DEFINITION,
      PerlStubElementTypes.LIGHT_SUB_DEFINITION,
      PerlStubElementTypes.LIGHT_METHOD_DEFINITION,
      PerlStubElementTypes.LIGHT_NAMESPACE_DEFINITION,
      PerlStubElementTypes.LIGHT_ATTRIBUTE_DEFINITION,
      PerlStubElementTypes.PERL_NAMESPACE,
      PerlStubElementTypes.USE_STATEMENT,
      PerlStubElementTypes.NO_STATEMENT,
      PerlStubElementTypes.PERL_DO_EXPR,
      PerlStubElementTypes.PERL_REQUIRE_EXPR,
      PerlStubElementTypes.PERL_GLOB,
      PerlStubElementTypes.PERL_VARIABLE_DECLARATION_ELEMENT,

      PerlElementTypesGenerated.SUB_CALL,

      ClassAccessorElementTypes.CLASS_ACCESSOR_METHOD,

      MooseElementTypes.MOOSE_STATEMENT_AUGMENT,
      MooseElementTypes.MOOSE_STATEMENT_OVERRIDE,

      PodStubElementTypes.HEAD_1_SECTION,
      PodStubElementTypes.HEAD_2_SECTION,
      PodStubElementTypes.HEAD_3_SECTION,
      PodStubElementTypes.HEAD_4_SECTION,
      PodStubElementTypes.POD_PARAGRAPH,
      PodStubElementTypes.UNKNOWN_SECTION,
      PodStubElementTypes.ITEM_SECTION,
      PodStubElementTypes.POD_FORMAT_INDEX
    );
  }
}
