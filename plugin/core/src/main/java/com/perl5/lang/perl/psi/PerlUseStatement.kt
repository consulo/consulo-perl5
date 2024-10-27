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
package com.perl5.lang.perl.psi

interface PerlUseStatement {
  /**
   * @return package name used in the statement
   */
  val packageName: String?

  /**
   * @return a name of containing namespace
   */
  val namespaceName: String?

  /**
   * @return mutable list of used import parameters or null if no parameters been specified.
   */
  val importParameters: MutableList<String>?
}
