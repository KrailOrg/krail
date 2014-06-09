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

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public class TargetTreeWrapper_MenuBar<S> implements TargetTreeWrapper<S, MenuItem> {

	private final MenuBar menuBar;
	private NodeModifier<S, MenuItem> nodeModifier;
	private TreeNodeCaption<S> captionReader;

	public TargetTreeWrapper_MenuBar(MenuBar menuBar) {
		super();
		this.menuBar = menuBar;
	}

	public NodeModifier<S, MenuItem> getNodeModifier() {
		return nodeModifier;
	}

	@Override
	public void setNodeModifier(NodeModifier<S, MenuItem> nodeModifier) {
		this.nodeModifier = nodeModifier;
	}

	@Override
	public MenuItem addNode(MenuItem parentNode, S sourceChildNode) {
		checkNotNull(sourceChildNode);
		checkNotNull(captionReader);
		MenuItem newTargetNode = null;
		if (parentNode == null) {
			newTargetNode = menuBar.addItem(captionReader.getCaption(sourceChildNode), null);
		} else {
			newTargetNode = parentNode.addItem(captionReader.getCaption(sourceChildNode), null);
		}
		return newTargetNode;
	}

	/**
	 * For the MenuBar, a leaf item should contain a command
	 */
	@Override
	public void setLeaf(MenuItem node, boolean isLeaf) {
		nodeModifier.setLeaf(node, isLeaf);
	}

	@Override
	public void setCaptionReader(TreeNodeCaption<S> captionReader) {
		this.captionReader = captionReader;
	}

}
