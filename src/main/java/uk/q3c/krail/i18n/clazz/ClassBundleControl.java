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

package uk.q3c.krail.i18n.clazz;

import uk.q3c.krail.i18n.persist.source.DefaultPatternSource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Bundle control to allow loading from classes only, and only the requested locale (that is, it does not look at other candidate locales - that is managed through {@link DefaultPatternSource} instead
 * <p>
 * Created by David Sowerby on 08/12/14.
 */
public class ClassBundleControl extends ResourceBundle.Control {


    @Override
    public List<String> getFormats(String baseName) {
        return ResourceBundle.Control.FORMAT_CLASS;
    }


    @Override
    public List<Locale> getCandidateLocales(String baseName, Locale locale) {
        return Arrays.asList(locale);
    }
}
