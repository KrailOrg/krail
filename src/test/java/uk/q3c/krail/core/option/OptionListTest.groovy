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

package uk.q3c.krail.core.option

import com.google.common.collect.ImmutableList
import spock.lang.Specification

class OptionListTest extends Specification {


    def "construct from list"() {
        given:
        ImmutableList<String> sourceList = ImmutableList.of("a", "b")

        when:

        OptionList<String> optionList = new OptionList<>(sourceList, String.class)

        then:

        optionList.getElementClass() == String.class
        optionList.getList().equals(sourceList)
        !optionList.isEmpty()
    }

    def "construct from list, list is null"() {
        when:
        OptionList<String> optionList = new OptionList<>(null, String.class)

        then:
        thrown(NullPointerException)
    }

    def "construct from list, class is null"() {
        given:
        ImmutableList<String> sourceList = ImmutableList.of("a", "b")

        when:
        OptionList<String> optionList = new OptionList<>(sourceList, null)

        then:
        thrown(NullPointerException)
    }
}
