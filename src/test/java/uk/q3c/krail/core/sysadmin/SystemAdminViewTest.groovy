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

package uk.q3c.krail.core.sysadmin

import uk.q3c.krail.core.i18n.Caption
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.view.ViewTest

/**
 * Created by David Sowerby on 07 Feb 2016
 */
class SystemAdminViewTest extends ViewTest {

    Navigator navigator = Mock()
    SystemAdminView thisView

    def setup() {
        thisView = new SystemAdminView(navigator)
        view = thisView
    }

    def "button has caption"() {
        expect:
        fieldHasCaption("buildReportBtn", Caption)
    }

    def "doBuild"() {
        when:
        thisView.doBuild(busMessage)

        then:
        thisView.getBuildReportBtn() != null
    }

    def "buildButton invokes navigator"() {
        given:
        view.buildView(busMessage)

        when:
        thisView.getBuildReportBtn().click()

        then:
        1 * navigator.navigateTo("system-admin/sitemap-build-report")
    }


}
