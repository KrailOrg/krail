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

package uk.q3c.krail.i18n.translate

import spock.lang.Specification
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.UnsupportedLocaleException
import uk.q3c.krail.i18n.locale.DefaultCurrentLocale
import uk.q3c.krail.i18n.persist.PatternSource
import uk.q3c.krail.i18n.test.TestLabelKey
import uk.q3c.util.text.DefaultMessageFormat
import uk.q3c.util.text.MessageFormat2

/**
 * Original replaced by Spock
 *
 * Created by David Sowerby on 14/07/15.
 */
class DefaultTranslateTest extends Specification {

    Translate translate

    CurrentLocale currentLocale

    Locale germanSwitzerland
    PatternSource patternSource = Mock()
    Set<Locale> supportedLocales = new HashSet<>()
    MessageFormat2 messageFormat = new DefaultMessageFormat()

    def setup() {
        Locale.setDefault(Locale.UK)
        currentLocale = new DefaultCurrentLocale()
        translate = new DefaultTranslate(patternSource, currentLocale, messageFormat, supportedLocales)
        currentLocale.setLocale(Locale.UK)
        germanSwitzerland = new Locale("de", "CH")
        patternSource.retrievePattern(LabelKey.Cancel, Locale.UK) >> "Cancel"
        patternSource.retrievePattern(LabelKey.Ok, Locale.UK) >> "Ok"
        patternSource.retrievePattern(LabelKey.Cancel, Locale.GERMANY) >> "Stornieren"
        patternSource.retrievePattern(LabelKey.Ok, Locale.GERMANY) >> "OK"
        patternSource.retrievePattern(LabelKey.Ok, Locale.FRANCE) >> "Ok"
        patternSource.retrievePattern(LabelKey.Cancel, germanSwitzerland) >> "Stornieren"
        patternSource.retrievePattern(LabelKey.Ok, germanSwitzerland) >> "OK"
        patternSource.retrievePattern(TestLabelKey.pattern_with_embedded_key, Locale.UK) >> "Your {0} request has been refused"
        patternSource.retrievePattern(TestLabelKey.pattern_with_embedded_key, Locale.GERMANY) >> "Your {0} request has been refused"
        patternSource.retrievePattern(LabelKey.Log_In, Locale.UK) >> "Log In"
        patternSource.retrievePattern(LabelKey.Log_In, Locale.GERMANY) >> "Einloggen"
    }


    def "translate from"() {
        given:
        supportedLocales.add(Locale.UK)
        supportedLocales.add(Locale.GERMANY)
        supportedLocales.add(germanSwitzerland)

        expect:
        translate.from(LabelKey.Cancel).equals("Cancel")
        translate.from(LabelKey.Ok).equals("Ok")

        translate.from(LabelKey.Cancel, Locale.GERMANY).equals("Stornieren")
        // OK is not redefined in _de
        translate.from(LabelKey.Ok, Locale.GERMANY).equals("OK")

        // this in inherited from Labels_de
        translate.from(LabelKey.Cancel, germanSwitzerland).equals("Stornieren")
        // this is inherited from Labels (2 levels of inheritance)
        translate.from(LabelKey.Ok, germanSwitzerland).equals("OK")
    }


    def "attempt to translate an unsupported locale should throw exception"() {
        given:
        supportedLocales.add(Locale.UK)
        supportedLocales.add(Locale.GERMANY)
        supportedLocales.add(germanSwitzerland)

        when:
        translate.from(LabelKey.Ok, Locale.FRANCE)

        then:
        thrown UnsupportedLocaleException
    }

    def "when pattern contains an I18NKey, that key should also be translated"() {
        given:
        supportedLocales.add(Locale.UK)
        supportedLocales.add(Locale.GERMANY)
        supportedLocales.add(germanSwitzerland)

        when:

        String translation = translate.from(TestLabelKey.pattern_with_embedded_key, LabelKey.Log_In)
        then:

        translation.equals("Your Log In request has been refused")

        when:
        currentLocale.setLocale(Locale.GERMANY)
        translation = translate.from(TestLabelKey.pattern_with_embedded_key, LabelKey.Log_In)

        then:
        translation.equals("Your Einloggen request has been refused")

    }

    def "disabled supported locales check allows any Locale to be processed"() {
        expect:
        translate.from(false, LabelKey.Ok, Locale.FRANCE).equals("Ok")
    }

    def "Explicitly enabled or disabled supported locales check throws exception correctly"() {

        when:
        translate.from(false, LabelKey.Ok, Locale.FRANCE)

        then:

        notThrown UnsupportedLocaleException

        when:
        translate.from(true, LabelKey.Ok, Locale.FRANCE)

        then:
        thrown UnsupportedLocaleException
    }

    def "null key key is null returns 'key is null'"() {
        given:
        supportedLocales.add(Locale.UK)

        when:
        def result = translate.from(null)

        then:
        result == "key is null"


    }


}