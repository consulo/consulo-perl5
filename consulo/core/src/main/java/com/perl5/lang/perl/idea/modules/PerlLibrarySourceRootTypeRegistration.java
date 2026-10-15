package com.perl5.lang.perl.idea.modules;

import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of the perl library root as a content folder type.
 * Upstream registers it as a JPS source root type with {@code PerlSourceTypesSerializationExtension}.
 * Instances are equal to {@link PerlLibrarySourceRootType#INSTANCE}, see {@link PerlSourceRootType#equals(Object)}
 */
@ExtensionImpl(id = "perl-library", order = "after test-resource")
public final class PerlLibrarySourceRootTypeRegistration extends PerlLibrarySourceRootType {
}
