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

package testutil

import spock.lang.Specification
import uk.q3c.krail.core.view.component.LocaleContainer

/**
 * Created by David Sowerby on 06 Feb 2016
 */
class MockOptionTest extends Specification {

    MockOption option

    def setup() {
        option = new MockOption()
    }

    def "return default"() {
        expect:
        option.get(LocaleContainer.optionKeyFlagSize) == 32
        option.getHierarchy() != null
        option.getHierarchy().toString().startsWith("Mock for UserHierarchy")
    }

    def "set, get and delete"() {
        when:
        option.set(LocaleContainer.optionKeyFlagSize, 43)

        then:
        option.get(LocaleContainer.optionKeyFlagSize) == 43
        option.getLowestRanked(LocaleContainer.optionKeyFlagSize) == 43
        option.getSpecificRanked(0, LocaleContainer.optionKeyFlagSize) == 43


        when:
        option.delete(LocaleContainer.optionKeyFlagSize, 0)

        then:
        option.get(LocaleContainer.optionKeyFlagSize) == 32
    }
}
