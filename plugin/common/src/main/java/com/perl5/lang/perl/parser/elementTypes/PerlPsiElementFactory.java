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

package com.perl5.lang.perl.parser.elementTypes;

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.ast.IElementType;
import com.perl5.lang.perl.parser.moose.psi.impl.*;
import com.perl5.lang.perl.psi.impl.*;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static com.perl5.lang.perl.idea.formatter.PerlFormattingTokenSets.FOR_OR_FOREACH;
import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.*;
import static com.perl5.lang.perl.parser.moose.MooseElementTypes.*;

public final class PerlPsiElementFactory {
  public static final PerlPsiElementFactory INSTANCE = new PerlPsiElementFactory();

  private static final Map<Object, Function<ASTNode, PsiElement>> factories = new LinkedHashMap<>();

  static {
    factories.put(ADD_EXPR, PsiPerlAddExprImpl::new);
    factories.put(AFTER_MODIFIER, PsiPerlAfterModifierImpl::new);
    factories.put(AND_EXPR, PsiPerlAndExprImpl::new);
    factories.put(ANNOTATION_ABSTRACT, PsiPerlAnnotationAbstractImpl::new);
    factories.put(ANNOTATION_DEPRECATED, PsiPerlAnnotationDeprecatedImpl::new);
    factories.put(ANNOTATION_INJECT, PsiPerlAnnotationInjectImpl::new);
    factories.put(ANNOTATION_METHOD, PsiPerlAnnotationMethodImpl::new);
    factories.put(ANNOTATION_NOINSPECTION, PsiPerlAnnotationNoinspectionImpl::new);
    factories.put(ANNOTATION_NO_INJECT, PsiPerlAnnotationNoInjectImpl::new);
    factories.put(ANNOTATION_OVERRIDE, PsiPerlAnnotationOverrideImpl::new);
    factories.put(ANNOTATION_RETURNS, PsiPerlAnnotationReturnsImpl::new);
    factories.put(ANNOTATION_TYPE, PsiPerlAnnotationTypeImpl::new);
    factories.put(ANNOTATION_VARIABLE, PsiPerlAnnotationVariableImpl::new);
    factories.put(ANON_ARRAY, PsiPerlAnonArrayImpl::new);
    factories.put(ANON_HASH, PsiPerlAnonHashImpl::new);
    factories.put(AROUND_MODIFIER, PsiPerlAroundModifierImpl::new);
    factories.put(AROUND_SIGNATURE_INVOCANTS, PsiPerlAroundSignatureInvocantsImpl::new);
    factories.put(ARRAYREF_TYPE, PsiPerlArrayrefTypeImpl::new);
    factories.put(ARRAY_CAST_EXPR, PsiPerlArrayCastExprImpl::new);
    factories.put(ARRAY_ELEMENT, PsiPerlArrayElementImpl::new);
    factories.put(ARRAY_INDEX, PsiPerlArrayIndexImpl::new);
    factories.put(ARRAY_INDEX_VARIABLE, PsiPerlArrayIndexVariableImpl::new);
    factories.put(ARRAY_POP_EXPR, PsiPerlArrayPopExprImpl::new);
    factories.put(ARRAY_PUSH_EXPR, PsiPerlArrayPushExprImpl::new);
    factories.put(ARRAY_SHIFT_EXPR, PsiPerlArrayShiftExprImpl::new);
    factories.put(ARRAY_SLICE, PsiPerlArraySliceImpl::new);
    factories.put(ARRAY_UNSHIFT_EXPR, PsiPerlArrayUnshiftExprImpl::new);
    factories.put(ARRAY_VARIABLE, PsiPerlArrayVariableImpl::new);
    factories.put(ASSIGN_EXPR, PsiPerlAssignExprImpl::new);
    factories.put(ATTRIBUTE, PsiPerlAttributeImpl::new);
    factories.put(ATTRIBUTES, PsiPerlAttributesImpl::new);
    factories.put(AUGMENT_MODIFIER, PsiPerlAugmentModifierImpl::new);
    factories.put(BEFORE_MODIFIER, PsiPerlBeforeModifierImpl::new);
    factories.put(BITWISE_AND_EXPR, PsiPerlBitwiseAndExprImpl::new);
    factories.put(BITWISE_OR_XOR_EXPR, PsiPerlBitwiseOrXorExprImpl::new);
    factories.put(BLESS_EXPR, PsiPerlBlessExprImpl::new);
    factories.put(BLOCK, PsiPerlBlockImpl::new);
    factories.put(BLOCK_ARRAY, PsiPerlBlockArrayImpl::new);
    factories.put(BLOCK_BRACELESS, PsiPerlBlockBracelessImpl::new);
    factories.put(BLOCK_CODE, PsiPerlBlockCodeImpl::new);
    factories.put(BLOCK_COMPOUND, PsiPerlBlockCompoundImpl::new);
    factories.put(BLOCK_GLOB, PsiPerlBlockGlobImpl::new);
    factories.put(BLOCK_HASH, PsiPerlBlockHashImpl::new);
    factories.put(BLOCK_SCALAR, PsiPerlBlockScalarImpl::new);
    factories.put(CALL_ARGUMENTS, PsiPerlCallArgumentsImpl::new);
    factories.put(CASE_COMPOUND, PsiPerlCaseCompoundImpl::new);
    factories.put(CASE_CONDITION, PsiPerlCaseConditionImpl::new);
    factories.put(CASE_DEFAULT, PsiPerlCaseDefaultImpl::new);
    factories.put(CATCH_CONDITION, PsiPerlCatchConditionImpl::new);
    factories.put(CATCH_EXPR, PsiPerlCatchExprImpl::new);
    factories.put(CODE_CAST_EXPR, PsiPerlCodeCastExprImpl::new);
    factories.put(CODE_VARIABLE, PsiPerlCodeVariableImpl::new);
    factories.put(COMMA_SEQUENCE_EXPR, PsiPerlCommaSequenceExprImpl::new);
    factories.put(COMMENT_ANNOTATION, PerlAnnotationContainerImpl::new);
    factories.put(COMPARE_EXPR, PsiPerlCompareExprImpl::new);
    factories.put(COMPILE_REGEX, PsiPerlCompileRegexImpl::new);
    factories.put(CONDITIONAL_BLOCK, PsiPerlConditionalBlockImpl::new);
    factories.put(CONDITION_EXPR, PsiPerlConditionExprImpl::new);
    factories.put(CONTINUATION_EXPR, PsiPerlContinuationExprImpl::new);
    factories.put(CONTINUE_BLOCK, PsiPerlContinueBlockImpl::new);
    factories.put(CONTINUE_EXPR, PsiPerlContinueExprImpl::new);
    factories.put(CUSTOM_ATOM_EXPR, PsiPerlCustomAtomExprImpl::new);
    factories.put(DEFAULT_COMPOUND, PsiPerlDefaultCompoundImpl::new);
    factories.put(DEFINED_EXPR, PsiPerlDefinedExprImpl::new);
    factories.put(DELETE_EXPR, PsiPerlDeleteExprImpl::new);
    factories.put(DEREF_EXPR, PsiPerlDerefExprImpl::new);
    factories.put(DO_BLOCK_EXPR, PsiPerlDoBlockExprImpl::new);
    factories.put(EACH_EXPR, PsiPerlEachExprImpl::new);
    factories.put(EQUAL_EXPR, PsiPerlEqualExprImpl::new);
    factories.put(ESC_CHAR, PsiPerlEscCharImpl::new);
    factories.put(EVAL_EXPR, PsiPerlEvalExprImpl::new);
    factories.put(EXCEPT_EXPR, PsiPerlExceptExprImpl::new);
    factories.put(EXIT_EXPR, PsiPerlExitExprImpl::new);
    factories.put(EXPR, PerlCompositeElementImpl::new);
    factories.put(FILE_GLOB_EXPR, PsiPerlFileGlobExprImpl::new);
    factories.put(FILE_READ_EXPR, PsiPerlFileReadExprImpl::new);
    factories.put(FINALLY_EXPR, PsiPerlFinallyExprImpl::new);
    factories.put(FLIPFLOP_EXPR, PsiPerlFlipflopExprImpl::new);
    factories.put(FOREACH_COMPOUND, PsiPerlForeachCompoundImpl::new);
    factories.put(FOREACH_ITERATOR, PsiPerlForeachIteratorImpl::new);
    factories.put(FORMAT_DEFINITION, PsiPerlFormatDefinitionImpl::new);
    factories.put(FOR_COMPOUND, PsiPerlForCompoundImpl::new);
    factories.put(FOR_CONDITION, PsiPerlForConditionImpl::new);
    factories.put(FOR_INIT, PsiPerlForInitImpl::new);
    factories.put(FOR_MUTATOR, PsiPerlForMutatorImpl::new);
    factories.put(FOR_OR_FOREACH, PerlCompositeElementImpl::new);
    factories.put(FOR_STATEMENT_MODIFIER, PsiPerlForStatementModifierImpl::new);
    factories.put(FUN_EXPR, PsiPerlFunExprImpl::new);
    factories.put(GIVEN_COMPOUND, PsiPerlGivenCompoundImpl::new);
    factories.put(GLOB_CAST_EXPR, PsiPerlGlobCastExprImpl::new);
    factories.put(GLOB_SLOT, PsiPerlGlobSlotImpl::new);
    factories.put(GOTO_EXPR, PsiPerlGotoExprImpl::new);
    factories.put(GREP_EXPR, PsiPerlGrepExprImpl::new);
    factories.put(HASHREF_TYPE, PsiPerlHashrefTypeImpl::new);
    factories.put(HASH_ARRAY_SLICE, PsiPerlHashArraySliceImpl::new);
    factories.put(HASH_CAST_EXPR, PsiPerlHashCastExprImpl::new);
    factories.put(HASH_ELEMENT, PsiPerlHashElementImpl::new);
    factories.put(HASH_HASH_SLICE, PsiPerlHashHashSliceImpl::new);
    factories.put(HASH_INDEX, PsiPerlHashIndexImpl::new);
    factories.put(HASH_SLICE, PsiPerlHashSliceImpl::new);
    factories.put(HASH_VARIABLE, PsiPerlHashVariableImpl::new);
    factories.put(HEREDOC, PerlHeredocElementImpl::new);
    factories.put(HEREDOC_OPENER, PsiPerlHeredocOpenerImpl::new);
    factories.put(HEREDOC_QQ, PerlHeredocElementImpl::new);
    factories.put(HEREDOC_QX, PerlHeredocElementImpl::new);
    factories.put(HEX_CHAR, PsiPerlHexCharImpl::new);
    factories.put(IF_COMPOUND, PsiPerlIfCompoundImpl::new);
    factories.put(IF_STATEMENT_MODIFIER, PsiPerlIfStatementModifierImpl::new);
    factories.put(ISA_EXPR, PsiPerlIsaExprImpl::new);
    factories.put(KEYS_EXPR, PsiPerlKeysExprImpl::new);
    factories.put(LABEL_DECLARATION, PsiPerlLabelDeclarationImpl::new);
    factories.put(LABEL_EXPR, PsiPerlLabelExprImpl::new);
    factories.put(LAST_EXPR, PsiPerlLastExprImpl::new);
    factories.put(LP_AND_EXPR, PsiPerlLpAndExprImpl::new);
    factories.put(LP_NOT_EXPR, PsiPerlLpNotExprImpl::new);
    factories.put(LP_OR_XOR_EXPR, PsiPerlLpOrXorExprImpl::new);
    factories.put(MAP_EXPR, PsiPerlMapExprImpl::new);
    factories.put(MATCH_REGEX, PsiPerlMatchRegexImpl::new);
    factories.put(METHOD, PsiPerlMethodImpl::new);
    factories.put(METHOD_EXPR, PsiPerlMethodExprImpl::new);
    factories.put(METHOD_SIGNATURE_INVOCANT, PsiPerlMethodSignatureInvocantImpl::new);
    factories.put(MOOSE_STATEMENT_AFTER, PerlMooseAfterStatementImpl::new);
    factories.put(MOOSE_STATEMENT_AROUND, PerlMooseAroundStatementImpl::new);
    factories.put(MOOSE_STATEMENT_BEFORE, PerlMooseBeforeStatementImpl::new);
    factories.put(MOOSE_STATEMENT_EXTENDS, PerlMooseExtendsStatementImpl::new);
    factories.put(MOOSE_STATEMENT_META, PerlMooseMetaStatementImpl::new);
    factories.put(MOOSE_STATEMENT_WITH, PerlMooseWithStatementImpl::new);
    factories.put(MUL_EXPR, PsiPerlMulExprImpl::new);
    factories.put(NAMED_BLOCK, PsiPerlNamedBlockImpl::new);
    factories.put(NAMESPACE_CONTENT, PsiPerlNamespaceContentImpl::new);
    factories.put(NEXT_EXPR, PsiPerlNextExprImpl::new);
    factories.put(NUMBER_CONSTANT, PsiPerlNumberConstantImpl::new);
    factories.put(NYI_STATEMENT, PsiPerlNyiStatementImpl::new);
    factories.put(OCT_CHAR, PsiPerlOctCharImpl::new);
    factories.put(OR_EXPR, PsiPerlOrExprImpl::new);
    factories.put(OTHERWISE_EXPR, PsiPerlOtherwiseExprImpl::new);
    factories.put(PACKAGE_EXPR, PsiPerlPackageExprImpl::new);
    factories.put(PARENTHESISED_CALL_ARGUMENTS, PsiPerlParenthesisedCallArgumentsImpl::new);
    factories.put(PARENTHESISED_EXPR, PsiPerlParenthesisedExprImpl::new);
    factories.put(PARSABLE_STRING_USE_VARS, PerlCompositeElementImpl::new);
    factories.put(PERL_HANDLE_EXPR, PsiPerlPerlHandleExprImpl::new);
    factories.put(PERL_REGEX, PsiPerlPerlRegexImpl::new);
    factories.put(PERL_REGEX_MODIFIERS, PsiPerlPerlRegexModifiersImpl::new);
    factories.put(POST_DEREF_ARRAY_SLICE_EXPR, PsiPerlPostDerefArraySliceExprImpl::new);
    factories.put(POST_DEREF_EXPR, PsiPerlPostDerefExprImpl::new);
    factories.put(POST_DEREF_GLOB_EXPR, PsiPerlPostDerefGlobExprImpl::new);
    factories.put(POST_DEREF_HASH_SLICE_EXPR, PsiPerlPostDerefHashSliceExprImpl::new);
    factories.put(POW_EXPR, PsiPerlPowExprImpl::new);
    factories.put(PREFIX_UNARY_EXPR, PsiPerlPrefixUnaryExprImpl::new);
    factories.put(PREF_PP_EXPR, PsiPerlPrefPpExprImpl::new);
    factories.put(PRINT_EXPR, PsiPerlPrintExprImpl::new);
    factories.put(REDO_EXPR, PsiPerlRedoExprImpl::new);
    factories.put(REF_EXPR, PsiPerlRefExprImpl::new);
    factories.put(REGEX_EXPR, PsiPerlRegexExprImpl::new);
    factories.put(REGEX_REPLACEMENT, PsiPerlRegexReplacementImpl::new);
    factories.put(REPLACEMENT_REGEX, PsiPerlReplacementRegexImpl::new);
    factories.put(RETURN_EXPR, PsiPerlReturnExprImpl::new);
    factories.put(SCALAR_CALL, PsiPerlScalarCallImpl::new);
    factories.put(SCALAR_CAST_EXPR, PsiPerlScalarCastExprImpl::new);
    factories.put(SCALAR_EXPR, PsiPerlScalarExprImpl::new);
    factories.put(SCALAR_INDEX_CAST_EXPR, PsiPerlScalarIndexCastExprImpl::new);
    factories.put(SCALAR_VARIABLE, PsiPerlScalarVariableImpl::new);
    factories.put(SHIFT_EXPR, PsiPerlShiftExprImpl::new);
    factories.put(SIGNATURE_CONTENT, PsiPerlSignatureContentImpl::new);
    factories.put(SIGNATURE_ELEMENT, PsiPerlSignatureElementImpl::new);
    factories.put(SORT_EXPR, PsiPerlSortExprImpl::new);
    factories.put(SPLICE_EXPR, PsiPerlSpliceExprImpl::new);
    factories.put(STATEMENT, PsiPerlStatementImpl::new);
    factories.put(STATEMENT_MODIFIER, PerlCompositeElementImpl::new);
    factories.put(STRING_BARE, PsiPerlStringBareImpl::new);
    factories.put(STRING_DQ, PsiPerlStringDqImpl::new);
    factories.put(STRING_LIST, PsiPerlStringListImpl::new);
    factories.put(STRING_SQ, PsiPerlStringSqImpl::new);
    factories.put(STRING_XQ, PsiPerlStringXqImpl::new);
    factories.put(SUB_EXPR, PsiPerlSubExprImpl::new);
    factories.put(SUB_SIGNATURE_ELEMENT_IGNORE, PsiPerlSubSignatureElementIgnoreImpl::new);
    factories.put(SUFF_PP_EXPR, PsiPerlSuffPpExprImpl::new);
    factories.put(SWITCH_COMPOUND, PsiPerlSwitchCompoundImpl::new);
    factories.put(SWITCH_CONDITION, PsiPerlSwitchConditionImpl::new);
    factories.put(TAG_SCALAR, PsiPerlTagScalarImpl::new);
    factories.put(TERNARY_EXPR, PsiPerlTernaryExprImpl::new);
    factories.put(TRYCATCH_COMPOUND, PsiPerlTrycatchCompoundImpl::new);
    factories.put(TRYCATCH_EXPR, PsiPerlTrycatchExprImpl::new);
    factories.put(TRY_EXPR, PsiPerlTryExprImpl::new);
    factories.put(TR_MODIFIERS, PsiPerlTrModifiersImpl::new);
    factories.put(TR_REGEX, PsiPerlTrRegexImpl::new);
    factories.put(TR_REPLACEMENTLIST, PsiPerlTrReplacementlistImpl::new);
    factories.put(TR_SEARCHLIST, PsiPerlTrSearchlistImpl::new);
    factories.put(TYPE_CONSTRAINTS, PsiPerlTypeConstraintsImpl::new);
    factories.put(TYPE_SPECIFIER, PsiPerlTypeSpecifierImpl::new);
    factories.put(UNCONDITIONAL_BLOCK, PsiPerlUnconditionalBlockImpl::new);
    factories.put(UNDEF_EXPR, PsiPerlUndefExprImpl::new);
    factories.put(UNICODE_CHAR, PsiPerlUnicodeCharImpl::new);
    factories.put(UNLESS_COMPOUND, PsiPerlUnlessCompoundImpl::new);
    factories.put(UNLESS_STATEMENT_MODIFIER, PsiPerlUnlessStatementModifierImpl::new);
    factories.put(UNTIL_COMPOUND, PsiPerlUntilCompoundImpl::new);
    factories.put(UNTIL_STATEMENT_MODIFIER, PsiPerlUntilStatementModifierImpl::new);
    factories.put(VALUES_EXPR, PsiPerlValuesExprImpl::new);
    factories.put(VARIABLE_DECLARATION_GLOBAL, PsiPerlVariableDeclarationGlobalImpl::new);
    factories.put(VARIABLE_DECLARATION_LEXICAL, PsiPerlVariableDeclarationLexicalImpl::new);
    factories.put(VARIABLE_DECLARATION_LOCAL, PsiPerlVariableDeclarationLocalImpl::new);
    factories.put(WANTARRAY_EXPR, PsiPerlWantarrayExprImpl::new);
    factories.put(WHEN_COMPOUND, PsiPerlWhenCompoundImpl::new);
    factories.put(WHEN_STATEMENT_MODIFIER, PsiPerlWhenStatementModifierImpl::new);
    factories.put(WHILE_COMPOUND, PsiPerlWhileCompoundImpl::new);
    factories.put(WHILE_STATEMENT_MODIFIER, PsiPerlWhileStatementModifierImpl::new);
  }

  private PerlPsiElementFactory() {
  }

  public static @NotNull PsiElement create(@NotNull IElementType elementType, @NotNull ASTNode node) {
    if (elementType instanceof PsiElementProvider psiElementProvider) {
      return psiElementProvider.getPsiElement(node);
    }
    Function<ASTNode, PsiElement> factory = factories.get(elementType);
    if (factory != null) {
      return factory.apply(node);
    }
    throw new IllegalArgumentException("No factory registered for " + elementType + " (" + elementType.getClass());
  }
}
