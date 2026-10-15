// <defaultLiveTemplates file="liveTemplates/Perl5TryCatch.xml"> in upstream perl5.plugin.backend.main.xml
package consulo.perl5.core.liveTemplates;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.template.LiveTemplateContributor;
import consulo.localize.LocalizeValue;

@ExtensionImpl
public class Perl5TryCatchLiveTemplateContributorRegistration implements LiveTemplateContributor {
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
    try (Builder builder = factory.newBuilder("Perl5.tr", "tr", "try{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("Try block"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.ca", "ca", "catch $EXPR$ {\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("Catch block"))) {
      builder.withReformat();
      builder.withVariable("VAR", "", "\"($expr)\"", true);
      builder.withContext(com.perl5.lang.perl.parser.trycatch.idea.liveTemplates.TryCatchTemplateContextTypeRegistration.class, true);
    }
  }
}
