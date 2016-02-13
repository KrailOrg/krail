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

import com.google.common.collect.Lists
import net.engio.mbassy.bus.common.PubSubSupport
import spock.lang.Specification
import uk.q3c.krail.core.eventbus.BusMessage
import uk.q3c.krail.core.eventbus.GlobalBusProvider
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.Translate

import static uk.q3c.krail.core.services.RelatedServicesExecutor.Action
import static uk.q3c.krail.core.services.Service.Cause

/**
 * Created by David Sowerby on 11 Jan 2016
 */
class DefaultRelatedServicesExecutorTest extends Specification {
    DefaultRelatedServicesExecutor exec;
    RelatedServicesExecutor mockExec = Mock(RelatedServicesExecutor)
    ServicesModel servicesModel = Mock(ServicesModel)
    Translate translate = Mock(Translate)
    ServicesClassGraph classGraph = Mock(ServicesClassGraph)
    ServicesInstanceGraph instanceGraph = Mock(ServicesInstanceGraph)
    GlobalBusProvider globalBusProvider = Mock(GlobalBusProvider)
    PubSubSupport<BusMessage> globalBus = Mock(PubSubSupport)
    MockService service
    MockService serviceA
    MockService serviceB
    MockService serviceC
    ServiceKey serviceKey
    ServiceKey serviceKeyA
    ServiceKey serviceKeyB
    ServiceKey serviceKeyC


    def setup() {

        globalBusProvider.get() >> globalBus
        exec = new DefaultRelatedServicesExecutor(servicesModel, translate)
        service = new MockService(translate, globalBusProvider, exec)
        service.setNameKey(LabelKey.Active_Source)
        exec.setService(service)

        serviceA = new MockService(translate, globalBusProvider, mockExec)
        serviceA.setNameKey(LabelKey.Alphabetic_Ascending)
        serviceB = new MockService(translate, globalBusProvider, mockExec)
        serviceB.setNameKey(LabelKey.Breadcrumb_is_Visible)
        serviceC = new MockService(translate, globalBusProvider, mockExec)
        serviceC.setNameKey(LabelKey.Export_complete)

        serviceKey = service.getServiceKey()
        serviceKeyA = serviceA.getServiceKey()
        serviceKeyB = serviceB.getServiceKey()
        serviceKeyC = serviceC.getServiceKey()

        servicesModel.getClassGraph() >> classGraph
        servicesModel.getInstanceGraph() >> instanceGraph
        mockExec.execute(Action.START, Cause.STARTED) >> true

    }

    def "start, all dependencies successful, execute returns true"() {
        given:

        serviceA.failToStart(false)
        serviceB.failToStart(false)
        serviceC.failToStart(false)
        classGraph.findDependencies(serviceKey, ServicesGraph.Selection.ALL) >> Lists.newArrayList(serviceKeyA, serviceKeyB, serviceKeyC)
        servicesModel.findInstanceDependencies(service) >> Lists.newArrayList(serviceA, serviceB, serviceC)

        when:
        boolean result = exec.execute(Action.START, Cause.STARTED)

        then:
        result
    }

    def "start, optional dependency fails, execute returns true"() {
        serviceA.failToStart(false)
        serviceB.failToStart(true)
        serviceC.failToStart(false)
        servicesModel.findInstanceDependencies(service) >> Lists.newArrayList(serviceA, serviceB, serviceC)
        classGraph.findDependencies(serviceKey, ServicesGraph.Selection.ALL) >> Lists.newArrayList(serviceKeyA, serviceKeyB, serviceKeyC)
        classGraph.isOptionalDependency(serviceKeyB, serviceKey) >> true

        when:
        boolean result = exec.execute(Action.START, Cause.STARTED)

        then:
        result
    }

    def "start, non-optional dependency fails, execute returns false"() {
        serviceA.failToStart(false)
        serviceB.failToStart(true)
        serviceC.failToStart(false)
        servicesModel.findInstanceDependencies(service) >> Lists.newArrayList(serviceA, serviceB, serviceC)
        classGraph.findDependencies(serviceKey, ServicesGraph.Selection.ALL) >> Lists.newArrayList(serviceKeyA, serviceKeyB, serviceKeyC)
        classGraph.isOptionalDependency(serviceKeyB, serviceKey) >> false

        when:
        boolean result = exec.execute(Action.START, Cause.STARTED)

        then:
        !result
    }

    def "start, no dependencies, returns true"() {
        given:
        servicesModel.findInstanceDependencies(service) >> new ArrayList<>()
        classGraph.findDependencies(serviceKey, ServicesGraph.Selection.ALL) >> Lists.newArrayList()

        when:
        boolean result = exec.execute(Action.START, Cause.STARTED)

        then:
        result
    }

    def "stop, no dependants, execute returns true"() {
        given:

        instanceGraph.findDependants(service, ServicesGraph.Selection.ALWAYS_REQUIRED) >> Lists.newArrayList()
        when:
        boolean result = exec.execute(Action.STOP, Cause.STOPPED)

        then:
        result
    }

    def "stop, all dependants stop correctly, execute returns true"() {
        given:
        serviceA.start()
        serviceB.start()
        serviceC.start()
        instanceGraph.findDependants(service, ServicesGraph.Selection.ALWAYS_REQUIRED) >> Lists.newArrayList(serviceA, serviceB, serviceC)

        when:
        boolean result = exec.execute(Action.STOP, Cause.STOPPED)

        then:
        result
    }

    def "stop, dependant fails to stop, execute returns false"() {
        given:
        serviceA.start()
        serviceB.start()
        serviceC.start()
        serviceB.failToStop(true)
        instanceGraph.findDependants(service, ServicesGraph.Selection.ALWAYS_REQUIRED) >> Lists.newArrayList(serviceA, serviceB, serviceC)
        when:
        boolean result = exec.execute(Action.STOP, Cause.STOPPED)

        then:
        !result
    }

    def "fail, no dependants, execute returns true"() {
        given:
        serviceA.start()
        serviceB.start()
        serviceC.start()
        instanceGraph.findDependants(service, ServicesGraph.Selection.ALWAYS_REQUIRED) >> Lists.newArrayList()

        when:
        boolean result = exec.execute(Action.STOP, Cause.FAILED)

        then:
        result
    }

    def "fail, all dependants return fail, execute returns true"() {
        given:
        serviceA.start()
        serviceB.start()
        serviceC.start()
        instanceGraph.findDependants(service, ServicesGraph.Selection.ALWAYS_REQUIRED) >> Lists.newArrayList(serviceA, serviceB, serviceC)

        when:
        boolean result = exec.execute(Action.STOP, Cause.FAILED)

        then:
        result
    }


}
