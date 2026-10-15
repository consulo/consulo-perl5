package com.perl5.lang.perl.debugger.ui;

import com.perl5.lang.perl.debugger.PerlDebugThread;
import com.perl5.lang.perl.debugger.PerlRemoteFileSystem;
import com.perl5.lang.perl.debugger.protocol.PerlLoadedFileDescriptor;
import com.perl5.lang.perl.fileTypes.PerlFileTypeScript;
import consulo.application.Application;
import consulo.ide.impl.idea.ide.actions.OpenFileAction;
import consulo.project.Project;
import consulo.ui.Component;
import consulo.ui.ListBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.ComponentContainer;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import consulo.ui.layout.ScrollableLayout;
import consulo.ui.model.FlatDataModel;
import consulo.ui.model.MutableFlatDataModel;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import org.jspecify.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Unified UI replacement of the Swing {@link PerlScriptsPanel} (a JBList, not available in web): list of loaded sources / compiled
 * evals of the debugged script, double click opens the file. Keeps the upstream panel API ({@code add/remove/clear/bulkChange}).
 */
public class PerlUnifiedScriptsPanel implements ComponentContainer {
  private static final Comparator<PerlLoadedFileDescriptor> COMPARE_ENTRIES =
    (o1, o2) -> StringUtil.compare(o1.getPresentableName(), o2.getPresentableName(), false);

  private final Project myProject;
  private final PerlDebugThread myDebugThread;
  // sorted entries, model is replaced with them on change
  private final List<PerlLoadedFileDescriptor> myEntries = new ArrayList<>();
  private final Set<PerlLoadedFileDescriptor> myEntriesSet = new HashSet<>();
  private @Nullable MutableFlatDataModel<PerlLoadedFileDescriptor> myModel;
  private @Nullable Component myComponent;

  public PerlUnifiedScriptsPanel(Project project, PerlDebugThread debugThread) {
    myProject = project;
    myDebugThread = debugThread;
  }

  @RequiredUIAccess
  @Override
  public Component getUIComponent() {
    if (myComponent == null) {
      MutableFlatDataModel<PerlLoadedFileDescriptor> model = FlatDataModel.of(new ArrayList<>(myEntries));
      myModel = model;
      ListBox<PerlLoadedFileDescriptor> listBox = ListBox.create(model);
      listBox.setRenderer((presentation, item) -> {
        PerlLoadedFileDescriptor fileDescriptor = item.getValue();
        if (fileDescriptor == null) {
          return;
        }
        VirtualFile virtualFile = getVirtualFileByName(mapPathToLocal(fileDescriptor.getPath()));
        presentation.withIcon(virtualFile != null ? virtualFile.getFileType().getIcon() : PerlFileTypeScript.INSTANCE.getIcon());
        presentation.append(fileDescriptor.getPresentableName());
      });
      listBox.addDoubleClickListener(event -> openFile(event.getValue()));
      myComponent = ScrollableLayout.create(listBox);
    }
    return myComponent;
  }

  @RequiredUIAccess
  @Override
  public @Nullable Component getUIPreferredFocusableComponent() {
    return getUIComponent();
  }

  /**
   * consulo: desktop runner layouts take Swing components
   */
  @Override
  @RequiredUIAccess
  public JComponent getComponent() {
    return (JComponent)TargetAWT.to(getUIComponent());
  }

  @Override
  @RequiredUIAccess
  public JComponent getPreferredFocusableComponent() {
    return getComponent();
  }

  @Override
  public void dispose() {
  }

  private String mapPathToLocal(String remotePath) {
    return myDebugThread.getDebugProfileState().mapPathToLocal(remotePath);
  }

  private static @Nullable VirtualFile getVirtualFileByName(String virtualFileName) {
    VirtualFile result = VirtualFileUtil.findFileByIoFile(new File(virtualFileName), false);
    if (result != null) {
      return result;
    }
    return VirtualFileManager.getInstance().findFileByUrl(PerlRemoteFileSystem.PROTOCOL_PREFIX + virtualFileName);
  }

  @RequiredUIAccess
  private void openFile(@Nullable PerlLoadedFileDescriptor fileDescriptor) {
    if (fileDescriptor == null) {
      return;
    }
    String remotePath = fileDescriptor.getPath();
    VirtualFile selectedVirtualFile = getVirtualFileByName(mapPathToLocal(remotePath));
    if (selectedVirtualFile == null) {
      selectedVirtualFile = myDebugThread.loadRemoteSource(remotePath);
    }
    if (selectedVirtualFile != null) {
      OpenFileAction.openFile(selectedVirtualFile, myProject);
    }
  }

  public void clear() {
    Application.get().invokeLater(() -> {
      myEntries.clear();
      myEntriesSet.clear();
      updateModel();
    });
  }

  public void add(PerlLoadedFileDescriptor value) {
    bulkChange(Collections.singletonList(value), Collections.emptyList());
  }

  public void remove(PerlLoadedFileDescriptor value) {
    bulkChange(Collections.emptyList(), Collections.singletonList(value));
  }

  public void bulkChange(List<? extends PerlLoadedFileDescriptor> toAdd, List<? extends PerlLoadedFileDescriptor> toRemove) {
    List<PerlLoadedFileDescriptor> added = new ArrayList<>(toAdd);
    List<PerlLoadedFileDescriptor> removed = new ArrayList<>(toRemove);
    Application.get().invokeLater(() -> {
      boolean changed = false;
      for (PerlLoadedFileDescriptor value : removed) {
        if (myEntriesSet.remove(value)) {
          myEntries.remove(value);
          changed = true;
        }
      }
      for (PerlLoadedFileDescriptor value : added) {
        if (myEntriesSet.add(value)) {
          int index = Collections.binarySearch(myEntries, value, COMPARE_ENTRIES);
          myEntries.add(index < 0 ? -index - 1 : index, value);
          changed = true;
        }
      }
      if (changed) {
        updateModel();
      }
    });
  }

  @RequiredUIAccess
  private void updateModel() {
    if (myModel != null) {
      myModel.replaceAll(new ArrayList<>(myEntries));
    }
  }
}
