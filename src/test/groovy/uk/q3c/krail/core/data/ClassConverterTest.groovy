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
import uk.q3c.krail.core.persist.clazz.i18n.ClassPatternSource
/**
 *
 * Created by David Sowerby on 08/08/15.
 */
class ClassConverterTest extends Specification {

    ClassConverter converter

    def setup() {
        converter = new ClassConverter()
    }

    def "round trip "() {

        expect:
        converter.convertToModel(converter.convertToString(ClassPatternSource)).equals(ClassPatternSource)
    }

    def "convert invalid class name throws ConversionException"() {
        when:

        converter.convertToModel("rubbish")

        then:

        thrown ConversionException
    }
}
