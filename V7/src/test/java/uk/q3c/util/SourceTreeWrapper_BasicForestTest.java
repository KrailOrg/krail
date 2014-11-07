/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.q3c.util;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SourceTreeWrapper_BasicForestTest {

    SourceTreeWrapper_BasicForest<SourceTestNode> wrapper;
    BasicForest<SourceTestNode> forest;
    private SourceTestNode nodeA;
    private SourceTestNode nodeB;
    private SourceTestNode nodeC;
    private SourceTestNode nodeD;

    @Before
    public void setup() {
        forest = new BasicForest<>();
        wrapper = new SourceTreeWrapper_BasicForest<>(forest);
        nodeA = new SourceTestNode("a");
        nodeB = new SourceTestNode("b");
        nodeC = new SourceTestNode("c");
        nodeD = new SourceTestNode("d");
    }

    @Test
    public void getRoots() {

        // given
        forest.addNode(nodeA);
        forest.addNode(nodeB);
        // when

        // then
        assertThat(wrapper.getRoots()).containsOnly(nodeA, nodeB);
    }

    @Test
    public void getChildren() {

        // given
        forest.addNode(nodeA);
        // when

        // then
        assertThat(wrapper.getChildren(nodeA)).isEmpty();

        // given
        forest.addChild(nodeA, nodeB);
        forest.addChild(nodeA, nodeC);
        forest.addChild(nodeA, nodeD);
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
