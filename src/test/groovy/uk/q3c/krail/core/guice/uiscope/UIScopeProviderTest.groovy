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

package uk.q3c.krail.core.guice.uiscope

import com.google.inject.Key
import com.google.inject.Provider
import com.vaadin.ui.UI
import com.vaadin.util.CurrentInstance
import spock.lang.Specification
import uk.q3c.krail.core.ui.ScopedUI
/**
 * Created by David Sowerby on 09 Feb 2016
 */
class UIScopeProviderTest extends Specification {

    UIScopeProvider provider
    UIScope uiScope = Mock()
    Key key = Mock()
    Provider unscoped = Mock()
    ScopedUI ui = Mock()
    UIKey uiKey = new UIKey()
    Map<Key<?>, Object> scopedObjects = new HashMap<>()
    Object returnedObject = Mock()

    def setup() {
        provider = new UIScopeProvider(uiScope, key, unscoped)
        UI.setCurrent(null)
        CurrentInstance.clearAll()
    }

    def "ui and uiKey are null, throw UIScopeException"() {
        when:
        provider.get()

        then:
        UIScopeException ex = thrown()
        ex.getMessage().contains('This can happen if you include UIScoped components')
    }

    def "uiKey is null and cannot be retrieved from the current UI, throw UIScopeException"() {
        given:
        UI.setCurrent(ui)

        when:
        provider.get()

        then:
        UIScopeException ex = thrown()
        ex.getMessage().contains('uiKey is null and cannot be obtained from the UI')

    }

    def "uiKey is not null, currentUI is not null, but currentUI has different uiKey, throw UIScopeException"() {
        given:
        UIKey uiKey2 = new UIKey()
        UI.setCurrent(ui)
        ui.getInstanceKey() >> uiKey2
        CurrentInstance.set(UIKey, uiKey)

        when:
        provider.get()

        then:
        UIScopeException ex = thrown()
        ex.getMessage().contains('The UI and its UIKey have got out of sync')

    }

    def "uiKey and currentUI resolve, key has existing entry and returns it"() {
        given:
        UI.setCurrent(ui)
        ui.getInstanceKey() >> uiKey
        CurrentInstance.set(UIKey, uiKey)
        uiScope.getScopedObjectMap(uiKey) >> scopedObjects
        scopedObjects.put(key, returnedObject)

        when:
        Object result = provider.get()

        then:
        result == returnedObject
    }

    def "uiKey and currentUI resolve, key has no existing entry, creates and adds it to scope"() {
        given:
        UI.setCurrent(ui)
        ui.getInstanceKey() >> uiKey
        key.toString() >> 'key to string'
        CurrentInstance.set(UIKey, uiKey)
        uiScope.getScopedObjectMap(uiKey) >> scopedObjects
        unscoped.get() >> returnedObject

        when:
        Object result = provider.get()

        then:
        result != null
        result == returnedObject
        scopedObjects.get(key) == returnedObject
    }
}
