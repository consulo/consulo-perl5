// <defaultLiveTemplates file="liveTemplates/Perl5POD.xml"> in upstream perl5.plugin.backend.main.xml
package consulo.perl5.core.liveTemplates;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.template.LiveTemplateContributor;
import consulo.localize.LocalizeValue;

@ExtensionImpl
public class Perl5PODLiveTemplateContributorRegistration implements LiveTemplateContributor {
  @Override
  public String groupId() {
    return "Perl5: POD";
  }

  @Override
  public LocalizeValue groupName() {
    return LocalizeValue.localizeTODO("Perl5: POD");
  }

  @Override
  public void contribute(Factory factory) {
    try (Builder builder = factory.newBuilder("Perl5: POD.=head1", "=head1", "=head1 $TEXT$\n\n$END$\n", LocalizeValue.localizeTODO("=head1"))) {
      builder.withReformat();
      builder.withVariable("TEXT", "complete()", "\"heading\"", true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=head2", "=head2", "=head2 $TEXT$\n\n$END$\n", LocalizeValue.localizeTODO("=head2"))) {
      builder.withReformat();
      builder.withVariable("TEXT", "complete()", "\"heading\"", true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=attr", "=attr", "=attr $TEXT$\n\n$END$\n", LocalizeValue.localizeTODO("=head2"))) {
      builder.withReformat();
      builder.withVariable("TEXT", "complete()", "\"name\"", true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=func", "=func", "=func $TEXT$\n\n$END$\n", LocalizeValue.localizeTODO("=head2"))) {
      builder.withReformat();
      builder.withVariable("TEXT", "complete()", "\"name\"", true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=method", "=method", "=method $TEXT$\n\n$END$\n", LocalizeValue.localizeTODO("=head2"))) {
      builder.withReformat();
      builder.withVariable("TEXT", "complete()", "\"name\"", true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=head3", "=head3", "=head3 $TEXT$\n\n$END$\n", LocalizeValue.localizeTODO("=head3"))) {
      builder.withReformat();
      builder.withVariable("TEXT", "complete()", "\"heading\"", true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=head4", "=head4", "=head4 $TEXT$\n\n$END$\n", LocalizeValue.localizeTODO("=head4"))) {
      builder.withReformat();
      builder.withVariable("TEXT", "complete()", "\"heading\"", true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=over", "=over", "=over $LEVEL$\n\n=item $TITLE$\n\n$SELECTION$$END$\n\n=back\n", LocalizeValue.localizeTODO("=over ... =back"))) {
      builder.withReformat();
      builder.withVariable("LEVEL", "\"1\"", "", true);
      builder.withVariable("TITLE", "complete()", "\"title\"", true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=begin", "=begin", "=begin $FORMAT$ \n\n$SELECTION$$END$\n\n=end $FORMAT$\n", LocalizeValue.localizeTODO("=begin ... =end"))) {
      builder.withReformat();
      builder.withVariable("FORMAT", "complete()", "\"html\"", true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=for", "=for", "=for $FORMAT$ $END$ \n", LocalizeValue.localizeTODO("=for"))) {
      builder.withReformat();
      builder.withVariable("FORMAT", "complete()", "\"html\"", true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=encoding", "=encoding", "=encoding $ENCODING$ \n\n$END$", LocalizeValue.localizeTODO("=encoding"))) {
      builder.withReformat();
      builder.withVariable("ENCODING", "complete()", "\"utf8\"", true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=pod", "=pod", "=pod\n\n$END$\n", LocalizeValue.localizeTODO("=pod"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=back", "=back", "=back\n\n$END$\n", LocalizeValue.localizeTODO("=back"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeInsideOverBulletedRegistration.class, true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeInsideOverNotBulletedRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=end", "=end", "=end $FORMAT$\n\n$END$\n", LocalizeValue.localizeTODO("=end"))) {
      builder.withReformat();
      builder.withVariable("FORMAT", "complete()", "\"html\"", true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=cut", "=cut", "=cut\n\n$END$\n", LocalizeValue.localizeTODO("=cut"))) {
      builder.withReformat();
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Perl5: POD.=item", "=item", "=item $TEXT$ \n\n$END$\n", LocalizeValue.localizeTODO("=item"))) {
      builder.withReformat();
      builder.withVariable("TEXT", "complete()", "\"title\"", true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeCommandPositionRegistration.class, false);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeInsideOverBulletedRegistration.class, true);
      builder.withContext(com.perl5.lang.pod.idea.livetemplates.PodTemplateContextTypeInsideOverNotBulletedRegistration.class, true);
    }
  }
}
