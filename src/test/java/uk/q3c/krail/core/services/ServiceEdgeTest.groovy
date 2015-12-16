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
