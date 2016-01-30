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
import uk.q3c.krail.core.data.DefaultOptionElementConverter
import uk.q3c.krail.core.data.OptionElementConverter

class OptionListTest extends Specification {


    OptionElementConverter optionStringConverter

    def setup() {
        optionStringConverter = new DefaultOptionElementConverter()
    }

    def "construct from list"() {
        given:
        ImmutableList<String> sourceList = ImmutableList.of("a", "b")

        when:

        OptionList<String> optionList = new OptionList<>(sourceList, String.class)

        then:

        optionList.getElementClass() == String.class
        optionList.getList().equals(sourceList)
        optionList.size() == 2
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

    def "construct empty"() {
        when:
        OptionList<String> optionList = new OptionList<>(String.class)

        then:
        optionList.getList().equals(ImmutableList.of())
    }

    def "construct with varargs"() {
        when:
        OptionList<String> optionList = new OptionList<>(String.class, "a", "b")

        then:
        optionList.size() == 2
        !optionList.isEmpty()
        optionList.getList().equals(ImmutableList.of("a", "b"))

    }

    def "equals and hashcode"() {
        given:
        OptionList<String> refEmpty = new OptionList<>(String.class)
        OptionList<String> ref = new OptionList<>(String.class, "a", "b")
        OptionList<Integer> differentClassEmpty = new OptionList<>(Integer.class)
        OptionList<String> same = new OptionList<>(String.class, "a", "b")
        OptionList<String> sameEmpty = new OptionList<>(String.class)
        OptionList<String> differentList = new OptionList<>(String.class, "b")

        expect:
        !differentClassEmpty.equals(refEmpty)
        !differentClassEmpty.hashCode() != refEmpty.hashCode()

        !differentList.equals(refEmpty)
        !differentList.hashCode() != refEmpty.hashCode()

        same.equals(ref)
        same.hashCode() == ref.hashCode()

        sameEmpty.equals(refEmpty)
        sameEmpty.hashCode() == refEmpty.hashCode()
    }
}
