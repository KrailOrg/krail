package uk.q3c.krail.i18n.locale

import spock.lang.Specification
import uk.q3c.krail.i18n.CurrentLocale

/**
 * Created by David Sowerby on 02 Aug 2017
 */
class DefaultCurrentLocaleTest extends Specification {

    CurrentLocale currentLocale

    def setup() {
        currentLocale = new DefaultCurrentLocale()
    }

    def "construction to Java default Locale"() {
        expect:
        currentLocale.locale == Locale.default
    }

    def "set new value"() {
        when:
        currentLocale.locale = Locale.FRANCE


        then:
        currentLocale.locale == Locale.FRANCE

        when:
        currentLocale.setLocale(Locale.CHINA, false)

        then:
        currentLocale.locale == Locale.CHINA
    }
}
