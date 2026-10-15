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

package com.perl5.lang.perl.idea.findusages;

import consulo.language.cacheBuilder.WordOccurrence;
import consulo.language.cacheBuilder.WordsScanner;
import consulo.language.psi.stub.IdDataConsumer;
import consulo.language.psi.stub.IdIndexEntry;
import consulo.language.psi.stub.IdIndexer;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.language.psi.search.UsageSearchContext;
import consulo.application.util.function.Processor;
import consulo.language.psi.stub.FileContent;
import consulo.util.lang.CharArrayUtil;
import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.fileTypes.PerlFileTypeScript;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// consulo: IntelliJ ScanningIdIndexer does not exist in Consulo, its processor logic is ported below
public class PerlIdIndexer implements IdIndexer {
  protected PerlWordsScanner createScanner() {
    return new PerlWordsScanner();
  }

  @Override
  public @NotNull Map<IdIndexEntry, Integer> map(@NotNull FileContent inputData) {
    final IdDataConsumer consumer = new IdDataConsumer();
    var psiFile = inputData.getPsiFile();
    var perlPsi = psiFile.getViewProvider().getPsi(PerlLanguage.INSTANCE);
    if (perlPsi != null) {
      createScanner().processWordsUsingPsi(psiFile, createProcessor(inputData.getContentAsText(), consumer));
    }
    return consumer.getResult();
  }

  // consulo: port of IntelliJ ScanningIdIndexer#createProcessor
  protected @NotNull Processor<WordOccurrence> createProcessor(@NotNull CharSequence chars, @NotNull IdDataConsumer consumer) {
    final char[] charsArray = CharArrayUtil.fromSequenceWithoutCopying(chars);
    return t -> {
      if (charsArray != null && t.getBaseText() == chars) {
        consumer.addOccurrence(charsArray, t.getStart(), t.getEnd(), convertToMask(t.getKind()));
      }
      else {
        consumer.addOccurrence(t.getBaseText(), t.getStart(), t.getEnd(), convertToMask(t.getKind()));
      }
      return true;
    };
  }

  // consulo: port of IntelliJ ScanningIdIndexer#convertToMask
  private static int convertToMask(WordOccurrence.Kind kind) {
    if (kind == null) return UsageSearchContext.ANY;
    if (kind == WordOccurrence.Kind.CODE) return UsageSearchContext.IN_CODE;
    if (kind == WordOccurrence.Kind.COMMENTS) return UsageSearchContext.IN_COMMENTS;
    if (kind == WordOccurrence.Kind.LITERALS) return UsageSearchContext.IN_STRINGS;
    if (kind == WordOccurrence.Kind.FOREIGN_LANGUAGE) return UsageSearchContext.IN_FOREIGN_LANGUAGES;
    return 0;
  }

  // consulo: upstream registers this indexer for "Perl5 Script", "Perl5 Package", "Cpanfile" and "Perl5 Test" file types,
  //  Consulo IdIndexer is bound to a single file type; each registration (*Registration subclass) overrides this
  @Override
  public @NotNull FileType getFileType() {
    return PerlFileTypeScript.INSTANCE;
  }
}
