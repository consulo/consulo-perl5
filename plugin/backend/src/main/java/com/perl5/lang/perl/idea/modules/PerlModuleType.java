/*
 * Copyright 2015-2020 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.modules;

import com.perl5.PerlBundle;
import com.perl5.PerlIcons;
import consulo.ui.image.Image;
import org.jetbrains.annotations.NotNull;

/**
 * TODO consulo: Consulo has no module types, upstream {@code ModuleType<ModuleBuilder>} registered as {@code moduleType}.
 *  A perl module in Consulo is a module with enabled {@code PerlModuleExtension}, see {@code PerlModuleExtensionProvider} which uses
 *  this class for presentation and {@code PerlNewModuleBuilder} for the "New Project/Module" wizard.
 *  Upstream {@code createModuleBuilder()} returned an unavailable {@code EmptyModuleBuilder}, it has no Consulo counterpart.
 */
public class PerlModuleType {
  public static final String PERL_MODULE_TYPE_ID = "PERL5_MODULE";
  private static final PerlModuleType INSTANCE = new PerlModuleType();

  public PerlModuleType() {
  }

  public @NotNull String getId() {
    return PERL_MODULE_TYPE_ID;
  }

  public @NotNull String getName() {
    return PerlBundle.message("perl.module.name");
  }

  public @NotNull String getDescription() {
    return PerlBundle.message("perl.module.description");
  }

  public @NotNull Image getNodeIcon(@Deprecated boolean isOpened) {
    return PerlIcons.PERL_LANGUAGE_ICON;
  }

  public static PerlModuleType getInstance() {
    return INSTANCE;
  }
}
