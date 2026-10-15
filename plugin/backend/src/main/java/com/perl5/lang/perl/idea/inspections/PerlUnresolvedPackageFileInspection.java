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

package com.perl5.lang.perl.idea.inspections;

import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.inspection.ProblemDescriptor;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.project.Project;
import consulo.content.bundle.Sdk;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElementVisitor;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.adapters.PackageManagerAdapter;
import com.perl5.lang.perl.adapters.PackageManagerAdapterFactory;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.psi.PerlNamespaceElement;
import com.perl5.lang.perl.psi.PerlVisitor;
import com.perl5.lang.perl.psi.PsiPerlRequireExpr;
import com.perl5.lang.perl.psi.impl.PerlFileImpl;
import com.perl5.lang.perl.psi.impl.PerlNoStatementElement;
import com.perl5.lang.perl.psi.impl.PerlUseStatementElement;
import com.perl5.lang.perl.util.PerlNamespaceUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.localize.LocalizeValue;
import com.perl5.lang.perl.PerlLanguage;
import org.jetbrains.annotations.Nullable;


@ExtensionImpl
public class PerlUnresolvedPackageFileInspection extends PerlInspection {
  @Override
  public @NotNull PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new PerlVisitor() {

      @Override
      public void visitUseStatement(@NotNull PerlUseStatementElement o) {
        if (o.getNamespaceElement() != null) {
          checkPackageFile(o.getNamespaceElement());
        }
      }

      @Override
      public void visitNoStatement(@NotNull PerlNoStatementElement o) {
        if (o.getNamespaceElement() != null) {
          checkPackageFile(o.getNamespaceElement());
        }
      }

      @Override
      public void visitRequireExpr(@NotNull PsiPerlRequireExpr o) {
        if (o.getNamespaceElement() != null) {
          checkPackageFile(o.getNamespaceElement());
        }
      }

      public void checkPackageFile(PerlNamespaceElement o) {
        List<PerlFileImpl> namespaceFiles = PerlNamespaceUtil.getNamespaceFiles(o);
        String packageName = o.getCanonicalName();

        if (namespaceFiles.isEmpty() && StringUtil.isNotEmpty(packageName)) {
          List<LocalQuickFix> fixes = new ArrayList<>();
          Project project = o.getProject();
          Sdk perlSdk = PerlProjectManager.getSdk(project);
          if (perlSdk != null) {
            fixes.add(new InstallPackageQuickfix(PackageManagerAdapterFactory.create(perlSdk, project), packageName));
          }

          registerProblem(holder, o,
                          PerlBundle.message("perl.inspection.missing.package.file", packageName),
                          fixes.toArray(LocalQuickFix.EMPTY_ARRAY));
        }
      }
    };
  }

  private record InstallPackageQuickfix(@SafeFieldForPreview @NotNull PackageManagerAdapter myAdapter, @NotNull String myPackageName)
    implements LocalQuickFix {

    // TODO consulo: Consulo QuickFix has no family name; kept for upstream parity (unused)
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getFamilyName() {
      return PerlBundle.message("perl.quickfix.install.family", myAdapter.getPresentableName());
    }

    @Override
    public @NotNull LocalizeValue getName() {
      return LocalizeValue.localizeTODO(PerlBundle.message("perl.quickfix.install.name", myPackageName, myAdapter.getPresentableName()));
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      myAdapter.queueInstall(myPackageName);
    }
  }

  // consulo: metadata from upstream <localInspection> registration in perl5.plugin.backend.main.xml
  @Override
  public @NotNull String getShortName() {
    return "PackageFileMissing";
  }

  @Override
  public @Nullable Language getLanguage() {
    return PerlLanguage.INSTANCE;
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.package.file.missing.display.name"));
  }

  @Override
  public @NotNull LocalizeValue getGroupDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.group.perl"));
  }

  @Override
  public @NotNull HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.WARNING;
  }
}
