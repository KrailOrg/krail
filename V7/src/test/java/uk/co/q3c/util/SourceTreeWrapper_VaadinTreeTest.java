package uk.co.q3c.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Tree;

public class SourceTreeWrapper_VaadinTreeTest {
	SourceTreeWrapper_VaadinTree<SourceTestNode> wrapper;

	Tree tree;
	private SourceTestNode nodeA;
	private SourceTestNode nodeB;
	private SourceTestNode nodeC;
	private SourceTestNode nodeD;

	static class SourceTestNode {
		String name;

		public SourceTestNode(String name) {
			super();
			this.name = name;
		}
	}

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
}
