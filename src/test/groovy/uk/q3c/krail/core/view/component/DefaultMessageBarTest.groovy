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

package uk.q3c.krail.core.view.component

import com.vaadin.ui.HorizontalLayout
import net.engio.mbassy.listener.Handler
import spock.lang.Specification
import uk.q3c.krail.core.user.notify.ErrorNotificationMessage
import uk.q3c.krail.core.user.notify.InformationNotificationMessage
import uk.q3c.krail.core.user.notify.WarningNotificationMessage
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.test.MockTranslate

/**
 * Created by David Sowerby on 09 Feb 2016
 */
class DefaultMessageBarTest extends Specification {

    DefaultMessageBar bar
    Translate translate = new MockTranslate()

    def setup() {
        bar = new DefaultMessageBar(translate)
    }

    def "construct and build"() {
        expect:
        bar.getDisplay() != null
        bar.getDisplay().getValue().equals('Message Bar')
        bar.getContent() instanceof HorizontalLayout
        ((HorizontalLayout) bar.getContent()).contains(bar.getDisplay())
    }

    def "error message displayed"() {
        given:
        ErrorNotificationMessage message = Mock()
        message.getTranslatedMessage() >> 'Really big blunder'

        when:
        bar.errorMessage(message)

        then:
        bar.getDisplay().getValue().equals('ERROR: Really big blunder')
    }

    def "warning message displayed"() {
        given:
        WarningNotificationMessage message = Mock()
        message.getTranslatedMessage() >> 'Fairly big blunder'

        when:
        bar.warningMessage(message)

        then:
        bar.getDisplay().getValue().equals('Warning: Fairly big blunder')
    }

    def "information message displayed"() {
        given:
        InformationNotificationMessage message = Mock()
        message.getTranslatedMessage() >> 'No blunder, just info'

        when:
        bar.informationMessage(message)

        then:
        bar.getDisplay().getValue().equals('No blunder, just info')
    }

    def "handler methods annotated with @Handler"() {

        expect:
        DefaultMessageBar.getMethod('errorMessage', ErrorNotificationMessage).isAnnotationPresent(Handler)
        DefaultMessageBar.getMethod('warningMessage', WarningNotificationMessage).isAnnotationPresent(Handler)
        DefaultMessageBar.getMethod('informationMessage', InformationNotificationMessage).isAnnotationPresent(Handler)
    }
}
