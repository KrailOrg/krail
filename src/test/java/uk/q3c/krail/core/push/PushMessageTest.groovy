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

package uk.q3c.krail.core.push

import spock.lang.Specification

/**
 * Created by David Sowerby on 19 Jan 2016
 */
class PushMessageTest extends Specification {

    def "NPE for null message"() {

        when:
        new PushMessage("a", null)

        then:

        thrown(NullPointerException)
    }

    def "NPE for null group"() {

        when:
        new PushMessage(null, "A")

        then:

        thrown(NullPointerException)
    }

    def "getters"() {
        when:

        PushMessage msg = new PushMessage("a", "b")

        then:

        msg.getGroup().equals("a")
        msg.getMessage().equals("b")
    }
}
