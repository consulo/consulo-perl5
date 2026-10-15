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

package com.perl5.lang.perl.extensions.cgi;

import com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessorBase;
import com.perl5.lang.perl.psi.PerlSubDefinitionElement;
import com.perl5.lang.perl.psi.impl.PerlUseStatementElement;
import com.perl5.lang.perl.util.PerlSubUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class PerlCgiPackageProcessor extends PerlPackageProcessorBase {
  // private top-level property of upstream PerlCgiPackageProcessor.kt
  private static final Map<String, List<String>> exportTags;

  static {
    Map<String, List<String>> map = new LinkedHashMap<>();
    map.put(":html2", List.of(
      "h1", "h2", "h3", "h4", "h5", "h6", "p", "br", "hr", "ol", "ul", "li", "dl", "dt", "dd", "menu", "code", "var", "strong", "em", "tt",
      "u", "i", "b", "blockquote", "pre", "img", "a", "address", "cite", "samp", "dfn", "html", "head", "base", "body", "Link", "nextid",
      "title", "meta", "kbd", "start_html", "end_html", "input", "Select", "option", "comment", "charset", "escapeHTML"
    ));
    map.put(":html3", List.of(
      "div", "table", "caption", "th", "td", "TR", "Tr", "sup", "Sub", "strike", "applet", "Param", "nobr", "embed", "basefont", "style",
      "span", "layer", "ilayer", "font", "frameset", "frame", "script", "small", "big", "Area", "Map"
    ));
    map.put(":html4", List.of(
      "abbr", "acronym", "bdo", "col", "colgroup", "del", "fieldset", "iframe", "ins", "label", "legend", "noframes", "noscript", "object",
      "optgroup", "Q", "thead", "tbody", "tfoot"
    ));
    map.put(":form", List.of(
      "textfield", "textarea", "filefield", "password_field", "hidden", "checkbox", "checkbox_group", "submit", "reset", "defaults",
      "radio_group", "popup_menu", "button", "autoEscape", "scrolling_list", "image_button", "start_form", "end_form",
      "start_multipart_form", "end_multipart_form", "isindex", "tmpFileName", "uploadInfo", "URL_ENCODED", "MULTIPART"
    ));
    map.put(":cgi", List.of(
      "param", "multi_param", "upload", "path_info", "path_translated", "request_uri", "url", "self_url", "script_name", "cookie", "Dump",
      "raw_cookie", "request_method", "query_string", "Accept", "user_agent", "remote_host", "content_type", "remote_addr", "referer",
      "server_name", "server_software", "server_port", "server_protocol", "virtual_port", "virtual_host", "remote_ident", "auth_type",
      "http", "append", "save_parameters", "restore_parameters", "param_fetch", "remote_user", "user_name", "header", "redirect",
      "import_names", "put", "Delete", "Delete_all", "url_param", "cgi_error", "env_query_string"
    ));
    map.put(":netscape", List.of("blink", "fontsize", "center"));
    map.put(":ssl", List.of("https"));
    map.put(":cgi-lib", List.of("ReadParse", "PrintHeader", "HtmlTop", "HtmlBot", "SplitParam", "Vars"));
    map.put(":push", List.of("multipart_init", "multipart_start", "multipart_end", "multipart_final"));
    map.put(":html", List.of(":html2", ":html3", ":html4", ":netscape"));
    map.put(":standard", List.of(":html2", ":html3", ":html4", ":form", ":cgi", ":ssl"));
    map.put(":all", List.of(":html2", ":html3", ":html4", ":netscape", ":form", ":cgi", ":ssl", ":push"));
    exportTags = Collections.unmodifiableMap(map);
  }

  @Override
  public void addExports(@NotNull PerlUseStatementElement useStatement,
                         @NotNull Set<? super String> export,
                         @NotNull Set<? super String> exportOk) {
    exportOk.addAll(exportTags.keySet());
    List<String> subNames = new ArrayList<>();
    for (PerlSubDefinitionElement it : PerlSubUtil.getSubDefinitionsInPackage(useStatement.getProject(), "CGI")) {
      String subName = it.getSubName();
      if (subName != null) {
        subNames.add(subName);
      }
    }
    exportOk.addAll(subNames);
  }

  @Override
  protected @Nullable List<String> getImportParameters(@NotNull PerlUseStatementElement useStatement) {
    List<String> importParameters = super.getImportParameters(useStatement);
    return importParameters == null ? null : new ArrayList<>(expandTags(importParameters));
  }

  private @NotNull Set<String> expandTags(@NotNull List<String> imports) {
    Set<String> result = new LinkedHashSet<>();

    for (String it : imports) {
      if (it.startsWith(":")) {
        result.addAll(expandTag(it));
      }
      else {
        result.add(it);
      }
    }

    return result;
  }

  private @NotNull Set<String> expandTag(@NotNull String tag) {
    List<String> tags = exportTags.get(tag);
    return tags != null ? new LinkedHashSet<>(expandTags(tags)) : Collections.emptySet();
  }
}
