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

import com.vaadin.ui.Component
import uk.q3c.krail.core.navigate.sitemap.View
import uk.q3c.krail.core.shiro.PageAccessControl
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.core.view.NavigationStateExt
import uk.q3c.krail.i18n.I18NKey

/**
 * This is NOT UIScoped to avoid unnecessary complexity in setting up some of the tests - if you need a UIScoped test
 * view you will need to create a different one
 *
 * @author David Sowerby
 */
@View(uri = "a/b", labelKeyName = "Confirm_Ok", pageAccessControl = PageAccessControl.PERMISSION)
class TestAnnotatedView : KrailView {
    override lateinit var rootComponent: Component

    override fun beforeBuild(navigationStateExt: NavigationStateExt) {
        TODO()
    }

    override fun buildView() {
        TODO()
    }

    override fun afterBuild() {
        TODO()
    }




    override fun init() {}



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
