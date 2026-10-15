// <internalFileTemplate> entries of the upstream descriptors
package consulo.perl5.core.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.fileTemplate.FileTemplateContributor;
import consulo.fileTemplate.FileTemplateRegistrator;

@ExtensionImpl
public class FileTemplateContributorRegistration implements FileTemplateContributor {
  @Override
  public void register(FileTemplateRegistrator registrator) {
    registrator.registerInternalTemplate("Perl5 package");
    registrator.registerInternalTemplate("Perl5 script");
    registrator.registerInternalTemplate("Perl5 test");
    registrator.registerInternalTemplate("Perl5 pod");
  }
}
