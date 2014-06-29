package uk.co.q3c.util;

import static org.assertj.core.api.Assertions.fail;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public class TargetTreeWrapper_MenuBarTest {

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

	TargetTreeWrapper_MenuBar<SourceTestNode, MenuItem> wrapper;
	private MenuBar menuBar;
	private SourceTestNode sourceNodeA;
	private SourceTestNode sourceNodeB;
	// private SourceTestNode sourceNodeC;
	// private SourceTestNode sourceNodeD;
	private MenuItem targetNodeA;
	// private TargetTestNode targetNodeB;
	// private TargetTestNode targetNodeC;
	// private TargetTestNode targetNodeD;
	private TestCaptionReader captionReader;

	@Before
	public void setup() {
		menuBar = new MenuBar();
		sourceNodeA = new SourceTestNode("sa");
		sourceNodeB = new SourceTestNode("sb");
		// sourceNodeC = new SourceTestNode("sc");
		// sourceNodeD = new SourceTestNode("sd");
		// targetNodeA = new TargetTestNode("ta");
		// targetNodeB = new TargetTestNode("tb");
		// targetNodeC = new TargetTestNode("tc");
		// targetNodeD = new TargetTestNode("td");
	}

	@Test(expected = NullPointerException.class)
	public void createNode_nullChild() {

		// given
		wrapper = new TargetTreeWrapper_MenuBar<>(menuBar);
		// when
		wrapper.createNode(targetNodeA, null);
		// then
	}

	@Test(expected = NullPointerException.class)
	public void createNode__noModifierSet() {

		// given
		wrapper = new TargetTreeWrapper_MenuBar<>(menuBar);
		captionReader = new TestCaptionReader();
		wrapper.setCaptionReader(captionReader);
		// when
		wrapper.createNode(null, sourceNodeA);
		// then

	}

	public void sort() {

		// given
		wrapper = new TargetTreeWrapper_MenuBar<>(menuBar);
		// when
		wrapper.sortChildren(targetNodeA, null);
		// then
		fail("not yet implemented");
	}

	@Test(expected = TreeCopyException.class)
	public void addChild() {

		// given
		wrapper = new TargetTreeWrapper_MenuBar<>(menuBar);
		// when
		wrapper.addChild(targetNodeA, targetNodeA);
		// then
	}
}
