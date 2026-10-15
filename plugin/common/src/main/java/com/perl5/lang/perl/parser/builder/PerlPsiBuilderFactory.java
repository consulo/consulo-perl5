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

package com.perl5.lang.perl.parser.builder;

import consulo.language.ast.ASTNode;
import consulo.language.Language;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiBuilder;
import consulo.language.parser.PsiBuilderFactory;
import consulo.language.lexer.Lexer;
import consulo.project.Project;
import consulo.util.lang.Pair;
import consulo.language.ast.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

public final class PerlPsiBuilderFactory {
  private static @Nullable PerlBuilderDiagnostics BUILDER_DIAGNOSTICS = null;

  public static final Companion Companion = new Companion();

  private PerlPsiBuilderFactory() {
  }

  public static final class Companion {
    private Companion() {
    }

    public @NotNull PsiBuilder createBuilder(@NotNull Project project,
                                             @NotNull ASTNode chameleon,
                                             @Nullable Lexer lexer,
                                             @NotNull Language lang,
                                             @NotNull CharSequence seq) {
      // TODO consulo: upstream creates PerlDebugBuilder here when BUILDER_DIAGNOSTICS is set (test-only parser diagnostics).
      //  It subclasses IntelliJ's internal PsiBuilderImpl, which has no Consulo counterpart, so it is not ported.
      return PsiBuilderFactory.getInstance().createBuilder(project, chameleon, lexer, lang, lang.getVersions()[0], seq);
    }

    private @NotNull ParserDefinition getParserDefinition(@Nullable Language language, @NotNull IElementType tokenType) {
      Language adjusted = language != null ? language : tokenType.getLanguage();
      ParserDefinition parserDefinition = ParserDefinition.forLanguage(adjusted);
      if (parserDefinition == null) {
        throw new AssertionError(
          "ParserDefinition absent for language: '" + adjusted.getID() + "' (" + adjusted.getClass().getName() + "), " +
          "for elementType: '" + tokenType + "' (" + tokenType.getClass().getName() + ")"
        );
      }
      return parserDefinition;
    }

    @TestOnly
    public void withDebugBuilder(@NotNull PerlBuilderDiagnostics diagnostics, @NotNull Runnable runnable) {
      PerlBuilderDiagnostics odlDiagnostics = BUILDER_DIAGNOSTICS;
      try {
        BUILDER_DIAGNOSTICS = diagnostics;
        runnable.run();
      }
      finally {
        BUILDER_DIAGNOSTICS = odlDiagnostics;
      }
    }
  }

  public static final class PerlBuilderDiagnostics {
    private final @NotNull Map<Integer, AtomicInteger> rollbacks = new HashMap<>();
    // TODO consulo: upstream uses kotlin.Pair<Int, Int> here; replaced with consulo.util.lang.Pair (no kotlin.* in Java sources)
    private final @NotNull List<Pair<Integer, Integer>> passes = new ArrayList<>();

    public @NotNull Map<Integer, AtomicInteger> getRollbacks() {
      return rollbacks;
    }

    public @NotNull List<Pair<Integer, Integer>> getPasses() {
      return passes;
    }

    @Override
    public String toString() {
      int totalRollbacks = 0;
      int totalRolledback = 0;
      for (Map.Entry<Integer, AtomicInteger> it : this.rollbacks.entrySet()) {
        totalRollbacks += it.getValue().get();
        totalRolledback += it.getKey() * it.getValue().get();
      }
      int lexemeCount = 0;
      int builders = 0;
      StringJoiner passesJoiner = new StringJoiner("\n");
      for (Pair<Integer, Integer> it : this.passes) {
        builders++;
        lexemeCount += it.second;
        passesJoiner.add(it.first + ";" + it.second);
      }
      String passes = passesJoiner.toString();

      List<Map.Entry<Integer, AtomicInteger>> sortedRollbacks = new ArrayList<>(this.rollbacks.entrySet());
      sortedRollbacks.sort(Map.Entry.comparingByKey());
      StringJoiner rollbacksJoiner = new StringJoiner("\n");
      for (Map.Entry<Integer, AtomicInteger> it : sortedRollbacks) {
        int length = it.getKey();
        int rollbacks = it.getValue().get();
        int tokensRolled = length * rollbacks;
        rollbacksJoiner.add(
          length + ";" + rollbacks + ";" + tokensRolled + ";" + tokensRolled * 100 / lexemeCount + "%;" + rollbacks * 100 / totalRollbacks +
          "%;" + tokensRolled * 100 / totalRolledback + "%");
      }
      String rollbacks = rollbacksJoiner.toString();
      return "Diagnostics:\n" +
             "          Passes: " + builders + "\n" +
             "    Tokens count: " + lexemeCount + "\n" +
             " Total rollbacks: " + totalRollbacks + "\n" +
             "     Rolled back: " + totalRolledback + " (" + totalRolledback * 100 / lexemeCount + "%) tokens\n" +
             "\n" +
             "Characters;Tokens\n" +
             passes + "\n" +
             "\n" +
             "Tokens;Rollbacks;Tokens rolled;% of tokens count;% of rollbacks;% of rolled back\n" +
             rollbacks + "\n" +
             "        ";
    }
  }
}
