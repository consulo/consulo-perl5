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

package com.perl5.lang.tt2;

import com.perl5.PerlIcons;
import consulo.perl5.tt2.icon.TemplateToolkitIconGroup;
import consulo.ui.image.Image;
import consulo.ui.image.ImageEffects;

// consulo: icons are generated from consulo/tt2/src/main/resources/ICON-LIB (TemplateToolkitIconGroup) instead of IconLoader
public final class TemplateToolkitIcons {
  public static final Image TTK2_ICON = TemplateToolkitIconGroup.template_toolkit_gutter_icon();
  public static final Image TTK2_MODIFIER = TemplateToolkitIconGroup.tt_modifier();
  public static final Image TTK2_BLOCK_ICON = TemplateToolkitIconGroup.template_toolkit_block_gutter_icon();
  public static final Image TTK2_ROOT = ImageEffects.layered(PerlIcons.TEMPLATE_ROOT, TTK2_MODIFIER);

  private TemplateToolkitIcons() {
  }
}
