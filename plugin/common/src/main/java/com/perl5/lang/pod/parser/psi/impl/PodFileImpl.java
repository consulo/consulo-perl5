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

package com.perl5.lang.pod.parser.psi.impl;

import consulo.language.impl.psi.PsiFileBase;
import consulo.navigation.ItemPresentation;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.util.lang.ObjectUtil;
import consulo.language.icon.IconDescriptorUpdaters;
import consulo.ui.image.Image;
import com.perl5.lang.perl.util.PerlPackageService;
import com.perl5.lang.pod.PodLanguage;
import com.perl5.lang.pod.filetypes.PodFileType;
import com.perl5.lang.pod.parser.psi.PodFile;
import com.perl5.lang.pod.parser.psi.PodRenderingContext;
import com.perl5.lang.pod.parser.psi.PodTitledSection;
import com.perl5.lang.pod.parser.psi.util.PodRenderUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PodFileImpl extends PsiFileBase implements PodFile {
  public PodFileImpl(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, PodLanguage.INSTANCE);
  }

  @Override
  public @NotNull FileType getFileType() {
    return PodFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return "POD file";
  }

  // TODO consulo: PsiElement#getIcon(int) does not exist in Consulo (element icons come from IconDescriptorUpdaters/IconDescriptorUpdater),
  //  so this is no longer an override and is not called by the platform; base-file icon delegation needs an IconDescriptorUpdater
  public Image getIcon(int flags) {
    PsiFile baseFile = getViewProvider().getStubBindingRoot();
    return baseFile == this ? IconDescriptorUpdaters.getIcon(this, flags) : IconDescriptorUpdaters.getIcon(baseFile, flags);
  }

  @Override
  public void renderElementAsHTML(StringBuilder builder, PodRenderingContext context) {
    PodRenderUtil.renderPsiRangeAsHTML(getFirstNamedBlock(), null, builder, context);
  }

  public @Nullable PsiElement getFirstNamedBlock() {
    return findChildByClass(PodTitledSection.class);
  }

  @Override
  public void renderElementAsText(StringBuilder builder, PodRenderingContext context) {
    PodRenderUtil.renderPsiRangeAsText(getFirstNamedBlock(), null, builder, context);
  }

  @Override
  public boolean isIndexed() {
    return false;
  }

  @Override
  public int getListLevel() {
    return 0;
  }

  @Override
  public ItemPresentation getPresentation() {
    return this;
  }

  @Override
  public @Nullable String getPresentableText() {
    String packageName = getNormalizedPackageName();
    return packageName == null ? getName() : packageName;
  }

  /**
   * Returns POD file normalized package name (with optionally trimmed {@code pod::})
   *
   * @return POD package name or null if this is a script file file or smth
   */
  private @Nullable String getNormalizedPackageName() {
    String packageName = PerlPackageService.getInstance().getFilePackageName(this);
    if (StringUtil.isEmpty(packageName)) {
      return null;
    }
    return StringUtil.trimStart(packageName, "pod::");
  }

  @Override
  public @Nullable String getLocationString() {
    final PsiDirectory psiDirectory = getParent();
    if (psiDirectory != null) {
      return psiDirectory.getVirtualFile().getPresentableUrl();
    }
    return null;
  }

  @Override
  public @Nullable Image getIcon(boolean unused) {
    return getFileType().getIcon();
  }

  @Override
  public @Nullable String getPodLink() {
    String normalizedPackageName = getNormalizedPackageName();
    if (StringUtil.isNotEmpty(normalizedPackageName)) {
      return normalizedPackageName;
    }
    return null;
  }

  @Override
  public @Nullable String getPodLinkText() {
    String normalizedPackageName = getNormalizedPackageName();
    if (StringUtil.isNotEmpty(normalizedPackageName)) {
      return normalizedPackageName;
    }
    return ObjectUtil.doIfNotNull(getVirtualFile(), VirtualFile::getName);
  }
}