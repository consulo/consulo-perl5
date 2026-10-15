// <defaultLiveTemplates file="liveTemplates/Perl5HTMLMason.xml"> in upstream perl5.lang.mason.htmlmason.backend.main.xml
package consulo.perl5.mason.liveTemplates;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.template.LiveTemplateContributor;
import consulo.localize.LocalizeValue;

@ExtensionImpl
public class Perl5HTMLMasonLiveTemplateContributorRegistration implements LiveTemplateContributor {
  @Override
  public String groupId() {
    return "Perl5 HTML::Mason";
  }

  @Override
  public LocalizeValue groupName() {
    return LocalizeValue.localizeTODO("Perl5 HTML::Mason");
  }

  @Override
  public void contribute(Factory factory) {
    try (Builder builder = factory.newBuilder("Perl5 HTML::Mason.%method", "%method", "<%method $NAME$>\n$END$\n</%method>", LocalizeValue.localizeTODO("HTML::Mason <%method>"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "", true);
      builder.withContext(com.perl5.lang.htmlmason.idea.livetemplates.HTMLMasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 HTML::Mason.%filter", "%filter", "<%filter>\n$END$\n</%filter>", LocalizeValue.localizeTODO("HTML::Mason <%filter>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.htmlmason.idea.livetemplates.HTMLMasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 HTML::Mason.%doc", "%doc", "<%doc>\n$END$\n</%doc>", LocalizeValue.localizeTODO("HTML::Mason <%doc>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.htmlmason.idea.livetemplates.HTMLMasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 HTML::Mason.%text", "%text", "<%text>\n$END$ \n</%text>", LocalizeValue.localizeTODO("HTML::Mason <%text>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.htmlmason.idea.livetemplates.HTMLMasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 HTML::Mason.%flags", "%flags", "<%flags>\n    inherit => '$COMPONENT$'\n</%flags>\n$END$", LocalizeValue.localizeTODO("HTML::Mason <%flags>"))) {
      builder.withReformat();
      builder.withVariable("COMPONENT", "", "", true);
      builder.withContext(com.perl5.lang.htmlmason.idea.livetemplates.HTMLMasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 HTML::Mason.%init", "%init", "<%init>\n$END$ \n</%init>", LocalizeValue.localizeTODO("HTML::Mason <%init>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.htmlmason.idea.livetemplates.HTMLMasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 HTML::Mason.%perl", "%perl", "<%perl>\n$END$ \n</%perl>", LocalizeValue.localizeTODO("HTML::Mason <%perl>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.htmlmason.idea.livetemplates.HTMLMasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 HTML::Mason.%def", "%def", "<%def $NAME$>\n$END$\n</%def>", LocalizeValue.localizeTODO("HTML::Mason <%def>"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "", true);
      builder.withContext(com.perl5.lang.htmlmason.idea.livetemplates.HTMLMasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 HTML::Mason.%cleanup", "%cleanup", "<%cleanup>\n$END$ \n</%cleanup>", LocalizeValue.localizeTODO("HTML::Mason <%cleanup>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.htmlmason.idea.livetemplates.HTMLMasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 HTML::Mason.%once", "%once", "<%once>\n$END$\n</%once>", LocalizeValue.localizeTODO("HTML::Mason <%once>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.htmlmason.idea.livetemplates.HTMLMasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 HTML::Mason.%shared", "%shared", "<%shared>\n$END$\n</%shared>", LocalizeValue.localizeTODO("HTML::Mason <%shared>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.htmlmason.idea.livetemplates.HTMLMasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 HTML::Mason.%attr", "%attr", "<%attr>\n$END$\n</%attr>", LocalizeValue.localizeTODO("HTML::Mason <%attr>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.htmlmason.idea.livetemplates.HTMLMasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 HTML::Mason.%args", "%args", "<%args>\n$END$\n</%args>", LocalizeValue.localizeTODO("HTML::Mason <%args>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.htmlmason.idea.livetemplates.HTMLMasonTemplateContextTypeRegistration.class, true);
    }
  }
}
