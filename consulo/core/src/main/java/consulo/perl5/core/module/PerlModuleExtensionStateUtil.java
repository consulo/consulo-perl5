package consulo.perl5.core.module;

import org.jdom.Element;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Serialization of the state of Perl framework module extensions (the per-module framework settings) into the module extension element:
 * only values which differ from the defaults are written.
 */
public final class PerlModuleExtensionStateUtil {
  private static final String VALUE_ATTRIBUTE = "value";

  private PerlModuleExtensionStateUtil() {
  }

  public static void writeAttribute(Element element, String name, String value, String defaultValue) {
    if (!value.equals(defaultValue)) {
      element.setAttribute(name, value);
    }
  }

  public static void writeAttribute(Element element, String name, boolean value, boolean defaultValue) {
    if (value != defaultValue) {
      element.setAttribute(name, Boolean.toString(value));
    }
  }

  public static String readAttribute(Element element, String name, String defaultValue) {
    String value = element.getAttributeValue(name);
    return value == null ? defaultValue : value;
  }

  public static boolean readAttribute(Element element, String name, boolean defaultValue) {
    String value = element.getAttributeValue(name);
    return value == null ? defaultValue : Boolean.parseBoolean(value);
  }

  /**
   * Writes {@code values} as {@code <listName><itemName value="..."/>...</listName>} if they differ from {@code defaultValues}
   */
  public static void writeStrings(Element element, String listName, String itemName, List<String> values, List<String> defaultValues) {
    writeList(element, listName, itemName, values, defaultValues, (value, itemElement) -> itemElement.setAttribute(VALUE_ATTRIBUTE, value));
  }

  /**
   * @return values written by {@link #writeStrings}, {@code defaultValues} if they were not written
   */
  public static List<String> readStrings(Element element, String listName, String itemName, List<String> defaultValues) {
    return readList(element, listName, itemName, defaultValues, itemElement -> itemElement.getAttributeValue(VALUE_ATTRIBUTE));
  }

  /**
   * Writes {@code values} as {@code <listName><itemName .../>...</listName>} if they differ from {@code defaultValues}, item elements are
   * filled by the {@code itemWriter}
   */
  public static <T> void writeList(Element element,
                                   String listName,
                                   String itemName,
                                   List<T> values,
                                   List<T> defaultValues,
                                   BiConsumer<T, Element> itemWriter) {
    if (values.equals(defaultValues)) {
      return;
    }
    Element listElement = new Element(listName);
    for (T value : values) {
      Element itemElement = new Element(itemName);
      itemWriter.accept(value, itemElement);
      listElement.addContent(itemElement);
    }
    element.addContent(listElement);
  }

  /**
   * @param itemReader returns null for an invalid item, which is skipped
   * @return values written by {@link #writeList}, {@code defaultValues} if they were not written
   */
  public static <T> List<T> readList(Element element,
                                     String listName,
                                     String itemName,
                                     List<T> defaultValues,
                                     Function<Element, @Nullable T> itemReader) {
    Element listElement = element.getChild(listName);
    if (listElement == null) {
      return defaultValues;
    }
    List<T> result = new ArrayList<>();
    for (Element itemElement : listElement.getChildren(itemName)) {
      T value = itemReader.apply(itemElement);
      if (value != null) {
        result.add(value);
      }
    }
    return result;
  }
}
