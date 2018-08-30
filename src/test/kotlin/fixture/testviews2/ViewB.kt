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
package fixture.testviews2

import com.google.inject.Inject
import com.vaadin.ui.Component
import com.vaadin.ui.Label
import fixture.TestViewChangeListener
import uk.q3c.krail.core.navigate.NavigationState
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.core.view.NavigationStateExt
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage
import uk.q3c.krail.core.view.component.ViewChangeBusMessage
import uk.q3c.krail.i18n.I18NKey

class ViewB @Inject
constructor(private val changeListener: TestViewChangeListener) : KrailView {
    override fun beforeBuild(navigationStateExt: NavigationStateExt?) {
    }

    override fun buildView() {
    }

    override fun afterBuild() {
    }


    private val label = Label("not used")

    override fun beforeBuild(busMessage: ViewChangeBusMessage) {
        changeListener.addCall("beforeBuild", busMessage)
    }

    override fun buildView(busMessage: ViewChangeBusMessage) {
        changeListener.addCall("buildView", busMessage)
    }

    override fun getRootComponent(): Component {
        return label
    }


    override fun init() {
        changeListener.addCall("init", EmptyViewChangeBusMessage())
    }

    override fun afterBuild(busMessage: AfterViewChangeBusMessage) {
        changeListener.addCall("afterBuild", busMessage)
    }


    override fun getNameKey(): I18NKey? {
        return null
    }

    override fun setNameKey(nameKey: I18NKey) {

    }

    override fun getDescriptionKey(): I18NKey? {
        return null
    }

    override fun setDescriptionKey(descriptionKey: I18NKey) {

    }

    override fun getName(): String? {
        return null
    }

    override fun getDescription(): String? {
        return null
    }
}

class EmptyViewChangeBusMessage : ViewChangeBusMessage(NavigationState(), NavigationState())
class EmptyAfterViewChangeBusMessage : AfterViewChangeBusMessage(NavigationState(), NavigationState())