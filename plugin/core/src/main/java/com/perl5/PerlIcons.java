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

package com.perl5;

import consulo.perl5.icon.Perl5IconGroup;
import consulo.ui.image.Image;

public final class PerlIcons {
  public static final Image PERL_LANGUAGE_ICON = Perl5IconGroup.perl5language();
  public static final Image PERLBREW_ICON = Perl5IconGroup.perlbrew();
  public static final Image STRAWBERRY_ICON = Perl5IconGroup.strawberry();
  public static final Image WINDOWS10_ICON = Perl5IconGroup.windows();
  public static final Image LINUX_ICON = Perl5IconGroup.linux();
  public static final Image PLENV_ICON = PERL_LANGUAGE_ICON;
  public static final Image ASDF_ICON = PERL_LANGUAGE_ICON;
  public static final Image MAC_ICON = Perl5IconGroup.mac();
  public static final Image WINDOWS_ICON = WINDOWS10_ICON;
  public static final Image DOCKER_ICON = Perl5IconGroup.docker();
  public static final Image FREEBSD_ICON = LINUX_ICON;
  public static final Image SOLARIS_ICON = LINUX_ICON;
  public static final Image UNIX_ICON = LINUX_ICON;
  public static final Image PERL_OPTION = Perl5IconGroup.option();
  public static final Image PERL_OPTIONS = Perl5IconGroup.options();
  public static final Image TEMPLATE_ROOT = Perl5IconGroup.templateroot();
  public static final Image PM_FILE = Perl5IconGroup.perl5();
  public static final Image PERL_SCRIPT_FILE_ICON = Perl5IconGroup.perl5classic();
  public static final Image CAMEL_MODIFIER = Perl5IconGroup.camel_modifier();
  public static final Image POD_FILE = PERL_LANGUAGE_ICON;
  public static final Image TEST_FILE = Perl5IconGroup.file_test();
  public static final Image XS_FILE = Perl5IconGroup.xsicon();
  public static final Image LIB_ROOT = Perl5IconGroup.library_root();
  public static final Image METACPAN = Perl5IconGroup.metacpan();
  public static final Image PACKAGE_GUTTER_ICON = Perl5IconGroup.package_gutter_icon();
  public static final Image PRAGMA_GUTTER_ICON = Perl5IconGroup.pragmaicon();
  public static final Image ATTRIBUTE_GUTTER_ICON = Perl5IconGroup.annotation_gutter_icon();
  public static final Image ATTRIBUTE_GROUP_ICON = Perl5IconGroup.attributes_group_icon();
  public static final Image XSUB_GUTTER_ICON = Perl5IconGroup.xsub_gutter_icon();
  public static final Image SUB_DECLARATION_GUTTER_ICON = Perl5IconGroup.sub_declaration_gutter_icon();
  public static final Image METHOD_GUTTER_ICON = Perl5IconGroup.method_gutter_icon();
  public static final Image SUB_GUTTER_ICON = Perl5IconGroup.subroutine_gutter_icon();
  public static final Image CONSTANT_GUTTER_ICON = Perl5IconGroup.constant_gutter_icon();
  public static final Image BEFORE_MODIFIER_GUTTER_ICON = METHOD_GUTTER_ICON;
  public static final Image AFTER_MODIFIER_GUTTER_ICON = METHOD_GUTTER_ICON;
  public static final Image AROUND_MODIFIER_GUTTER_ICON = METHOD_GUTTER_ICON;
  public static final Image AUGMENT_MODIFIER_GUTTER_ICON = METHOD_GUTTER_ICON;
  public static final Image ANNOTATION_GUTTER_ICON = Perl5IconGroup.annotation_gutter_icon();
  public static final Image REGEX_GUTTER_ICON = Perl5IconGroup.re_gutter_icon();
  public static final Image ANON_SUB_ICON = SUB_GUTTER_ICON;
  public static final Image FORMAT_GUTTER_ICON = Perl5IconGroup.format_gutter_icon();
  public static final Image HANDLE_GUTTER_ICON = Perl5IconGroup.handle_gutter_icon();
  public static final Image ARGS_GUTTER_ICON = Perl5IconGroup.args_gutter_icon();
  public static final Image MAIN_GUTTER_ICON = Perl5IconGroup.main_gutter_icon();
  public static final Image MY_GUTTER_ICON = Perl5IconGroup.my_gutter_icon();
  public static final Image OUR_GUTTER_ICON = Perl5IconGroup.our_gutter_icon();
  public static final Image SCALAR_GUTTER_ICON = Perl5IconGroup.scalar_gutter_icon();
  public static final Image UTF_SCALAR_GUTTER_ICON = Perl5IconGroup.scalar_utf_gutter_icon();
  public static final Image ARRAY_GUTTER_ICON = Perl5IconGroup.array_gutter_icon();
  public static final Image HASH_GUTTER_ICON = Perl5IconGroup.hash_gutter_icon();
  public static final Image GLOB_GUTTER_ICON = Perl5IconGroup.glob_gutter_icon();

  private PerlIcons() {
  }

  public static final Image PERL_TEST_CONFIGURATION = TEST_FILE;
}