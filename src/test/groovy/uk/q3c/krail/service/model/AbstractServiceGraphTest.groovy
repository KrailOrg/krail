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

package uk.q3c.krail.service.model

import spock.lang.Specification
import uk.q3c.krail.service.DuplicateDependencyException
import uk.q3c.krail.service.Service
import uk.q3c.krail.service.test.*
import uk.q3c.util.dag.CycleDetectedException

import static org.assertj.core.api.Assertions.*
import static uk.q3c.krail.service.Dependency.*
import static uk.q3c.krail.service.ServiceGraph.*

/**
 *
 * Created by david on 27/10/15.
 */
class AbstractServiceGraphTest extends Specification {

    AbstractServiceGraph<Class<? extends Service>> graph

    Class<? extends Service> keyA = MockServiceA.class
    Class<? extends Service> keyB = MockServiceB.class
    Class<? extends Service> keyC = MockServiceC.class
    Class<? extends Service> keyD = MockServiceD.class
    Class<? extends Service> keyE = MockServiceE.class
    Class<? extends Service> keyF = MockServiceF.class
    Class<? extends Service> keyG = MockServiceG.class
    Class<? extends Service> keyH = MockServiceH.class




    def setup() {
        graph = new DefaultServiceClassGraph()
    }

    @SuppressWarnings("UnnecessaryQualifiedReference")
    def "create and find dependencies"() {

        when:
        graph.createDependency(keyA, keyB, Type.ALWAYS_REQUIRED)
        graph.createDependency(keyA, keyC, Type.ALWAYS_REQUIRED)
        graph.createDependency(keyA, keyD, Type.REQUIRED_ONLY_AT_START)
        graph.createDependency(keyA, keyE, Type.OPTIONAL)
        graph.createDependency(keyB, keyF, Type.REQUIRED_ONLY_AT_START)

        then:
        assertThat(graph.findDependencies(keyA, Selection.ALL)).containsOnly(keyB, keyC, keyD, keyE)
        assertThat(graph.findDependencies(keyA, Selection.OPTIONAL)).containsOnly(keyE)
        assertThat(graph.findDependencies(keyA, Selection.ALWAYS_REQUIRED)).containsOnly(keyB, keyC)
        assertThat(graph.findDependencies(keyA, Selection.ONLY_REQUIRED_AT_START)).containsOnly(keyD)
        assertThat(graph.findDependencies(keyA, Selection.REQUIRED_AT_START)).containsOnly(keyB, keyC, keyD)

        assertThat(graph.findDependencies(keyB, Selection.ALL)).containsOnly(keyF)
        assertThat(graph.findDependencies(keyB, Selection.OPTIONAL)).containsOnly()
        assertThat(graph.findDependencies(keyB, Selection.ALWAYS_REQUIRED)).containsOnly()
        assertThat(graph.findDependencies(keyB, Selection.ONLY_REQUIRED_AT_START)).containsOnly(keyF)
        assertThat(graph.findDependencies(keyB, Selection.REQUIRED_AT_START)).containsOnly(keyF)

    }

    def "has dependency"() {
        when:
        graph.createDependency(keyA, keyB, Type.ALWAYS_REQUIRED)
        graph.createDependency(keyA, keyC, Type.OPTIONAL)
        graph.createDependency(keyB, keyD, Type.ALWAYS_REQUIRED)

        then:
        graph.hasDependency(keyA, keyB)
        graph.hasDependency(keyA, keyC)
        !graph.hasDependency(keyA, keyD)
    }

    def "has dependant"() {
        when:
        graph.createDependency(keyB, keyA, Type.ALWAYS_REQUIRED)
        graph.createDependency(keyC, keyA, Type.OPTIONAL)
        graph.createDependency(keyD, keyB, Type.ALWAYS_REQUIRED)

        then:
        graph.hasDependant(keyA, keyB)
        graph.hasDependant(keyA, keyC)
        !graph.hasDependant(keyA, keyD)
    }


    def "cannot add a duplicate dependency"() {
        when:
        graph.createDependency(keyA, keyB, Type.ALWAYS_REQUIRED)
        graph.createDependency(keyA, keyB, Type.OPTIONAL)

        then:

        thrown(DuplicateDependencyException)
    }

    @SuppressWarnings("UnnecessaryQualifiedReference")
    def "create and find dependants"() {
        when:
        graph.createDependency(keyB, keyA, Type.ALWAYS_REQUIRED)
        graph.createDependency(keyC, keyA, Type.ALWAYS_REQUIRED)
        graph.createDependency(keyD, keyA, Type.REQUIRED_ONLY_AT_START)
        graph.createDependency(keyE, keyA, Type.OPTIONAL)
        graph.createDependency(keyF, keyB, Type.REQUIRED_ONLY_AT_START)

        then:
        assertThat(graph.findDependants(keyA, Selection.ALL)).containsOnly(keyB, keyC, keyD, keyE)
        assertThat(graph.findDependants(keyA, Selection.OPTIONAL)).containsOnly(keyE)
        assertThat(graph.findDependants(keyA, Selection.ALWAYS_REQUIRED)).containsOnly(keyB, keyC)
        assertThat(graph.findDependants(keyA, Selection.ONLY_REQUIRED_AT_START)).containsOnly(keyD)
        assertThat(graph.findDependants(keyA, Selection.REQUIRED_AT_START)).containsOnly(keyB, keyC, keyD)

        assertThat(graph.findDependants(keyB, Selection.ALL)).containsOnly(keyF)
        assertThat(graph.findDependants(keyB, Selection.OPTIONAL)).containsOnly()
        assertThat(graph.findDependants(keyB, Selection.ALWAYS_REQUIRED)).containsOnly()
        assertThat(graph.findDependants(keyB, Selection.ONLY_REQUIRED_AT_START)).containsOnly(keyF)
        assertThat(graph.findDependants(keyB, Selection.REQUIRED_AT_START)).containsOnly(keyF)

    }

    def "find dependencies for dependant not in graph returns empty list, dependant added to graph"() {
        expect:

        graph.findDependencies(keyA, Selection.ALL).isEmpty()

    }

    def "find dependants for dependency not in graph returns empty list, dependency added to graph "() {
        expect:

        graph.findDependants(keyA, Selection.ALL).isEmpty()

    }


    def "remove service"() {

        when:
        graph.addService(keyA)

        then:
        graph.contains(keyA)
        graph.getServices().contains(keyA)

        when:
        graph.removeService(keyA)

        then:
        !graph.contains(keyA)
        graph.getServices().isEmpty()


    }

    def "isOptionalDependency returns false if not a dependency, or not optional"() {
        when:
        graph.createDependency(keyB, keyA, Type.ALWAYS_REQUIRED)
        graph.createDependency(keyC, keyA, Type.OPTIONAL)

        then:
        !graph.isOptionalDependency(keyA, keyB)
        graph.isOptionalDependency(keyA, keyC)
        !graph.isOptionalDependency(keyA, keyD)

    }


    def "findEdge is present or not"() {
        when:
        graph.createDependency(keyB, keyA, Type.ALWAYS_REQUIRED)
        graph.createDependency(keyC, keyA, Type.ALWAYS_REQUIRED)

        then:
        graph.getEdge(keyB, keyA).get().getType() == Type.ALWAYS_REQUIRED
        graph.getEdge(keyC, keyB) == Optional.empty()
    }


    def "cycle created throws CycleException"() {
        given:

        graph.createDependency(keyA, keyB, Type.OPTIONAL)

        when:
        graph.createDependency(keyB, keyA, Type.OPTIONAL)

        then:
        thrown CycleDetectedException
    }

    def "dependency on itself throws CycleException"() {
        when:
        graph.createDependency(keyA, keyA, Type.ALWAYS_REQUIRED)

        then:
        thrown CycleDetectedException

    }

}
