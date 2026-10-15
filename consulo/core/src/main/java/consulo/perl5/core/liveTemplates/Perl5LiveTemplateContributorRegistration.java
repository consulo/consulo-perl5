// <defaultLiveTemplates file="liveTemplates/Perl5.xml"> in upstream perl5.plugin.backend.main.xml
package consulo.perl5.core.liveTemplates;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.template.LiveTemplateContributor;
import consulo.localize.LocalizeValue;

@ExtensionImpl
public class Perl5LiveTemplateContributorRegistration implements LiveTemplateContributor {
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
    try (Builder builder = factory.newBuilder("Perl5.sql", "sql", "my $query = <<'SQL';\n    $END$\nSQL\n", LocalizeValue.localizeTODO("query"))) {
      builder.withReformat();
      // TODO consulo: context SQL=false is not a Perl context
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.do", "do", "do\n{\n    $SELECTION$$END$\n} ", LocalizeValue.localizeTODO("do block"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.if", "if", "if ($CONDITION$)\n{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("if block"))) {
      builder.withReformat();
      builder.withVariable("CONDITION", "", "\"$cond\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.as", "as", "async sub $NAME$ \n{\n    my ($ARGUMENTS$) = @_;\n    $END$\n}", LocalizeValue.localizeTODO("async sub definition"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "\"subname\"", true);
      builder.withVariable("ARGUMENTS", "", "\"$self\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.sub", "sub", "sub $NAME$ \n{\n    my ($ARGUMENTS$) = @_;\n    $END$\n}", LocalizeValue.localizeTODO("sub definition"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "\"subname\"", true);
      builder.withVariable("ARGUMENTS", "", "\"$self\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.ust", "ust", "use strict;\n$END$\n", LocalizeValue.localizeTODO("use strict"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.upa", "upa", "use parent qw/$PACKAGE$/;\n$END$", LocalizeValue.localizeTODO("use parent"))) {
      builder.withReformat();
      builder.withVariable("PACKAGE", "", "\"Foo::Bar\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.uwa", "uwa", "use warnings$PARAM$;\n$END$\n", LocalizeValue.localizeTODO("use warnings"))) {
      builder.withReformat();
      builder.withVariable("PARAM", "", "\" FATAL => 'all'\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.uv", "uv", "use v5.$MINOR$;\n$END$\n", LocalizeValue.localizeTODO("use version"))) {
      builder.withReformat();
      builder.withVariable("MINOR", "", "\"10\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.fe", "fe", "foreach my $ELEMENT$ ($LIST$)\n{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("foreach block"))) {
      builder.withReformat();
      builder.withVariable("ELEMENT", "", "\"$element\"", true);
      builder.withVariable("LIST", "", "\"@list\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.ma", "ma", "map {$END$} $LIST$", LocalizeValue.localizeTODO("map expression"))) {
      builder.withReformat();
      builder.withVariable("LIST", "", "\"@list\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeGenericRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.fo", "fo", "for(my $INIT$; $CONDITION$; $INCREMENT$)\n{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("for block"))) {
      builder.withReformat();
      builder.withVariable("INIT", "", "\"$i = 0\"", true);
      builder.withVariable("CONDITION", "", "\"$i < 0\"", true);
      builder.withVariable("INCREMENT", "", "\"$i++\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.el", "el", "else\n{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("else block"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeUnfinishedIfRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.eli", "eli", "elsif($CONDITION$)\n{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("elsif block"))) {
      builder.withReformat();
      builder.withVariable("CONDITION", "", "\"$cond\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeUnfinishedIfRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.gr", "gr", "grep {$END$} $LIST$", LocalizeValue.localizeTODO("grep expression"))) {
      builder.withReformat();
      builder.withVariable("LIST", "", "\"@list\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeGenericRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.unl", "unl", "unless ($EXPR$)\n{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("unless block"))) {
      builder.withReformat();
      builder.withVariable("EXPR", "", "\"$expr\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.gi", "gi", "given ($EXPRESSION$)\n{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("given block"))) {
      builder.withReformat();
      builder.withVariable("EXPRESSION", "", "\"$expr\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.wh", "wh", "while ($EXPR$)\n{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("while block"))) {
      builder.withReformat();
      builder.withVariable("EXPR", "", "\"$expr\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.cont", "cont", "continue\n{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("continue block"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeContinueRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.ev", "ev", "eval {$SELECTION$$END$}", LocalizeValue.localizeTODO("eval block"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.unt", "unt", "until ($EXR$)\n{\n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("until block"))) {
      builder.withReformat();
      builder.withVariable("EXR", "", "\"$expr\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.pod", "pod", "=head3 $SUBNAME$\n\n$END$\n\n=cut\n", LocalizeValue.localizeTODO("quick pod template"))) {
      builder.withVariable("SUBNAME", "", "\"subname\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.cons", "cons", "use constant {\n    $CONST_NAME$ => $CONST_VAL$\n};", LocalizeValue.localizeTODO("Constant definition"))) {
      builder.withReformat();
      builder.withVariable("CONST_NAME", "", "\"CONST\"", true);
      builder.withVariable("CONST_VAL", "", "\"42\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.sa", "sa", "sub {$END$}", LocalizeValue.localizeTODO("anon sub"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeGenericRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.so", "so", "sort {$END$} $LIST$", LocalizeValue.localizeTODO("sort expression"))) {
      builder.withReformat();
      builder.withVariable("LIST", "", "\"@list\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeGenericRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.R", "R", "[\n    $SELECTION$$END$\n]", LocalizeValue.localizeTODO("surround with []"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeGenericRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.serr", "serr", "say STDERR \"$END$\";\n", LocalizeValue.localizeTODO("say STDERR..."))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.sout", "sout", "say \"$END$\";\n", LocalizeValue.localizeTODO("say STDOUT ..."))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.perr", "perr", "print STDERR \"$END$\";\n", LocalizeValue.localizeTODO("print STDERR..."))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5.pout", "pout", "print \"$END$\";\n", LocalizeValue.localizeTODO("print STDOUT ..."))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypePrefixRegistration.class, true);
    }
  }
}
