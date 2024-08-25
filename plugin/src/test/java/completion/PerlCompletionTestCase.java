/*
 * Copyright 2015-2024 Alexandr Evstigneev
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

package completion;

import base.PerlLightTestCase;
import com.intellij.codeInsight.CodeInsightSettings;
import com.perl5.lang.perl.idea.project.PerlNamesCache;
import org.jdom.Element;

public abstract class PerlCompletionTestCase extends PerlLightTestCase {
  private Element myState;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    updateNamesCacheSynchronously();
    myState = CodeInsightSettings.getInstance().getState();
    disableAutoInsertion();
  }

  protected void disableAutoInsertion() {
    CodeInsightSettings.getInstance().AUTOCOMPLETE_ON_CODE_COMPLETION = false;
  }

  @Override
  protected void tearDown() throws Exception {
    try {
      CodeInsightSettings.getInstance().loadState(myState);
    }
    finally {
      super.tearDown();
    }
  }
}
