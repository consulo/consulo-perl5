/*
 * Copyright 2015-2019 Alexandr Evstigneev
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

package intellilang;

import base.PerlLightTestCase;

public class PerlInjectionSupportTest extends PerlLightTestCase {
  @Override
  protected String getTestDataPath() {
    return "testData/intellilang/perl/injectionSupport";
  }

  public void testHtmlQ() {doFileTest();}

  public void testHtmlQQ() {doFileTest();}

  public void testHtmlQX() {doFileTest();}

  public void testHtmlHeredocQ() {doFileTest();}

  public void testHtmlHeredocQQ() {doFileTest();}

  public void testHtmlHeredocQX() {doFileTest();}

  private void doFileTest() {
    initWithFileSmartWithoutErrors();
    assertInjected();
  }
}