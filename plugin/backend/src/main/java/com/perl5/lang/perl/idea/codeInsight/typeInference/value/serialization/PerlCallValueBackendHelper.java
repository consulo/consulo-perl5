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
import com.perl5.lang.perl.extensions.packageprocessor.PerlExportDescriptor;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.*;
import com.perl5.lang.perl.idea.configuration.settings.PerlSharedSettings;
import com.perl5.lang.perl.psi.PerlCallableElement;
import com.perl5.lang.perl.psi.references.PerlImplicitDeclarationsService;
import com.perl5.lang.perl.util.PerlNamespaceUtil;
import com.perl5.lang.perl.util.PerlPackageUtil;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.perl5.lang.perl.util.PerlSubUtilCore.SUB_AUTOLOAD;


public abstract class PerlCallValueBackendHelper<Val extends PerlCallValue> extends PerlParametrizedOperationValueBackendHelper<Val> {
  @Override
  public void serializeData(@NotNull Val value, @NotNull PerlValueSerializer serializer) throws IOException {
    super.serializeData(value, serializer);
    serializer.writeValuesList(value.getArguments());
  }

  @Override
  protected final @NotNull PerlValue deserialize(@NotNull PerlValueDeserializer deserializer,
                                                 @NotNull PerlValue baseValue,
                                                 @NotNull PerlValue parameter) throws IOException {
    return deserialize(deserializer, baseValue, parameter, deserializer.readValuesList());
  }

  protected abstract @NotNull PerlValue deserialize(@NotNull PerlValueDeserializer deserializer,
                                                    @NotNull PerlValue baseValue,
                                                    @NotNull PerlValue parameter,
                                                    @NotNull List<? extends PerlValue> arguments) throws IOException;

  /**
   * Processes all possible call targets: subs declarations, definitions and typeglobs with `processor`
   * @param contextElement invocation point. Context element, necessary to compute additional imports
   */
  @SuppressWarnings("UnusedReturnValue")
  public boolean processCallTargets(@NotNull Val callValue,
                                    @NotNull PsiElement contextElement,
                                    @NotNull Processor<? super PsiNamedElement> processor) {
    Project project = contextElement.getProject();
    var searchScope = contextElement.getResolveScope();
    Set<String> subNames = callValue.getSubNameValue().resolve(contextElement).getSubNames();
    Set<String> namespaceNames = computeNamespaceNames(callValue.getNamespaceNameValue().resolve(contextElement));
    return !subNames.isEmpty() && !namespaceNames.isEmpty() &&
           processCallTargets(callValue, project, searchScope, contextElement, namespaceNames, subNames, processor);
  }

  protected boolean processItemsInNamespace(@NotNull Project project,
                                            @NotNull GlobalSearchScope searchScope,
                                            @NotNull Set<String> subNames,
                                            @NotNull Processor<? super PsiNamedElement> processor,
                                            @NotNull String namespaceName,
                                            @NotNull ProcessingContext processingContext,
                                            @Nullable PsiElement contextElement) {
    Processor<PsiNamedElement> processorWrapper = it -> {
      processingContext.setProcessBuiltIns(false);
      processingContext.setProcessAutoload(false);
      return processor.process(it);
    };

    GlobalSearchScope subsEffectiveScope = getEffectiveScope(project, searchScope, namespaceName, contextElement);
    for (String subName : subNames) {
      if (!PerlPackageUtil.processCallables(
        project,
        subsEffectiveScope,
        PerlPackageUtilCore.join(namespaceName, subName),
        processorWrapper
      )) {
        return false;
      }
    }

    // exports
    var exportDescriptors = PerlNamespaceUtil.getExportDescriptors(project, searchScope, namespaceName);
    if (!processExportDescriptorsItems(project, searchScope, subNames, processorWrapper, exportDescriptors)) {
      return false;
    }

    // built-ins
    if (processingContext.getProcessBuiltIns()) {
      processingContext.setProcessBuiltIns(false);
      for (String subName : subNames) {
        var coreSub = PerlImplicitDeclarationsService.getInstance(project).getCoreSub(subName);
        if (coreSub != null && !processorWrapper.process(coreSub)) {
          return false;
        }
      }
    }

    // AUTOLOAD
    return !processingContext.getProcessAutoload() ||
           PerlPackageUtilCore.isUNIVERSAL(namespaceName) || PerlPackageUtilCore.isCORE(namespaceName) ||
           PerlPackageUtil.processCallables(project, searchScope, PerlPackageUtilCore.join(namespaceName, SUB_AUTOLOAD), processorWrapper);
  }

  /**
   * Adjust search scope if necessary. Used to handle simple main resolution
   *
   * @return original or adjusted scope to search
   */
  private @NotNull GlobalSearchScope getEffectiveScope(@NotNull Project project,
                                                       @NotNull GlobalSearchScope originalScope,
                                                       @NotNull String namespaceName,
                                                       @Nullable PsiElement contextElement) {
    var containingFile = contextElement == null ? null : contextElement.getContainingFile();
    var contextFile = containingFile == null ? null : containingFile.getOriginalFile();
    if (PerlPackageUtilCore.MAIN_NAMESPACE_NAME.equals(namespaceName) &&
        PerlSharedSettings.getInstance(project).SIMPLE_MAIN_RESOLUTION && contextFile != null
    ) {
      return GlobalSearchScope.fileScope(contextFile);
    }
    return originalScope;
  }

  protected boolean processExportDescriptorsItems(@NotNull Project project,
                                                  @NotNull GlobalSearchScope searchScope,
                                                  @NotNull Set<String> subNames,
                                                  @NotNull Processor<? super PsiNamedElement> processorWrapper,
                                                  @NotNull Set<? extends PerlExportDescriptor> exportDescriptors) {
    for (PerlExportDescriptor exportDescriptor : exportDescriptors) {
      if (subNames.contains(exportDescriptor.getImportedName()) &&
          !PerlPackageUtil.processCallables(project, searchScope, exportDescriptor.getTargetCanonicalName(), processorWrapper)
      ) {
        return false;
      }
    }
    return true;
  }


  /**
   * @return set of a namepsaces names that should be used for the `resolvedNamespaceValue`
   */
  public @NotNull Set<String> computeNamespaceNames(@NotNull PerlValue resolvedNamespaceValue) {
    return resolvedNamespaceValue.getNamespaceNames();
  }

  /**
   * Processes all possible call targets: subs declarations, definitions and typeglobs with `processor` for
   * the `namespaceName`
   */
  public abstract boolean processCallTargets(@NotNull Val callValue,
                                             @NotNull Project project,
                                             @NotNull GlobalSearchScope searchScope,
                                             @Nullable PsiElement contextElement,
                                             @NotNull Set<String> namespaceNames,
                                             @NotNull Set<String> subNames,
                                             @NotNull Processor<? super PsiNamedElement> processor);

  /**
   * Processes all elements in all namespaces targeted by current call qualifying namespace. E.g. for `Foo::Bar->foo` processes
   * all elements from `Foo::Bar`
   * @param contextElement origin element of a call. Used to process implicit import into the file
   */
  public abstract boolean processTargetNamespaceElements(@NotNull Val callValue,
                                                         @NotNull PsiElement contextElement,
                                                         @NotNull PerlNamespaceItemProcessor<? super PsiNamedElement> processor);

  protected static final class ProcessingContext {
    private boolean processAutoload = true;
    private boolean processBuiltIns = true;

    public boolean getProcessAutoload() {
      return processAutoload;
    }

    public void setProcessAutoload(boolean processAutoload) {
      this.processAutoload = processAutoload;
    }

    public boolean getProcessBuiltIns() {
      return processBuiltIns;
    }

    public void setProcessBuiltIns(boolean processBuiltIns) {
      this.processBuiltIns = processBuiltIns;
    }
  }

  protected boolean processExportDescriptors(@NotNull Project project,
                                             @NotNull GlobalSearchScope searchScope,
                                             @NotNull PerlNamespaceItemProcessor<? super PsiNamedElement> processor,
                                             @NotNull Set<? extends PerlExportDescriptor> exportDescriptors) {
    if (exportDescriptors.isEmpty()) {
      return true;
    }
    var foundOne = new boolean[]{false};
    for (PerlExportDescriptor exportDescriptor : exportDescriptors) {
      foundOne[0] = false;
      if (!PerlPackageUtil.processCallables(
        project,
        searchScope,
        exportDescriptor.getTargetCanonicalName(),
        (PerlCallableElement it) -> {
          foundOne[0] = true;
          return processor.processImportedItem(it, exportDescriptor);
        })
      ) {
        return false;
      }

      if (!foundOne[0]) {
        if (!processor.processOrphanDescriptor(exportDescriptor)) {
          return false;
        }
      }
    }
    return true;
  }

  protected boolean processTargetNamespaceElements(@NotNull Project project,
                                                   @NotNull GlobalSearchScope searchScope,
                                                   @NotNull PerlNamespaceItemProcessor<? super PsiNamedElement> processor,
                                                   @NotNull String currentNamespaceName,
                                                   @NotNull PsiElement contextElement) {
    var effectiveScope = getEffectiveScope(project, searchScope, currentNamespaceName, contextElement);

    if (!PerlPackageUtil.processCallablesInNamespace(
      project,
      effectiveScope,
      currentNamespaceName,
      (PerlCallableElement t) -> processor.processItem(t))
    ) {
      return false;
    }

    // exports
    var exportDescriptors =
      PerlNamespaceUtil.getExportDescriptors(project, effectiveScope, currentNamespaceName);
    return processExportDescriptors(project, effectiveScope, processor, exportDescriptors);
  }

  /**
   * Computes a fallback value. This method should handle two cases:
   * - invisible/complex constructor, where we can't compute a proper return value
   * - incorrectly lexed namespace FQNs, where `Foo::Bar` was lexed and parsed as `Foo::Bar()`
   *
   * @param hasTarget true iff we has processed a real target of this call
   */
  public abstract void addFallbackTargets(@NotNull Val callValue,
                                          @NotNull Set<String> namespaceNames,
                                          @NotNull Set<String> subNames,
                                          @NotNull List<PerlValue> resolvedArguments,
                                          boolean hasTarget,
                                          @NotNull PerlOneOfValue.Builder builder,
                                          @NotNull PerlValue resolvedNamespaceValue,
                                          @NotNull PerlValueResolver resolver);

  public static @NotNull PerlCallValueBackendHelper<PerlCallValue> get(@NotNull PerlCallValue callValue) {
    return (PerlCallValueBackendHelper<PerlCallValue>)PerlValueBackendHelper.get(callValue);
  }
}
