package uk.q3c.krail.core.navigate

import spock.lang.Specification
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey
import uk.q3c.krail.core.user.notify.UserNotifier
import uk.q3c.krail.i18n.MessageKey

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
