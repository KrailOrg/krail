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

package uk.q3c.krail.core.ui

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.TypeLiteral
import spock.lang.Specification
import uk.q3c.krail.core.i18n.LabelKey

/**
 * Created by David Sowerby on 08 Feb 2016
 */
class DefaultUIModuleTest extends Specification {

    def "configure with defaults"() {
        given:
        DefaultUIModule module = new DefaultUIModule();
        TypeLiteral<Map<String, Class<? extends ScopedUI>>> mapTypeLiteral = new TypeLiteral<Map<String, Class<? extends ScopedUI>>>() {
        }
        Key<Map<String, Class<? extends ScopedUI>>> mapKey = Key.get(mapTypeLiteral)

        when:
        Injector injector = Guice.createInjector(module)

        then:
        ApplicationTitle applicationTitle = injector.getInstance(ApplicationTitle)
        applicationTitle != null
        applicationTitle.titleKey == LabelKey.Krail
        Map<String, Class<? extends ScopedUI>> map = injector.getInstance(mapKey)
        map != null
        map.get(DefaultApplicationUI.getName()) == DefaultApplicationUI.class
    }

    def "configure with defaults changed"() {
        given:
        DefaultUIModule module = new DefaultUIModule().uiClass(BasicUI).applicationTitleKey(LabelKey.Yes);
        TypeLiteral<Map<String, Class<? extends ScopedUI>>> mapTypeLiteral = new TypeLiteral<Map<String, Class<? extends ScopedUI>>>() {
        }
        Key<Map<String, Class<? extends ScopedUI>>> mapKey = Key.get(mapTypeLiteral)

        when:
        Injector injector = Guice.createInjector(module)

        then:
        ApplicationTitle applicationTitle = injector.getInstance(ApplicationTitle)
        applicationTitle != null
        applicationTitle.titleKey == LabelKey.Yes
        Map<String, Class<? extends ScopedUI>> map = injector.getInstance(mapKey)
        map != null
        map.get(BasicUI.getName()) == BasicUI.class
        map.get(DefaultApplicationUI.getName()) == null
    }
}
