package consulo.perl5.core.ui;

import consulo.application.Application;
import consulo.disposer.Disposable;
import consulo.language.editor.generation.ClassMember;
import consulo.language.editor.generation.MemberChooserObject;
import consulo.language.editor.generation.PsiElementMemberChooserObject;
import consulo.language.icon.IconDescriptorUpdaters;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.Component;
import consulo.ui.MultiSelectListBox;
import consulo.ui.TextAttribute;
import consulo.ui.WidthAndHeight;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.dialog.DialogDescriptor;
import consulo.ui.ex.dialog.DialogValue;
import consulo.ui.image.Image;
import consulo.ui.layout.ScrollableLayout;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Unified UI (works in desktop and web) counterpart of the Swing {@code MemberChooser} (and of {@code MemberChooserBuilder}, which
 * has no web implementation): several members are picked from a list, each one shown with the container it comes from.
 */
public final class PerlUnifiedMemberChooser<T extends ClassMember> extends DialogDescriptor {
  private record Item<T>(T member, String text, String containerText, @Nullable Image icon) {
  }

  private record Selection<T>(List<T> members) implements DialogValue {
  }

  private final List<Item<T>> myItems;

  private @Nullable MultiSelectListBox<Item<T>> myList;

  private PerlUnifiedMemberChooser(LocalizeValue title, List<Item<T>> items) {
    super(title);
    myItems = items;
  }

  /**
   * Asks the user to choose some of the {@code members}, the first one is chosen initially.
   *
   * @return the chosen members, never empty; a dismissal fails the result with {@link consulo.ui.DialogCancelledException}
   */
  @RequiredUIAccess
  @SuppressWarnings("unchecked")
  public static <T extends ClassMember> CompletableFuture<List<T>> showAsync(Project project, LocalizeValue title, List<T> members) {
    List<Item<T>> items = Application.get().runReadAction((Supplier<List<Item<T>>>)() -> {
      List<Item<T>> result = new ArrayList<>(members.size());
      for (T member : members) {
        MemberChooserObject container = member.getParentNodeDelegate();
        Image icon = member instanceof PsiElementMemberChooserObject psiMember
                     ? IconDescriptorUpdaters.getIcon(psiMember.getPsiElement(), 0)
                     : null;
        result.add(new Item<>(member, member.getText(), container == null ? "" : container.getText(), icon));
      }
      return result;
    });

    PerlUnifiedMemberChooser<T> chooser = new PerlUnifiedMemberChooser<>(title, items);
    return PerlDialogUtil.showAsync(project, chooser).thenApply(value -> ((Selection<T>)value).members());
  }

  @RequiredUIAccess
  @Override
  public Component createCenterComponent(Disposable uiDisposable) {
    MultiSelectListBox<Item<T>> list = MultiSelectListBox.create(myItems);
    list.setRenderer((presentation, renderItem) -> {
      Item<T> item = renderItem.getValue();
      if (item == null) {
        return;
      }
      presentation.withIcon(item.icon());
      presentation.append(item.text());
      if (!item.containerText().isEmpty()) {
        presentation.append("  " + item.containerText(), TextAttribute.GRAYED);
      }
    });
    list.setSpeedSearchConverter(Item::text);
    if (!myItems.isEmpty()) {
      list.setValue(List.of(myItems.getFirst()));
    }
    list.addValueListener(event -> updateOkButtonState());
    myList = list;
    return ScrollableLayout.create(list);
  }

  @Override
  public @Nullable WidthAndHeight getInitialSize() {
    return WidthAndHeight.ofFont(30, 35);
  }

  @Override
  public boolean doUpdateOkButtonState() {
    return !getSelectedMembers().isEmpty();
  }

  @Override
  public DialogValue getOkValue() {
    return new Selection<>(getSelectedMembers());
  }

  @RequiredUIAccess
  @Override
  public @Nullable Component getPreferredFocusedComponent() {
    return myList;
  }

  private List<T> getSelectedMembers() {
    List<Item<T>> selected = myList == null ? null : myList.getValue();
    if (selected == null) {
      return List.of();
    }
    List<T> members = new ArrayList<>(selected.size());
    for (Item<T> item : selected) {
      members.add(item.member());
    }
    return members;
  }
}
