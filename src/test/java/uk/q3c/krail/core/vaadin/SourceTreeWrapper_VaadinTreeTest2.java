/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.vaadin;

import com.vaadin.ui.Tree;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class SourceTreeWrapper_VaadinTreeTest2 {

    SourceTreeWrapper_VaadinTree<SourceTestNode> wrapper;

    Tree tree;
    private SourceTestNode nodeA;
    private SourceTestNode nodeB;
    private SourceTestNode nodeC;
    private SourceTestNode nodeD;

    @Before
    public void setup() {
        tree = new Tree();
        wrapper = new SourceTreeWrapper_VaadinTree<>(tree);
        nodeA = new SourceTestNode("a");
        nodeB = new SourceTestNode("b");
        nodeC = new SourceTestNode("c");
        nodeD = new SourceTestNode("d");
    }

    @Test
    public void getRoots() {

        // given
        tree.addItem(nodeA);
        tree.addItem(nodeB);
        // when

        // then
        assertThat(wrapper.getRoots()).containsOnly(nodeA, nodeB);
    }

    @Test
    public void getChildren() {

        // given
        tree.addItem(nodeA);
        // when

        // then
        assertThat(wrapper.getChildren(nodeA)).isEmpty();

        // given
        tree.addItem(nodeB);
        tree.addItem(nodeC);
        tree.addItem(nodeD);
        tree.setParent(nodeB, nodeA);
        tree.setParent(nodeC, nodeA);
        tree.setParent(nodeD, nodeA);
        // when

        // then
        assertThat(wrapper.getChildren(nodeA)).containsOnly(nodeB, nodeC, nodeD);
    }

    static class SourceTestNode {
        String name;

        public SourceTestNode(String name) {
            super();
            this.name = name;
        }
    }
}
