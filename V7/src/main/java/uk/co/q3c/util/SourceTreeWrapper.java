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

import java.util.List;

import com.vaadin.ui.Tree;

/**
 * Implementations of this interface wrap a tree of a particular type (for example a Vaadin {@link Tree}) to provide a
 * common interface for utility operations such as copying a tree from, for example, a {@link BasicForest} to a Vaadin
 * Tree<br>
 * <br>
 *
 * Parameter 'N' it the node type
 *
 * @author David Sowerby
 * @date 26 May 2014
 */
public interface SourceTreeWrapper<N> {

	// public abstract boolean hasChildren(N parentNode);

	// public abstract int getChildCount(N parentNode);

	public abstract List<N> getRoots();

	/**
	 * Returns an empty list of there are no children. Implementations must not return null
	 *
	 * @param parentNode
	 * @return
	 */
	public abstract List<N> getChildren(N parentNode);

	// public abstract void addNode(N parentNode, N childNode);

	/**
	 * Called by {@link TreeCopy} to offer the chance to mark this node as not having any children. Not used by all
	 * implementations
	 *
	 * @param isLeaf
	 */
	// public abstract void setLeaf(N parentNode, boolean isLeaf);

	// public boolean hasParent(N childNode);
}
