package uk.co.q3c.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Tree;

public class TargetTreeWrapper_VaadinTreeTest {

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

	private Tree tree;
	private SourceTestNode sourceNodeA;
	private SourceTestNode sourceNodeB;
	private SourceTestNode sourceNodeC;
	// private SourceTestNode sourceNodeD;
	private TargetTestNode targetNodeA;
	// private TargetTestNode targetNodeB;
	// private TargetTestNode targetNodeC;
	// private TargetTestNode targetNodeD;
	private TestCaptionReader captionReader;
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

	@Test(expected = NullPointerException.class)
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
}
