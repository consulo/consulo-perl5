package com.perl5.lang.perl.idea.sdk;

import consulo.content.bundle.SdkAdditionalData;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public interface SaveAwareSdkAdditionalData extends SdkAdditionalData {
  void save(@NotNull Element additional);
}
