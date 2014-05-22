/*
 * Copyright (C) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.co.q3c.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import com.vaadin.ui.Tree;

public class TreeCopierTest {

	class NodeTypeA {
		String ref;

		protected NodeTypeA(String ref) {
			super();
			this.ref = ref;
		}

		@Override
		public String toString() {
			return "NodeTypeA [ref=" + ref + "]";
		}

	}

	class Sorter implements Comparator<NodeTypeA> {

		@Override
		public int compare(NodeTypeA o1, NodeTypeA o2) {
			return o1.ref.compareTo(o2.ref);
		}

	}

	class NodeTypeACaption implements VaadinTreeItemCaption<NodeTypeA> {

		@Override
		public String caption(NodeTypeA node) {
			return node.ref;
		}

	}

	class SourceFilter implements TreeCopierFilter<NodeTypeA> {

		@Override
		public boolean accept(NodeTypeA node) {
			return !node.ref.equals("b11");
		}

	}

	private NodeTypeA nodeA;
	private NodeTypeA nodeA1;
	private NodeTypeA nodeA11;
	private NodeTypeA nodeB;
	private NodeTypeA nodeB1;
	private NodeTypeA nodeB11;
	private NodeTypeA nodeA2;
	private NodeTypeA nodeA3;

	@Test
	public void copySameNodeTypes_emptySource() {
		// given
		BasicForest<NodeTypeA> source = new BasicForest<>();
		Tree vaadinTree = new Tree();
		VaadinTreeWrapper<NodeTypeA> target = new VaadinTreeWrapper<>(vaadinTree);
		TreeCopier<NodeTypeA, NodeTypeA> copier = new TreeCopier<>(source, target);
		copier.setNodeCreator(new DefaultNodeCreator<NodeTypeA>());
		// when
		copier.copy();
		// then
		assertThat(vaadinTree.getItemIds()).isEmpty();
	}

	@Test
	public void copySameNodeTypes_populatedSource() {
		// given
		BasicForest<NodeTypeA> source = new BasicForest<>();
		Tree vaadinTree = new Tree();
		VaadinTreeWrapper<NodeTypeA> target = new VaadinTreeWrapper<>(vaadinTree);
		TreeCopier<NodeTypeA, NodeTypeA> copier = new TreeCopier<>(source, target);
		copier.setNodeCreator(new DefaultNodeCreator<NodeTypeA>());
		populateSource(source);
		// when
		copier.copy();
		// then
		assertThat(vaadinTree.getItemIds()).isNotEmpty();
		assertThat(vaadinTree.getItemIds()).hasSize(6);
		assertThat(vaadinTree.getParent(nodeA1)).isEqualTo(nodeA);
		assertThat(vaadinTree.getParent(nodeA11)).isEqualTo(nodeA1);
		assertThat(vaadinTree.getParent(nodeB1)).isEqualTo(nodeB);
		assertThat(vaadinTree.getParent(nodeB11)).isEqualTo(nodeB1);

	}

	@Test
	public void copySameNodeTypes_limitDepth() {
		// given
		BasicForest<NodeTypeA> source = new BasicForest<>();
		Tree vaadinTree = new Tree();
		VaadinTreeWrapper<NodeTypeA> target = new VaadinTreeWrapper<>(vaadinTree);
		TreeCopier<NodeTypeA, NodeTypeA> copier = new TreeCopier<>(source, target);
		copier.setNodeCreator(new DefaultNodeCreator<NodeTypeA>());
		populateSource(source);
		copier.setMaxDepth(2);
		// when
		copier.copy();
		// then
		assertThat(vaadinTree.getItemIds()).isNotEmpty();
		@SuppressWarnings("unchecked")
		List<NodeTypeA> result = (List<NodeTypeA>) vaadinTree.getItemIds();
		assertThat(result).containsOnly(nodeA, nodeA1, nodeB, nodeB1);
		assertThat(vaadinTree.getParent(nodeA1)).isEqualTo(nodeA);
		assertThat(vaadinTree.getParent(nodeB1)).isEqualTo(nodeB);

		// given
		vaadinTree.removeAllItems();
		copier.setLimitedDepth(false);
		// when
		copier.copy();
		// then
		assertThat(vaadinTree.getItemIds()).hasSize(6);
		assertThat(vaadinTree.getParent(nodeA1)).isEqualTo(nodeA);
		assertThat(vaadinTree.getParent(nodeA11)).isEqualTo(nodeA1);
		assertThat(vaadinTree.getParent(nodeB1)).isEqualTo(nodeB);
		assertThat(vaadinTree.getParent(nodeB11)).isEqualTo(nodeB1);
	}

	@Test
	public void sorted() {
		// given
		BasicForest<NodeTypeA> source = new BasicForest<>();
		Tree vaadinTree = new Tree();
		VaadinTreeWrapper<NodeTypeA> target = new VaadinTreeWrapper<>(vaadinTree);
		TreeCopier<NodeTypeA, NodeTypeA> copier = new TreeCopier<>(source, target);
		copier.setNodeCreator(new DefaultNodeCreator<NodeTypeA>());
		populateSource2(source);
		copier.setSortComparator(new Sorter());
		// when
		copier.copy();
		// then
		List<NodeTypeA> nodes = target.getChildren(nodeA);
		assertThat(nodes).containsExactly(nodeA1, nodeA3, nodeA2);
	}

	@Test
	public void vaadinTree_to_BasicForest() {
		// given
		BasicForest<NodeTypeA> target = new BasicForest<>();
		Tree vaadinTree = new Tree();
		VaadinTreeWrapper<NodeTypeA> source = new VaadinTreeWrapper<>(vaadinTree);
		TreeCopier<NodeTypeA, NodeTypeA> copier = new TreeCopier<>(source, target);
		copier.setNodeCreator(new DefaultNodeCreator<NodeTypeA>());
		populateSource(source);
		copier.setMaxDepth(2);
		// when
		copier.copy();
		// then
		assertThat(target.getAllNodes()).isNotEmpty();
		assertThat(target.getAllNodes()).containsOnly(nodeA, nodeA1, nodeB, nodeB1);
		assertThat(target.getParent(nodeA1)).isEqualTo(nodeA);
		assertThat(target.getParent(nodeB1)).isEqualTo(nodeB);

		// given
		target.clear();
		copier.setLimitedDepth(false);
		// when
		copier.copy();
		// then
		assertThat(target.getAllNodes()).hasSize(6);
		assertThat(target.getParent(nodeA1)).isEqualTo(nodeA);
		assertThat(target.getParent(nodeA11)).isEqualTo(nodeA1);
		assertThat(target.getParent(nodeB1)).isEqualTo(nodeB);
		assertThat(target.getParent(nodeB11)).isEqualTo(nodeB1);
	}

	@Test
	public void sourceFilter() {
		// given
		BasicForest<NodeTypeA> source = new BasicForest<>();
		Tree vaadinTree = new Tree();
		VaadinTreeWrapper<NodeTypeA> target = new VaadinTreeWrapper<>(vaadinTree);
		TreeCopier<NodeTypeA, NodeTypeA> copier = new TreeCopier<>(source, target);
		copier.setNodeCreator(new DefaultNodeCreator<NodeTypeA>());
		copier.addSourceFilter(new SourceFilter());
		populateSource(source);
		// when
		copier.copy();
		// then
		@SuppressWarnings("unchecked")
		List<NodeTypeA> result = (List<NodeTypeA>) vaadinTree.getItemIds();
		assertThat(result).containsOnly(nodeA, nodeA1, nodeA11, nodeB, nodeB1);
	}

	private void populateSource2(TreeWrapper<NodeTypeA> source) {
		populateSource(source);
		nodeA2 = new NodeTypeA("z");
		nodeA3 = new NodeTypeA("v");

		source.addChild(nodeA, nodeA2);
		source.addChild(nodeA, nodeA3);

	}

	private void populateSource(TreeWrapper<NodeTypeA> source) {
		nodeA = new NodeTypeA("a");
		nodeA1 = new NodeTypeA("a1");
		nodeA11 = new NodeTypeA("a11");
		nodeB = new NodeTypeA("b");
		nodeB1 = new NodeTypeA("b1");
		nodeB11 = new NodeTypeA("b11");

		source.addChild(nodeA, nodeA1);
		source.addChild(nodeA1, nodeA11);
		source.addChild(nodeB, nodeB1);
		source.addChild(nodeB1, nodeB11);

	}

}
