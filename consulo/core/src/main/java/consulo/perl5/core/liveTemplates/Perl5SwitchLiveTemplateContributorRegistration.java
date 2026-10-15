// <defaultLiveTemplates file="liveTemplates/Perl5Switch.xml"> in upstream perl5.plugin.backend.main.xml
package consulo.perl5.core.liveTemplates;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.template.LiveTemplateContributor;
import consulo.localize.LocalizeValue;

@ExtensionImpl
public class Perl5SwitchLiveTemplateContributorRegistration implements LiveTemplateContributor {
  @Override
  public String groupId() {
    return "Perl5: Switch";
  }

  @Override
  public LocalizeValue groupName() {
    return LocalizeValue.localizeTODO("Perl5: Switch");
  }

  @Override
  public void contribute(Factory factory) {
    try (Builder builder = factory.newBuilder("Perl5: Switch.sw", "sw", "switch ($VAL$)\n{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("switch block"))) {
      builder.withReformat();
      builder.withVariable("VAL", "", "\"$expr\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Switch.ca", "ca", "case $COND$ { $SELECTION$$END$ }\n", LocalizeValue.localizeTODO("case block"))) {
      builder.withReformat();
      builder.withVariable("COND", "\"$expr\"", "", true);
      builder.withContext(com.perl5.lang.perl.parser.perlswitch.idea.liveTemplates.PerlSwitchTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Switch.el", "el", "else { $SELECTION$$END$ }\n", LocalizeValue.localizeTODO("else block"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.parser.perlswitch.idea.liveTemplates.PerlInSwitchAfterCaseTemplateContextTypeRegistration.class, true);
    }
  }
}
