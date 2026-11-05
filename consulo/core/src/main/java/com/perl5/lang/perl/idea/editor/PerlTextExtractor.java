/*
 * Copyright 2013-2026 consulo.io
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
package com.perl5.lang.perl.idea.editor;

import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.lexer.PerlTokenSets;
import com.perl5.lang.perl.parser.PerlElementTypesGenerated;
import com.perl5.lang.perl.psi.impl.PerlHeredocElementImpl;
import com.perl5.lang.perl.psi.mixins.PerlStringMixin;
import com.perl5.lang.perl.psi.utils.PerlPsiUtil;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.document.util.TextRange;
import consulo.language.Language;
import consulo.language.psi.PsiComment;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiUtilCore;
import consulo.language.psi.PsiWhiteSpace;
import consulo.language.spellcheker.text.TextContent;
import consulo.language.spellcheker.text.TextContentBuilder;
import consulo.language.spellcheker.text.TextExtractor;
import consulo.language.spellcheker.text.util.PsiUtils;
import consulo.language.spellcheker.text.util.Text;
import consulo.util.collection.ContainerUtil;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Natural language text in Perl code: line comments and the content of quoted strings and heredocs.
 * POD blocks belong to the POD language and are handled by {@link com.perl5.lang.pod.idea.editor.PodTextExtractor}.
 *
 * @author VISTALL
 * @since 2026-11-02
 */
@ExtensionImpl
public class PerlTextExtractor extends TextExtractor {
    private static final Pattern DIRECTIVE_COMMENT = Pattern.compile("#(!|#\\s*(no|use)\\s+critic|<<<|>>>|\\s*line\\s+\\d)");
    private static final Pattern FORMAT_SPECIFIER = Pattern.compile("%[-+#0]*(\\*|\\d+)?(\\.(\\*|\\d+)?)?[a-zA-Z%]");

    private static final TextContentBuilder COMMENT_BUILDER =
        TextContentBuilder.FromPsi.removingIndents(" \t#").removingLineSuffixes(" \t#");

    @Override
    @RequiredReadAction
    protected List<TextContent> buildTextContents(PsiElement element, Set<TextContent.TextDomain> allowedDomains) {
        if (element instanceof PsiComment) {
            if (!allowedDomains.contains(TextContent.TextDomain.COMMENTS)) {
                return List.of();
            }
            return buildComment(element);
        }

        if (element instanceof PerlStringMixin string) {
            if (!allowedDomains.contains(TextContent.TextDomain.LITERALS)) {
                return List.of();
            }
            return asList(buildString(string));
        }

        if (element instanceof PerlHeredocElementImpl heredoc) {
            if (!allowedDomains.contains(TextContent.TextDomain.LITERALS)) {
                return List.of();
            }
            return asList(buildHeredoc(heredoc));
        }
        return List.of();
    }

    @RequiredReadAction
    private static List<TextContent> buildComment(PsiElement element) {
        // heredoc terminators and type annotations are comments too, only plain line comments are prose
        if (!isProseComment(element)) {
            return List.of();
        }
        List<PsiElement> siblings = PsiUtils.getNotSoDistantSimilarSiblings(element, PerlTextExtractor::isProseComment);
        return asList(TextContent.joinWithWhitespace(
            '\n',
            ContainerUtil.mapNotNull(siblings, it -> COMMENT_BUILDER.build(it, TextContent.TextDomain.COMMENTS))
        ));
    }

    @RequiredReadAction
    private static boolean isProseComment(PsiElement element) {
        return PsiUtilCore.getElementType(element) == PerlElementTypesGenerated.COMMENT_LINE
            && !DIRECTIVE_COMMENT.matcher(element.getText()).lookingAt();
    }

    @RequiredReadAction
    private static @Nullable TextContent buildString(PerlStringMixin string) {
        if (PsiUtilCore.getElementType(string) == PerlElementTypesGenerated.STRING_XQ) {
            return null;
        }
        PsiElement firstContent = string.getFirstContentToken();
        if (firstContent == null || PerlPsiUtil.getLightNameIdentifierOwner(firstContent) != null) {
            return null;
        }

        int start = firstContent.getStartOffsetInParent();
        PsiElement closeQuote = string.getCloseQuoteElement();
        int end = closeQuote != null ? closeQuote.getStartOffsetInParent() : string.getTextLength();
        if (end <= start) {
            return null;
        }
        // identifier-like values (hash keys, names, paths) are not prose
        if (!hasWhitespace(string.getText(), start, end)) {
            return null;
        }

        TextContent content = contentBuilder(string).build(string, TextContent.TextDomain.LITERALS, new TextRange(start, end));
        return markFormatSpecifiers(content);
    }

    @RequiredReadAction
    private static @Nullable TextContent buildHeredoc(PerlHeredocElementImpl heredoc) {
        if (PsiUtilCore.getElementType(heredoc) == PerlElementTypesGenerated.HEREDOC_QX) {
            return null;
        }
        TextContent content = contentBuilder(heredoc).removingIndents(" \t").build(heredoc, TextContent.TextDomain.LITERALS);
        return markFormatSpecifiers(content);
    }

    private static TextContentBuilder contentBuilder(PsiElement root) {
        // interpolated variables, expressions and escape sequences are not text
        return TextContentBuilder.FromPsi.withUnknown(it -> it != root && !isContent(it));
    }

    private static boolean isContent(PsiElement element) {
        return element instanceof PsiWhiteSpace || PerlTokenSets.STRING_CONTENT_TOKENSET.contains(PsiUtilCore.getElementType(element));
    }

    private static @Nullable TextContent markFormatSpecifiers(@Nullable TextContent content) {
        if (content == null) {
            return null;
        }
        List<TextContent.Exclusion> specifiers =
            ContainerUtil.map(Text.allOccurrences(FORMAT_SPECIFIER, content), TextContent.Exclusion::markUnknown);
        return specifiers.isEmpty() ? content : content.excludeRanges(specifiers);
    }

    private static boolean hasWhitespace(String text, int start, int end) {
        for (int i = start; i < end; i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static List<TextContent> asList(@Nullable TextContent content) {
        return content == null ? List.of() : List.of(content);
    }

    @Override
    public Language getLanguage() {
        return PerlLanguage.INSTANCE;
    }
}
