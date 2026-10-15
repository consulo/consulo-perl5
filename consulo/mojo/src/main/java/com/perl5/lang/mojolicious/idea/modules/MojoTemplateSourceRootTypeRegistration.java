package com.perl5.lang.mojolicious.idea.modules;

import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of the mojo templates root as a content folder type.
 * Upstream registers it as a JPS source root type with {@code MojoTemplateSourceTypesSerializationExtension}.
 * Instances are equal to {@link MojoTemplateSourceRootType#INSTANCE}, see {@link com.perl5.lang.perl.idea.modules.PerlSourceRootType#equals(Object)}
 */
@ExtensionImpl(id = "mojo-template", order = "after perl-library")
public final class MojoTemplateSourceRootTypeRegistration extends MojoTemplateSourceRootType {
}
