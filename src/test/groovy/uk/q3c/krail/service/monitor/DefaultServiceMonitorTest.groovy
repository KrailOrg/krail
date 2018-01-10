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

package uk.q3c.krail.service.monitor

import spock.lang.Specification
import uk.q3c.krail.eventbus.MessageBus
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.service.*

import java.time.LocalDateTime

import static uk.q3c.krail.service.Service.State.*

class DefaultServiceMonitorTest extends Specification {

    MessageBus globalBus = Mock(MessageBus)
    MessageBus globalBusProvider = Mock(MessageBus)
    Translate translate = Mock(Translate)
    RelatedServiceExecutor servicesExecutor = Mock(RelatedServiceExecutor)
    Service serviceA

    def setup() {
        servicesExecutor.execute(_, _) >> true
        globalBusProvider.get() >> globalBus
        serviceA = new TestService0(translate, globalBusProvider, servicesExecutor)
    }


    def "initial message, 'registers' service, records state, then change reflected correctly, then clear()"() {
        given:
        DefaultServiceMonitor monitor = new DefaultServiceMonitor(globalBusProvider)
        serviceA.start()

        when:

        monitor.serviceStatusChange(new ServiceBusMessage(serviceA, INITIAL, RUNNING, Service.Cause.STARTED))

        then:

        monitor.getMonitoredServices().contains(serviceA)
        ServiceStatusRecord status = monitor.getServiceStatus(serviceA)
        status.getService().equals(serviceA)
        status.getCurrentState().equals(RUNNING)
        LocalDateTime startTime = status.getLastStartTime()
        status.getLastStartTime() != null
        status.getLastStartTime().equals(status.getStatusChangeTime())

        when:
        serviceA.stop()
        monitor.serviceStatusChange(new ServiceBusMessage(serviceA, RUNNING, STOPPED, Service.Cause.STOPPED))
        status = monitor.getServiceStatus(serviceA)

        then:


        status.getCurrentState().equals(STOPPED)
        status.getLastStartTime().equals(startTime)
        status.getLastStopTime() != null
        status.getLastStopTime().equals(status.getStatusChangeTime())

        when:
        monitor.clear()

        then:

        monitor.getMonitoredServices().isEmpty()
    }


}
