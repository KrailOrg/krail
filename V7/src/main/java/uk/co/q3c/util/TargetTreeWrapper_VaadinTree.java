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

import com.vaadin.ui.Tree;

public class TargetTreeWrapper_VaadinTree<S, T> implements TargetTreeWrapper<S, T> {

	private NodeModifier<S, T> nodeModifier;// = new DefaultNodeModifier<S, T>();
	private final Tree tree;
	private TreeNodeCaption<S> captionReader;

	public TargetTreeWrapper_VaadinTree(Tree tree) {
		super();
		this.tree = tree;
	}

	public NodeModifier<S, T> getNodeModifier() {
		return nodeModifier;
	}

	@Override
	public void setNodeModifier(NodeModifier<S, T> nodeModifier) {
		this.nodeModifier = nodeModifier;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T addNode(T parentNode, S sourceChildNode) {
		checkNotNull(sourceChildNode);
		T newTargetNode = null;
		if (nodeModifier == null) {
			newTargetNode = (T) sourceChildNode;
		} else {
			newTargetNode = nodeModifier.create(parentNode, sourceChildNode);
		}
		tree.setItemCaption(newTargetNode, captionReader.getCaption(sourceChildNode));
		return newTargetNode;
	}

	public void addChild(T parentNode, T childNode) {
		tree.setParent(childNode, parentNode);
	}

	@Override
	public void setLeaf(T node, boolean isLeaf) {
		nodeModifier.setLeaf(node, isLeaf);
	}

	@Override
	public void setCaptionReader(TreeNodeCaption<S> captionReader) {
		this.captionReader = captionReader;

	}

	public TreeNodeCaption<S> getCaptionReader() {
		return captionReader;
	}

}
