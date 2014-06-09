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

public class TargetTreeWrapper_BasicForest<S, T> implements TargetTreeWrapper<S, T> {

	private final BasicForest<T> forest;
	private NodeModifier<S, T> nodeModifier;// = new DefaultNodeModifier<S, T>();

	public TargetTreeWrapper_BasicForest(BasicForest<T> forest) {
		super();
		this.forest = forest;
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
		return newTargetNode;
	}

	public void addChild(T parentNode, T childNode) {
		forest.addChild(parentNode, childNode);
	}

	@Override
	public void setLeaf(T node, boolean isLeaf) {
		nodeModifier.setLeaf(node, isLeaf);
	}

	/**
	 * Not used in this implementation
	 */
	@Override
	public void setCaptionReader(TreeNodeCaption<S> captionreader) {

	}

}
