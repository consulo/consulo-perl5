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

package com.perl5.lang.perl.extensions.generation;

import consulo.application.ApplicationManager;
import consulo.document.Document;
import consulo.codeEditor.Editor;
import consulo.codeEditor.ScrollType;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.project.Project;
import consulo.ui.InputBoxBuilder;
import consulo.localize.LocalizeValue;
import consulo.undoRedo.CommandProcessor;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.util.PsiTreeUtil;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.extensions.PerlCodeGenerator;
import com.perl5.lang.perl.idea.codeInsight.PerlMethodMember;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValue;
import com.perl5.lang.perl.parser.PerlParserUtil;
import com.perl5.lang.perl.psi.PerlMethodDefinition;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.psi.PerlSubDefinitionElement;
import com.perl5.lang.perl.psi.PerlSubElement;
import com.perl5.lang.perl.psi.utils.PerlElementFactory;
import com.perl5.lang.perl.psi.utils.PerlSubAnnotations;
import com.perl5.lang.perl.psi.utils.PerlSubArgument;
import com.perl5.lang.perl.util.PerlPackageUtil;
import consulo.perl5.core.ui.PerlDialogUtil;
import consulo.perl5.core.ui.PerlUnifiedMemberChooser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;


public class PerlCodeGeneratorImpl implements PerlCodeGenerator {
  private static UnaryOperator<List<PerlMethodMember>> ourTestChooser = null;

  @Override
  public @Nullable String getOverrideCodeText(PsiElement subBase) {
    if (subBase instanceof PerlSubElement perlSubBase) {
      StringBuilder code = new StringBuilder();
      code.append("#@override\n");

      PerlSubAnnotations annotations = perlSubBase.getAnnotations();
      if (annotations != null) {
        if (annotations.isDeprecated()) {
          code.append("#@deprecated\n");
        }
        if (annotations.isAbstract()) {
          code.append("#@abstract\n");
        }
        if (annotations.isMethod() || subBase instanceof PerlMethodDefinition) {
          code.append("#@method\n");
        }
        PerlValue returnValue = annotations.getReturnValue();
        if (!returnValue.isUnknown()) {
          code.append("#@returns ");
          code.append(returnValue.toCode());
          code.append("\n");
        }
      }

      code.append("sub ");
      code.append(perlSubBase.getSubName());
      code.append("{\n");

      List<String> superArgs = new ArrayList<>();
      List<PerlSubArgument> arguments = Collections.emptyList();

      if (perlSubBase instanceof PerlSubDefinitionElement subDefinitionElement) {
        //noinspection unchecked
        arguments = subDefinitionElement.getSubArgumentsList();

        if (!arguments.isEmpty()) {
          boolean useShift = false;

          for (PerlSubArgument argument : arguments) {
            if (StringUtil.isNotEmpty(argument.getVariableClass())) {
              useShift = true;
              break;
            }
          }

          if (useShift) {
            for (PerlSubArgument argument : arguments) {
              if (!argument.isEmpty()) {
                code.append("my ");
                code.append(argument.getVariableClass());
                code.append(" ");
                String superArg = argument.toStringShort();
                superArgs.add(superArg);
                code.append(superArg);
                code.append(" = ");
              }
              code.append("shift;\n");
            }
          }
          else {
            code.append("my ");
            code.append('(');
            boolean insertComma = false;
            for (PerlSubArgument argument : arguments) {
              if (insertComma) {
                code.append(", ");
              }
              else {
                insertComma = true;
              }

              String superArg = argument.toStringShort();
              superArgs.add(superArg);
              code.append(superArg);
            }
            code.append(") = @_;\n");
          }
        }
        else {
          code.append("my ($self) = @_;\n");
        }
      }

      if (!superArgs.isEmpty()) {
        superArgs.removeFirst();
      }

      if (!arguments.isEmpty() && !arguments.getFirst().isEmpty()) {
        //noinspection StringConcatenationInsideStringBufferAppend
        code.append(
          arguments.getFirst().toStringShort() + "->SUPER::" + perlSubBase.getSubName() + "(" + StringUtil.join(superArgs, ", ") + ");\n");
      }
      code.append("}");
      return code.toString();
    }
    return null;
  }

  @Override
  public void generateOverrideMethod(PsiElement anchor, Editor editor) {
    if (anchor != null) {
      final List<PerlMethodMember> subDefinitions = new ArrayList<>();

      PerlPackageUtil.processNotOverridedMethods(
        PsiTreeUtil.getParentOfType(anchor, PerlNamespaceDefinitionElement.class),
        subDefinitionBase ->
        {
          subDefinitions.add(new PerlMethodMember(subDefinitionBase));
          return true;
        }
      );

      // consulo: the members are chosen asynchronously, see getMembersToOverride
      getMembersToOverride(anchor, editor, subDefinitions, selectedElements -> {
        StringBuilder generatedCode = new StringBuilder();
        for (PerlMethodMember methodMember : selectedElements) {
          String code = getOverrideCodeText(methodMember.getPsiElement());
          if (StringUtil.isNotEmpty(code)) {
            generatedCode.append(code);
            generatedCode.append("\n\n");
          }
        }

        insertCodeAfterElement(anchor, generatedCode.toString(), editor);
      });
    }
  }

  /**
   * consulo: MemberChooser is a Swing dialog (not available in web), the unified chooser is asynchronous. The chosen members are
   * passed to the {@code membersConsumer} after the override action command is finished, so the consumer runs in its own command
   */
  private void getMembersToOverride(@NotNull PsiElement anchor,
                                    @NotNull Editor editor,
                                    @NotNull List<PerlMethodMember> subDefinitions,
                                    @NotNull Consumer<? super List<PerlMethodMember>> membersConsumer) {
    if (ourTestChooser != null) {
      List<PerlMethodMember> selectedElements = ourTestChooser.apply(subDefinitions);
      if (selectedElements != null) {
        membersConsumer.accept(selectedElements);
      }
      return;
    }

    Project project = anchor.getProject();
    LocalizeValue title = LocalizeValue.of(PerlBundle.message("dialog.title.override.implement.method"));
    PerlDialogUtil.showAndThen(
      () -> PerlUnifiedMemberChooser.showAsync(project, title, subDefinitions),
      selectedElements -> {
        if (selectedElements == null || !anchor.isValid() || editor.isDisposed()) {
          return;
        }
        CommandProcessor.getInstance().newCommand()
          .project(project)
          .name(title)
          .run(() -> membersConsumer.accept(selectedElements));
      });
  }

  // consulo: the names are asked asynchronously, see askFieldsNames
  @Override
  public void generateSetters(PsiElement anchor, Editor editor) {
    askFieldsNames(anchor, PerlBundle.message("dialog.message.type.comma.separated.setters.names"),
                   PerlBundle.message("dialog.title.generating.setters"), names -> {
      StringBuilder code = new StringBuilder();

      for (String name : names) {
        code.append(getSetterCode(name));
      }

      if (!code.isEmpty()) {
        insertCodeAfterElement(anchor, code.toString(), editor);
      }
    });
  }

  @Override
  public void generateGetters(PsiElement anchor, Editor editor) {
    askFieldsNames(anchor, PerlBundle.message("dialog.message.type.comma.separated.getters.names"),
                   PerlBundle.message("dialog.title.generating.getters"), names -> {
      StringBuilder code = new StringBuilder();

      for (String name : names) {
        code.append(getGetterCode(name));
      }

      if (!code.isEmpty()) {
        insertCodeAfterElement(anchor, code.toString(), editor);
      }
    });
  }

  @Override
  public void generateGettersAndSetters(PsiElement anchor, Editor editor) {
    askFieldsNames(anchor, PerlBundle.message("dialog.message.type.comma.separated.accessors.names"),
                   PerlBundle.message("dialog.title.generating.getters.setters"), names -> {
      StringBuilder code = new StringBuilder();

      for (String name : names) {
        code.append(getGetterCode(name));
        code.append(getSetterCode(name));
      }

      if (!code.isEmpty()) {
        insertCodeAfterElement(anchor, code.toString(), editor);
      }
    });
  }

  @Override
  public void generateConstructor(PsiElement anchor, Editor editor) {
    insertCodeAfterElement(anchor, getConstructorCode(), editor);
  }

  /**
   * consulo: Messages are Swing dialogs (not available in web), the unified input box is asynchronous. The names are passed
   * to the {@code namesConsumer} when the box is confirmed with some, after the generate action command is finished, so the
   * consumer runs in its own command
   */
  protected void askFieldsNames(@NotNull PsiElement anchor,
                                String promptText,
                                String promptTitle,
                                @NotNull Consumer<? super List<String>> namesConsumer
  ) {
    Project project = anchor.getProject();
    InputBoxBuilder.text()
      .title(LocalizeValue.of(promptTitle))
      .text(LocalizeValue.of(promptText))
      .asQuestion()
      .showAsync(project)
      .whenComplete((name, error) -> {
        Set<String> result = new HashSet<>();

        if (!StringUtil.isEmpty(name)) {

          for (String nameChunk : name.split("[ ,]+")) {
            if (!nameChunk.isEmpty() && PerlParserUtil.isIdentifier(nameChunk)) {
              result.add(nameChunk);
            }
          }
        }

        if (result.isEmpty() || !anchor.isValid()) {
          return;
        }
        CommandProcessor.getInstance().newCommand()
          .project(project)
          .name(LocalizeValue.of(promptTitle))
          .run(() -> namesConsumer.accept(new ArrayList<>(result)));
      });
  }

  protected void insertCodeAfterElement(PsiElement anchor, String code, Editor editor) {
    ApplicationManager.getApplication().runWriteAction(() ->
                                                       {
                                                         FileType fileType = anchor.getContainingFile().getFileType();
                                                         final PsiDocumentManager manager =
                                                           PsiDocumentManager.getInstance(anchor.getProject());
                                                         final Document document = manager.getDocument(anchor.getContainingFile());

                                                         if (!code.isEmpty() && document != null) {
                                                           manager.doPostponedOperationsAndUnblockDocument(document);

                                                           PsiFile newFile =
                                                             PerlElementFactory.createFile(anchor.getProject(), "\n" + code, fileType);
                                                           PsiElement container = anchor.getParent();
                                                           int newOffset = anchor.getTextOffset() + anchor.getTextLength();

                                                           if (newFile.getFirstChild() != null && newFile.getLastChild() != null) {
                                                             container
                                                               .addRangeAfter(newFile.getFirstChild(), newFile.getLastChild(), anchor);
                                                           }

                                                           manager.commitDocument(document);
                                                           editor.getCaretModel().moveToOffset(newOffset);
                                                           editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                                                         }
                                                       });
  }

  public static String getGetterCode(String name) {
    return "sub get_" + name + "\n" +
           "{\n" +
           "	return $_[0]->{" + name + "};\n" +
           "}\n";
  }

  public static String getSetterCode(String name) {
    return "sub set_" + name + "\n" +
           "{\n" +
           "	my ($self, $new_value) = @_;\n" +
           "	$$self{" + name + "} = $new_value;\n" +
           "	return $self;\n" +
           "}\n";
  }

  @SuppressWarnings("SameReturnValue")
  public static String getConstructorCode() {
    return """
      
      sub new
      {
        my ($proto) = @_;
        my $self = bless {}, $proto;
        return $self;
      }
      
      """;
  }

  @TestOnly
  public static void withChooser(@NotNull UnaryOperator<List<PerlMethodMember>> testChooser, @NotNull Runnable runnable) {
    ourTestChooser = testChooser;
    try {
      runnable.run();
    }
    finally {
      ourTestChooser = null;
    }
  }
}
