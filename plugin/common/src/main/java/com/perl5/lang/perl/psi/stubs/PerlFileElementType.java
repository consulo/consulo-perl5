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

package com.perl5.lang.perl.psi.stubs;

import com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessorService;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization.PerlValueBackendHelper;
import com.perl5.lang.perl.parser.builder.PerlPsiBuilderFactory;
import com.perl5.lang.perl.psi.PerlFile;
import com.perl5.lang.perl.psi.PerlSubCallHandlerVersionService;
import com.perl5.lang.perl.psi.stubs.namespaces.PerlNamespaceDefinitionElementType;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiBuilder;
import consulo.language.parser.PsiParser;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.stub.DefaultStubBuilder;
import consulo.language.psi.stub.IStubFileElementType;
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubBuilder;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.language.version.LanguageVersion;
import consulo.language.version.LanguageVersionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static com.perl5.lang.perl.psi.stubs.namespaces.PerlNamespaceIndex.NAMESPACE_KEY;

// consulo: IStubFileElementType instead of IFileElementType. The file element type provides the stub builder and the stub version
//  (upstream: PerlStubDefinition and PerlTemplatingStubDefinition registered as languageStubDefinition) and (de)serializes and indexes
//  the file stub (upstream: PerlFileStubserializer registered in PerlStubRegistryExtension)
public class PerlFileElementType extends IStubFileElementType<PerlFileStub> {
  /**
   * consulo: 7 upstream, bumped after moving stubs serialization back to the element types
   */
  private static final int BASE_VERSION = 8;

  public PerlFileElementType(@NotNull String debugName, @Nullable Language language) {
    super(debugName, language);
  }

  /**
   * consulo: for templating languages {@code super.getStubVersion()} is the template stub version, like upstream
   * {@code PerlTemplatingStubDefinition} adds {@code TemplateLanguageStubBaseVersion.version}
   */
  @Override
  public int getStubVersion() {
    return super.getStubVersion() +
           BASE_VERSION +
           PerlValueBackendHelper.getVersion() +
           PerlSubCallHandlerVersionService.getHandlersVersion() +
           PerlPackageProcessorService.getVersion();
  }

  @Override
  public StubBuilder getBuilder() {
    return new DefaultStubBuilder() {
      @SuppressWarnings("rawtypes")
      @Override
      protected @NotNull StubElement createStubForFile(@NotNull PsiFile file) {
        return new PerlFileStub((PerlFile)file, PerlFileElementType.this);
      }
    };
  }

  @Override
  public @NotNull String getExternalId() {
    return "perl5.file." + this;
  }

  @Override
  public void serialize(@NotNull PerlFileStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    PerlNamespaceDefinitionElementType.serializeNamespaceData(dataStream, stub.getData());
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlFileStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
    return new PerlFileStub(PerlNamespaceDefinitionElementType.deserializeNamespaceData(dataStream), this);
  }

  @Override
  public void indexStub(@NotNull PerlFileStub stub, @NotNull IndexSink sink) {
    if (stub.isEmpty() && PerlPackageUtilCore.MAIN_NAMESPACE_NAME.equals(stub.getNamespaceName())) {
      return;
    }
    sink.occurrence(NAMESPACE_KEY, stub.getNamespaceName());
  }

  @Override
  protected @Nullable ASTNode doParseContents(@NotNull ASTNode chameleon, @NotNull PsiElement psi) {
    return getParser(psi).parse(this, getBuilder(psi, chameleon), getLanguageVersion(psi)).getFirstChildNode();
  }

  private @NotNull PsiParser getParser(@NotNull PsiElement psi) {
    return ParserDefinition.forLanguage(getLanguageForParser(psi)).createParser(getLanguageVersion(psi));
  }

  // consulo: parsers are created per language version, not per project
  private @NotNull LanguageVersion getLanguageVersion(@NotNull PsiElement psi) {
    return LanguageVersionUtil.findLanguageVersion(getLanguageForParser(psi), psi);
  }

  private @NotNull PsiBuilder getBuilder(@NotNull PsiElement psi, @NotNull ASTNode chameleon) {
    return PerlPsiBuilderFactory.Companion.createBuilder(
      psi.getProject(), chameleon, null, getLanguageForParser(psi), chameleon.getChars()
    );
  }
}
