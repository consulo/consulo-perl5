/*
 * Copyright 2015-2023 Alexandr Evstigneev
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

package highlighting;


import base.PodLightTestCase;
import org.junit.Test;
public class PodFindUsagesHighlightingTest extends PodLightTestCase {
  @Override
  protected String getBaseDataPath() {
    return "highlighting/pod/usages";
  }

  @Test
  public void testMultiDeclarations() {doTest();}

  @Test
  public void testMultiDeclarationsExternal() {
    withPerl528();
    doTest();
  }

  private void doTest() {doTestUsagesHighlighting();}
}
