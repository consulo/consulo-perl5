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

package com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization;

import consulo.project.Project;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiNamedElement;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.application.util.function.Processor;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.*;
import com.perl5.lang.perl.util.PerlMroUtil;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public final class PerlCallObjectValueBackendHelper extends PerlCallValueBackendHelper<PerlCallObjectValue> {
  @Override
  public int getSerializationId() {
    return PerlValueBackendHelper.CALL_OBJECT_ID;
  }

  @Override
  public void serializeData(@NotNull PerlCallObjectValue value, @NotNull PerlValueSerializer serializer) throws IOException {
    super.serializeData(value, serializer);
    serializer.writeName(value.getSuperContext());
  }

  @Override
  protected @NotNull PerlValue deserialize(@NotNull PerlValueDeserializer deserializer,
                                           @NotNull PerlValue baseValue,
                                           @NotNull PerlValue parameter,
                                           @NotNull List<? extends PerlValue> arguments) throws IOException {
    return PerlCallObjectValue.create(baseValue, parameter, arguments, deserializer.readNameString());
  }

  @Override
  public @NotNull Set<String> computeNamespaceNames(@NotNull PerlValue resolvedNamespaceValue) {
    return resolvedNamespaceValue.isUnknown() ? new LinkedHashSet<>(List.of(PerlPackageUtilCore.UNIVERSAL_NAMESPACE))
                                              : super.computeNamespaceNames(resolvedNamespaceValue);
  }

  @Override
  public boolean processCallTargets(@NotNull PerlCallObjectValue callValue,
                                    @NotNull Project project,
                                    @NotNull GlobalSearchScope searchScope,
                                    @Nullable PsiElement contextElement,
                                    @NotNull Set<String> namespaceNames,
                                    @NotNull Set<String> subNames,
                                    @NotNull Processor<? super PsiNamedElement> processor) {
    for (String contextNamespace : namespaceNames) {
      for (String currentNamespaceName : PerlMroUtil.getLinearISA(
        project,
        searchScope,
        getEffectiveNamespaceName(callValue, contextNamespace),
        callValue.isSuper()
      )) {
        var processingContext = new ProcessingContext();
        processingContext.setProcessBuiltIns(false);
        if (!processItemsInNamespace(project, searchScope, subNames, processor, currentNamespaceName, processingContext, contextElement)) {
          return false;
        }
        if (!processingContext.getProcessAutoload()) { // marker that we've got at least one result
          break;
        }
      }
    }
    return true;
  }

  @Override
  public boolean processTargetNamespaceElements(@NotNull PerlCallObjectValue callValue,
                                                @NotNull PsiElement contextElement,
                                                @NotNull PerlNamespaceItemProcessor<? super PsiNamedElement> processor) {
    Project project = contextElement.getProject();
    GlobalSearchScope searchScope = contextElement.getResolveScope();
    for (String contextNamespace : callValue.getNamespaceNameValue().resolve(contextElement).getNamespaceNames()) {
      for (String currentNamespaceName : PerlMroUtil.getLinearISA(
        project, searchScope, getEffectiveNamespaceName(callValue, contextNamespace), callValue.isSuper()
      )) {
        if (!processTargetNamespaceElements(project, searchScope, processor, currentNamespaceName, contextElement)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public void addFallbackTargets(@NotNull PerlCallObjectValue callValue,
                                 @NotNull Set<String> namespaceNames,
                                 @NotNull Set<String> subNames,
                                 @NotNull List<PerlValue> resolvedArguments,
                                 boolean hasTarget,
                                 @NotNull PerlOneOfValue.Builder builder,
                                 @NotNull PerlValue resolvedNamespaceValue,
                                 @NotNull PerlValueResolver resolver) {
    if (subNames.size() == 1 && "new".equals(subNames.iterator().next())) {
      builder.addVariant(resolvedNamespaceValue);
    }
  }

  private @NotNull String getEffectiveNamespaceName(@NotNull PerlCallObjectValue callValue, @NotNull String contextNamespace) {
    var superContext = callValue.getSuperContext();
    return superContext != null ? superContext : contextNamespace;
  }
}
