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

import consulo.application.util.NotNullLazyValue;
import consulo.ui.image.Image;
import org.jetbrains.annotations.NotNull;

public final class PerlImages {
  public static final Image PERL_LANGUAGE_Image = load("/Images/perl5language.png");
  public static final Image PERLBREW_Image = load("/Images/perlbrew.png");
  public static final Image STRAWBERRY_Image = load("/Images/strawberry.png");
  public static final Image WINDOWS10_Image = load("/Images/windows.svg");
  public static final Image LINUX_Image = load("/Images/linux.svg");
  public static final Image PLENV_Image = PERL_LANGUAGE_Image;
  public static final Image ASDF_Image = PERL_LANGUAGE_Image;
  public static final Image MAC_Image = load("/Images/mac.svg");
  public static final Image WINDOWS_Image = WINDOWS10_Image;
  public static final Image DOCKER_Image = load("/Images/docker.svg");
  public static final Image FREEBSD_Image = LINUX_Image;
  public static final Image SOLARIS_Image = LINUX_Image;
  public static final Image UNIX_Image = LINUX_Image;
  public static final Image PERL_OPTION = load("/Images/option.png");
  public static final Image PERL_OPTIONS = load("/Images/options.png");
  public static final Image TEMPLATE_ROOT = load("/Images/templateRoot.png");
  public static final Image PM_FILE = load("/Images/perl5.png");
  public static final Image PERL_SCRIPT_FILE_Image = load("/Images/perl5classic.png");
  public static final Image CAMEL_MODIFIER = load("/Images/camel_modifier.png");
  public static final Image POD_FILE = PERL_LANGUAGE_Image;
  public static final Image TEST_FILE = load("/Images/file-test.png");
  public static final Image XS_FILE = load("/Images/xsImage.png");
  public static final Image LIB_ROOT = load("/Images/library_root.png");
  public static final Image METACPAN = load("/Images/metacpan.png");
  public static final Image PACKAGE_GUTTER_Image = load("/Images/package_gutter_Image.png");
  public static final Image PRAGMA_GUTTER_Image = load("/Images/pragmaImage.png");
  public static final Image ATTRIBUTE_GUTTER_Image = load("/Images/attributes_gutter_Image.png");
  public static final Image ATTRIBUTE_GROUP_Image = load("/Images/attributes_group_Image.png");
  public static final Image XSUB_GUTTER_Image = load("/Images/xsub_gutter_Image.png");
  public static final Image SUB_DECLARATION_GUTTER_Image = load("/Images/sub_declaration_gutter_Image.png");
  public static final Image METHOD_GUTTER_Image = load("/Images/method_gutter_Image.png");
  public static final Image SUB_GUTTER_Image = load("/Images/subroutine_gutter_Image.png");
  public static final Image CONSTANT_GUTTER_Image = load("/Images/constant_gutter_Image.png");
  public static final Image BEFORE_MODIFIER_GUTTER_Image = METHOD_GUTTER_Image;
  public static final Image AFTER_MODIFIER_GUTTER_Image = METHOD_GUTTER_Image;
  public static final Image AROUND_MODIFIER_GUTTER_Image = METHOD_GUTTER_Image;
  public static final Image AUGMENT_MODIFIER_GUTTER_Image = METHOD_GUTTER_Image;
  public static final Image ANNOTATION_GUTTER_Image = load("/Images/annotation_gutter_Image.png");
  public static final Image REGEX_GUTTER_Image = load("/Images/re_gutter_Image.png");
  public static final Image ANON_SUB_Image = SUB_GUTTER_Image;
  public static final Image FORMAT_GUTTER_Image = load("/Images/format_gutter_Image.png");
  public static final Image HANDLE_GUTTER_Image = load("/Images/handle_gutter_Image.png");
  public static final Image ARGS_GUTTER_Image = load("/Images/args_gutter_Image.png");
  public static final Image MAIN_GUTTER_Image = load("/Images/main_gutter_Image.png");
  public static final Image MY_GUTTER_Image = load("/Images/my_gutter_Image.png");
  public static final Image OUR_GUTTER_Image = load("/Images/our_gutter_Image.png");
  public static final Image SCALAR_GUTTER_Image = load("/Images/scalar_gutter_Image.png");
  public static final Image UTF_SCALAR_GUTTER_Image = load("/Images/scalar_utf_gutter_Image.png");
  public static final Image ARRAY_GUTTER_Image = load("/Images/array_gutter_Image.png");
  public static final Image HASH_GUTTER_Image = load("/Images/hash_gutter_Image.png");
  public static final Image GLOB_GUTTER_Image = load("/Images/glob_gutter_Image.png");

  private PerlImages() {
  }

  private static @NotNull Image load(@NotNull String resourcePath) {
    // TODO
      throw new UnsupportedOperationException();
  }

  public static final Image PERL_TEST_CONFIGURATION = TEST_FILE;

  /**
   * @see #createImageWithModifier(Image, Image)
   */
  public static @NotNull NotNullLazyValue<Image> createLazyImageWithModifier(@NotNull Image baseImage, @NotNull Image modifierImage) {
    return NotNullLazyValue.createValue(() -> createImageWithModifier(baseImage, modifierImage));
  }

  public static @NotNull NotNullLazyValue<Image> createLazyImageWithModifier(@NotNull Image baseImage,
                                                                           @NotNull Image modifierImage,
                                                                           float modifierScale) {
    return NotNullLazyValue.createValue(() -> createImageWithModifier(baseImage, modifierImage, modifierScale));
  }

  public static @NotNull Image createImageWithModifier(@NotNull Image baseImage, @NotNull Image modifierImage) {
    return createImageWithModifier(baseImage, modifierImage, 0.5f);
  }

  /**
   * Creates an Image by combining {@code baseImage} and scaled {@code modifierImage}
   *
   * @apiNote modifier Image is scaled in {@code modifierScale} and put into right bottom corner of the base Image
   */
  private static @NotNull Image createImageWithModifier(@NotNull Image baseImage,
                                                      @NotNull Image modifierImage,
                                                      float modifierScale) {
    LayeredImage result = new LayeredImage(2);
    result.setImage(baseImage, 0);
    Image modifier = ImageUtil.scale(modifierImage, null, modifierScale);
    result.setImage(modifier, 1, baseImage.getImageHeight() - modifier.getImageHeight(), baseImage.getImageWidth() - modifier.getImageWidth());
    return result;
  }
}
