package consulo.perl5.mason.module;

import com.perl5.lang.mason2.idea.configuration.VariableDescription;
import consulo.perl5.core.module.PerlModuleExtensionStateUtil;
import consulo.util.lang.StringUtil;
import org.jdom.Element;

import java.util.List;

/**
 * Serialization of the state shared by the Mason module extensions (HTML::Mason, Mason2)
 */
final class MasonModuleExtensionStateUtil {
  private static final String GLOBAL_VARIABLES = "global-variables";
  private static final String VARIABLE = "variable";
  private static final String NAME = "name";
  private static final String TYPE = "type";

  private MasonModuleExtensionStateUtil() {
  }

  static void writeGlobalVariables(Element element, List<VariableDescription> globalVariables, List<VariableDescription> defaults) {
    PerlModuleExtensionStateUtil.writeList(element, GLOBAL_VARIABLES, VARIABLE, globalVariables, defaults, (variable, variableElement) -> {
      variableElement.setAttribute(NAME, StringUtil.notNullize(variable.variableName));
      if (StringUtil.isNotEmpty(variable.variableType)) {
        variableElement.setAttribute(TYPE, variable.variableType);
      }
    });
  }

  static List<VariableDescription> readGlobalVariables(Element element, List<VariableDescription> defaults) {
    return PerlModuleExtensionStateUtil.readList(element, GLOBAL_VARIABLES, VARIABLE, defaults, variableElement -> {
      String name = variableElement.getAttributeValue(NAME);
      return StringUtil.isEmpty(name) ? null : new VariableDescription(name, variableElement.getAttributeValue(TYPE));
    });
  }
}
