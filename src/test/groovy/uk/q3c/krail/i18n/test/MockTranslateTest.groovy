package uk.q3c.krail.i18n.test

import spock.lang.Specification
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.i18n.UnsupportedLocaleException
import uk.q3c.util.text.MessageFormatException

/**
 * Created by David Sowerby on 06 Aug 2017
 */
class MockTranslateTest extends Specification {

    MockTranslate translate

    def setup() {
        translate = new MockTranslate()
    }

    def "default settings"() {

        expect:
        translate.from(LabelKey.Yes) == "Yes"


    }

    def "full response, no params"() {
        given:
        translate.returnNameOnly = false

        expect:
        translate.from(LabelKey.Yes) == "Yes-en-GB"
    }

    def "full response, with params"() {
        given:
        translate.returnNameOnly = false

        expect:
        translate.from(LabelKey.Yes, 1, 2) == "Yes-en-GB-1,2"
        translate.from(LabelKey.Yes, Locale.GERMANY, 1, 2) == "Yes-de-DE-1,2"
    }

    def "fail on strictness"() {
        given:
        translate.failOnStrictness = true

        when:
        translate.from(LabelKey.Yes)

        then:
        thrown MessageFormatException
    }

    def "fail on supported locale"() {
        given:
        translate.failOnCheckLocaleIsSupported = true

        when:
        translate.from(LabelKey.Yes)

        then:
        thrown UnsupportedLocaleException
    }

    def "Both fails set, checkLocale reported"() {
        given:
        translate.failOnCheckLocaleIsSupported = true
        translate.failOnStrictness = true

        when:
        translate.from(LabelKey.Yes)

        then:
        thrown UnsupportedLocaleException
    }
}