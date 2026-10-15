package consulo.perl5.mojo.psi;

import com.perl5.lang.mojolicious.MojoliciousElementTypes;
import com.perl5.lang.perl.psi.stubs.PerlStubElementTypeHolderBase;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.ast.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Consulo registration of the Mojolicious template file element type (upstream: {@code stubElementTypeHolder} for
 * {@link MojoliciousElementTypes} in plugin xml)
 */
@ExtensionImpl
public class MojoStubElementTypeHolder extends PerlStubElementTypeHolderBase<MojoliciousElementTypes> {
  @Override
  protected @NotNull List<IElementType> getStubElementTypes() {
    return List.of(MojoliciousElementTypes.FILE);
  }
}
