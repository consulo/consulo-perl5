/*
 * Copyright 2015-2025 Alexandr Evstigneev
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

package com.perl5.lang.htmlmason.idea.configuration;

import com.perl5.lang.mason2.idea.configuration.VariableDescription;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Settings of a Mason framework (HTML::Mason, Mason2) of a module.
 * <p>
 * TODO consulo: upstream settings are project-level persistent services, edited on tabs of the Perl5 settings page, with a change counter
 *  to reset caches. In Consulo the settings are per module: the state of the framework module extension (a child of the Perl module
 *  extension), edited in the module settings. Instances are immutable, caches may be keyed by the settings instance.
 */
public abstract class AbstractMasonSettings {
  private final @NotNull List<VariableDescription> myGlobalVariables;

  protected AbstractMasonSettings(@NotNull List<VariableDescription> globalVariables) {
    myGlobalVariables = globalVariables.stream().map(VariableDescription::copy).toList();
  }

  /**
   * @return global variables of components, must not be modified
   */
  public @NotNull List<VariableDescription> getGlobalVariables() {
    return myGlobalVariables;
  }

  @Override
  public boolean equals(Object o) {
    return this == o || o != null && getClass() == o.getClass() && myGlobalVariables.equals(((AbstractMasonSettings)o).myGlobalVariables);
  }

  @Override
  public int hashCode() {
    return myGlobalVariables.hashCode();
  }
}
