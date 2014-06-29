package uk.co.q3c.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class SourceTreeWrapper_BasicForestTest {

	SourceTreeWrapper_BasicForest<SourceTestNode> wrapper;
	BasicForest<SourceTestNode> forest;
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
}
