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

import spock.lang.Specification
import uk.q3c.krail.UnitTestFor
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters

/**
 *
 * Converts an Enum to a full String representation (the package name, class name and constannt name concatenated), and back again
 *
 * Created by David Sowerby on 07/08/15.
 */
@UnitTestFor(EnumConverter)
class EnumConverterTest extends Specification {

    EnumConverter converter

    def setup() {
        converter = new EnumConverter()
    }

    def "round trip"() {
        given:

        def DefaultUserSitemapSorters.SortType testValue = DefaultUserSitemapSorters.SortType.INSERTION

        when:
        String s = converter.convertToString(testValue)
        Enum returnedValue = converter.convertToModel(s)

        then:

        returnedValue == testValue
    }

    def "invalid String - no Enum found, throws ConversionException"() {
        when:
        converter.convertToModel("rubbish")


        then:

        thrown ConversionException

    }
}
