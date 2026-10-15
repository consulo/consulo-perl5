// <highlightUsagesHandlerFactory> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.pod.idea.codeInsight.highlighting;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl(order = "before mainone")
public class PodUsagesHighlightingFactoryRegistration extends com.perl5.lang.pod.idea.codeInsight.highlighting.PodUsagesHighlightingFactory {
}
