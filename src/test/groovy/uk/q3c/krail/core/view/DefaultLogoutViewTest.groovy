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

package uk.q3c.krail.core.view

import com.vaadin.ui.Panel
import com.vaadin.ui.VerticalLayout

/**
 * Created by David Sowerby on 08 Feb 2016
 */
class DefaultLogoutViewTest extends ViewTest {

    def setup() {
        view = new DefaultLogoutView()
    }

    def "doBuild"() {
        given:
        view.buildView(busMessage)

        expect:
        view.getRootComponent() instanceof VerticalLayout
        Panel panel = ((VerticalLayout) view.getRootComponent()).getComponent(0)
        panel != null
        "Logged out".equals(panel.getCaption())
    }
}
