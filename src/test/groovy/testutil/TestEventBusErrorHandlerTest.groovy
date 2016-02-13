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

package testutil

import net.engio.mbassy.bus.error.PublicationError
import spock.lang.Specification
import uk.q3c.util.testutil.LogMonitor

/**
 * Created by David Sowerby on 06 Feb 2016
 */
class TestEventBusErrorHandlerTest extends Specification {

    PublicationError errorMessage = Mock()
    TestEventBusErrorHandler handler

    def "x"() {

        given:
        handler = new TestEventBusErrorHandler()
        LogMonitor logMonitor = new LogMonitor()
        logMonitor.addClassFilter(TestEventBusErrorHandler)
        errorMessage.getMessage() >> "an error message"
        errorMessage.getCause() >> new NullPointerException()

        when:
        handler.handleError(errorMessage)

        then:
        logMonitor.errorCount() == 1
        logMonitor.errorLogs().contains("an error message")
    }
}
