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

package com.perl5.lang.mojolicious;

import consulo.application.util.NotNullLazyValue;
import com.perl5.PerlIcons;
import consulo.perl5.mojo.icon.MojoIconGroup;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.image.Image;
import org.jetbrains.annotations.NotNull;

// consulo: icons are generated from consulo/mojo/src/main/resources/ICON-LIB (MojoIconGroup) instead of IconLoader
public final class MojoIcons {
  public static final Image MOJO_FILE = MojoIconGroup.mojolicious();
  public static final Image MOJO_LOGO = MojoIconGroup.mojolicious_color();

  public static final Image MOJO_APP_ICON = MOJO_LOGO;
  public static final Image MOJO_LITE_APP_ICON = MOJO_LOGO;
  private static final NotNullLazyValue<Image> MOJO_ROOT = PerlIcons.createLazyIconWithModifier(PerlIcons.TEMPLATE_ROOT, MOJO_LOGO);
  private static final NotNullLazyValue<Image> MOJO_PLUGIN_LOGO = PerlIcons.createLazyIconWithModifier(PlatformIconGroup.nodesPlugin(), MOJO_LOGO);

  public static @NotNull Image pluginIcon() {
    return MOJO_PLUGIN_LOGO.getValue();
  }

  public static @NotNull Image rootIcon() {
    return MOJO_ROOT.getValue();
  }

  private MojoIcons() {
  }
}
