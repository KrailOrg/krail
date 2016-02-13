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

import com.vaadin.server.Page
import com.vaadin.server.WebBrowser
import com.vaadin.ui.UI
import spock.lang.Specification

/**
 * Created by David Sowerby on 08 Feb 2016
 */
class BrowserProviderTest extends Specification {

    Page page = Mock()
    UI ui = Mock()
    WebBrowser browser


    def "get"() {
        given:
        UI.setCurrent(ui)
        ui.getPage() >> page
        page.getWebBrowser() >> browser
        BrowserProvider provider = new BrowserProvider()

        expect:
        provider.get() == browser

    }
}
