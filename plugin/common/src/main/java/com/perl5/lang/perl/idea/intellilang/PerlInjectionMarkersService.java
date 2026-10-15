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

package com.perl5.lang.perl.idea.intellilang;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import jakarta.inject.Singleton;
import consulo.language.Language;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.project.Project;
import consulo.util.lang.lazy.ClearableLazyValue;
import consulo.util.xml.serializer.annotation.Tag;
import consulo.util.xml.serializer.annotation.Transient;
import com.perl5.lang.perl.idea.PerlPathMacros;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.perl5.lang.perl.idea.intellilang.PerlDefaultInjectionMarkers.DEFAULT_MARKERS;


@State(
  name = "InjectionMarkers",
  storages = @Storage(PerlPathMacros.PERL5_PROJECT_SETTINGS_FILE)
)
@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
@Singleton
public class PerlInjectionMarkersService implements PersistentStateComponent<PerlInjectionMarkersService> {
  @Tag("MARKERS_MAP")
  private Map<String, String> myCustomMarkersMap = Collections.emptyMap();

  private final transient ClearableLazyValue<Map<String, Language>> myLanguageMapProvider =
    ClearableLazyValue.notNull(this::computeMarkersMap);

  @Override
  public @Nullable PerlInjectionMarkersService getState() {
    return this;
  }

  @Transient
  public @NotNull Set<String> getSupportedMarkers() {
    return new HashSet<>(myLanguageMapProvider.get().keySet());
  }

  @Transient
  public @NotNull Map<String, String> computeMergedMarkersMap() {
    HashMap<String, String> mergedMap = new HashMap<>(myCustomMarkersMap);
    DEFAULT_MARKERS.forEach(mergedMap::putIfAbsent);
    return mergedMap;
  }

  public void setCustomMarkersMap(Map<String, String> customMarkersMap) {
    Map<String, String> result = new LinkedHashMap<>();
    customMarkersMap.forEach((marker, languageId) -> {
      String defaultLanguageId = DEFAULT_MARKERS.get(marker);
      if (defaultLanguageId == null || !defaultLanguageId.equals(languageId)) {
        result.putIfAbsent(marker, languageId);
      }
    });
    myCustomMarkersMap = result;
    myLanguageMapProvider.clear();
  }

  /**
   * @return language which should be injected for this marker or null if not available
   */
  @Contract(value = "null -> null", pure = true)
  public @Nullable Language getLanguageByMarker(@Nullable String marker) {
    return myLanguageMapProvider.get().get(marker);
  }


  @Override
  public void loadState(@NotNull PerlInjectionMarkersService state) {
    // we are not copying here, because default markers could be changed, e.g. some marker was added and duplicates custom one
    setCustomMarkersMap(state.myCustomMarkersMap);
  }

  private @NotNull Map<String, Language> computeMarkersMap() {
    HashMap<String, Language> result = new HashMap<>();
    computeMergedMarkersMap().forEach((marker, languageid) -> result.put(marker, Language.findLanguageByID(languageid)));
    return result;
  }

  // TODO consulo: PersistentStateComponent has no noStateLoaded() callback, so this is never called
  public void noStateLoaded() {
    loadState(new PerlInjectionMarkersService());
  }

  public static @NotNull PerlInjectionMarkersService getInstance(@NotNull Project project) {
    return project.getService(PerlInjectionMarkersService.class);
  }
}
