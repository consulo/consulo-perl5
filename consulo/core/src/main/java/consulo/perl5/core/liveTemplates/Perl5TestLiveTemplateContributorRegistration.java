// <defaultLiveTemplates file="liveTemplates/Perl5Test.xml"> in upstream perl5.plugin.backend.main.xml
package consulo.perl5.core.liveTemplates;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.template.LiveTemplateContributor;
import consulo.localize.LocalizeValue;

@ExtensionImpl
public class Perl5TestLiveTemplateContributorRegistration implements LiveTemplateContributor {
  @Override
  public String groupId() {
    return "Perl5: Test::More";
  }

  @Override
  public LocalizeValue groupName() {
    return LocalizeValue.localizeTODO("Perl5: Test::More");
  }

  @Override
  public void contribute(Factory factory) {
    try (Builder builder = factory.newBuilder("Perl5: Test::More.st", "st", "subtest $NAME$ => sub{\n    $SELECTION$$END$\n};", LocalizeValue.localizeTODO("subtest"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "\"testname\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.ok", "ok", "ok( $EXPR$, '$MESSAGE$' );\n$END$", LocalizeValue.localizeTODO("ok"))) {
      builder.withReformat();
      builder.withVariable("EXPR", "", "\"$expr\"", true);
      builder.withVariable("MESSAGE", "", "\"message\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.is", "is", "is( $ACT$, $EXP$, '$MESSAGE$' );\n$END$", LocalizeValue.localizeTODO("is"))) {
      builder.withReformat();
      builder.withVariable("ACT", "", "\"$actual\"", true);
      builder.withVariable("EXP", "", "\"$expected\"", true);
      builder.withVariable("MESSAGE", "", "\"message\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.it", "it", "isnt( $ACT$, $EXP$, '$MESSAGE$' );\n$END$", LocalizeValue.localizeTODO("isnt"))) {
      builder.withReformat();
      builder.withVariable("ACT", "", "\"$actual\"", true);
      builder.withVariable("EXP", "", "\"$expected\"", true);
      builder.withVariable("MESSAGE", "", "\"message\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.li", "li", "like( $ACT$, $EXP$, '$MESSAGE$' );\n$END$", LocalizeValue.localizeTODO("like"))) {
      builder.withReformat();
      builder.withVariable("ACT", "", "\"$actual\"", true);
      builder.withVariable("EXP", "", "\"$expected\"", true);
      builder.withVariable("MESSAGE", "", "\"message\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.ul", "ul", "unlike( $ACT$, $EXP$, '$MESSAGE$' );\n$END$", LocalizeValue.localizeTODO("unlike"))) {
      builder.withReformat();
      builder.withVariable("ACT", "", "\"$actual\"", true);
      builder.withVariable("EXP", "", "\"$expected\"", true);
      builder.withVariable("MESSAGE", "", "\"message\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.cmo", "cmo", "cmp_ok( $ACT$, '$OP$', $EXPECTED$, '$MESSAGE$' );\n$END$", LocalizeValue.localizeTODO("cmp_ok"))) {
      builder.withReformat();
      builder.withVariable("ACT", "", "\"$actual\"", true);
      builder.withVariable("OP", "", "\"op\"", true);
      builder.withVariable("EXPECTED", "", "\"$expected\"", true);
      builder.withVariable("MESSAGE", "", "\"message\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.cno", "cno", "can_ok( $OBJ$, qw/$SUBS$/ );\n$END$", LocalizeValue.localizeTODO("can_ok"))) {
      builder.withReformat();
      builder.withVariable("OBJ", "", "\"$obj\"", true);
      builder.withVariable("SUBS", "", "\"subname\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.iso", "iso", "isa_ok( $OBJ$, '$CLASS$', '$MESSAGE$' );\n$END$", LocalizeValue.localizeTODO("isa_ok"))) {
      builder.withReformat();
      builder.withVariable("OBJ", "", "\"$obj\"", true);
      builder.withVariable("CLASS", "", "\"Foo::Bar\"", true);
      builder.withVariable("MESSAGE", "", "\"message\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.neo", "neo", "new_ok( $CLASS$ => $ARGS$, '$MESSAGE$' );\n$END$", LocalizeValue.localizeTODO("new_ok"))) {
      builder.withReformat();
      builder.withVariable("CLASS", "", "\"$class\"", true);
      builder.withVariable("ARGS", "", "\"$args_ref\"", true);
      builder.withVariable("MESSAGE", "", "\"message\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.pa", "pa", "pass( '$TEST_NAME$' );\n$END$", LocalizeValue.localizeTODO("pass"))) {
      builder.withReformat();
      builder.withVariable("TEST_NAME", "", "\"test_name\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.fa", "fa", "fail( '$TEST_NAME$' );\n$END$", LocalizeValue.localizeTODO("fail"))) {
      builder.withReformat();
      builder.withVariable("TEST_NAME", "", "\"test_name\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.ro", "ro", "require_ok( '$MODULE_OR_PACKAGE$' );\n$END$", LocalizeValue.localizeTODO("require_ok"))) {
      builder.withReformat();
      builder.withVariable("MODULE_OR_PACKAGE", "", "\"Foo::Bar\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.uo", "uo", "use_ok( '$MODULE_OR_PACKAGE$' );\n$END$", LocalizeValue.localizeTODO("use_ok"))) {
      builder.withReformat();
      builder.withVariable("MODULE_OR_PACKAGE", "", "\"Foo::Bar\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.id", "id", "is_deeply( $ACT$, $EXP$, '$MESSAGE$' );\n$END$", LocalizeValue.localizeTODO("is_deepy"))) {
      builder.withReformat();
      builder.withVariable("ACT", "", "\"$actual\"", true);
      builder.withVariable("EXP", "", "\"$expected\"", true);
      builder.withVariable("MESSAGE", "", "\"message\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.di", "di", "diag( '$MESSAGE$' );\n$END$", LocalizeValue.localizeTODO("diag"))) {
      builder.withReformat();
      builder.withVariable("MESSAGE", "", "\"message\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.nt", "nt", "note( '$MESSAGE$' );\n$END$", LocalizeValue.localizeTODO("note"))) {
      builder.withReformat();
      builder.withVariable("MESSAGE", "", "\"message\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.exp", "exp", "explain( $REFERENCE$ );\n$END$", LocalizeValue.localizeTODO("explain"))) {
      builder.withReformat();
      builder.withVariable("REFERENCE", "", "\"$reference\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.sk", "sk", "SKIP: {\n    skip '$REASON$', $HOW_MANY$$CONDITION$;\n    \n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("skip block"))) {
      builder.withReformat();
      builder.withVariable("REASON", "", "\"reason\"", true);
      builder.withVariable("HOW_MANY", "", "\"$number\"", true);
      builder.withVariable("CONDITION", "", "\" if $condition\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.td", "td", "TODO: {\n    local $TODO = '$REASON$'$CONDITION$;\n    \n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("todo block"))) {
      builder.withReformat();
      builder.withVariable("REASON", "", "\"reason\"", true);
      builder.withVariable("CONDITION", "", "\" if $condition\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.tds", "tds", "TODO: {\n    todo_skip '$REASON$', $HOW_MANY$$CONDITION$;\n    \n    $SELECTION$$END$\n}", LocalizeValue.localizeTODO("todo_skip block"))) {
      builder.withReformat();
      builder.withVariable("REASON", "", "\"reason\"", true);
      builder.withVariable("HOW_MANY", "", "\"$number\"", true);
      builder.withVariable("CONDITION", "", "\" if $condition\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.bo", "bo", "BAIL_OUT( '$RESAON$' );\n$END$", LocalizeValue.localizeTODO("bail_out"))) {
      builder.withReformat();
      builder.withVariable("RESAON", "", "\"reason\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.pl", "pl", "plan tests => $NUMBER_OF_TESTS$;\n$END$", LocalizeValue.localizeTODO("plan tests"))) {
      builder.withReformat();
      builder.withVariable("NUMBER_OF_TESTS", "", "\"$number\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.psa", "psa", "plan skip_all => '$REASON$';\n$END$", LocalizeValue.localizeTODO("plan skip_all"))) {
      builder.withReformat();
      builder.withVariable("REASON", "", "\"reason\"", true);
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: Test::More.pnp", "pnp", "plan 'no_plan';\n$END$", LocalizeValue.localizeTODO("plan no_plan"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextTypeTestFileRegistration.class, true);
    }
  }
}
