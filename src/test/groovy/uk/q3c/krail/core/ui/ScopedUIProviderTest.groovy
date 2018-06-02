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
import com.google.inject.Provider
import com.vaadin.server.UIClassSelectionEvent
import com.vaadin.server.UICreateEvent
import com.vaadin.ui.UI
import spock.lang.Specification
import uk.q3c.util.testutil.LogMonitor
/**
 * Created by David Sowerby on 08 Feb 2016
 */
class ScopedUIProviderTest extends Specification {

    ScopedUIProvider provider
    Injector injector = Mock()
    Map<String, Class<? extends ScopedUI>> uiMapBinder
    Map<String, Provider<ScopedUI>> uiMapBinderProvider
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
        uiMapBinder = new HashMap<>()
        uiMapBinderProvider = new HashMap<>()
        UIClassSelectionEvent event = Mock()
        provider.init(uiMapBinder, uiMapBinderProvider)

        when:
        provider.getUIClass(event)

        then:
        thrown(UIProviderException)
    }

    def "more than one defined, log warning and return the first"() {
        given:

        uiMapBinder = new HashMap<>()
        uiMapBinder.put("a", BasicUI)
        uiMapBinder.put("b", DefaultApplicationUI)

        UIClassSelectionEvent event = Mock()
        provider.init(uiMapBinder, uiMapBinderProvider)

        when:
        Class<? extends UI> result = provider.getUIClass(event)

        then:
        logMonitor.warnCount() == 1
        result == BasicUI.class
    }

    def "create instance"() {
        given:
        BasicUI basicUI = Mock()
        Provider<BasicUI> basicUiProvider = Mock()
        basicUiProvider.get() >> basicUI
        uiMapBinderProvider = new HashMap<>()
        uiMapBinderProvider.put(BasicUI.class.getName(), basicUiProvider)

        uiMapBinder = new HashMap<>()
        uiMapBinder.put(BasicUI.class.getName(), BasicUI)
        UICreateEvent event = Mock()
        event.getUIClass() >> BasicUI.class
        provider.init(uiMapBinder, uiMapBinderProvider)

        when:
        ScopedUI result = provider.createInstance(event)

        then:
        logMonitor.warnCount() == 0
        result == basicUI
    }

    def "get() returns UI.getCurrent()"() {
        given:
        ScopedUI ui = Mock()
        UI.setCurrent(ui)

        expect:
        provider.get() == ui
    }
}
