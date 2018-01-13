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

import com.vaadin.ui.Tree
import spock.lang.Specification
import uk.q3c.krail.core.vaadin.TargetTreeWrapper_VaadinTree
import uk.q3c.util.forest.*

/**
 * Created by David Sowerby on 18 Jan 2016
 */
class TreeCopyTest2 extends Specification {

    private TreeCopy<NodeTypeA, NodeTypeA> copy
    private BasicForest<NodeTypeA> forest
    private SourceTreeWrapper<NodeTypeA> source
    private TargetTreeWrapper<NodeTypeA, NodeTypeA> target
    private Tree<NodeTypeA> vaadinTree
    private NodeTypeA nodeA
    private NodeTypeA nodeA1
    private NodeTypeA nodeA11
    private NodeTypeA nodeA2
    private NodeTypeA nodeA3
    private NodeTypeA nodeB
    private NodeTypeA nodeB1
    private NodeTypeA nodeB11

    NodeFilter nf = Mock(NodeFilter)

    def setup() {
        forest = new BasicForest<>()
        source = new SourceTreeWrapper_BasicForest<>(forest)

        vaadinTree = new Tree<>()
        target = new TargetTreeWrapper_VaadinTree<>(vaadinTree)
        copy = new TreeCopy<>(source, target)
    }

    def "getMaxDepth"() {

        when:
        copy.setMaxDepth(2)

        then:
        copy.getMaxDepth() == 2
        copy.isLimitedDepth()
    }

    def "add-remove source filters"() {

        when:
        copy.addSourceFilter(nf)

        then:
        copy.getSourceFilters().contains(nf)

        when:
        copy.removeSourceFilter(nf)

        then:
        !copy.getSourceFilters().contains(nf)
    }


    def "extension is invoked"() {
        given:
        populateSource(source)
        CaptionReader captionReader = Mock(CaptionReader)
        TreeCopyExtension<NodeTypeA, NodeTypeA> extension = Mock()
        copy.setExtension(extension)
        target.setCaptionReader(captionReader)

        when:
        copy.copy()

        then:
        6 * extension.invoke(_, _, _) //drill down
    }

    private void populateSource(SourceTreeWrapper<NodeTypeA> source) {
        nodeA = new NodeTypeA("a")
        nodeA1 = new NodeTypeA("a1")
        nodeA11 = new NodeTypeA("a11")
        nodeB = new NodeTypeA("b")
        nodeB1 = new NodeTypeA("b1")
        nodeB11 = new NodeTypeA("b11")

        forest.addChild(nodeA, nodeA1)
        forest.addChild(nodeA1, nodeA11)
        forest.addChild(nodeB, nodeB1)
        forest.addChild(nodeB1, nodeB11)

    }

    class NodeTypeAComparator implements Comparator<NodeTypeA> {

        @Override
        int compare(NodeTypeA o1, NodeTypeA o2) {
            return o1.ref.compareTo(o2.ref)
        }

    }

    class TestCaptionReader implements CaptionReader<NodeTypeA> {

        @Override
        String getCaption(NodeTypeA sourceNode) {
            return sourceNode.ref
        }

    }

    class NodeTypeA implements Comparable {
        String ref

        protected NodeTypeA(String ref) {
            super()
            this.ref = ref
        }

        @Override
        String toString() {
            return "NodeTypeA [ref=" + ref + "]"
        }

        @Override
        int compareTo(Object o) {
            if (!o instanceof NodeTypeA) {
                return false
            }
            NodeTypeA other = (NodeTypeA) o
            ref.compareTo(other.ref)
        }
    }

    class Sorter implements Comparator<NodeTypeA> {

        @Override
        int compare(NodeTypeA o1, NodeTypeA o2) {
            return o1.ref.compareTo(o2.ref)
        }

    }

    class SourceFilter implements NodeFilter<NodeTypeA> {

        @Override
        boolean accept(NodeTypeA node) {
            return !node.ref.equals("b11")
        }

    }

}
