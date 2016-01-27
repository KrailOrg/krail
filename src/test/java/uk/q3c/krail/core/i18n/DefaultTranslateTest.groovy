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

package uk.q3c.krail.core.i18n

import com.google.inject.Inject
import spock.guice.UseModules
import spock.lang.Specification
import uk.q3c.krail.core.data.DataModule
import uk.q3c.krail.core.eventbus.EventBusModule
import uk.q3c.krail.core.guice.uiscope.UIScopeModule
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.testutil.MockOption
import uk.q3c.krail.testutil.TestI18NModule
import uk.q3c.krail.testutil.TestOptionModule
import uk.q3c.krail.testutil.TestPersistenceModule

/**
 * Original replaced by Spock
 *
 * Created by David Sowerby on 14/07/15.
 */
@UseModules([TestI18NModule, TestOptionModule, TestPersistenceModule, EventBusModule, UIScopeModule, VaadinSessionScopeModule, DataModule])
class DefaultTranslateTest extends Specification {

    @Inject
    Translate translate;

    @Inject
    CurrentLocale currentLocale;

    @Inject
    MockOption option;

    Locale germanSwitzerland

    def setup() {
        Locale.setDefault(Locale.UK);
        currentLocale.setLocale(Locale.UK);
        germanSwitzerland = new Locale("de", "CH");
    }


    def "translate from"() {

        expect:
        translate.from(LabelKey.Cancel).equals("Cancel");
        translate.from(LabelKey.Ok).equals("Ok")

        translate.from(LabelKey.Cancel, Locale.GERMANY).equals("Stornieren");
        // OK is not redefined in _de
        translate.from(LabelKey.Ok, Locale.GERMANY).equals("OK");

        // this in inherited from Labels_de
        translate.from(LabelKey.Cancel, germanSwitzerland).equals("Stornieren");
        // this is inherited from Labels (2 levels of inheritance)
        translate.from(LabelKey.Ok, germanSwitzerland).equals("OK");
    }


    def "attempt to translate an unsupported locale should throw exception"() {
        when:
        translate.from(LabelKey.Ok, Locale.FRANCE)

        then:
        thrown UnsupportedLocaleException
    }

    def "when pattern contains an I18NKey, that key should also be translated"() {

        when:

        String translation = translate.from(TestLabelKey.pattern_with_embedded_key, LabelKey.Log_In);
        then:

        translation.equals("Your Log In request has been refused")

        when:
        currentLocale.setLocale(Locale.GERMANY)
        translation = translate.from(TestLabelKey.pattern_with_embedded_key, LabelKey.Log_In);

        then:
        translation.equals("Your Einloggen request has been refused");

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

    def "null key should return 'key is null'"() {

        expect:

        translate.from(null).equals("key is null")


    }


}
