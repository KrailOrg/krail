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

import java.util.Collections;
import java.util.Comparator;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public class TargetTreeWrapper_MenuBar<S> extends TargetTreeWrapperBase<S, MenuItem> {

	private final MenuBar menuBar;

	public TargetTreeWrapper_MenuBar(MenuBar menuBar) {
		super();
		this.menuBar = menuBar;
	}

	@Override
	public MenuItem createNode(MenuItem parentNode, S sourceChildNode) {
		checkNotNull(sourceChildNode);
		checkNotNull(getCaptionReader(), "This implementation requires a caption reader");
		MenuItem newTargetNode = null;
		if (parentNode == null) {
			newTargetNode = menuBar.addItem(getCaptionReader().getCaption(sourceChildNode), null);
		} else {
			newTargetNode = parentNode.addItem(getCaptionReader().getCaption(sourceChildNode), null);
		}
		return newTargetNode;
	}

	@Override
	public void sort(MenuItem parentNode, Comparator<MenuItem> comparator) {
		Collections.sort(parentNode.getChildren(), comparator);

	}

	@Override
	public void addChild(MenuItem parentNode, MenuItem childNode) {
		throw new TreeCopyException("addNode cannot be used with this implementation, node is added during creation");

	}

}
