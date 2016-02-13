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

/**
 * Created by David Sowerby on 18 Dec 2015
 */
class ServiceEdgeTest extends Specification {
    def "GetType"() {
        given:

        ServiceEdge edge = new ServiceEdge(Dependency.Type.ALWAYS_REQUIRED)

        expect:
        edge.getType().equals(Dependency.Type.ALWAYS_REQUIRED)

    }

    def "RequiredOnlyAtStart"() {
        given:

        ServiceEdge edge = new ServiceEdge(Dependency.Type.REQUIRED_ONLY_AT_START)

        expect:
        !edge.alwaysRequired()
        !edge.optional()
        edge.requiredOnlyAtStart()
    }

    def "Optional"() {
        given:

        ServiceEdge edge = new ServiceEdge(Dependency.Type.OPTIONAL)

        expect:
        !edge.alwaysRequired()
        edge.optional()
        !edge.requiredOnlyAtStart()

    }

    def "AlwaysRequired"() {
        given:

        ServiceEdge edge = new ServiceEdge(Dependency.Type.ALWAYS_REQUIRED)

        expect:
        edge.alwaysRequired()
        !edge.optional()
        !edge.requiredOnlyAtStart()

    }
}
