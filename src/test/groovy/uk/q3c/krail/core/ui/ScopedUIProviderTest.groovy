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

import com.google.inject.Injector
import com.vaadin.server.UIClassSelectionEvent
import com.vaadin.ui.UI
import spock.lang.Specification
import uk.q3c.krail.core.guice.uiscope.UIKeyProvider
import uk.q3c.util.testutil.LogMonitor

/**
 * Created by David Sowerby on 08 Feb 2016
 */
class ScopedUIProviderTest extends Specification {

    ScopedUIProvider provider
    Injector injector = Mock()
    UIKeyProvider keyProvider = Mock()
    Map<String, Class<? extends ScopedUI>> map
    LogMonitor logMonitor

    def setup() {
        provider = new ScopedUIProvider()
        logMonitor = new LogMonitor()
        logMonitor.addClassFilter(ScopedUIProvider)
    }

    def cleanup() {
        logMonitor.close()
    }

    def "getUIClass with no entries in map, throws UIProviderException"() {
        given:
        map = new HashMap<>()
        UIClassSelectionEvent event = Mock()
        provider.init(injector, keyProvider, map)

        when:
        provider.getUIClass(event)

        then:
        thrown(UIProviderException)
    }

    def "more than one defined, log warning and return the first"() {
        given:
        map = new HashMap<>()
        map.put("a", BasicUI)
        map.put("b", DefaultApplicationUI)
        UIClassSelectionEvent event = Mock()
        provider.init(injector, keyProvider, map)

        when:
        Class<? extends UI> result = provider.getUIClass(event)

        then:
        logMonitor.warnCount() == 1
        result == BasicUI.class
    }

    def "get() returns UI.getCurrent()"() {
        given:
        ScopedUI ui = Mock()
        UI.setCurrent(ui)

        expect:
        provider.get() == ui
    }
}
