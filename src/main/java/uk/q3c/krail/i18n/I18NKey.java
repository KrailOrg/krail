/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.i18n;

public interface I18NKey {
    /**
     * Provides a default bundle name by removing the last 3 characters from the key name and replacing with an 's'.
     * This conforms to the Krail convention of having, for example, LabelKey, referring to Labels.
     * <p>
     * This can of course be overridden by implementing classes.
     *
     * @return
     */
    default String bundleName() {
        return "uk.q3c.krail.i18n." + this.getClass()
                                          .getSimpleName()
                   .substring(0, getClass().getSimpleName()
                                           .length() - 3) + "s";
    }

}