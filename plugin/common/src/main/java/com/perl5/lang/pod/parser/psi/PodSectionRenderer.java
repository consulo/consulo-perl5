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

package com.perl5.lang.pod.parser.psi;

import com.perl5.lang.pod.parser.psi.util.PodRenderUtil;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.application.Application;
import consulo.component.extension.ByClassGrouper;
import consulo.component.extension.ExtensionPointCacheKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;


public interface PodSectionRenderer<Section extends PodSection> {
  /**
   * Appends HTML representation of the section to the `builder`
   */
  default void renderElementAsHTML(@NotNull Section podSection, @NotNull StringBuilder builder, @NotNull PodRenderingContext context) {
    PodRenderUtil.renderPsiRangeAsHTML(podSection.getFirstChild(), null, builder, context);
  }

  @SuppressWarnings("unchecked")
  static <Section extends PodSection> @NotNull PodSectionRenderer<Section> instance(@NotNull Class<Section> clazz) {
    return (PodSectionRenderer<Section>)PodSectionRendererKt.EP.findSingle(clazz);
  }

  @SuppressWarnings("unchecked")
  static <Section extends PodSection> @NotNull PodSectionRenderer<Section> get(@NotNull Section section) {
    return instance((Class<Section>)section.getClass());
  }

  /**
   * consulo: replacement of IntelliJ {@code ClassExtensionPoint} for the {@code com.perl5.lang.pod.sectionRenderer} extension point,
   * binds a class ({@code forClass}) to the renderer
   */
  @ExtensionAPI(ComponentScope.APPLICATION)
  interface ClassExtensionPoint {
    @NotNull
    Class<?> getForClass();

    @NotNull
    PodSectionRenderer<?> getInstance();
  }
}

final class PodSectionRendererKt {
  // TODO consulo: IntelliJ ClassExtension is missing in Consulo; renderers are bound to classes by @ExtensionImpl of
  //  PodSectionRenderer.ClassExtensionPoint instead
  static final ClassExtension EP = new ClassExtension();

  private PodSectionRendererKt() {
  }

  /**
   * consulo: replacement of IntelliJ {@code ClassExtension} over {@link PodSectionRenderer.ClassExtensionPoint} extensions
   */
  static final class ClassExtension {
    private static final ExtensionPointCacheKey<PodSectionRenderer.ClassExtensionPoint, Function<Class, PodSectionRenderer.ClassExtensionPoint>>
      CACHE_KEY = ExtensionPointCacheKey.create("PodSectionRendererKt.EP",
                                                ByClassGrouper.build(PodSectionRenderer.ClassExtensionPoint::getForClass));

    @Nullable
    PodSectionRenderer<?> findSingle(@NotNull Class<?> cls) {
      PodSectionRenderer.ClassExtensionPoint extension =
        Application.get().getExtensionPoint(PodSectionRenderer.ClassExtensionPoint.class).getOrBuildCache(CACHE_KEY).apply(cls);
      return extension == null ? null : extension.getInstance();
    }
  }
}
