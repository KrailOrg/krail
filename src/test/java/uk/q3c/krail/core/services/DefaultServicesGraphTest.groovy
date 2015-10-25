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

import com.google.common.collect.Lists
import org.mockito.Mock
import spock.lang.Specification
import uk.q3c.krail.UnitTestFor
import uk.q3c.krail.i18n.LabelKey
import uk.q3c.util.CycleDetectedException

import static uk.q3c.krail.core.services.Dependency.Type.*

/**
 *
 * Created by david on 27/10/15.
 */
@UnitTestFor(DefaultServicesGraph)
class DefaultServicesGraphTest extends Specification {

    DefaultServicesGraph graph

    ServiceKey keyA = new ServiceKey(LabelKey.Active_Source);
    ServiceKey keyB = new ServiceKey(LabelKey.Alphabetic_Ascending);
    ServiceKey keyC = new ServiceKey(LabelKey.Alphabetic_Descending);
    ServiceKey keyD = new ServiceKey(LabelKey.Application_Configuration_Service);
    ServiceKey keyE = new ServiceKey(LabelKey.Authentication);
    ServiceKey keyF = new ServiceKey(LabelKey.Authorisation);
    ServiceKey keyG = new ServiceKey(LabelKey.Auto_Stub);
    ServiceKey keyH = new ServiceKey(LabelKey.Connection_URL);

    @Mock
    Service serviceA

    @Mock
    Service serviceB


    def setup() {
        Set<DependencyDefinition> dependencyDefinitions = new HashSet<>()
        graph = new DefaultServicesGraph(dependencyDefinitions)
    }

    def "dependenciesRequiredAtStartFor identifies all, and only, immediate dependencies needed to start dependant"() {

        when:
        graph.alwaysDependsOn(keyA, keyB)
        graph.addDependency(keyA, keyC, Dependency.Type.ALWAYS_REQUIRED)
        graph.requiresOnlyAtStart(keyA, keyD)
        graph.optionallyUses(keyA, keyE)
        graph.requiresOnlyAtStart(keyB, keyF)

        then: //E is optional, should not be included
        graph.findDependenciesOnlyRequiredAtStartFor(keyA).containsAll(keyD)
    }

    def "dependenciesOptionalAtStartFor identifies only optional dependencies to start before dependant"() {
        when:
        graph.alwaysDependsOn(keyA, keyB)
        graph.alwaysDependsOn(keyA, keyC)
        graph.requiresOnlyAtStart(keyA, keyD)
        graph.optionallyUses(keyA, keyE)

        //E is optional, should be included
        // F is not a direct dependency, should not be included
        then:
        graph.findOptionalDependencies(keyA).containsAll([keyE])
    }

    def "dependenciesAlwaysRequired identifies only dependencies which are required to keep dependant running"() {

        when:
        graph.alwaysDependsOn(keyA, keyB)
        graph.alwaysDependsOn(keyA, keyC)
        graph.requiresOnlyAtStart(keyA, keyD)
        graph.optionallyUses(keyA, keyE)

        then:

        graph.findDependenciesAlwaysRequiredFor(keyA).containsAll([keyB, keyC])
    }

    def "'dependantsOf' identifies all dependants which require 'dependency' to be running in order to keep running"() {
        when:
        graph.alwaysDependsOn(keyA, keyB)
        graph.alwaysDependsOn(keyA, keyC)
        graph.requiresOnlyAtStart(keyA, keyD)
        graph.optionallyUses(keyA, keyE)
        graph.requiresOnlyAtStart(keyB, keyF)
        graph.alwaysDependsOn(keyG, keyB)
        graph.alwaysDependsOn(keyH, keyB)

        then:
        graph.findDependantsAlwaysRequiringDependency(keyB).containsAll([keyA, keyG, keyH])
        graph.findDependantsAlwaysRequiringDependency(keyC).containsAll([keyA])
        graph.findDependantsAlwaysRequiringDependency(keyE).isEmpty()
    }

    def "'optionalDependantsOf' identifies all dependants which identify 'dependency' as an optional dependency"() {
        when:
        graph.alwaysDependsOn(keyA, keyB)
        graph.alwaysDependsOn(keyA, keyC)
        graph.requiresOnlyAtStart(keyA, keyD)
        graph.optionallyUses(keyA, keyE)
        graph.requiresOnlyAtStart(keyB, keyF)
        graph.optionallyUses(keyG, keyB)
        graph.optionallyUses(keyH, keyB)

        then:
        graph.findDependantsOptionallyUsingDependency(keyB).containsAll([keyG, keyH])
        graph.findDependantsOptionallyUsingDependency(keyE).containsAll(keyA)
    }

    def "'startupDependantsOf' identifies all dependants which are only required in order for dependency to start"() {
        when:
        graph.alwaysDependsOn(keyA, keyB)
        graph.alwaysDependsOn(keyA, keyC)
        graph.requiresOnlyAtStart(keyA, keyD)
        graph.optionallyUses(keyA, keyE)
        graph.requiresOnlyAtStart(keyB, keyF)
        graph.optionallyUses(keyG, keyB)
        graph.requiresOnlyAtStart(keyH, keyF)

        then:
        graph.findDependantsRequiringDependencyOnlyToStart(keyD).containsAll([keyA])
        graph.findDependantsRequiringDependencyOnlyToStart(keyF).containsAll([keyB, keyH])
        graph.findDependantsRequiringDependencyOnlyToStart(keyE).isEmpty()
    }

    def "no dependencies for buildDependencies() does not NPE"() {
        given:

        graph.addService(keyA)

        expect:

        graph.findDependenciesAlwaysRequiredFor(keyA).isEmpty()
        graph.findDependenciesOnlyRequiredAtStartFor(keyA).isEmpty()
        graph.findOptionalDependencies(keyA).isEmpty()
    }

    def "no dependants for buildDependants() does not NPE"() {

        given:

        graph.addService(keyA)

        expect:

        graph.findDependantsAlwaysRequiringDependency(keyA).isEmpty()
        graph.findDependantsOptionallyUsingDependency(keyA).isEmpty()
        graph.findDependantsRequiringDependencyOnlyToStart(keyA).isEmpty()
    }

    def "use Guice config to defined dependencies"() {

        given:
        Set<DependencyDefinition> dependencyDefinitions = new HashSet<>()
        dependencyDefinitions.add(new DependencyDefinition(keyA, keyB, ALWAYS_REQUIRED))
        dependencyDefinitions.add(new DependencyDefinition(keyA, keyC, ALWAYS_REQUIRED))
        dependencyDefinitions.add(new DependencyDefinition(keyA, keyD, REQUIRED_ONLY_AT_START))
        dependencyDefinitions.add(new DependencyDefinition(keyA, keyE, OPTIONAL))
        dependencyDefinitions.add(new DependencyDefinition(keyB, keyF, REQUIRED_ONLY_AT_START))
        dependencyDefinitions.add(new DependencyDefinition(keyG, keyB, ALWAYS_REQUIRED))
        dependencyDefinitions.add(new DependencyDefinition(keyH, keyB, ALWAYS_REQUIRED))


        when:

        graph = new DefaultServicesGraph(dependencyDefinitions)


        then:
        graph.findDependantsAlwaysRequiringDependency(keyB).containsAll([keyA, keyG, keyH])
        graph.findDependantsAlwaysRequiringDependency(keyC).containsAll([keyA])
        graph.findDependantsAlwaysRequiringDependency(keyE).isEmpty()
    }

    def "find dependencies for dependant not in graph returns empty list, dependant autmotically registered"() {
        expect:

        graph.findDependenciesAlwaysRequiredFor(keyA).isEmpty()
        graph.isRegistered(keyA)
        graph.findDependenciesOnlyRequiredAtStartFor(keyB).isEmpty()
        graph.isRegistered(keyB)
        graph.findOptionalDependencies(keyC).isEmpty()
        graph.isRegistered(keyC)

    }

    def "find dependants for dependencies not in graph throws IllegalArgumentException"() {
        expect:

        graph.findDependantsRequiringDependencyOnlyToStart(keyA).isEmpty()
        graph.isRegistered(keyA)
        graph.findDependantsOptionallyUsingDependency(keyB).isEmpty()
        graph.isRegistered(keyB)
        graph.findDependantsAlwaysRequiringDependency(keyC).isEmpty()
        graph.isRegistered(keyC)
    }

    def "servicesForKeys should throw ServiceKeyException if keys are without services"() {
        given:

        graph.addService(keyA)

        when:

        graph.servicesForKeys(Lists.asList(keyA))

        then:

        thrown(ServiceKeyException)
    }

    def "services for keys is valid"() {
        when:

        MockService serviceA = new MockService()
        graph.addService(new ServiceKey(LabelKey.Yes))
        graph.registerService(serviceA)

        then:
        graph.servicesForKeys(Lists.asList(new ServiceKey(LabelKey.Yes))).contains(serviceA)
        graph.isRegistered(serviceA)
        graph.registeredServices().contains(serviceA)
    }

    def "cycle created throws CycleException"() {
        given:

        graph.alwaysDependsOn(keyA, keyB)

        when:
        graph.alwaysDependsOn(keyB, keyA)

        then:
        thrown CycleDetectedException
    }

}
