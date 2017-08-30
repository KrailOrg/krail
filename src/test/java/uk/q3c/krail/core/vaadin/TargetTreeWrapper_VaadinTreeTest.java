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

package uk.q3c.krail.core.vaadin;

import com.vaadin.v7.ui.Tree;
import org.junit.Before;
import org.junit.Test;
import uk.q3c.util.forest.CaptionReader;
import uk.q3c.util.forest.TreeCopyException;

import static org.assertj.core.api.Assertions.*;

public class TargetTreeWrapper_VaadinTreeTest {

    // private TargetTestNode targetNodeB;
    // private TargetTestNode targetNodeC;
    // private TargetTestNode targetNodeD;
    private TestCaptionReader captionReader;
    private SourceTestNode sourceNodeA;
    private SourceTestNode sourceNodeB;
    private SourceTestNode sourceNodeC;
    // private SourceTestNode sourceNodeD;
    private TargetTestNode targetNodeA;
    private Tree tree;
    private TargetTreeWrapper_VaadinTree<SourceTestNode, SourceTestNode> wrapper;

    @Before
    public void setup() {
        tree = new Tree();
        sourceNodeA = new SourceTestNode("sa");
        sourceNodeB = new SourceTestNode("sb");
        sourceNodeC = new SourceTestNode("sc");
        // sourceNodeD = new SourceTestNode("sd");
        targetNodeA = new TargetTestNode("ta");
        // targetNodeB = new TargetTestNode("tb");
        // targetNodeC = new TargetTestNode("tc");
        // targetNodeD = new TargetTestNode("td");
    }

    @Test(expected = NullPointerException.class)
    public void createNode_nullChild() {

        // given
        wrapper = new TargetTreeWrapper_VaadinTree<>(tree);
        // when
        wrapper.createNode(sourceNodeA, null);
        // then
    }

    @Test(expected = TreeCopyException.class)
    public void createNode_noCaptionReader() {

        // given
        wrapper = new TargetTreeWrapper_VaadinTree<>(tree);
        // when
        wrapper.createNode(null, sourceNodeA);
        // then
    }

    @Test
    public void createNode__defaultNodeModifier() {

        // given
        wrapper = new TargetTreeWrapper_VaadinTree<>(tree);
        captionReader = new TestCaptionReader();
        wrapper.setCaptionReader(captionReader);
        // when
        SourceTestNode result = wrapper.createNode(null, sourceNodeA);
        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(sourceNodeA);
        assertThat(tree.getItemCaption(sourceNodeA)).isEqualTo(sourceNodeA.name);
    }

    @Test(expected = TreeCopyException.class)
    public void sort() {

        // given
        wrapper = new TargetTreeWrapper_VaadinTree<>(tree);
        // when
        wrapper.sortChildren(sourceNodeA, null);
        // then
    }

    @Test
    public void addChild() {

        // given
        wrapper = new TargetTreeWrapper_VaadinTree<>(tree);
        // when
        wrapper.addChild(sourceNodeA, sourceNodeB);
        // then
        assertThat(tree.getParent(sourceNodeB)).isEqualTo(sourceNodeA);
    }

    static class TargetTestNode {
        String name;

        public TargetTestNode(String name) {
            super();
            this.name = name;
        }
    }

    static class SourceTestNode {
        String name;

        public SourceTestNode(String name) {
            super();
            this.name = name;
        }
    }

    static class TestCaptionReader implements CaptionReader<SourceTestNode> {

        @Override
        public String getCaption(SourceTestNode sourceNode) {
            return sourceNode.name;
        }

    }
}
