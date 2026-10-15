package com.perl5.lang.htmlmason.idea.configuration;

import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of the HTML::Mason components root as a content folder type.
 * Upstream registers it as a JPS source root type with {@code HTMLMasonSourceTypesSerializationExtension}.
 * Instances are equal to {@link HTMLMasonSourceRootType#INSTANCE}, see {@link com.perl5.lang.perl.idea.modules.PerlSourceRootType#equals(Object)}
 */
@ExtensionImpl(id = "html-mason", order = "after perl-library")
public final class HTMLMasonSourceRootTypeRegistration extends HTMLMasonSourceRootType {
}
