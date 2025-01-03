/*
 * Copyright 2015-2023 Alexandr Evstigneev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.perl5.lang.perl.idea.run.prove;

import com.perl5.lang.perl.fileTypes.PerlFileTypeTest;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfigurationProducer;
import consulo.virtualFileSystem.VirtualFile;
import org.jetbrains.annotations.NotNull;

public abstract class PerlAbstractTestRunConfigurationProducer<Configuration extends PerlAbstractTestRunConfiguration> extends GenericPerlRunConfigurationProducer<Configuration> {

  @Override
  public boolean isOurFile(@NotNull VirtualFile virtualFiles) {
    return virtualFiles.isDirectory() || virtualFiles.getFileType() == PerlFileTypeTest.INSTANCE;
  }

  @Override
  public boolean allowMultipleFiles() {
    return true;
  }
}
