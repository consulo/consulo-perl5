// <fileType> entries of the upstream descriptors
package consulo.perl5.embedded.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;
import consulo.virtualFileSystem.fileType.FileTypeFactory;
import consulo.virtualFileSystem.fileType.FileNameMatcherFactory;

@ExtensionImpl
public class FileTypeFactoryRegistration extends FileTypeFactory {
  @Override
  public void createFileTypes(FileTypeConsumer consumer) {
    consumer.consume(com.perl5.lang.embedded.filetypes.EmbeddedPerlFileType.INSTANCE, "thtml;phtml");
  }
}
