/*
 * Copyright 2015-2026 Alexandr Evstigneev
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
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiNamedElement;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.application.util.function.Processor;
import com.perl5.lang.perl.extensions.imports.PerlImportsProvider;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.*;
import com.perl5.lang.perl.util.PerlNamespaceUtil;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Set;


public final class PerlCallStaticValueBackendHelper extends PerlCallValueBackendHelper<PerlCallStaticValue> {
  @Override
  public int getSerializationId() {
    return PerlValueBackendHelper.CALL_STATIC_ID;
  }

  @Override
  public void serializeData(@NotNull PerlCallStaticValue value, @NotNull PerlValueSerializer serializer) throws IOException {
    super.serializeData(value, serializer);
    serializer.writeBoolean(value.hasExplicitNamespace());
  }

  @Override
  protected @NotNull PerlValue deserialize(@NotNull PerlValueDeserializer deserializer,
                                           @NotNull PerlValue baseValue,
                                           @NotNull PerlValue parameter,
                                           @NotNull List<? extends PerlValue> arguments) throws IOException {
    return new PerlCallStaticValue(baseValue, parameter, arguments, deserializer.readBoolean());
  }

  // fixme resolve namespace and subs first
  @Override
  public boolean processTargetNamespaceElements(@NotNull PerlCallStaticValue callValue,
                                                @NotNull PsiElement contextElement,
                                                @NotNull PerlNamespaceItemProcessor<? super PsiNamedElement> processor) {
    Project project = contextElement.getProject();
    var searchScope = contextElement.getResolveScope();
    for (String currentNamespaceName : callValue.getNamespaceNameValue().resolve(contextElement).getNamespaceNames()) {
      if (!processTargetNamespaceElements(project, searchScope, processor, currentNamespaceName, contextElement)) {
        return false;
      }
    }

    var containingNamespace = PerlPackageUtilCore.getContainingNamespace(contextElement.getOriginalElement());
    var namespaceName = containingNamespace == null ? null : containingNamespace.getNamespaceName();
    if (!StringUtil.isEmpty(namespaceName)) {
      processExportDescriptors(
        project, searchScope, processor, PerlImportsProvider.getAllExportDescriptors(containingNamespace)
      );
    }
    return true;
  }

  @Override
  public boolean processCallTargets(@NotNull PerlCallStaticValue callValue,
                                    @NotNull Project project,
                                    @NotNull GlobalSearchScope searchScope,
                                    @Nullable PsiElement contextElement,
                                    @NotNull Set<String> namespaceNames,
                                    @NotNull Set<String> subNames,
                                    @NotNull Processor<? super PsiNamedElement> processor) {
    for (String contextNamespace : namespaceNames) {
      var processingContext = new ProcessingContext();
      processingContext.setProcessBuiltIns(!callValue.hasExplicitNamespace());
      if (!processItemsInNamespace(project, searchScope, subNames, processor, contextNamespace, processingContext, contextElement)) {
        return false;
      }
    }

    if (!callValue.hasExplicitNamespace() && contextElement != null) {
      var containingNamespace = PerlPackageUtilCore.getContainingNamespace(contextElement.getOriginalElement());
      var namespaceName = containingNamespace == null ? null : containingNamespace.getNamespaceName();
      if (!StringUtil.isEmpty(namespaceName)) {
        processExportDescriptorsItems(
          project, searchScope, subNames, processor, PerlImportsProvider.getAllExportDescriptors(containingNamespace)
        );
      }
    }

    return true;
  }

  @Override
  public void addFallbackTargets(@NotNull PerlCallStaticValue callValue,
                                 @NotNull Set<String> namespaceNames,
                                 @NotNull Set<String> subNames,
                                 @NotNull List<PerlValue> resolvedArguments,
                                 boolean hasTarget,
                                 @NotNull PerlOneOfValue.Builder builder,
                                 @NotNull PerlValue resolvedNamespaceValue,
                                 @NotNull PerlValueResolver resolver) {
    if (!hasTarget && callValue.hasExplicitNamespace() && subNames.size() == 1 && namespaceNames.size() == 1 && resolvedArguments.isEmpty()) {
      var possiblePackageName = PerlPackageUtilCore.join(namespaceNames.iterator().next(), subNames.iterator().next());
      if (!PerlNamespaceUtil.getNamespaceDefinitions(resolver.getProject(), resolver.getResolveScope(), possiblePackageName).isEmpty()) {
        builder.addVariant(PerlScalarValue.create(possiblePackageName));
      }
    }
  }
}
