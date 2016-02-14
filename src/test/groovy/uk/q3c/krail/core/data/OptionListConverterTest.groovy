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

package uk.q3c.krail.core.data

import org.apache.commons.collections15.ListUtils
import spock.lang.Specification
import uk.q3c.krail.core.option.OptionList
/**
 *
 * Created by David Sowerby on 06/08/15.
 */
class OptionListConverterTest extends Specification {

    OptionListConverter converter

    def setup() {
        converter = new OptionListConverter(new DefaultOptionElementConverter())
    }


    def "convert empty list to String, should be empty String"() {
        given:
        OptionList<String> optionList = new OptionList<>(String.class)

        expect:
        converter.convertToString(optionList).equals("")
    }


    def "round trip convert list with comma, single quote and double quote inside original String list entries"() {
        given:
        OptionList<String> defaultValue = new OptionList<>(String.class)
        OptionList<String> testValue = new OptionList<>(String.class, "a,b", "d\"q")

        when:
        String s = converter.convertToString(testValue)
        OptionList<String> returnedValue = converter.convertToModel(defaultValue, s)

        then:
        ListUtils.isEqualList(testValue.getList(), returnedValue.getList())
        returnedValue.getElementClass().equals(String.class)
    }


    def "round trip a list of numeric values"() {
        given:
        OptionList<Integer> defaultValue = new OptionList<>(Integer.class)
        OptionList<Integer> testValue = new OptionList<>(Integer.class, 1, 3)

        when:
        String s = converter.convertToString(testValue)
        OptionList<Integer> returnedValue = converter.convertToModel(defaultValue, s)
        then:
        ListUtils.isEqualList(testValue.getList(), returnedValue.getList())
        returnedValue.getElementClass().equals(Integer.class)
    }

    def "round trip empty list"() {
        given:
        OptionList<Integer> defaultValue = new OptionList<>(Integer.class)
        OptionList<Integer> testValue = new OptionList<>(Integer.class)

        when:
        String s = converter.convertToString(testValue)
        OptionList<Integer> returnedValue = converter.convertToModel(defaultValue, s)

        then:
        ListUtils.isEqualList(testValue.getList(), returnedValue.getList())
        returnedValue.getElementClass().equals(Integer.class)
    }

    def "convertToString NPE on null"() {
        when:
        converter.convertToString(null)

        then:
        thrown(NullPointerException)
    }

    def "construct with null NPE"() {
        when:
        converter = new OptionListConverter(null)

        then:
        thrown(NullPointerException)
    }

    def "convert to model, null element class, NPE"() {

        when:
        converter.convertToModel(null, "x")

        then:
        thrown(NullPointerException)
    }

    def "convert to model, null value, NPE"() {
        given:
        OptionList<Integer> defaultValue = new OptionList<>(Integer.class)

        when:
        converter.convertToModel(defaultValue, null)

        then:
        thrown(NullPointerException)
    }

    def "convert to String, null value NPE"() {
        when:
        converter.convertToString(null)

        then:
        thrown(NullPointerException)

    }


}
