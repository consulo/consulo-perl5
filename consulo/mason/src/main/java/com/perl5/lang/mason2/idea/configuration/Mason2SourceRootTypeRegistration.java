package com.perl5.lang.mason2.idea.configuration;

import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of the Mason2 components root as a content folder type.
 * Upstream registers it as a JPS source root type with {@code Mason2SourceTypesSerializationExtension}.
 * Instances are equal to {@link Mason2SourceRootType#INSTANCE}, see {@link com.perl5.lang.perl.idea.modules.PerlSourceRootType#equals(Object)}
 */
@ExtensionImpl(id = "mason2", order = "after perl-library")
public final class Mason2SourceRootTypeRegistration extends Mason2SourceRootType {
}
