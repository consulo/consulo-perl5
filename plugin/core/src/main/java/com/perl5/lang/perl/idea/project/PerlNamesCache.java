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

package com.perl5.lang.perl.idea.project;

import com.perl5.lang.perl.psi.stubs.namespaces.PerlLightNamespaceIndex;
import com.perl5.lang.perl.psi.stubs.namespaces.PerlNamespaceIndex;
import com.perl5.lang.perl.psi.stubs.subsdeclarations.PerlSubDeclarationIndex;
import com.perl5.lang.perl.psi.stubs.subsdefinitions.PerlLightSubDefinitionsIndex;
import com.perl5.lang.perl.psi.stubs.subsdefinitions.PerlSubDefinitionsIndex;
import com.perl5.lang.perl.util.PerlPackageUtil;
import com.perl5.lang.perl.util.PerlTimeLogger;
import consulo.application.ReadAction;
import consulo.application.progress.ProgressManager;
import consulo.component.messagebus.MessageBusConnection;
import consulo.disposer.Disposable;
import consulo.language.psi.PsiManager;
import consulo.language.psi.event.PsiTreeChangeAdapter;
import consulo.language.psi.event.PsiTreeChangeEvent;
import consulo.logging.Logger;
import consulo.module.content.layer.event.ModuleRootEvent;
import consulo.module.content.layer.event.ModuleRootListener;
import consulo.project.DumbService;
import consulo.project.Project;
import consulo.project.event.DumbModeListener;
import consulo.ui.UIAccess;
import consulo.ui.ex.awt.util.MergingUpdateQueue;
import consulo.ui.ex.awt.util.Update;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;


public class PerlNamesCache implements Disposable {
  private static final Logger LOG = Logger.getInstance(PerlNamesCache.class);
  private final MergingUpdateQueue myQueue = new MergingUpdateQueue("Perl names cache updater", 1000, true, null, this, null, false);
  private final Project myProject;
  private final AtomicBoolean myIsUpdating = new AtomicBoolean(false);
  private volatile Set<String> myKnownSubs = Collections.emptySet();
  private volatile Set<String> myKnownNamespaces = Collections.emptySet();

  public PerlNamesCache(Project project) {
    myProject = project;
    if (project.isDefault()) {
      return;
    }
    MessageBusConnection connection = project.getMessageBus().connect(this);
    connection.subscribe(ModuleRootListener.class, new ModuleRootListener() {
      @Override
      public void rootsChanged(@NotNull ModuleRootEvent event) {
        queueUpdate();
      }
    });
    connection.subscribe(DumbModeListener.class, new DumbModeListener() {
      @Override
      public void exitDumbMode() {
        queueUpdate();
      }
    });
    PsiManager.getInstance(myProject).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
      @Override
      public void childAdded(@NotNull PsiTreeChangeEvent event) {
        queueUpdate();
      }

      @Override
      public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        queueUpdate();
      }

      @Override
      public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        queueUpdate();
      }

      @Override
      public void childMoved(@NotNull PsiTreeChangeEvent event) {
        queueUpdate();
      }

      @Override
      public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
        queueUpdate();
      }

      @Override
      public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
        queueUpdate();
      }
    }, this);
  }

  private void queueUpdate() {
    if (!myProject.isDefault()) {
      myQueue.queue(Update.create(this, this::doUpdateSingleThread));
    }
  }

  private void doUpdateSingleThread() {
    if (!DumbService.isDumb(myProject) && myIsUpdating.compareAndSet(false, true)) {
      try {
        doUpdateCache();
      }
      finally {
        myIsUpdating.set(false);
      }
    }
    else {
      queueUpdate();
    }
  }

  private void doUpdateCache() {
    ReadAction.nonBlocking(() -> {
      PerlTimeLogger logger = PerlTimeLogger.create(LOG);
      logger.debug("Starting to update names cache at");

      PerlSubDeclarationIndex subDeclarationIndex = PerlSubDeclarationIndex.getInstance();
      Collection<String> declarationsNames = subDeclarationIndex.getAllNames(myProject);
      Set<String> subsSet = new HashSet<>(declarationsNames);
      logger.debug("Got declarations names: ", declarationsNames.size());
      ProgressManager.checkCanceled();

      PerlSubDefinitionsIndex subDefinitionsIndex = PerlSubDefinitionsIndex.getInstance();
      Collection<String> definitionsNames = subDefinitionsIndex.getAllNames(myProject);
      subsSet.addAll(definitionsNames);
      logger.debug("Got definitions names: ", definitionsNames.size());
      ProgressManager.checkCanceled();

      PerlLightSubDefinitionsIndex lightSubDefinitionsIndex = PerlLightSubDefinitionsIndex.getInstance();
      Collection<String> lightDefinitionsNames = lightSubDefinitionsIndex.getAllNames(myProject);
      subsSet.addAll(lightDefinitionsNames);
      logger.debug("Got light definitions names: ", lightDefinitionsNames.size());
      ProgressManager.checkCanceled();
      myKnownSubs = Collections.unmodifiableSet(subsSet);

      Set<String> namespacesSet = new HashSet<>(PerlPackageUtil.CORE_PACKAGES_ALL);

      PerlNamespaceIndex namespaceIndex = PerlNamespaceIndex.getInstance();
      Collection<String> namespacesNames = namespaceIndex.getAllNames(myProject);
      namespacesSet.addAll(namespacesNames);
      logger.debug("Got namespaces names: ", namespacesNames.size());
      ProgressManager.checkCanceled();

      PerlLightNamespaceIndex lightNamespaceIndex = PerlLightNamespaceIndex.getInstance();
      Collection<String> lightNamespacesNames = lightNamespaceIndex.getAllNames(myProject);
      namespacesSet.addAll(lightNamespacesNames);
      logger.debug("Got light namespaces names: ", lightNamespacesNames.size());
      myKnownNamespaces = Collections.unmodifiableSet(namespacesSet);

      logger.debug("Names cache updated");
      return null;
    }).inSmartMode(myProject).expireWhen(myProject::isDisposed).executeSynchronously();
  }

  public void forceCacheUpdate() {
    UIAccess.assetIsNotUIThread();
    doUpdateCache();
  }

  @TestOnly
  public void cleanCache() {
    myKnownSubs = Collections.emptySet();
    myKnownNamespaces = Collections.emptySet();
  }

  @Override
  public void dispose() {
  }

  public Set<String> getSubsNamesSet() {
    return myKnownSubs;
  }

  public Set<String> getNamespacesNamesSet() {
    return myKnownNamespaces;
  }

  public static @NotNull PerlNamesCache getInstance(@NotNull Project project) {
    return project.getInstance(PerlNamesCache.class);
  }
}