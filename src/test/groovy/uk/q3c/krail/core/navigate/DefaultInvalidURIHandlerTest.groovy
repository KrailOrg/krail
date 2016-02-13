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

package uk.q3c.krail.core.navigate

import spock.lang.Specification
import uk.q3c.krail.core.i18n.MessageKey
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey
import uk.q3c.krail.core.user.notify.UserNotifier

/**
 * Created by David Sowerby on 28 Dec 2015
 */
class DefaultInvalidURIHandlerTest extends Specification {

    DefaultInvalidURIHandler handler

    UserNotifier userNotifier = Mock(UserNotifier)

    Navigator navigator = Mock(Navigator)


    def setup() {
        handler = new DefaultInvalidURIHandler(userNotifier)
    }

    def "invoke with valid current state calls user notifier"() {

        when:

        handler.invoke(navigator, "somewhere")

        then:

        1 * navigator.getCurrentNavigationState() >> new NavigationState()
        1 * userNotifier.notifyInformation(MessageKey.Invalid_URI, "somewhere")
    }

    def "invoke with null current state navigates to home, then calls user notifier"() {
        when:

        handler.invoke(navigator, "somewhere")

        then:

        1 * navigator.getCurrentNavigationState() >> null
        1 * navigator.navigateTo(StandardPageKey.Public_Home)
        1 * userNotifier.notifyInformation(MessageKey.Invalid_URI, "somewhere")
    }
}
