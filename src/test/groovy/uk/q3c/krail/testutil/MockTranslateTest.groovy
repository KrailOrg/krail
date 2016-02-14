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

package uk.q3c.krail.testutil

import spock.lang.Specification
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.MessageKey
import uk.q3c.krail.testutil.i18n.MockTranslate

/**
 * Created by David Sowerby on 09 Feb 2016
 */
class MockTranslateTest extends Specification {

    MockTranslate translate

    def setup() {
        translate = new MockTranslate()
    }

    def "all translates"() {
        expect:
        translate.from(LabelKey.Yes).equals('Yes')
        translate.from(MessageKey.Service_not_Started, 'Wiggly').equals('Service not Started')
        translate.from(MessageKey.Service_not_Started, Locale.CANADA, 'Wiggly').equals('Service not Started')
        translate.from(true, MessageKey.Service_not_Started, Locale.CANADA, 'Wiggly').equals('Service not Started')
    }

    def "no collator, throw exception"() {
        when:
        translate.collator()

        then:
        thrown(RuntimeException)
    }
}
