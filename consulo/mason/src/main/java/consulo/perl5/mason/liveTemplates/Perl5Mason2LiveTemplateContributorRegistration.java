// <defaultLiveTemplates file="liveTemplates/Perl5Mason2.xml"> in upstream perl5.lang.mason.mason2.backend.main.xml
package consulo.perl5.mason.liveTemplates;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.template.LiveTemplateContributor;
import consulo.localize.LocalizeValue;

@ExtensionImpl
public class Perl5Mason2LiveTemplateContributorRegistration implements LiveTemplateContributor {
  @Override
  public String groupId() {
    return "Perl5 Mason2";
  }

  @Override
  public LocalizeValue groupName() {
    return LocalizeValue.localizeTODO("Perl5 Mason2");
  }

  @Override
  public void contribute(Factory factory) {
    try (Builder builder = factory.newBuilder("Perl5 Mason2.%method", "%method", "<%method $NAME$ ($SIGNATURE$)>\n$END$\n</%method>", LocalizeValue.localizeTODO("Mason <%method>"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "", true);
      builder.withVariable("SIGNATURE", "", "", true);
      builder.withContext(com.perl5.lang.mason2.idea.livetemplates.MasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 Mason2.%override", "%override", "<%override $NAME$>\n$END$\n</%override>", LocalizeValue.localizeTODO("Mason <%override>"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "", true);
      builder.withContext(com.perl5.lang.mason2.idea.livetemplates.MasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 Mason2.%filter", "%filter", "<%filter $NAME$ ($SIGNATURE$)>\n$END$\n</%filter>", LocalizeValue.localizeTODO("Mason <%filter>"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "", true);
      builder.withVariable("SIGNATURE", "", "", true);
      builder.withContext(com.perl5.lang.mason2.idea.livetemplates.MasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 Mason2.%class", "%class", "<%class>\n$END$\n</%class>", LocalizeValue.localizeTODO("Mason <%class>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.mason2.idea.livetemplates.MasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 Mason2.%doc", "%doc", "<%doc>\n$END$\n</%doc>", LocalizeValue.localizeTODO("Mason <%doc>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.mason2.idea.livetemplates.MasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 Mason2.%text", "%text", "<%text>\n$END$\n</%text>", LocalizeValue.localizeTODO("Mason <%text>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.mason2.idea.livetemplates.MasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 Mason2.%flags", "%flags", "<%flags>\n    extends => '$COMPONENT$'\n</%flags>\n$END$", LocalizeValue.localizeTODO("Mason <%flags>"))) {
      builder.withReformat();
      builder.withVariable("COMPONENT", "", "", true);
      builder.withContext(com.perl5.lang.mason2.idea.livetemplates.MasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 Mason2.%init", "%init", "<%init>\n$END$\n</%init>", LocalizeValue.localizeTODO("Mason <%init>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.mason2.idea.livetemplates.MasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 Mason2.%perl", "%perl", "<%perl>\n$END$\n</%perl>", LocalizeValue.localizeTODO("Mason <%perl>"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.mason2.idea.livetemplates.MasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 Mason2.%augment", "%augment", "<%augment $NAME$>\n$END$\n</%augment>", LocalizeValue.localizeTODO("Mason <%augment>"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "", true);
      builder.withContext(com.perl5.lang.mason2.idea.livetemplates.MasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 Mason2.%after", "%after", "<%after $NAME$>\n$END$\n</%after>", LocalizeValue.localizeTODO("Mason <%after>"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "", true);
      builder.withContext(com.perl5.lang.mason2.idea.livetemplates.MasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 Mason2.%around", "%around", "<%around $NAME$>\n$END$\n</%around>", LocalizeValue.localizeTODO("Mason <%around>"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "", true);
      builder.withContext(com.perl5.lang.mason2.idea.livetemplates.MasonTemplateContextTypeRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5 Mason2.%before", "%before", "<%before $NAME$>\n$END$\n</%before>", LocalizeValue.localizeTODO("Mason <%before>"))) {
      builder.withReformat();
      builder.withVariable("NAME", "", "", true);
      builder.withContext(com.perl5.lang.mason2.idea.livetemplates.MasonTemplateContextTypeRegistration.class, true);
    }
  }
}
