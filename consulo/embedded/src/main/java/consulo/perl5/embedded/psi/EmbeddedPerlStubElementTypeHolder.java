package consulo.perl5.embedded.psi;

import com.perl5.lang.embedded.psi.EmbeddedPerlElementTypes;
import com.perl5.lang.perl.psi.stubs.PerlStubElementTypeHolderBase;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.ast.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Consulo registration of the Embedded Perl file element type (upstream: {@code stubElementTypeHolder} for the parser definition
 * in plugin xml)
 */
@ExtensionImpl
public class EmbeddedPerlStubElementTypeHolder extends PerlStubElementTypeHolderBase<EmbeddedPerlElementTypes> {
  @Override
  protected @NotNull List<IElementType> getStubElementTypes() {
    return List.of(EmbeddedPerlElementTypes.FILE);
  }
}
