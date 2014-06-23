/*
 * Copyright (C) 2013 David Sowerby
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

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class BasicForestTest {

	BasicForest<String> tree;
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
		tree = new BasicForest<>();
	}

	@Test
	public void addNode() {
		// given

		// when
		tree.addNode(s1);
		// then
		assertThat(tree.containsNode(s1)).isTrue();

	}

	@Test
	public void addChildHasChild() {
		// given

		// when
		tree.addNode(s1);
		tree.addChild(s1, s11);
		// then
		assertThat(tree.containsNode(s1)).isTrue();
		assertThat(tree.containsNode(s11)).isTrue();
		assertThat(tree.hasChild(s1, s11)).isTrue();
	}

	@Test
	public void addChildParentNotInTree() {
		// given

		// when
		tree.addChild(s1, s11);
		// then
		assertThat(tree.containsNode(s1)).isTrue();
		assertThat(tree.containsNode(s11)).isTrue();
		assertThat(tree.hasChild(s1, s11)).isTrue();
	}

	@Test
	public void getParent() {
		// given

		// when
		tree.addNode(s1);
		tree.addChild(s1, s11);
		// then
		assertThat(tree.getParent(s11)).isEqualTo(s1);
	}

	@Test
	public void addBranch() {
		// given
		List<String> branch = new ArrayList<>();
		branch.add(s1);
		branch.add(s11);
		branch.add(s111);
		// when
		tree.addBranch(branch);
		// then
		assertThat(tree.containsNode(s1)).isTrue();
		assertThat(tree.containsNode(s11)).isTrue();
		assertThat(tree.containsNode(s111)).isTrue();
		assertThat(tree.getParent(s11)).isEqualTo(s1);
		assertThat(tree.getParent(s111)).isEqualTo(s11);
	}

	@Test
	public void getNode() {
		// given
		List<String> branch = new ArrayList<>();
		branch.add(s1);
		branch.add(s11);
		branch.add(s111);
		// when
		tree.addBranch(branch);
		// then
		assertThat(tree.getNode(s11)).isEqualTo(s11);
	}

	@Test
	public void getChildren() {
		// given
		List<String> branch = new ArrayList<>();
		branch.add(s1);
		branch.add(s11);
		branch.add(s111);
		// when
		tree.addBranch(branch);
		tree.addChild(s1, s12);
		// then
		assertThat(tree.getChildCount(s1)).isEqualTo(2);
		assertThat(tree.getChildren(s1)).containsOnly(s11, s12);
	}

	@Test
	public void getSubtreeNodes() {
		// given

		// when
		addAllNodes();
		// then
		assertThat(tree.getSubtreeNodes(s1)).containsOnly(s1, s11, s111, s12, s121);
	}

	@Test
	public void findLeavesOf() {
		// given

		// when
		addAllNodes();
		// then
		assertThat(tree.findLeaves(s1)).containsOnly(s111, s121);
	}

	@Test
	public void findLeaves() {
		// given

		// when
		addAllNodes();
		// then
		assertThat(tree.findLeaves()).containsOnly(s111, s121, s21, s22);
	}

	@Test
	public void getEntries() {
		// given

		// when
		tree.addNode(s1);
		tree.addChild(s1, s11);
		tree.addChild(s1, s12);
		tree.addNode(s2);
		// then
		assertThat(tree.getEntries()).containsOnly(s1, s11, s12, s2);
	}

	@Test
	public void clear() {
		// given
		addAllNodes();
		// when
		tree.clear();
		// then
		assertThat(tree.getEntries()).isEmpty();
	}

	@Test
	public void getBranchRoots() {
		// given

		// when
		addAllNodes();
		// then
		assertThat(tree.getRoots()).containsOnly(s0);
	}

	/**
	 * toString() puts a blank line at the start
	 */
	@Test
	public void tostring() {
		// given

		// when
		addAllNodes();
		// then
		assertThat(tree.toString()).isEqualTo("\n-0\n--2\n---2.1\n---2.2\n--1\n---1.2\n----1.2.1\n---1.1\n----1.1.1\n");
	}

	@Test
	public void text() {
		// given
		StringBuilder buf = new StringBuilder();
		// when
		addAllNodes();
		tree.text(s0, buf, 0);
		String s = buf.toString();
		// then
		assertThat(s).isEqualTo("-0\n--2\n---2.1\n---2.2\n--1\n---1.2\n----1.2.1\n---1.1\n----1.1.1\n");
	}

	@Test
	public void getChildCount() {
		// given

		// when
		addAllNodes();
		// then
		assertThat(tree.getChildCount(s0)).isEqualTo(2);
		assertThat(tree.getChildCount(s111)).isEqualTo(0);
	}

	@Test
	public void hasChildren() {
		// given

		// when
		addAllNodes();
		// then
		assertThat(tree.hasChildren(s0)).isTrue();
		assertThat(tree.hasChildren(s111)).isFalse();
	}

	@Test
	public void getRoot() {
		// given

		// when
		addAllNodes();
		// then
		assertThat(tree.getRoot()).isEqualTo(s0);
	}

	@Test
	public void getNodeCount() {
		// given

		// when
		addAllNodes();
		// then
		assertThat(tree.getNodeCount()).isEqualTo(9);
	}

	@Test
	public void getRootFor() {

		// given
		addAllNodes();
		tree.addNode("x");
		// when

		// then
		assertThat(tree.getRootFor(null)).isNull();
		assertThat(tree.getRootFor(s1)).isEqualTo(s0);
		assertThat(tree.getRootFor(s0)).isEqualTo(s0);
		assertThat(tree.getRootFor(s121)).isEqualTo(s0);
	}

	private void addAllNodes() {

		tree.addNode(s0);
		tree.addChild(s0, s1);
		tree.addChild(s0, s2);
		tree.addChild(s1, s11);
		tree.addChild(s1, s12);
		tree.addChild(s11, s111);
		tree.addChild(s12, s121);
		tree.addChild(s2, s21);
		tree.addChild(s2, s22);

	}
}
