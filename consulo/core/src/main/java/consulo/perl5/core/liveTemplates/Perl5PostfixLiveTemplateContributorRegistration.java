// <defaultLiveTemplates file="liveTemplates/Perl5Postfix.xml"> in upstream perl5.plugin.backend.main.xml
package consulo.perl5.core.liveTemplates;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.template.LiveTemplateContributor;
import consulo.localize.LocalizeValue;

@ExtensionImpl
public class Perl5PostfixLiveTemplateContributorRegistration implements LiveTemplateContributor {
  @Override
  public String groupId() {
    return "Perl5: Modifiers";
  }

  @Override
  public LocalizeValue groupName() {
    return LocalizeValue.localizeTODO("Perl5: Modifiers");
  }

  @Override
  public void contribute(Factory factory) {
    try (Builder builder = factory.newBuilder("Perl5: Modifiers.if", "if", "if ($CONDITION$);\n$END$", LocalizeValue.localizeTODO("postfix if"))) {
      builder.withReformat();
      builder.withVariable("CONDITION", "", "\"$expr\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePostfixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Modifiers.unl", "unl", "unless ($CONDITION$);\n$END$", LocalizeValue.localizeTODO("postfix unless"))) {
      builder.withReformat();
      builder.withVariable("CONDITION", "", "\"$expr\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePostfixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Modifiers.wh", "wh", "while ($CONDITION$);\n$END$", LocalizeValue.localizeTODO("postfix while"))) {
      builder.withReformat();
      builder.withVariable("CONDITION", "", "\"$expr\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePostfixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Modifiers.unt", "unt", "until ($CONDITION$);\n$END$", LocalizeValue.localizeTODO("postfix until"))) {
      builder.withReformat();
      builder.withVariable("CONDITION", "", "\"$expr\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePostfixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Modifiers.fo", "fo", "for ($LIST$);\n$END$", LocalizeValue.localizeTODO("postfix for"))) {
      builder.withReformat();
      builder.withVariable("LIST", "", "\"@list\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePostfixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Modifiers.fe", "fe", "foreach ($LIST$);\n$END$\n", LocalizeValue.localizeTODO("postfix foreach"))) {
      builder.withReformat();
      builder.withVariable("LIST", "", "\"@list\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePostfixRegistration.class, true);
    }
  }
}
