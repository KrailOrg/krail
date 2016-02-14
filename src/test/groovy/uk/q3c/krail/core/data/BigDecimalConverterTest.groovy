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
/**
 *
 * Created by David Sowerby on 04/08/15.
 */
class BigDecimalConverterTest extends Specification {

    BigDecimalConverter converter

    def setup() {
        converter = new BigDecimalConverter()
    }

    def "roundTrip"() {
        given:

        String input = "123.4321125"

        when:

        BigDecimal model = converter.convertToModel(input, BigDecimal.class, Locale.UK)
        String presentation = converter.convertToPresentation(model, String.class, Locale.UK)

        then:
        presentation.equals(input)
    }

    def "model class"() {
        expect:
        converter.modelType.equals(BigDecimal.class)
    }

    def "presentation class"() {
        expect:
        converter.presentationType.equals(String.class)
    }
}
