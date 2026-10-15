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

package com.perl5.lang.perl.extensions;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.application.Application;
import consulo.codeEditor.Editor;
import consulo.component.extension.ByClassGrouper;
import consulo.component.extension.ExtensionPointCacheKey;
import consulo.language.psi.PsiElement;
import com.perl5.lang.perl.psi.PerlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface PerlCodeGenerator {
  // TODO consulo: IntelliJ ClassExtension (forClass declared in plugin.xml) is missing in Consulo; generators are bound to classes
  //  by @ExtensionImpl of PerlCodeGenerator.ClassExtensionPoint instead
  ClassExtension EP = new ClassExtension();

  static PerlCodeGenerator getInstance(PerlFile perlFile) {
    return EP.forClass(perlFile.getClass());
  }

  /**
   * consulo: replacement of IntelliJ {@code ClassExtensionPoint} for the {@code com.perl5.codeGenerator} extension point,
   * binds a class ({@code forClass}) to the generator
   */
  @ExtensionAPI(ComponentScope.APPLICATION)
  interface ClassExtensionPoint {
    @NotNull
    Class<?> getForClass();

    @NotNull
    PerlCodeGenerator getInstance();
  }

  /**
   * consulo: replacement of IntelliJ {@code ClassExtension} over {@link ClassExtensionPoint} extensions
   */
  final class ClassExtension {
    private static final ExtensionPointCacheKey<ClassExtensionPoint, Function<Class, ClassExtensionPoint>> CACHE_KEY =
      ExtensionPointCacheKey.create("PerlCodeGenerator.EP", ByClassGrouper.build(ClassExtensionPoint::getForClass));

    public @Nullable PerlCodeGenerator forClass(@NotNull Class<?> cls) {
      ClassExtensionPoint extension =
        Application.get().getExtensionPoint(ClassExtensionPoint.class).getOrBuildCache(CACHE_KEY).apply(cls);
      return extension == null ? null : extension.getInstance();
    }
  }

  /**
   * Returns code for overriding provided sub definition
   *
   * @param subBase sub to override
   * @return generated code
   */
  @SuppressWarnings("unused")
  @Nullable
  String getOverrideCodeText(PsiElement subBase);

  /**
   * Generating and inserting overridden method
   *
   * @param anchor element to insert method after
   */
  void generateOverrideMethod(PsiElement anchor, Editor editor);

  void generateSetters(PsiElement anchor, Editor editor);

  void generateGetters(PsiElement anchor, Editor editor);

  void generateGettersAndSetters(PsiElement anchor, Editor editor);

  void generateConstructor(PsiElement anchor, Editor editor);
}
