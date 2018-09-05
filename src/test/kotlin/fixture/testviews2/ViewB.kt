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
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode
import uk.q3c.krail.core.navigate.sitemap.NodeRecord
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.core.view.NavigationStateExt
import uk.q3c.krail.i18n.I18NKey

class ViewB @Inject
constructor(private val changeListener: TestViewChangeListener) : KrailView {
    private val label = Label("not used")
    override var rootComponent: Component = label
    lateinit var navigationStateExt: NavigationStateExt

    override fun beforeBuild(navigationStateExt: NavigationStateExt) {
        this.navigationStateExt = navigationStateExt
        changeListener.addCall("beforeBuild", navigationStateExt)
    }

    override fun buildView() {
        changeListener.addCall("buildView", navigationStateExt)
    }


    override fun init() {
        changeListener.addCall("init", NavigationStateExt(NavigationState(), NavigationState(), UserSitemapNode(MasterSitemapNode(1, NodeRecord("?")))))
    }

    override fun afterBuild() {
        changeListener.addCall("afterBuild", navigationStateExt)
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

