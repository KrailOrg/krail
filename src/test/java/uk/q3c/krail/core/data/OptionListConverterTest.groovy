/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.data

import org.apache.commons.collections15.ListUtils
import spock.lang.Specification
import uk.q3c.krail.UnitTestFor
import uk.q3c.krail.core.option.OptionList

/**
 *
 * Created by David Sowerby on 06/08/15.
 */
@UnitTestFor(OptionListConverter)
class OptionListConverterTest extends Specification {

    OptionListConverter converter

    def setup() {
        converter = new OptionListConverter(new DefaultOptionStringConverter())
    }


    def "convert empty list to String, should be empty String"() {
        given:
        OptionList<String> optionList = new OptionList<>(String.class)

        expect:
        converter.convertToString(optionList).equals("")
    }

    def "convert empty String to List, should be empty list"() {
        given:
        OptionList<String> defaultValue = new OptionList<>(String.class, "a", "b")

        expect:
        converter.convertToModel(defaultValue, "").isEmpty()
    }

    def "round trip convert list with comma, single quote and double quote inside original String list entries"() {
        given:
        OptionList<String> testValue = new OptionList<>(String.class, "a,b", "d\"q")
        OptionList<String> defaultValue = new OptionList<>(String.class)

        when:
        String s = converter.convertToString(testValue)
        OptionList<String> returnedValue = converter.convertToModel(defaultValue, s)
        then:
        ListUtils.isEqualList(testValue.getList(), returnedValue.getList())
        returnedValue.getElementClass().equals(String.class)
    }


    def "round trip a list of numeric values"() {
        given:
        OptionList<Integer> testValue = new OptionList<>(Integer.class, 1, 3)
        OptionList<Integer> defaultValue = new OptionList<>(Integer.class, 12, 3)

        when:
        String s = converter.convertToString(testValue)
        OptionList<Integer> returnedValue = converter.convertToModel(defaultValue, s)
        then:
        ListUtils.isEqualList(testValue.getList(), returnedValue.getList())
        returnedValue.getElementClass().equals(Integer.class)
    }


}
