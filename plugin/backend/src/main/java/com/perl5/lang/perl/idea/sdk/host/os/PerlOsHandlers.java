/*
 * Copyright 2015-2026 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.sdk.host.os;

import com.perl5.PerlIcons;
import consulo.ui.ex.awtUnsafe.TargetAWT;

// TODO consulo: PerlIcons are consulo.ui.image.Image, os handlers keep Swing icons
public interface PerlOsHandlers {
  PerlOsHandler WINDOWS10 = new PerlWindows10Handler("Windows 10", TargetAWT.to(PerlIcons.WINDOWS10_ICON));
  PerlOsHandler WINDOWS = new PerlWindowsHandler("Windows", TargetAWT.to(PerlIcons.WINDOWS_ICON));
  PerlOsHandler MACOS = new PerlMacOsHandler("MacOs", TargetAWT.to(PerlIcons.MAC_ICON));
  PerlOsHandler FREEBSD = new PerlBsdHandler("FreeBsd", TargetAWT.to(PerlIcons.FREEBSD_ICON));
  PerlOsHandler LINUX = new PerlLinuxHandler("Linux", TargetAWT.to(PerlIcons.LINUX_ICON));
  PerlOsHandler UNIX = new PerlUnixHandler("UNIX", TargetAWT.to(PerlIcons.UNIX_ICON));
}
