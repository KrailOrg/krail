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

import com.google.inject.Provider
import net.engio.mbassy.bus.common.PubSubSupport
import spock.lang.Specification
import uk.q3c.krail.core.eventbus.BusMessage
import uk.q3c.krail.core.eventbus.GlobalBusProvider
import uk.q3c.krail.core.i18n.Translate

import static org.assertj.core.api.Assertions.assertThat
import static uk.q3c.krail.testutil.i18n.TestLabelKey.*
/**
 *
 * Created by david on 27/10/15.
 */
@SuppressWarnings("GroovyAssignabilityCheck")
class DefaultServicesModelTest extends Specification {


    def translate = Mock(Translate)
    ServicesModel servicesModel

    MockServiceA sA
    MockServiceB sB
    MockServiceC sC
    MockServiceD sD
    MockServiceE sE
    MockServiceF sF
    MockServiceG sG
    MockServiceH sH

    Provider<MockServiceA> providerA = Mock()
    Provider<MockServiceB> providerB = Mock()
    Provider<MockServiceC> providerC = Mock()
    Provider<MockServiceD> providerD = Mock()
    Provider<MockServiceE> providerE = Mock()
    Provider<MockServiceF> providerF = Mock()
    Provider<MockServiceG> providerG = Mock()
    Provider<MockServiceH> providerH = Mock()


    Map<ServiceKey, Provider<Service>> registeredServices
    GlobalBusProvider globalBusProvider = Mock(GlobalBusProvider)
    RelatedServicesExecutor servicesExecutor
    PubSubSupport<BusMessage> globalBus = Mock(PubSubSupport)

    def setup() {
        Set<DependencyDefinition> dependencyDefinitions = new HashSet<>()
        registeredServices = new HashMap<>()
        servicesModel = new DefaultServicesModel(dependencyDefinitions, new DefaultServicesClassGraph(), new DefaultServicesInstanceGraph(), registeredServices)
        servicesExecutor = new DefaultRelatedServicesExecutor(servicesModel, translate)
        translate.from(_, _) >> "translated key"
        globalBusProvider.get() >> globalBus

        sA = new MockServiceA(translate, globalBusProvider, servicesExecutor)
        sB = new MockServiceB(translate, globalBusProvider, servicesExecutor)
        sC = new MockServiceC(translate, globalBusProvider, servicesExecutor)
        sD = new MockServiceD(translate, globalBusProvider, servicesExecutor)
        sE = new MockServiceE(translate, globalBusProvider, servicesExecutor)
        sF = new MockServiceF(translate, globalBusProvider, servicesExecutor)
        sG = new MockServiceG(translate, globalBusProvider, servicesExecutor)
        sH = new MockServiceH(translate, globalBusProvider, servicesExecutor)

        providerA.get() >> sA
        providerB.get() >> sB
        providerC.get() >> sC
        providerD.get() >> sD
        providerE.get() >> sE
        providerF.get() >> sF
        providerG.get() >> sG
        providerH.get() >> sH

        registeredServices.put(new ServiceKey(ServiceA), providerA)
        registeredServices.put(new ServiceKey(ServiceB), providerB)
        registeredServices.put(new ServiceKey(ServiceC), providerC)
        registeredServices.put(new ServiceKey(ServiceD), providerD)
        registeredServices.put(new ServiceKey(ServiceE), providerE)
        registeredServices.put(new ServiceKey(ServiceF), providerF)
        registeredServices.put(new ServiceKey(ServiceG), providerG)
        registeredServices.put(new ServiceKey(ServiceH), providerH)





        servicesModel.addService(new ServiceKey(ServiceA))
        servicesModel.addService(new ServiceKey(ServiceB))
        servicesModel.addService(new ServiceKey(ServiceC))
        servicesModel.addService(new ServiceKey(ServiceD))


    }

    def "contains registered service"() {
        expect:

        servicesModel.contains(new ServiceKey(ServiceA))
    }


    def "when a service instance is added, all its dependencies (to full depth)  are also added"() {
        given:

        servicesModel.alwaysDependsOn(new ServiceKey(ServiceA), new ServiceKey(ServiceB))
        servicesModel.requiresOnlyAtStart(new ServiceKey(ServiceA), new ServiceKey(ServiceC))
        servicesModel.optionallyUses(new ServiceKey(ServiceA), new ServiceKey(ServiceD))
        servicesModel.alwaysDependsOn(new ServiceKey(ServiceB), new ServiceKey(ServiceE))
        servicesModel.alwaysDependsOn(new ServiceKey(ServiceB), new ServiceKey(ServiceD))

        when:

        servicesModel.addService(sA)

        then:
        servicesModel.getInstanceGraph().size() == 5
        servicesModel.getInstanceGraph().contains(sA)
        servicesModel.getInstanceGraph().contains(sB)
        servicesModel.getInstanceGraph().contains(sC)
        servicesModel.getInstanceGraph().contains(sD)
        servicesModel.getInstanceGraph().contains(sE)
        servicesModel.getInstanceGraph().hasDependency(sA, sB)
        servicesModel.getInstanceGraph().hasDependency(sA, sC)
        servicesModel.getInstanceGraph().hasDependency(sA, sD)
        servicesModel.getInstanceGraph().hasDependency(sB, sE)
        servicesModel.getInstanceGraph().hasDependency(sB, sD)
    }


    def "stop all services"() {
        given:

        servicesModel.alwaysDependsOn(new ServiceKey(ServiceA), new ServiceKey(ServiceB))
        servicesModel.requiresOnlyAtStart(new ServiceKey(ServiceC), new ServiceKey(ServiceB))
        servicesModel.optionallyUses(new ServiceKey(ServiceD), new ServiceKey(ServiceB))

        servicesModel.addService(sA)
        servicesModel.addService(sC)
        servicesModel.addService(sD)

        when:

        sA.start()
        sB.start()
        sC.start()
        sD.start()

        then: //validate set up

        servicesModel.getInstanceGraph().size() == 4

        when:

        servicesModel.stopAllServices()

        then:

        sA.isStopped()
        sB.isStopped()
        sC.isStopped()
        sD.isStopped()
    }

    @SuppressWarnings(["GroovyAssignabilityCheck", "UnnecessaryQualifiedReference"])
    def "use Guice config to defined dependencies"() {

        given:
        ServiceKey sac = new ServiceKey(ServiceA)
        ServiceKey sbc = new ServiceKey(ServiceB)
        ServiceKey scc = new ServiceKey(ServiceC)
        ServiceKey sdc = new ServiceKey(ServiceD)
        ServiceKey sec = new ServiceKey(ServiceE)
        ServiceKey sfc = new ServiceKey(ServiceF)
        ServiceKey sgc = new ServiceKey(ServiceG)
        ServiceKey shc = new ServiceKey(ServiceH)

        Set<DependencyDefinition> dependencyDefinitions = new HashSet<>()
        dependencyDefinitions.add(new DependencyDefinition(ServiceA, ServiceB, Dependency.Type.ALWAYS_REQUIRED))
        dependencyDefinitions.add(new DependencyDefinition(ServiceA, ServiceC, Dependency.Type.ALWAYS_REQUIRED))
        dependencyDefinitions.add(new DependencyDefinition(ServiceA, ServiceD, Dependency.Type.REQUIRED_ONLY_AT_START))
        dependencyDefinitions.add(new DependencyDefinition(ServiceA, ServiceE, Dependency.Type.OPTIONAL))
        dependencyDefinitions.add(new DependencyDefinition(ServiceB, ServiceF, Dependency.Type.REQUIRED_ONLY_AT_START))
        dependencyDefinitions.add(new DependencyDefinition(ServiceG, ServiceB, Dependency.Type.ALWAYS_REQUIRED))
        dependencyDefinitions.add(new DependencyDefinition(ServiceH, ServiceB, Dependency.Type.ALWAYS_REQUIRED))





        when:

        servicesModel = new DefaultServicesModel(dependencyDefinitions, new DefaultServicesClassGraph(), new DefaultServicesInstanceGraph(), registeredServices)


        then:
        assertThat(servicesModel.getClassGraph().findDependencies(sac, ServicesGraph.Selection.ALWAYS_REQUIRED)).containsOnly(sbc, scc)
        assertThat(servicesModel.getClassGraph().findDependencies(sac, ServicesGraph.Selection.ONLY_REQUIRED_AT_START)).containsOnly(sdc)
        assertThat(servicesModel.getClassGraph().findDependencies(sac, ServicesGraph.Selection.OPTIONAL)).containsOnly(sec)
        assertThat(servicesModel.getClassGraph().findDependencies(sbc, ServicesGraph.Selection.ONLY_REQUIRED_AT_START)).containsOnly(sfc)
        assertThat(servicesModel.getClassGraph().findDependencies(sgc, ServicesGraph.Selection.ALWAYS_REQUIRED)).containsOnly(sbc)
        assertThat(servicesModel.getClassGraph().findDependencies(shc, ServicesGraph.Selection.ALWAYS_REQUIRED)).containsOnly(sbc)
    }

    def "registered services empty when none registered"() {
        given:
        Map<ServiceKey, Provider<Service>> registeredServices = new HashMap<>()
        Set<DependencyDefinition> dependencyDefinitions = new HashSet<>()


        when:
        servicesModel = new DefaultServicesModel(dependencyDefinitions, new DefaultServicesClassGraph(), new DefaultServicesInstanceGraph(), registeredServices)

        then:
        servicesModel.registeredServices() != null
        servicesModel.registeredServices().isEmpty()
    }

}