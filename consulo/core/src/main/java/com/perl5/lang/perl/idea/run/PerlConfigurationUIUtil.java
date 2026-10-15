package com.perl5.lang.perl.idea.run;

import consulo.localize.LocalizeValue;
import consulo.util.lang.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Helpers for unified UI ({@code consulo.ui}) run configuration editors, which replace upstream Swing editors built with
 * {@code LabeledComponent}.
 */
public final class PerlConfigurationUIUtil {
  private PerlConfigurationUIUtil() {
  }

  /**
   * @return label text for a form row: upstream bundle texts may contain mnemonics and may lack a trailing colon, which
   * Swing {@code LabeledComponent} handles itself
   */
  public static LocalizeValue labelText(String text) {
    String result = StringUtil.trimEnd(text.replace("&", "").trim(), ":");
    return LocalizeValue.of(result + ":");
  }

  /**
   * @return {@code keys} with {@code preferredOrder} items first (upstream option maps are created with {@code Map.of} which has no
   * stable iteration order) and remaining keys after them
   */
  public static List<String> orderedKeys(Collection<String> keys, String... preferredOrder) {
    List<String> result = new ArrayList<>();
    for (String key : preferredOrder) {
      if (keys.contains(key)) {
        result.add(key);
      }
    }
    for (String key : keys) {
      if (!result.contains(key)) {
        result.add(key);
      }
    }
    return result;
  }
}
