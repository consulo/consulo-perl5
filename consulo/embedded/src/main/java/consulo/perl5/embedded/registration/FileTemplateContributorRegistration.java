// <internalFileTemplate> entries of the upstream descriptors
package consulo.perl5.embedded.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.fileTemplate.FileTemplateContributor;
import consulo.fileTemplate.FileTemplateRegistrator;

@ExtensionImpl
public class FileTemplateContributorRegistration implements FileTemplateContributor {
  @Override
  public void register(FileTemplateRegistrator registrator) {
    registrator.registerInternalTemplate("Perl5 embedded");
  }
}
