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

import net.engio.mbassy.bus.common.PubSubSupport
import spock.lang.Specification
import uk.q3c.krail.UnitTestFor
import uk.q3c.krail.core.eventbus.BusMessage
import uk.q3c.krail.core.eventbus.GlobalBusProvider
import uk.q3c.krail.core.i18n.I18NKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.Translate

import java.time.LocalDateTime

import static uk.q3c.krail.core.services.Service.State.*

@UnitTestFor(DefaultServicesMonitor)
class DefaultServicesMonitorTest extends Specification {

    PubSubSupport<BusMessage> globalBus = Mock(PubSubSupport)
    GlobalBusProvider globalBusProvider = Mock(GlobalBusProvider)
    ServicesModel servicesModel = Mock(ServicesModel)
    Translate translate = Mock(Translate)
    RelatedServicesExecutor servicesExecutor = Mock(RelatedServicesExecutor)
    Service serviceA

    def setup() {
        servicesExecutor.execute(_, _) >> true
        globalBusProvider.get() >> globalBus
        serviceA = new AbstractServiceTest.TestService(translate, globalBusProvider, servicesExecutor)
    }


    def "initial message, 'registers' service, records state, then change reflected correctly, then clear()"() {
        given:
        DefaultServicesMonitor monitor = new DefaultServicesMonitor(globalBusProvider)
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

    class TestService extends AbstractService {

        protected TestService(Translate translate, ServicesModel servicesModel, GlobalBusProvider globalBusProvider) {
            super(translate, servicesModel, globalBusProvider)
        }

        @Override
        protected void doStop() throws Exception {

        }

        @Override
        protected void doStart() throws Exception {

        }

        @Override
        I18NKey getNameKey() {
            return LabelKey.Active_Source
        }
    }
}
