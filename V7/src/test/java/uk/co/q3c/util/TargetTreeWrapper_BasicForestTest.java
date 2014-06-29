package uk.co.q3c.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

public class TargetTreeWrapper_BasicForestTest {
	static class TargetTestNode {
		SourceTestNode sourceNode;
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

	static class TestNodeModifier implements NodeModifier<SourceTestNode, TargetTestNode> {

		@Override
		public TargetTestNode create(TargetTestNode parentNode, SourceTestNode sourceNode) {
			TargetTestNode newNode = new TargetTestNode(sourceNode.name);
			newNode.sourceNode = sourceNode;
			return newNode;
		}

		@Override
		public boolean attachOnCreate() {
			return false;
		}

		@Override
		public SourceTestNode sourceNodeFor(TargetTestNode targetNode) {
			return targetNode.sourceNode;
		}

		@Override
		public void setLeaf(TargetTestNode targetNode, boolean isLeaf) {
			// do nothing

		}

		@Override
		public void setCaption(TargetTestNode targetNode, String caption) {
			targetNode.name = caption;
		}

		@Override
		public void sortChildren(TargetTestNode parentNode, Comparator<TargetTestNode> comparator) {
			// TODO Auto-generated method stub

		}

	}

	static class TestCaptionReader implements CaptionReader<SourceTestNode> {

		@Override
		public String getCaption(SourceTestNode sourceNode) {
			return sourceNode.name;
		}

	}

	private BasicForest<TargetTestNode> forest;
	private SourceTestNode sourceNodeA;
	private SourceTestNode sourceNodeB;
	// private SourceTestNode sourceNodeC;
	// private SourceTestNode sourceNodeD;
	private TargetTestNode targetNodeA;
	private TargetTestNode targetNodeB;
	private TestCaptionReader captionReader;
	private TargetTreeWrapper_BasicForest<SourceTestNode, TargetTestNode> wrapper;

	@Before
	public void setup() {
		forest = new BasicForest<>();
		sourceNodeA = new SourceTestNode("sa");
		sourceNodeB = new SourceTestNode("sb");
		// sourceNodeC = new SourceTestNode("sc");
		// sourceNodeD = new SourceTestNode("sd");
		wrapper = new TargetTreeWrapper_BasicForest<>(forest);
		wrapper.setNodeModifier(new TestNodeModifier());
		wrapper.setCaptionReader(captionReader);
	}

	@Test(expected = NullPointerException.class)
	public void createNode_nullChild() {

		// given
		// when
		wrapper.createNode(targetNodeA, null);
		// then
	}

	@Test
	public void createNode() {

		// given

		// when
		TargetTestNode result = wrapper.createNode(null, sourceNodeA);
		// then
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(TargetTestNode.class);
		assertThat(result.name).isEqualTo(sourceNodeA.name);
	}

	@Test(expected = TreeCopyException.class)
	public void sort() {

		// given
		// when
		wrapper.sortChildren(targetNodeA, null);
		// then
	}

	@Test
	public void addChild() {

		// given
		targetNodeA = wrapper.createNode(null, sourceNodeA);
		targetNodeB = wrapper.createNode(null, sourceNodeB);
		// when
		wrapper.addChild(targetNodeA, targetNodeB);
		// then
		assertThat(forest.getParent(targetNodeB)).isEqualTo(targetNodeA);
	}
}
