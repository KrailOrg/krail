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

package uk.q3c.krail.service

import spock.lang.Specification

/**
 * Simple tests for all Service Exceptions
 *
 * Created by David Sowerby on 14/11/15.
 */
class ServiceExceptionTests
        extends Specification {


    def "construction and return of message"() {
        given:
        def msg = "message"

        when:

        ServiceRegistrationException sre = new ServiceRegistrationException(msg)
        ServiceKeyException ske = new ServiceKeyException(msg)
        ServiceStatusException sse = new ServiceStatusException(msg)
        ServiceException se = new ServiceException(msg)

        then:

        sse.getMessage().equals(msg)
        ske.getMessage().equals(msg)
        sre.getMessage().equals(msg)
        se.getMessage().equals(msg)
    }
}
