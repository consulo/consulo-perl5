// <fileType> entries of the upstream descriptors
package consulo.perl5.core.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;
import consulo.virtualFileSystem.fileType.FileTypeFactory;
import consulo.virtualFileSystem.fileType.FileNameMatcherFactory;

@ExtensionImpl
public class FileTypeFactoryRegistration extends FileTypeFactory {
  @Override
  public void createFileTypes(FileTypeConsumer consumer) {
    consumer.consume(com.perl5.lang.perl.fileTypes.PerlFileTypeScript.INSTANCE, "pl;ph;al;cgi");
    consumer.consume(com.perl5.lang.perl.fileTypes.PerlFileTypePackage.INSTANCE, "pm");
    consumer.consume(com.perl5.lang.perl.fileTypes.PerlFileTypeTest.INSTANCE, "t");
    consumer.consume(com.perl5.lang.xs.filetypes.XSFileType.INSTANCE, "xs");
    consumer.consume(com.perl5.lang.pod.filetypes.PodFileType.INSTANCE, "pod");
    consumer.consume(com.perl5.lang.perl.cpanminus.cpanfile.PerlFileTypeCpanfile.INSTANCE, FileNameMatcherFactory.getInstance().createExactFileNameMatcher("cpanfile"));
  }
}
