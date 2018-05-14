/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.i18n

import java.io.Serializable

/**
 * Translate the captions, descriptions and values to the current Locale, depending on criteria set by the
 * implementation (for example by the use of annotations)
 *
 * @author David Sowerby 10 Feb 2013
 */
interface I18NProcessor : Serializable {

    /**
     * Translate the captions, descriptions and values of `target` to the current Locale. This should only be
     * called with a `target` which is annotated with [I18N], but no errors occur if it hasn't
     *
     * @param target
     */
    fun translate(target: Any)

}