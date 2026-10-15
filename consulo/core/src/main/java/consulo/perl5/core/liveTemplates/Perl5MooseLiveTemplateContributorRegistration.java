// <defaultLiveTemplates file="liveTemplates/Perl5Moose.xml"> in upstream perl5.plugin.backend.main.xml
package consulo.perl5.core.liveTemplates;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.template.LiveTemplateContributor;
import consulo.localize.LocalizeValue;

@ExtensionImpl
public class Perl5MooseLiveTemplateContributorRegistration implements LiveTemplateContributor {
  @Override
  public String groupId() {
    return "Perl5: Moose";
  }

  @Override
  public LocalizeValue groupName() {
    return LocalizeValue.localizeTODO("Perl5: Moose");
  }

  @Override
  public void contribute(Factory factory) {
    try (Builder builder = factory.newBuilder("Perl5: Moose.ex", "ex", "extends '$CLASSNAME$'$VERSION$;\n$END$", LocalizeValue.localizeTODO("extends statement with version"))) {
      builder.withReformat();
      builder.withVariable("CLASSNAME", "", "\"Foo::Bar\"", true);
      builder.withVariable("VERSION", "", "\" => { -version => 1.0 }\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Moose.wi", "wi", "with '$CLASSNAME$'$VERSION$;\n$END$", LocalizeValue.localizeTODO("with statement with version"))) {
      builder.withReformat();
      builder.withVariable("CLASSNAME", "", "\"Foo::Bar\"", true);
      builder.withVariable("VERSION", "", "\" => { -version => 1.0 }\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Moose.ha", "ha", "has $NAME$ => ( is => '$IS$'$END$ );", LocalizeValue.localizeTODO("has statement"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "\"attr_name\"", true);
      builder.withVariable("IS", "", "\"rw\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Moose.be", "be", "before $NAME$ => sub{\n    $SELECTION$$END$\n};\n", LocalizeValue.localizeTODO("before statement"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "\"sub_name\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Moose.af", "af", "after $NAME$ => sub{\n    $SELECTION$$END$\n};\n", LocalizeValue.localizeTODO("after statement"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "\"sub_name\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Moose.ar", "ar", "around $NAME$ => sub{\n    $SELECTION$$END$\n};\n", LocalizeValue.localizeTODO("around statement"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "\"sub_name\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Moose.ov", "ov", "override $NAME$ => sub{\n    $SELECTION$$END$\n};\n", LocalizeValue.localizeTODO("override statement"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "\"sub_name\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Moose.au", "au", "augment $NAME$ => sub{\n    $SELECTION$$END$\n};\n", LocalizeValue.localizeTODO("augment statement"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "\"sub_name\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
  }
}
