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

package uk.q3c.krail.core.user.notify

import com.vaadin.server.Page
import com.vaadin.ui.UI
import spock.lang.Specification
import uk.q3c.util.testutil.LogMonitor

/**
 * Created by David Sowerby on 06 Feb 2016
 */
class DefaultVaadinNotificationTest extends Specification {

    DefaultVaadinNotification notification
    LogMonitor logMonitor
    UI ui
    Page page


    def setup() {
        notification = new DefaultVaadinNotification()
        logMonitor = new LogMonitor()
        logMonitor.addClassFilter(DefaultVaadinNotification)
        ui = Mock(UI)
        page = Mock(Page)
        ui.getPage() >> page
        UI.setCurrent(ui)
    }

    def cleanup() {
        logMonitor.close()
    }

    def "error"() {
        given:
        ErrorNotificationMessage message = new ErrorNotificationMessage("translated message")

        when:
        notification.errorMessage(message)

        then:
        1 * page.showNotification(_)
        logMonitor.debugCount() == 1
    }

    def "warn"() {
        given:
        WarningNotificationMessage message = new WarningNotificationMessage("translated message")

        when:
        notification.warningMessage(message)

        then:
        1 * page.showNotification(_)
        logMonitor.debugCount() == 1
    }

    def "info"() {
        given:
        InformationNotificationMessage message = new InformationNotificationMessage("translated message")

        when:
        notification.informationMessage(message)

        then:
        1 * page.showNotification(_)
        logMonitor.debugCount() == 1
    }
}
