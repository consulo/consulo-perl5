package consulo.perl5.mason.psi;

import com.perl5.lang.htmlmason.elementType.HTMLMasonElementTypes;
import com.perl5.lang.mason2.elementType.Mason2ElementTypes;
import com.perl5.lang.perl.psi.stubs.PerlStubElementTypeHolderBase;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.ast.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Consulo registration of HTML::Mason and Mason2 stub element types (upstream: {@code stubElementTypeHolder} for
 * {@link HTMLMasonElementTypes} and {@link Mason2ElementTypes} in plugin xml)
 */
@ExtensionImpl
public class MasonStubElementTypeHolder extends PerlStubElementTypeHolderBase<Mason2ElementTypes> {
  @Override
  protected @NotNull List<IElementType> getStubElementTypes() {
    return List.of(
      HTMLMasonElementTypes.FILE,
      HTMLMasonElementTypes.HTML_MASON_METHOD_DEFINITION,
      HTMLMasonElementTypes.HTML_MASON_SUBCOMPONENT_DEFINITION,
      HTMLMasonElementTypes.HTML_MASON_FLAGS_STATEMENT,
      HTMLMasonElementTypes.HTML_MASON_ARGS_BLOCK,

      Mason2ElementTypes.PP_FILE,
      Mason2ElementTypes.COMPONENT_FILE,
      Mason2ElementTypes.MASON_NAMESPACE_DEFINITION,
      Mason2ElementTypes.MASON_AUGMENT_MODIFIER,
      Mason2ElementTypes.MASON_OVERRIDE_DEFINITION,
      Mason2ElementTypes.MASON_METHOD_DEFINITION,
      Mason2ElementTypes.MASON_FILTER_DEFINITION
    );
  }
}
