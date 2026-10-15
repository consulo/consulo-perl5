// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
// Consulo port of com.intellij.xml.template.formatter (IntelliJ xml-ui-common): missing in Consulo, used by Perl5 template languages.
package consulo.perl5.xml.template.formatter;

import consulo.language.codeStyle.Indent;

public interface IndentInheritingBlock {
  void setIndent(Indent indent);
}
