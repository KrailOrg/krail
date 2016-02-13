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

package uk.q3c.util

import spock.lang.Specification

/**
 * Created by David Sowerby on 18 Jan 2016
 */
class TargetTreeWrapperBaseTest extends Specification {

    TestWrapper wrapper

    def setup() {
        wrapper = new TestWrapper()
    }

    def "getCaptionReader throws exception when not set"() {
        when:
        wrapper.getCaptionReader()
        then:
        thrown(TreeCopyException)
    }

    def "setNodeModifier exception on null"() {

        when:
        wrapper.setNodeModifier(null)

        then:
        thrown(NullPointerException)
    }

    def "set-get NodeModifier"() {
        given:
        NodeModifier nodeModifier = Mock()

        when:
        wrapper.setNodeModifier(nodeModifier)

        then:
        wrapper.getNodeModifier() == nodeModifier
    }

    def "getNodeModifier throws exception when not set"() {
        when:
        wrapper.getNodeModifier()
        then:
        thrown(TreeCopyException)
    }

    def "sortChildren throws exception when NodeModifier not set"() {
        given:
        Object parentNode = Mock(Object)
        Comparator<Object> comparator = Mock(Comparator)

        when:
        wrapper.sortChildren(parentNode, comparator)

        then:
        thrown(TreeCopyException)
    }

    def "sortChildren delegates to NodeModifier"() {
        given:
        NodeModifier nodeModifier = Mock(NodeModifier)
        Object parentNode = Mock(Object)
        Comparator<Object> comparator = Mock(Comparator)
        wrapper.setNodeModifier(nodeModifier)

        when:
        wrapper.sortChildren(parentNode, comparator)

        then:
        1 * nodeModifier.sortChildren(parentNode, comparator)
    }


    class TestWrapper extends TargetTreeWrapperBase {

        @Override
        void addChild(Object parentNode, Object childNode) {

        }
    }
}
