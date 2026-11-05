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
package com.perl5.lang.pod.idea.editor;

import com.perl5.lang.pod.PodLanguage;
import com.perl5.lang.pod.parser.PodElementTypesGenerated;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiUtilCore;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.spellcheker.text.TextContent;
import consulo.language.spellcheker.text.TextContentBuilder;
import consulo.language.spellcheker.text.TextExtractor;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Natural language text in POD: ordinary paragraphs, heading and item titles.
 * Directives, verbatim paragraphs and {@code =for}/{@code =begin} blocks are skipped,
 * formatting codes are markup, code-like formatting codes are unknown fragments.
 *
 * @author VISTALL
 * @since 2026-11-02
 */
@ExtensionImpl
public class PodTextExtractor extends TextExtractor {
    private static final TokenSet TEXT_ROOTS = TokenSet.create(
        PodElementTypesGenerated.POD_PARAGRAPH,
        PodElementTypesGenerated.SECTION_TITLE,
        PodElementTypesGenerated.ITEM_SECTION_TITLE
    );

    private static final TokenSet FORMATTERS = TokenSet.create(
        PodElementTypesGenerated.POD_FORMAT_BOLD,
        PodElementTypesGenerated.POD_FORMAT_ITALIC,
        PodElementTypesGenerated.POD_FORMAT_NBSP,
        PodElementTypesGenerated.POD_FORMAT_CODE,
        PodElementTypesGenerated.POD_FORMAT_FILE,
        PodElementTypesGenerated.POD_FORMAT_ESCAPE,
        PodElementTypesGenerated.POD_FORMAT_LINK,
        PodElementTypesGenerated.POD_FORMAT_INDEX,
        PodElementTypesGenerated.POD_FORMAT_NULL
    );

    private static final TokenSet UNKNOWN_FORMATTERS = TokenSet.create(
        PodElementTypesGenerated.POD_FORMAT_CODE,
        PodElementTypesGenerated.POD_FORMAT_FILE,
        PodElementTypesGenerated.POD_FORMAT_ESCAPE
    );

    private static final TokenSet EXCLUDED_FORMATTERS = TokenSet.create(
        PodElementTypesGenerated.POD_FORMAT_INDEX,
        PodElementTypesGenerated.POD_FORMAT_NULL
    );

    private static final TextContentBuilder BUILDER = TextContentBuilder.FromPsi
        .excluding(PodTextExtractor::isExcluded)
        .withUnknown(PodTextExtractor::isUnknown)
        .withMarkup(PodTextExtractor::isMarkup);

    @Override
    @RequiredReadAction
    protected List<TextContent> buildTextContents(PsiElement element, Set<TextContent.TextDomain> allowedDomains) {
        if (!allowedDomains.contains(TextContent.TextDomain.DOCUMENTATION)) {
            return List.of();
        }
        if (!TEXT_ROOTS.contains(PsiUtilCore.getElementType(element))) {
            return List.of();
        }
        TextContent content = BUILDER.build(element, TextContent.TextDomain.DOCUMENTATION);
        return content == null ? List.of() : List.of(content);
    }

    private static boolean isExcluded(PsiElement element) {
        return EXCLUDED_FORMATTERS.contains(PsiUtilCore.getElementType(element));
    }

    private static boolean isUnknown(PsiElement element) {
        IElementType type = PsiUtilCore.getElementType(element);
        if (UNKNOWN_FORMATTERS.contains(type)) {
            return true;
        }
        // a link without an explicit text is rendered as its target: a name, not prose
        return type == PodElementTypesGenerated.POD_FORMAT_LINK && findLinkText(element) == null;
    }

    private static boolean isMarkup(PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent == null) {
            return false;
        }
        IElementType parentType = PsiUtilCore.getElementType(parent);
        if (FORMATTERS.contains(parentType)) {
            // formatter letter and angle brackets
            return PsiUtilCore.getElementType(element) != PodElementTypesGenerated.FORMATTING_SECTION_CONTENT;
        }
        if (parentType == PodElementTypesGenerated.FORMATTING_SECTION_CONTENT
            && PsiUtilCore.getElementType(parent.getParent()) == PodElementTypesGenerated.POD_FORMAT_LINK) {
            // link target and separator, only the link text is kept
            return PsiUtilCore.getElementType(element) != PodElementTypesGenerated.LINK_TEXT;
        }
        return false;
    }

    private static @Nullable PsiElement findLinkText(PsiElement link) {
        for (PsiElement child : PsiTreeUtil.findChildrenOfType(link, PsiElement.class)) {
            if (PsiUtilCore.getElementType(child) == PodElementTypesGenerated.LINK_TEXT) {
                return child;
            }
        }
        return null;
    }

    @Override
    public Language getLanguage() {
        return PodLanguage.INSTANCE;
    }
}
