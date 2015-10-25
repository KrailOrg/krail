/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.services

import spock.lang.Specification
import uk.q3c.krail.UnitTestFor
import uk.q3c.krail.i18n.LabelKey
import uk.q3c.krail.i18n.Translate
/**
 *
 * Created by david on 27/10/15.
 */
@UnitTestFor(DefaultServicesController)
class DefaultServicesControllerTest extends Specification {


    DefaultServicesController controller
    def translate = Mock(Translate)
    ServicesGraph servicesGraph

    MockService sA
    MockService sB
    MockService sC
    MockService sD

    def setup() {
        Set<DependencyDefinition> dependencyDefinitions = new HashSet<>()
        servicesGraph = new DefaultServicesGraph(dependencyDefinitions)
        translate.from(_, _) >> "translated key"
        controller = new DefaultServicesController(servicesGraph, translate)

        sA = new MockService().nameKey(LabelKey.Active_Source)
        sB = new MockService().nameKey(LabelKey.Auto_Stub)
        sC = new MockService().nameKey(LabelKey.Connection_URL)
        sD = new MockService().nameKey(LabelKey.Alphabetic_Ascending)

        servicesGraph.registerService(sA)
        servicesGraph.registerService(sB)
        servicesGraph.registerService(sC)
        servicesGraph.registerService(sD)


    }

    def "no dependencies, 'startDependenciesFor' returns true"() {
        given:

        servicesGraph.addService(sA.getServiceKey())

        when:

        boolean result = controller.startDependenciesFor(sA)

        then:

        result

    }


    def "alwaysDependsOn dependencies 'start()' is called, 'startDependenciesFor' returns true"() {
        given:


        servicesGraph.alwaysDependsOn(sA.getServiceKey(), sB.getServiceKey())
        servicesGraph.alwaysDependsOn(sA.getServiceKey(), sC.getServiceKey())

        when:

        boolean result = controller.startDependenciesFor(sA)

        then: //dependencies started

        result
        sB.isStarted()
        sC.isStarted()

        sB.getCallsToStart() == 1;
        sC.getCallsToStart() == 1;

    }

    def "alwaysDependsOn, dependsOnAtStart and optional dependencies 'start()' is called, 'startDependenciesFor' returns true"() {
        given:


        servicesGraph.alwaysDependsOn(sA.getServiceKey(), sB.getServiceKey())
        servicesGraph.requiresOnlyAtStart(sA.getServiceKey(), sC.getServiceKey())
        servicesGraph.optionallyUses(sA.getServiceKey(), sD.getServiceKey())

        when:

        boolean result = controller.startDependenciesFor(sA)

        then: //dependencies started

        result
        sB.isStarted()
        sC.isStarted()
        sD.isStarted()

        sB.getCallsToStart() == 1;
        sC.getCallsToStart() == 1;
        sD.getCallsToStart() == 1;

    }

    def "alwaysDependsOn, dependsOnAtStart and optional dependencies 'start()' is called, alwaysDependsOn fails, 'startDependenciesFor' returns false"() {
        given:

        sB.failToStart(true)
        servicesGraph.alwaysDependsOn(sA.getServiceKey(), sB.getServiceKey())
        servicesGraph.requiresOnlyAtStart(sA.getServiceKey(), sC.getServiceKey())
        servicesGraph.optionallyUses(sA.getServiceKey(), sD.getServiceKey())

        when:

        boolean result = controller.startDependenciesFor(sA)

        then: //dependencies started

        !result
        !sB.isStarted()
        sC.isStarted()
        sD.isStarted()

        sB.getCallsToStart() == 1;
        sC.getCallsToStart() == 1;
        sD.getCallsToStart() == 1;

    }

    def "alwaysDependsOn, dependsOnAtStart and optional dependencies 'start()' is called, dependsOnAtStart fails, 'startDependenciesFor' returns false"() {
        given:

        sC.failToStart(true)
        servicesGraph.alwaysDependsOn(sA.getServiceKey(), sB.getServiceKey())
        servicesGraph.requiresOnlyAtStart(sA.getServiceKey(), sC.getServiceKey())
        servicesGraph.optionallyUses(sA.getServiceKey(), sD.getServiceKey())

        when:

        boolean result = controller.startDependenciesFor(sA)

        then: //dependencies started

        !result
        sB.isStarted()
        !sC.isStarted()
        sD.isStarted()

        sB.getCallsToStart() == 1;
        sC.getCallsToStart() == 1;
        sD.getCallsToStart() == 1;

    }

    def "alwaysDependsOn, dependsOnAtStart and optional dependencies 'start()' is called, optionallyUses fails, 'startDependenciesFor' returns true"() {
        given:

        sD.failToStart(true)
        servicesGraph.alwaysDependsOn(sA.getServiceKey(), sB.getServiceKey())
        servicesGraph.requiresOnlyAtStart(sA.getServiceKey(), sC.getServiceKey())
        servicesGraph.optionallyUses(sA.getServiceKey(), sD.getServiceKey())

        when:

        boolean result = controller.startDependenciesFor(sA)

        then: //dependencies started

        result
        sB.isStarted()
        sC.isStarted()
        !sD.isStarted()

        sB.getCallsToStart() == 1;
        sC.getCallsToStart() == 1;
        sD.getCallsToStart() == 1;

    }


    def "dependency stops, dependants with 'alwaysRequired' also stop, optional and 'requiredAtStart' do not"() {
        given:

        servicesGraph.alwaysDependsOn(sA.getServiceKey(), sB.getServiceKey())
        servicesGraph.requiresOnlyAtStart(sC.getServiceKey(), sB.getServiceKey())
        servicesGraph.optionallyUses(sD.getServiceKey(), sB.getServiceKey())

        sA.start()
        sB.start()
        sC.start()
        sD.start()


        when:

        boolean result = controller.stopDependantsOf(sB, false)

        then: //dependencies started

        sA.isStopped()
        sA.getState().equals(Service.State.DEPENDENCY_STOPPED)
        sC.isStarted()
        sD.isStarted()

        sA.getCallsToStop() == 1;
        sC.getCallsToStop() == 0;
        sD.getCallsToStop() == 0;
    }

    def "dependency fails, dependants with 'alwaysRequired' also fail, optional and 'requiredAtStart' do not"() {
        given:

        servicesGraph.alwaysDependsOn(sA.getServiceKey(), sB.getServiceKey())
        servicesGraph.requiresOnlyAtStart(sC.getServiceKey(), sB.getServiceKey())
        servicesGraph.optionallyUses(sD.getServiceKey(), sB.getServiceKey())

        sA.start()
        sB.start()
        sC.start()
        sD.start()


        when:

        boolean result = controller.stopDependantsOf(sB, true)

        then: //dependencies started

        sA.isStopped()
        sA.getState().equals(Service.State.DEPENDENCY_FAILED)
        sC.isStarted()
        sD.isStarted()

        sA.getCallsToStop() == 1;
        sC.getCallsToStop() == 0;
        sD.getCallsToStop() == 0;
    }

    def "stop all services"() {
        given:

        servicesGraph.alwaysDependsOn(sA.getServiceKey(), sB.getServiceKey())
        servicesGraph.requiresOnlyAtStart(sC.getServiceKey(), sB.getServiceKey())
        servicesGraph.optionallyUses(sD.getServiceKey(), sB.getServiceKey())

        sA.start()
        sB.start()
        sC.start()
        sD.start()

        when:

        controller.stopAllServices()

        then:

        sA.isStopped()
        sB.isStopped()
        sC.isStopped()
        sD.isStopped()
    }
}