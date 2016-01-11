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

package uk.q3c.krail.core.services

import spock.lang.Specification
import uk.q3c.krail.UnitTestFor

import java.time.LocalDateTime

import static uk.q3c.krail.core.services.Service.State.STOPPED
import static uk.q3c.krail.core.services.Service.State.STOPPING

@UnitTestFor(ServiceStatusRecord)
class ServiceStatusRecordTest extends Specification {

    Service serviceA = Mock(Service)

    def ""() {
        given:

        LocalDateTime lastStartTime = LocalDateTime.now()
        LocalDateTime lastStopTime = LocalDateTime.now().plusDays(1)
        LocalDateTime changeTime = LocalDateTime.now().plusMinutes(7)

        ServiceStatusRecord record = new ServiceStatusRecord()

        record.setCurrentState(STOPPED)
        record.setLastStartTime(lastStartTime)
        record.setLastStopTime(lastStopTime)
        record.setPreviousState(STOPPING)
        record.setService(serviceA)
        record.setStatusChangeTime(changeTime)

        expect:

        record.getCurrentState().equals(STOPPED)
        record.getLastStartTime().equals(lastStartTime)
        record.getLastStopTime().equals(lastStopTime)
        record.getPreviousState().equals(STOPPING)
        record.getService().equals(serviceA)
        record.getStatusChangeTime().equals(changeTime)


    }
}
