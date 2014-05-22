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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.ui.Tree;

public class VaadinTreeWrapper<V> implements TreeWrapper<V> {

	private final Tree tree;
	private VaadinTreeItemCaption<V> itemCaption;

	public VaadinTreeWrapper(Tree tree) {
		super();
		this.tree = tree;
	}

	@Override
	public boolean hasChildren(V parentNode) {
		return tree.hasChildren(parentNode);
	}

	@Override
	public int getChildCount(V parentNode) {
		return getChildCount(parentNode);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<V> getRoots() {
		List<V> roots = new ArrayList<>();
		for (Object node : tree.getItemIds()) {
			if (tree.getParent(node) == null) {
				roots.add((V) node);
			}
		}
		return roots;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<V> getChildren(V parentNode) {
		LinkedList<V> children = new LinkedList<>();
		// have to copy, cannot cast the collection
		Collection<?> nodes = tree.getChildren(parentNode);
		if (nodes != null) {
			for (Object node : nodes) {
				children.add((V) node);
			}
		}
		return children;
	}

	@Override
	public void addNode(V node) {
		tree.addItem(node);
		if (itemCaption != null) {
			tree.setItemCaption(node, itemCaption.caption(node));
		}
	}

	@Override
	public void addChild(V parentNode, V childNode) {
		checkNotNull(childNode);
		if ((parentNode != null) && (!tree.containsId(parentNode))) {
			addNode(parentNode);
		}
		addNode(childNode);
		tree.setParent(childNode, parentNode);
	}

	@Override
	public void setLeaf(V parentNode, boolean isLeaf) {
		tree.setChildrenAllowed(parentNode, !isLeaf);
	}

	/**
	 * Set a caption generator to populate the Vaadin Item caption. Not needed if the Vaadin tree is being used as the
	 * source for a copy.
	 *
	 * @param itemCaption
	 */
	public void setItemCaption(VaadinTreeItemCaption<V> itemCaption) {
		this.itemCaption = itemCaption;
	}

}
