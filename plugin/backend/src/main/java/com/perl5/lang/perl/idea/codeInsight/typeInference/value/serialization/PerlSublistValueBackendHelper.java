/*
 * Copyright 2015-2025 Alexandr Evstigneev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization;

import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlSublistValue;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValue;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValueDeserializer;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValueSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class PerlSublistValueBackendHelper extends PerlOperationValueBackendHelper<PerlSublistValue> {
  @Override
  public int getSerializationId() {
    return PerlValueBackendHelper.SUBLIST_ID;
  }

  @Override
  public void serializeData(@NotNull PerlSublistValue value, @NotNull PerlValueSerializer serializer) throws IOException {
    super.serializeData(value, serializer);
    serializer.writeVarInt(value.getStartOffset());
    serializer.writeVarInt(value.getEndOffset());
  }

  @Override
  protected @NotNull PerlValue deserialize(@NotNull PerlValueDeserializer deserializer, @NotNull PerlValue baseValue)
    throws IOException {
    return PerlSublistValue.create(baseValue, deserializer.readVarInt(), deserializer.readVarInt());
  }
}
