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

import com.mycila.testing.junit.MycilaJunitRunner;
import com.vaadin.ui.Tree;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.navigate.sitemap.CountNodeVisitor;
import uk.q3c.krail.core.navigate.sitemap.ListNodeVisitor;
import uk.q3c.krail.core.navigate.sitemap.NodeVisitable;
import uk.q3c.krail.core.navigate.sitemap.NodeVisitor;
import uk.q3c.krail.core.navigate.sitemap.TreeDataWalker;
import uk.q3c.util.forest.BasicForest;
import uk.q3c.util.forest.CaptionReader;
import uk.q3c.util.forest.DefaultNodeModifier;
import uk.q3c.util.forest.NodeFilter;
import uk.q3c.util.forest.SourceTreeWrapper;
import uk.q3c.util.forest.SourceTreeWrapper_BasicForest;
import uk.q3c.util.forest.TargetTreeWrapper;
import uk.q3c.util.forest.TreeCopy;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
public class TreeCopyTest {

    private TreeCopy<NodeTypeA, NodeTypeA> copy;
    private BasicForest<NodeTypeA> forest;
    private NodeTypeA nodeA;
    private NodeTypeA nodeA1;
    private NodeTypeA nodeA11;
    private NodeTypeA nodeA2;
    private NodeTypeA nodeA3;
    private NodeTypeA nodeB;
    private NodeTypeA nodeB1;
    private NodeTypeA nodeB11;
    private SourceTreeWrapper<NodeTypeA> source;
    private TargetTreeWrapper<NodeTypeA, NodeTypeA> target;
    private Tree<NodeTypeA> vaadinTree;

    @Mock
    private TargetTreeWrapper<NodeTypeA, NodeTypeA> mockTarget;

    @Before
    public void setup() {
        forest = new BasicForest<>();
        source = new SourceTreeWrapper_BasicForest<>(forest);

        vaadinTree = new Tree<>();
        target = new TargetTreeWrapper_VaadinTree<>(vaadinTree);
        copy = new TreeCopy<>(source, target);
        copy.setTargetSortComparator(new NodeTypeAComparator());
        target.setNodeModifier(new DefaultNodeModifier<NodeTypeA, NodeTypeA>());
        target.setCaptionReader(new TestCaptionReader());

    }

    @Test
    public void copySameNodeTypes_emptySource() {
        // given

        // when
        copy.copy();
        // then
        assertThat(vaadinTree.getTreeData().getRootItems()).isEmpty();
    }

    @Test
    public void copySameNodeTypes_populatedSource() {
        // given
        populateSource(source);
        // when
        copy.copy();
        // then
        assertThat(vaadinTree.getTreeData().getRootItems()).isNotEmpty();
        assertThat(countNodes(vaadinTree)).isEqualTo(6);
        assertThat(vaadinTree.getTreeData().getParent(nodeA1)).isEqualTo(nodeA);
        assertThat(vaadinTree.getTreeData().getParent(nodeA11)).isEqualTo(nodeA1);
        assertThat(vaadinTree.getTreeData().getParent(nodeB1)).isEqualTo(nodeB);
        assertThat(vaadinTree.getTreeData().getParent(nodeB11)).isEqualTo(nodeB1);

    }

    private void populateSource(SourceTreeWrapper<NodeTypeA> source) {
        nodeA = new NodeTypeA("a");
        nodeA1 = new NodeTypeA("a1");
        nodeA11 = new NodeTypeA("a11");
        nodeB = new NodeTypeA("b");
        nodeB1 = new NodeTypeA("b1");
        nodeB11 = new NodeTypeA("b11");

        forest.addChild(nodeA, nodeA1);
        forest.addChild(nodeA1, nodeA11);
        forest.addChild(nodeB, nodeB1);
        forest.addChild(nodeB1, nodeB11);

    }

    @Test
    public void copySameNodeTypes_limitDepth() {
        // given
        populateSource(source);
        copy.setMaxDepth(2);
        // when
        copy.copy();
        // then
        assertThat(vaadinTree.getTreeData().getRootItems()).isNotEmpty();
        List<NodeTypeA> result = listNodes(vaadinTree);
        assertThat(result).containsOnly(nodeA, nodeA1, nodeB, nodeB1);
        assertThat(vaadinTree.getTreeData().getParent(nodeA1)).isEqualTo(nodeA);
        assertThat(vaadinTree.getTreeData().getParent(nodeB1)).isEqualTo(nodeB);

        // given
        vaadinTree.getTreeData().clear();
        copy.setLimitedDepth(false);
        // when
        copy.copy();
        // then
        assertThat(countNodes(vaadinTree)).isEqualTo(6);
        assertThat(vaadinTree.getTreeData().getParent(nodeA1)).isEqualTo(nodeA);
        assertThat(vaadinTree.getTreeData().getParent(nodeA11)).isEqualTo(nodeA1);
        assertThat(vaadinTree.getTreeData().getParent(nodeB1)).isEqualTo(nodeB);
        assertThat(vaadinTree.getTreeData().getParent(nodeB11)).isEqualTo(nodeB1);
    }

    @Test
    public void sorted() {
        // given

        populateSource2(source);
        copy.setSourceSortComparator(new Sorter());
        // when
        copy.copy();
        // then
        Collection<NodeTypeA> nodes = vaadinTree.getTreeData().getChildren(nodeA);
        assertThat(nodes).containsExactly(nodeA1, nodeA3, nodeA2);
    }


    @Test
    public void sortTargetNodesAfterAdd() {
        // given
        populateSource2(source);
        when(mockTarget.createNode(null, nodeA)).thenReturn(nodeA1);
        when(mockTarget.createNode(nodeA, nodeA2)).thenReturn(nodeA11);
        when(mockTarget.createNode(null, nodeB)).thenReturn(nodeB1);
        when(mockTarget.createNode(nodeA, nodeA3)).thenReturn(nodeB11);


        copy = new TreeCopy<>(source, mockTarget);
        Comparator<NodeTypeA> targetSortComparator = mock(Comparator.class);
        copy.setTargetSortComparator(targetSortComparator);
        copy.setSortOption(TreeCopy.SortOption.SORT_TARGET_NODES_AFTER_ADD);
        // when
        copy.copy();
        // then
        verify(mockTarget, times(2)).addChild(any(NodeTypeA.class), any(NodeTypeA.class));
        verify(mockTarget).sortChildren(null, targetSortComparator);
    }

    @Test
    public void sortTargetNodesAfterAdd_NotSorted() {
        // given
        populateSource2(source);
        when(mockTarget.createNode(null, nodeA)).thenReturn(nodeA1);
        when(mockTarget.createNode(nodeA, nodeA2)).thenReturn(nodeA11);
        when(mockTarget.createNode(null, nodeB)).thenReturn(nodeB1);
        when(mockTarget.createNode(nodeA, nodeA3)).thenReturn(nodeB11);


        copy = new TreeCopy<>(source, mockTarget);
        Comparator<NodeTypeA> targetSortComparator = mock(Comparator.class);
        copy.setTargetSortComparator(targetSortComparator);
        copy.setSortOption(TreeCopy.SortOption.SORT_TARGET_NODES_AFTER_ADD);
        copy.setSorted(false);
        // when
        copy.copy();
        // then
        verify(mockTarget, times(2)).addChild(any(NodeTypeA.class), any(NodeTypeA.class));
        verify(mockTarget, times(0)).sortChildren(null, targetSortComparator);
    }

    private void populateSource2(SourceTreeWrapper<NodeTypeA> source) {
        populateSource(source);
        nodeA2 = new NodeTypeA("z");
        nodeA3 = new NodeTypeA("v");

        forest.addChild(nodeA, nodeA2);
        forest.addChild(nodeA, nodeA3);

    }

    @Test
    public void vaadinTree_to_BasicForest() {
        // given

        populateSource(source);
        copy.setMaxDepth(2);
        // when
        copy.copy();
        // then
        List<NodeTypeA> nodes = listNodes(vaadinTree);
        assertThat(nodes).isNotEmpty();
        assertThat(nodes).containsOnly(nodeA, nodeA1, nodeB, nodeB1);
        assertThat(vaadinTree.getTreeData().getParent(nodeA1)).isEqualTo(nodeA);
        assertThat(vaadinTree.getTreeData().getParent(nodeB1)).isEqualTo(nodeB);

        // given
        vaadinTree.getTreeData().clear();
        copy.setLimitedDepth(false);
        // when
        copy.copy();
        // then
        List<NodeTypeA> nodes2 = listNodes(vaadinTree);
        assertThat(nodes2).hasSize(6);
        assertThat(vaadinTree.getTreeData().getParent(nodeA1)).isEqualTo(nodeA);
        assertThat(vaadinTree.getTreeData().getParent(nodeA11)).isEqualTo(nodeA1);
        assertThat(vaadinTree.getTreeData().getParent(nodeB1)).isEqualTo(nodeB);
        assertThat(vaadinTree.getTreeData().getParent(nodeB11)).isEqualTo(nodeB1);
    }

    @Test
    public void sourceFilter() {
        // given
        populateSource(source);
        copy.addSourceFilter(new SourceFilter());
        // when
        copy.copy();
        // then
        @SuppressWarnings("unchecked") List<NodeTypeA> result = listNodes(vaadinTree);
        assertThat(result).containsOnly(nodeA, nodeA1, nodeA11, nodeB, nodeB1);
    }

    private int countNodes(Tree<NodeTypeA> tree) {
        TreeDataWalker<NodeTypeA> walker = new TreeDataWalker<>(tree.getTreeData());
        CountNodeVisitor visitor = new CountNodeVisitor();
        walker.walk(visitor);
        return visitor.getCount();
    }

    private List<NodeTypeA> listNodes(Tree<NodeTypeA> tree) {
        TreeDataWalker<NodeTypeA> walker = new TreeDataWalker<>(tree.getTreeData());
        ListNodeVisitor<NodeTypeA> visitor = new ListNodeVisitor<>();
        walker.walk(visitor);
        return visitor.getList();
    }


    class NodeTypeAComparator implements Comparator<NodeTypeA> {

        @Override
        public int compare(NodeTypeA o1, NodeTypeA o2) {
            return o1.ref.compareTo(o2.ref);
        }

    }

    class TestCaptionReader implements CaptionReader<NodeTypeA> {

        @Override
        public String getCaption(NodeTypeA sourceNode) {
            return sourceNode.ref;
        }

    }

    class NodeTypeA implements NodeVisitable {
        String ref;

        protected NodeTypeA(String ref) {
            super();
            this.ref = ref;
        }

        @Override
        public String toString() {
            return "NodeTypeA [ref=" + ref + "]";
        }

        @Override
        public void accept(@NotNull NodeVisitor visitor) {
            visitor.visit(this);
        }
    }

    class Sorter implements Comparator<NodeTypeA> {

        @Override
        public int compare(NodeTypeA o1, NodeTypeA o2) {
            return o1.ref.compareTo(o2.ref);
        }

    }

    class SourceFilter implements NodeFilter<NodeTypeA> {

        @Override
        public boolean accept(NodeTypeA node) {
            return !node.ref.equals("b11");
        }

    }


}
