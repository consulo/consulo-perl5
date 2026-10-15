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

package com.perl5.lang.perl.idea.codeInsight.typeInference.value;

import consulo.annotation.component.ServiceImpl;
import jakarta.inject.Singleton;
import consulo.application.progress.ProgressManager;
import consulo.application.util.RecursionManager;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiNamedElement;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization.PerlCallValueBackendHelper;
import com.perl5.lang.perl.psi.PerlCallableElement;
import com.perl5.lang.perl.psi.PerlSubElement;
import com.perl5.lang.perl.util.PerlMroUtil;
import com.perl5.lang.perl.util.PerlPackageUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValues.UNKNOWN_VALUE;


@ServiceImpl
@Singleton
public final class PerlValueBackendResolveService implements PerlValueResolveService {
  @Override
  public @NotNull PerlValue computeResolve(@NotNull PerlCallValue callValue,
                                           @NotNull PerlValue resolvedNamespaceValue,
                                           @NotNull PerlValue resolvedSubNameValue,
                                           @NotNull PerlValueResolver resolver) {
    var subNames = resolvedSubNameValue.getSubNames();
    if (subNames.isEmpty()) {
      return UNKNOWN_VALUE;
    }

    var namespaceNames = PerlCallValueBackendHelper.get(callValue).computeNamespaceNames(resolvedNamespaceValue);
    if (namespaceNames.isEmpty()) {
      return UNKNOWN_VALUE;
    }

    List<PerlValue> resolvedArguments = callValue.computeResolvedArguments(resolvedNamespaceValue, resolver);
    var argumentsValue = PerlArrayValue.builder().addElements(resolvedArguments).build();

    var builder = PerlOneOfValue.builder();
    var hasTargets = new boolean[]{false};
    RecursionManager.doPreventingRecursion(
      consulo.util.lang.Pair.create(resolver.getResolveScope(), callValue), true, () -> {
        PerlCallValueBackendHelper.get(callValue)
          .processCallTargets(
            callValue, resolver.getProject(), resolver.getResolveScope(), resolver.getContextFile(), namespaceNames, subNames,
            (PsiNamedElement it) -> {
              hasTargets[0] = true;
              if (it instanceof PerlSubElement subElement) {
                builder.addVariant(new PerlSubValueResolver(it, argumentsValue).resolve(subElement.getReturnValue()));
              }
              return true;
            });
        return null;
      });

    PerlCallValueBackendHelper.get(callValue).addFallbackTargets(
      callValue, namespaceNames, subNames, resolvedArguments, hasTargets[0], builder, resolvedNamespaceValue, resolver
    );

    return builder.build();
  }

  @Override
  public @NotNull PerlValue computeResolve(@NotNull PerlDuckValue duckValue,
                                           @NotNull PerlValueResolver resolver,
                                           @NotNull List<? extends PerlValue> resolvedElements) {
    if (duckValue.isEmpty() || !PerlValue.isDuckTypingEnabled()) {
      return UNKNOWN_VALUE;
    }
    var usedCallableNames = resolvedElements.stream()
      .flatMap(it -> it.getSubNames().stream())
      .distinct()
      .sorted()
      .collect(Collectors.toList());

    if (usedCallableNames.isEmpty()) {
      return UNKNOWN_VALUE;
    }

    var baseSubName = usedCallableNames.removeFirst();
    var baseNamespaces = new HashSet<String>();
    PerlPackageUtil.processCallablesNamespaceNames(resolver, baseSubName, (PerlCallableElement it) -> {
      baseNamespaces.add(it.getNamespaceName());
      return true;
    });

    if (baseNamespaces.isEmpty()) {
      return UNKNOWN_VALUE;
    }

    var valueBuilder = PerlOneOfValue.builder();
    Consumer<String> namespaceNameProcessor = it -> valueBuilder.addVariant(PerlScalarValue.create(it));
    if (usedCallableNames.isEmpty()) {
      baseNamespaces.forEach(namespaceNameProcessor);
    }
    else {
      baseNamespaces.forEach(namespaceName -> processNamespacesWithAllCallables(
        namespaceName, Set.copyOf(usedCallableNames), resolver, new HashSet<String>(), namespaceNameProcessor
      ));
    }
    return valueBuilder.build();
  }

  private void processNamespacesWithAllCallables(@Nullable String namespaceName,
                                                 @NotNull Set<String> callableNames,
                                                 @NotNull PerlValueResolver resolver,
                                                 @NotNull Set<String> recursionControlSet,
                                                 @NotNull Consumer<? super String> namespaceNameConsumer) {
    if (namespaceName == null || StringUtil.isEmpty(namespaceName) || !recursionControlSet.add(namespaceName)) {
      return;
    }

    ProgressManager.checkCanceled();
    var callablesLeft = new HashSet<>(callableNames);
    var project = resolver.getProject();
    var resolveScope = resolver.getResolveScope();
    var notFinished =
      PerlMroUtil.processCallables(project, resolveScope, namespaceName, callableNames, false, (PerlCallableElement it) -> {
        callablesLeft.remove(it.getCallableName());
        return !callablesLeft.isEmpty();
      }, false);

    if (!notFinished) {
      namespaceNameConsumer.accept(namespaceName);
      return;
    }

    var callablesToFind = Collections.unmodifiableSet(callablesLeft);
    for (var childNamespace : PerlPackageUtil.getChildNamespaces(project, namespaceName, resolveScope)) {
      processNamespacesWithAllCallables(
        Objects.requireNonNull(childNamespace.getNamespaceName()), callablesToFind, resolver, recursionControlSet, namespaceNameConsumer
      );
    }
  }

}
