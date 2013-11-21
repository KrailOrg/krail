package uk.co.q3c.util;

import org.junit.Before;
import org.junit.Test;

public class DynamicDAGTest {

	DynamicDAG<String> dag;
	String s0 = new String("0");
	String s1 = new String("1");
	String s11 = new String("1.1");
	String s12 = new String("1.2");
	String s121 = new String("1.2.1");
	String s111 = new String("1.1.1");
	String s2 = new String("2");
	String s21 = new String("2.1");
	String s22 = new String("2.2");

	@Before
	public void setup() {
		dag = new DynamicDAG<>();
	}

	@Test(expected = CycleDetectedException.class)
	public void cycleCheck() {

		// given
		dag.addNode(s0);
		dag.addChild(s0, s1);
		// when
		dag.addChild(s1, s0);
		// then
		// exception expected
	}

	@Test(expected = CycleDetectedException.class)
	public void cycleCheckMultipleRoots() {

		// given
		dag.addNode(s0);
		dag.addNode(s1);
		dag.addNode(s2);

		dag.addChild(s0, s11);
		dag.addChild(s0, s21);
		dag.addChild(s2, s21);
		dag.addChild(s1, s21);
		dag.addChild(s22, s21);
		// when
		dag.addChild(s21, s1);

		// then
		// exception
	}

	@Test(expected = CycleDetectedException.class)
	public void parentSelf() {

		// given

		// when
		dag.addChild(s11, s11);
		// then

	}

	/**
	 * Just to make sure exception isn't thrown when it shouldn't be
	 */
	@Test
	public void cycleCheck_ok() {

		// given
		// when

		dag.addNode(s0);
		dag.addChild(s0, s1);
		dag.addChild(s0, s2);
		dag.addChild(s1, s11);
		dag.addChild(s1, s12);
		dag.addChild(s11, s111);
		dag.addChild(s12, s121);
		dag.addChild(s2, s21);
		dag.addChild(s2, s22);

		// then
		// no need to check structure, that is done by BasicForest
	}
}
