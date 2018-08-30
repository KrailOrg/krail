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

package uk.q3c.krail.core.shiro

import com.vaadin.server.VaadinSession
import spock.lang.Specification
import uk.q3c.krail.core.vaadin.JavaMockVaadinSession

/**
 * Created by David Sowerby on 09 Feb 2016
 */
class DefaultVaadinSessionProviderTest extends Specification {

    DefaultVaadinSessionProvider provider

    def setup() {
        provider = new DefaultVaadinSessionProvider()
        JavaMockVaadinSession.clear()
    }

    def "no VaadinSession, throw exception"() {
        when:
        provider.get()

        then:
        thrown(IllegalStateException)
    }

    def "VaadinSession available, returns it"() {
        given:
        VaadinSession session = JavaMockVaadinSession.setup()

        when:
        VaadinSession result = provider.get()

        then:
        result == session
    }
}
