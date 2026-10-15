// <defaultLiveTemplates file="liveTemplates/Perl5MethodSignaturesSimple.xml"> in upstream perl5.plugin.backend.main.xml
package consulo.perl5.core.liveTemplates;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.template.LiveTemplateContributor;
import consulo.localize.LocalizeValue;

@ExtensionImpl
public class Perl5MethodSignaturesSimpleLiveTemplateContributorRegistration implements LiveTemplateContributor {
  @Override
  public String groupId() {
    return "Perl5";
  }

  @Override
  public LocalizeValue groupName() {
    return LocalizeValue.localizeTODO("Perl5");
  }

  @Override
  public void contribute(Factory factory) {
    try (Builder builder = factory.newBuilder("Perl5.me", "me", "method $NAME$ ($SIGNATURE$)\n{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("method definition"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "\"methodname\"", true);
      builder.withVariable("SIGNATURE", "", "\"$param\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.fu", "fu", "func $NAME$ ($SIGNATURE$)\n{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("function definition"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "\"funcname\"", true);
      builder.withVariable("SIGNATURE", "", "\"$arg\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
  }
}
