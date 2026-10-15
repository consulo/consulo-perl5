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

package com.intellij.lang.impl;

import consulo.language.ast.ASTNode;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiBuilder;
import consulo.language.lexer.Lexer;
import consulo.project.Project;
import consulo.util.lang.Pair;
import com.perl5.lang.perl.parser.builder.PerlPsiBuilderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class PerlDebugBuilder extends PsiBuilderImpl {
  private final @NotNull PerlPsiBuilderFactory.PerlBuilderDiagnostics diagnostics;

  private final HashMap<PsiBuilder.Marker, PerlStartMarker> markersMap = new HashMap<>();

  public PerlDebugBuilder(@NotNull Project project,
                          @NotNull ParserDefinition parserDefinition,
                          @NotNull Lexer lexer,
                          @NotNull ASTNode chameleon,
                          @NotNull CharSequence text,
                          @NotNull PerlPsiBuilderFactory.PerlBuilderDiagnostics diagnostics
  ) {
    super(project, parserDefinition, lexer, chameleon, text);
    this.diagnostics = diagnostics;
    diagnostics.getPasses().add(Pair.create(text.length(), getLexemeCount()));
  }

  public @NotNull PerlPsiBuilderFactory.PerlBuilderDiagnostics getDiagnostics() {
    return diagnostics;
  }

  @Override
  public @NotNull PsiBuilder.Marker mark() {
    return createPerlMarker(super.mark());
  }

  public @NotNull PerlStartMarker createPerlMarker(@NotNull PsiBuilder.Marker realMarker) {
    PerlStartMarker perlMarker = new PerlStartMarker(realMarker, this);
    markersMap.put(realMarker, perlMarker);
    return perlMarker;
  }

  @Override
  public @Nullable StartMarker getLatestDoneMarker() {
    StartMarker realMarker = super.getLatestDoneMarker();
    if (realMarker == null) {
      return null;
    }
    return Objects.requireNonNull(markersMap.get(realMarker));
  }

  public void registerRollback(int tokens) {
    diagnostics.getRollbacks().computeIfAbsent(tokens, it -> new AtomicInteger()).incrementAndGet();
  }
}
