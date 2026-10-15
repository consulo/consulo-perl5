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

package com.perl5.lang.perl.psi.stubs.subsdefinitions;

import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValue;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValueSerializer;
import com.perl5.lang.perl.parser.elementTypes.PsiElementProvider;
import com.perl5.lang.perl.psi.PerlSubDefinitionElement;
import com.perl5.lang.perl.psi.impl.PsiPerlSubDefinitionImpl;
import com.perl5.lang.perl.psi.utils.PerlSubAnnotations;
import com.perl5.lang.perl.psi.utils.PerlSubArgument;
import com.perl5.lang.perl.psi.utils.PerlVariableType;
import com.perl5.lang.perl.util.PerlValuesUtil;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubIndexKey;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// consulo: IStubElementType instead of IElementType, the element type creates, (de)serializes and indexes its stubs
//  (upstream: PerlSubDefinitionStubSerializingFactory registered in PerlStubRegistryExtension)
public class PerlSubDefinitionElementType extends IStubElementType<PerlSubDefinitionStub, PerlSubDefinitionElement>
  implements PsiElementProvider {
  public PerlSubDefinitionElementType(@NotNull String name) {
    super(name, PerlLanguage.INSTANCE);
  }

  public PerlSubDefinitionElementType(@NotNull @NonNls String debugName, @Nullable Language language) {
    super(debugName, language);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new PsiPerlSubDefinitionImpl(node);
  }

  @Override
  public @NotNull String getExternalId() {
    return "perl." + this;
  }

  @Override
  public PerlSubDefinitionElement createPsi(@NotNull PerlSubDefinitionStub stub) {
    return new PsiPerlSubDefinitionImpl(stub, this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlSubDefinitionStub createStub(@NotNull PerlSubDefinitionElement psi, @Nullable StubElement parentStub) {
    return createStubElement(
      parentStub,
      psi.getNamespaceName(),
      psi.getSubName(),
      psi.getSubArgumentsList(),
      psi.getReturnValueFromCode(),
      psi.getAnnotations()
    );
  }

  @Override
  public void serialize(@NotNull PerlSubDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getNamespaceName());
    dataStream.writeName(stub.getSubName());
    serializeArguments(dataStream, stub.getSubArgumentsList());
    PerlSubAnnotations subAnnotations = stub.getAnnotations();
    if (subAnnotations == null) {
      dataStream.writeBoolean(false);
    }
    else {
      dataStream.writeBoolean(true);
      writeSubAnnotations(dataStream, subAnnotations);
    }
    PerlValueSerializer.serialize(stub.getReturnValueFromCode(), dataStream);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlSubDefinitionStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub)
    throws IOException {
    String packageName = Objects.requireNonNull(dataStream.readName()).getString();
    String functionName = Objects.requireNonNull(dataStream.readName()).getString();
    List<PerlSubArgument> arguments = deserializeArguments(dataStream);
    PerlSubAnnotations annotations = dataStream.readBoolean() ? readSubAnnotations(dataStream) : null;
    return createStubElement(parentStub, packageName, Objects.requireNonNull(functionName), arguments, PerlValuesUtil.readValue(dataStream),
                             annotations);
  }

  @Override
  public void indexStub(@NotNull PerlSubDefinitionStub stub, @NotNull IndexSink sink) {
    String canonicalName = stub.getCanonicalName();
    if (canonicalName != null) {
      sink.occurrence(getDirectKey(), canonicalName);
    }

    String packageName = stub.getNamespaceName();
    if (packageName != null) {
      sink.occurrence(getReverseKey(), packageName);
    }

    String callableName = stub.getCallableName();
    if (StringUtil.isNotEmpty(callableName)) {
      sink.occurrence(getCallableNameKey(), callableName);
    }
  }

  @Override
  public boolean shouldCreateStub(@NotNull ASTNode node) {
    PsiElement element = node.getPsi();
    return element instanceof PerlSubDefinitionElement subDefinitionElement &&
           subDefinitionElement.isValid() &&
           StringUtil.isNotEmpty(subDefinitionElement.getCanonicalName());
  }

  protected @NotNull PerlSubDefinitionStub createStubElement(@Nullable StubElement<? extends PsiElement> parentStub,
                                                             @Nullable String packageName,
                                                             @NotNull String functionName,
                                                             @NotNull List<PerlSubArgument> arguments,
                                                             @NotNull PerlValue returnValueFromCode,
                                                             @Nullable PerlSubAnnotations annotations) {
    return new PerlSubDefinitionStub(parentStub, packageName, functionName, arguments, annotations, returnValueFromCode, this);
  }

  protected @NotNull StubIndexKey<String, ? extends PsiElement> getDirectKey() {
    return PerlSubDefinitionsIndex.KEY;
  }

  protected @NotNull StubIndexKey<String, ? extends PsiElement> getReverseKey() {
    return PerlSubDefinitionReverseIndex.KEY;
  }

  protected @NotNull StubIndexKey<String, ? extends PsiElement> getCallableNameKey() {
    return PerlCallableNamesIndex.KEY;
  }

  public static void serializeArguments(@NotNull StubOutputStream dataStream, @NotNull List<PerlSubArgument> arguments)
    throws IOException {
    dataStream.writeVarInt(arguments.size());
    for (PerlSubArgument argument : arguments) {
      serializeArgument(dataStream, argument);
    }
  }

  private static void serializeArgument(@NotNull StubOutputStream dataStream, @NotNull PerlSubArgument argument) throws IOException {
    dataStream.writeName(argument.getArgumentType().toString());
    dataStream.writeName(argument.getArgumentName());
    dataStream.writeName(argument.getVariableClass());
    dataStream.writeBoolean(argument.isOptional());
  }

  private static @NotNull PerlSubArgument deserializeArgument(@NotNull StubInputStream dataStream) throws IOException {
    PerlVariableType argumentType = PerlVariableType.valueOf(String.valueOf(dataStream.readName()));
    String argumentName = String.valueOf(dataStream.readName());
    String variableClass = String.valueOf(dataStream.readName());
    boolean isOptional = dataStream.readBoolean();
    return PerlSubArgument.create(argumentType, argumentName, variableClass, isOptional);
  }

  public static @NotNull List<PerlSubArgument> deserializeArguments(@NotNull StubInputStream dataStream) throws IOException {
    int argumentsNumber = dataStream.readVarInt();
    if (argumentsNumber <= 0) {
      return Collections.emptyList();
    }
    List<PerlSubArgument> arguments = new ArrayList<>(argumentsNumber);
    for (int i = 0; i < argumentsNumber; i++) {
      arguments.add(deserializeArgument(dataStream));
    }
    return arguments;
  }

  public static void writeSubAnnotations(@NotNull StubOutputStream dataStream, @NotNull PerlSubAnnotations annotations)
    throws IOException {
    dataStream.writeByte(annotations.getFlags());
    PerlValueSerializer.serialize(annotations.getReturnValue(), dataStream);
  }

  public static @NotNull PerlSubAnnotations readSubAnnotations(@NotNull StubInputStream dataStream) throws IOException {
    return new PerlSubAnnotations(dataStream.readByte(), PerlValuesUtil.readValue(dataStream));
  }
}
