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

package uk.q3c.krail.core.navigate

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import spock.lang.Specification
import uk.q3c.util.testutil.LogMonitor

/**
 * Created by David Sowerby on 10 Feb 2016
 */
class NavigationStateTest extends Specification {

    URIFragmentHandler handler = new StrictURIFragmentHandler()
    NavigationState navigationState
    NavigationState navigationState1
    LogMonitor logMonitor

    def setup() {
        logMonitor = new LogMonitor()
        logMonitor.addClassFilter(NavigationState)
        navigationState = new NavigationState()
    }


    def "set fragment, confirm fragmentChanged is set"() {
        when:
        navigationState.fragment('"home/view/wiggly/id=1"')

        then:
        navigationState.isFragmentChanged()
    }


    def "set fragment, update and check all parts"() {
        given:
        navigationState.fragment("home/view/wiggly/id=1")

        when:
        navigationState.update(handler)

        then:
        navigationState.getVirtualPage().equals('home/view/wiggly')
        navigationState.getParameterValue('id').equals('1')
        navigationState.getUriSegment().equals('wiggly')
        navigationState.getParameterList().equals(ImmutableList.of('id=1'))
        navigationState.getPathSegments().equals(ImmutableList.of('home', 'view', 'wiggly'))
        navigationState.getParameters().equals(ImmutableMap.of('id', '1'))

    }


    def "set all parts, update and check fragment"() {
        when:
        navigationState.parameter('id', '1').virtualPage('home/view/wiggly').parameter('age', '33').update(handler)

        then:
        navigationState.getFragment().equals("home/view/wiggly/id=1/age=33")

        when:
        navigationState.virtualPage('new/segments').update(handler)

        then:
        navigationState.getFragment().equals("new/segments/id=1/age=33")
        navigationState.getPathSegments().equals(ImmutableList.of('new', 'segments'))
        navigationState.getParameters().equals(ImmutableMap.of('id', '1', 'age', '33'));

        when:
        navigationState.pathSegments(ImmutableList.of('a', 'different', 'path')).update(handler)

        then:
        navigationState.getFragment().equals("a/different/path/id=1/age=33")
        navigationState.getPathSegments().equals(ImmutableList.of('a', 'different', 'path'))
        navigationState.getParameters().equals(ImmutableMap.of('id', '1', 'age', '33'));
        navigationState.hasParameter('id')
        navigationState.hasParameter('age')
        !navigationState.hasParameter('ag')

    }


    def "remove parameter"() {
        given:
        navigationState.parameter('id', '1').virtualPage('home/view/wiggly').parameter('age', '33').update(handler)

        when:
        navigationState.removeParameter('id').update(handler)

        then:
        navigationState.getFragment().equals("home/view/wiggly/age=33")
        !navigationState.partsChanged
    }

    def 'change parameter value'() {
        given:
        navigationState.parameter('id', '1').virtualPage('home/view/wiggly').parameter('age', '33').update(handler)

        when:
        navigationState.parameter('id', '2').update(handler)

        then:
        navigationState.getFragment().equals("home/view/wiggly/id=2/age=33")
    }

    def 'change parameter value, partsChanged'() {
        given:
        navigationState.parameter('id', '1').virtualPage('home/view/wiggly').parameter('age', '33').update(handler)

        when:
        navigationState.parameter('id', '2')

        then:
        navigationState.isPartsChanged()
    }

    def "set a part and the fragment, update should prefer the fragment"() {
        when:
        navigationState.parameter('id', '1').virtualPage('home/view/wiggly').parameter('age', '33').fragment('fragment/only').update(handler)

        then:
        navigationState.getFragment().equals('fragment/only')
    }

    def "set the fragment, no update,  access parameter should throw exception"() {
        given:
        navigationState.fragment('fragment/only')

        when:
        navigationState.getParameterValue('id')

        then:
        thrown(NavigationStateException)
    }

    def "set the fragment, no update,  access virtualPage should throw exception"() {
        given:
        navigationState.fragment('fragment/only')

        when:
        navigationState.getVirtualPage()

        then:
        thrown(NavigationStateException)
    }

    def "set the fragment, no update,  access parameter list should throw exception"() {
        given:
        navigationState.fragment('fragment/only')

        when:
        navigationState.getParameterList()

        then:
        thrown(NavigationStateException)
    }

    def "set the fragment, no update,  access parameter map should throw exception"() {
        given:
        navigationState.fragment('fragment/only')

        when:
        navigationState.getParameters()

        then:
        thrown(NavigationStateException)
    }

    def "set the fragment, no update,  access segments should throw exception"() {
        given:
        navigationState.fragment('fragment/only')

        when:
        navigationState.getPathSegments()

        then:
        thrown(NavigationStateException)
    }

    def "set a part, no update, access the fragment, should throw exception"() {
        given:
        navigationState.virtualPage('x/y')

        when:
        navigationState.getFragment()

        then:
        thrown(NavigationStateException)
    }

    def "equals and hashcode, this one not updated, throws exception"() {
        given:
        navigationState.fragment('fragment/only')

        when:
        navigationState.equals(navigationState1)

        then:
        thrown(NavigationStateException)
    }

    def "equals and hashcode, other one not updated, throws exception"() {
        given:
        navigationState.fragment('fragment/only').update(handler)
        navigationState1 = new NavigationState().fragment('fragment/only')

        when:
        navigationState.equals(navigationState1)

        then:
        thrown(NavigationStateException)
    }

    def "equals and hashcode, matching"() {
        given:
        navigationState.fragment('fragment/only/id=1').update(handler)
        navigationState1 = new NavigationState()
        navigationState1.fragment('fragment/only/id=1').update(handler)

        expect:
        navigationState.equals(navigationState1)
        navigationState.hashCode() == navigationState1.hashCode()
    }

    def "equals and hashcode, null other, not equal"() {
        given:
        navigationState.fragment('fragment/only/id=1').update(handler)
        navigationState1 = null

        expect:
        !navigationState.equals(navigationState1)
    }

    def "equals and hashcode, other different, not equals"() {
        given:
        navigationState.fragment('fragment/only/id=1').update(handler)
        navigationState1 = new NavigationState()
        navigationState1.fragment('fragment/only/id=2').update(handler)

        expect:
        !navigationState.equals(navigationState1)
        navigationState.hashCode() != navigationState1.hashCode()
    }

    def "unnecessary update, debug log"() {
        given:
        navigationState.fragment('fragment/only/id=1').update(handler)

        when:
        navigationState.update(handler)

        then:
        logMonitor.debugLogs().contains("State has not been changed, no update needed")
    }

    def "equals and hashcode, this empty, other not empty, not equals"() {
        given:
        navigationState1 = new NavigationState()
        navigationState1.fragment('fragment/only/id=2').update(handler)

        expect:
        !navigationState.equals(navigationState1)
        navigationState.hashCode() != navigationState1.hashCode()
    }

    def "equals and hashcode, this empty, other empty, equals"() {
        given:
        navigationState1 = new NavigationState()

        expect:
        navigationState.equals(navigationState1)
        navigationState.hashCode() == navigationState1.hashCode()
    }


    def "fluency"() {

        when:
        StrictURIFragmentHandler uriHandler = new StrictURIFragmentHandler();
        //when
        NavigationState navState = new NavigationState().virtualPage("session")
                .parameter("a", "1")
                .parameter("b", "3")
        uriHandler.updateFragment(navState);

        then:
        navState.getFragment().equals("session/a=1/b=3")
    }


}