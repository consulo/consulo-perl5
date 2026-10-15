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

package com.intellij.lang.impl;

import consulo.language.parser.PsiBuilder;
import com.intellij.lang.SyntaxTreeBuilder;
import consulo.language.parser.WhitespacesAndCommentsBinder;
import consulo.logging.Logger;
import consulo.language.ast.IElementType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PerlStartMarker extends PsiBuilderImpl.StartMarker implements PsiBuilder.Marker {
  private static final Logger LOG = Logger.getInstance(PerlStartMarker.class);

  private final PsiBuilderImpl.StartMarker myDelegate;
  private final int myStartTokenIndex;
  private final PerlDebugBuilder myPerlDebugBuilder;

  public PerlStartMarker(@NotNull PsiBuilder.Marker delegate, @NotNull PerlDebugBuilder perlDebugBuilder) {
    super(((PsiBuilderImpl.ProductionMarker)delegate).markerId, perlDebugBuilder);
    myDelegate = (PsiBuilderImpl.StartMarker)delegate;
    myStartTokenIndex = ((PsiBuilderImpl.ProductionMarker)delegate).getStartIndex();
    myPerlDebugBuilder = (PerlDebugBuilder)myDelegate.myBuilder;
    LOG.assertTrue(delegate instanceof PsiBuilderImpl.StartMarker, "Expected start marker, got " + delegate.getClass());
  }

  @Override
  public void clean() {
    myDelegate.clean();
  }

  @Override
  public boolean tokenTextMatches(@NotNull CharSequence chars) {
    return myDelegate.tokenTextMatches(chars);
  }

  @Override
  public int getEndOffset() {
    return myDelegate.getEndOffset();
  }

  @Override
  public int getEndIndex() {
    return myDelegate.getEndIndex();
  }

  @Override
  public void setLexemeIndex(int lexemeIndex, boolean done) {
    myDelegate.setLexemeIndex(lexemeIndex, done);
  }

  @Override
  public int getLexemeIndex(boolean done) {
    return myDelegate.getLexemeIndex(done);
  }

  @Override
  public void addChild(@NotNull PsiBuilderImpl.ProductionMarker node) {
    myDelegate.addChild(node);
  }

  @Override
  public @NotNull PsiBuilder.Marker precede() {
    return myPerlDebugBuilder.createPerlMarker(myDelegate.precede());
  }

  @Override
  public void drop() {
    myDelegate.drop();
  }

  @Override
  public void rollbackTo() {
    myPerlDebugBuilder.registerRollback(rawTokenIndex() - myStartTokenIndex);
    myDelegate.rollbackTo();
  }


  private int rawTokenIndex() {
    return ((PsiBuilderImpl)((PsiBuilderImpl.ProductionMarker)myDelegate).myBuilder).rawTokenIndex();
  }

  @Override
  public void done(@NotNull IElementType type) {
    myDelegate.done(type);
  }

  @Override
  public void collapse(@NotNull IElementType type) {
    myDelegate.collapse(type);
  }

  @Override
  public void doneBefore(@NotNull IElementType type, @NotNull PsiBuilder.Marker before) {
    myDelegate.doneBefore(type, before);
  }

  @Override
  public void doneBefore(@NotNull IElementType type, @NotNull PsiBuilder.Marker before, @Nls @NotNull String errorMessage) {
    myDelegate.doneBefore(type, before, errorMessage);
  }

  @Override
  public void error(@Nls @NotNull String message) {
    myDelegate.error(message);
  }

  @Override
  public void errorBefore(@Nls @NotNull String message, @NotNull PsiBuilder.Marker before) {
    myDelegate.errorBefore(message, before);
  }

  @Override
  public @NotNull IElementType getTokenType() {
    return myDelegate.getTokenType();
  }

  @Override
  public void remapTokenType(@NotNull IElementType type) {
    myDelegate.remapTokenType(type);
  }

  @Override
  public void setCustomEdgeTokenBinders(@Nullable WhitespacesAndCommentsBinder left, @Nullable WhitespacesAndCommentsBinder right) {
    myDelegate.setCustomEdgeTokenBinders(left, right);
  }

  @Override
  public @NotNull String toString() {
    return myDelegate.toString();
  }

  @Override
  public int getStartOffset() {
    return myDelegate.getStartOffset();
  }

  @Override
  public int getStartIndex() {
    return myDelegate.getStartIndex();
  }

  @Override
  public int getTextLength() {
    return myDelegate.getTextLength();
  }

  @Override
  public void doneBefore(@NotNull IElementType type, @NotNull SyntaxTreeBuilder.Marker before) {
    myDelegate.doneBefore(type, before);
  }

  @Override
  public void doneBefore(@NotNull IElementType type,
                         @NotNull SyntaxTreeBuilder.Marker before,
                         @NotNull String errorMessage) {
    myDelegate.doneBefore(type, before, errorMessage);
  }

  @Override
  public void errorBefore(@NotNull String message, @NotNull SyntaxTreeBuilder.Marker before) {
    myDelegate.errorBefore(message, before);
  }
}
