/*
 * Copyright 2015-2017 Alexandr Evstigneev
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

package com.perl5.lang.perl.fileTypes;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.perl5.lang.perl.fileTypes.PerlFileTypeService.RootDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface PerlFileTypeProvider {
  ExtensionPointName<PerlFileTypeProvider> EP_NAME = ExtensionPointName.create("com.perl5.fileTypeProvider");

  /**
   * Should pass descriptors created by {@link RootDescriptor#create} for each root with custom types
   */
  void addDescriptors(@NotNull Project project, Consumer<RootDescriptor> descriptorConsumer);
}
