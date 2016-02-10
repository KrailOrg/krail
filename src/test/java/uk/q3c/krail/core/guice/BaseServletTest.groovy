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

package uk.q3c.krail.core.guice

import com.vaadin.server.DeploymentConfiguration
import com.vaadin.server.SessionInitEvent
import com.vaadin.server.VaadinSession
import spock.lang.Specification
import uk.q3c.krail.core.ui.ScopedUIProvider

/**
 * Mostly this has to be functionally tested
 *
 * Created by David Sowerby on 10 Feb 2016
 */
class BaseServletTest extends Specification {

    BaseServlet servlet
    ScopedUIProvider uiProvider = Mock()
    VaadinSession vaadinSession = Mock()

    def setup() {
        servlet = new BaseServlet(uiProvider)
    }

    def "session init"() {
        given:
        SessionInitEvent event = Mock()
        event.getSession() >> vaadinSession

        when:
        servlet.sessionInit(event)

        then:
        1 * vaadinSession.addUIProvider(uiProvider)
    }

    def "defaults"() {
        expect:
        servlet.widgetset().equals("default")
        !servlet.productionMode()
    }

    def "createDeploymentConfiguration, widgetset of 'default' not added to properties, productionMode false"() {
        given:
        Properties properties = new Properties()

        when:
        DeploymentConfiguration deploymentConfiguration = servlet.createDeploymentConfiguration(properties)

        then:
        deploymentConfiguration != null
        deploymentConfiguration.getWidgetset("any").equals("any") //'default does not get added to properties
        !deploymentConfiguration.isProductionMode()
    }

}
