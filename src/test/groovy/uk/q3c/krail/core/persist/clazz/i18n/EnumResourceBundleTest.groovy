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

package uk.q3c.krail.core.persist.clazz.i18n

import spock.lang.Specification
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.Labels_de

/**
 *
 * Created by David Sowerby on 03/08/15.
 */
class EnumResourceBundleTest extends Specification {

    Labels_de labels

    def setup() {
        labels = new Labels_de()
    }

    def "getKeys() is superseded, throw exception"() {
        when:

        labels.getKeys()

        then:

        thrown RuntimeException
    }

    def "handleGetObject() is superseded, throw exception"() {
        when:

        labels.handleGetObject("x")

        then:

        thrown RuntimeException
    }

    def "getValue with null key returns null"() {
        expect:
        labels.getValue(null) == null
    }

    def "reset reloads the map and sets loaded to true"() {
        when:

        labels.setKeyClass(LabelKey.class)
        labels.load()
        labels.getMap().put(LabelKey.Yes, "Wiggly") // change a value
        labels.reset()

        then:

        labels.isLoaded() == true
        labels.getValue(LabelKey.Yes).equals("Ja")

    }

    def "getKeyClass"() {
        given:

        labels.setKeyClass(LabelKey.class)
        expect:
        labels.getKeyClass().equals(LabelKey.class)
    }

}
