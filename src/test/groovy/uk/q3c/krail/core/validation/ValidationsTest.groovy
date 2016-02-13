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

package uk.q3c.krail.core.validation

import spock.lang.Specification

/**
 * Created by David Sowerby on 06 Feb 2016
 */
class ValidationsTest extends Specification {


    def "de"() {
        given:
        Validations_de de = new Validations_de()
        de.setKeyClass(ValidationKey)
        de.load()


        expect:
        for (ValidationKey key : ValidationKey.values()) {
            assert de.getMap().get(key) != null
        }
    }

    def "es"() {
        given:
        Validations_es es = new Validations_es()
        es.setKeyClass(ValidationKey)
        es.load()


        expect:
        for (ValidationKey key : ValidationKey.values()) {
            assert es.getMap().get(key) != null
        }
    }

    def "it"() {
        given:
        Validations_it its = new Validations_it()
        its.setKeyClass(ValidationKey)
        its.load()


        expect:
        for (ValidationKey key : ValidationKey.values()) {
            assert its.getMap().get(key) != null
        }
    }


}
