package com.perl5.lang.tt2.idea.settings;

import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of the Template Toolkit templates root as a content folder type.
 * Upstream registers it as a JPS source root type with {@code TemplateToolkitSourceTypesSerializationExtension}.
 * Instances are equal to {@link TemplateToolkitSourceRootType#INSTANCE}, see {@code PerlSourceRootType#equals(Object)}
 */
@ExtensionImpl(id = "ttk2-templates", order = "after perl-library")
public final class TemplateToolkitSourceRootTypeRegistration extends TemplateToolkitSourceRootType {
}
