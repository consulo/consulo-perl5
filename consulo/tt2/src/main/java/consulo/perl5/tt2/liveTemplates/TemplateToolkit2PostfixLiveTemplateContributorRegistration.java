// <defaultLiveTemplates file="liveTemplates/TemplateToolkit2Postfix.xml"> in upstream perl5.lang.tt2.backend.main.xml
package consulo.perl5.tt2.liveTemplates;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.template.LiveTemplateContributor;
import consulo.localize.LocalizeValue;

@ExtensionImpl
public class TemplateToolkit2PostfixLiveTemplateContributorRegistration implements LiveTemplateContributor {
  @Override
  public String groupId() {
    return "Template Toolkit 2 Postfix";
  }

  @Override
  public LocalizeValue groupName() {
    return LocalizeValue.localizeTODO("Template Toolkit 2 Postfix");
  }

  @Override
  public void contribute(Factory factory) {
    try (Builder builder = factory.newBuilder("Template Toolkit 2 Postfix.if", "if", "IF $COND$", LocalizeValue.localizeTODO("IF condition"))) {
      builder.withVariable("COND", "\"condition\"", "", true);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypePostfixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2 Postfix.un", "un", "IF $COND$", LocalizeValue.localizeTODO("UNLESS condition"))) {
      builder.withVariable("COND", "\"condition\"", "", true);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypePostfixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2 Postfix.fi", "fi", "FILTER $NAME$", LocalizeValue.localizeTODO("FILTER directive"))) {
      builder.withVariable("NAME", "tt2FilterName()", "", true);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypePostfixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2 Postfix.fe", "fe", "FOREACH $ITERATOR$ IN $ITERABLE$", LocalizeValue.localizeTODO("FOREACH directive"))) {
      builder.withVariable("ITERATOR", "\"iterator\"", "", true);
      builder.withVariable("ITERABLE", "\"iterable\"", "", true);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypePostfixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2 Postfix.wh", "wh", "WHILE $CONDITION$", LocalizeValue.localizeTODO("WHILE directive"))) {
      builder.withVariable("CONDITION", "\"condition\"", "", true);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypePostfixRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2 Postfix.wr", "wr", "WRAPPER $NAME$", LocalizeValue.localizeTODO("WRAPPER directive"))) {
      builder.withVariable("NAME", "\"name\"", "", true);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypePostfixRegistration.class, true);
    }
  }
}
