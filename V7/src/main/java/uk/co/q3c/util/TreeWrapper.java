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
 * {@link BasicForest} already implements this interface <br>
 * <br>
 * Parameter 'V' it the node type
 *
 * @author David Sowerby
 * @date 26 May 2014
 */
public interface TreeWrapper<V> {

	public abstract boolean hasChildren(V parentNode);

	public abstract int getChildCount(V parentNode);

	public abstract List<V> getRoots();

	public abstract List<V> getChildren(V parentNode);

	public abstract void addNode(V node);

	public abstract void addChild(V parentNode, V childNode);

	/**
	 * Mark this node as not having any children. Not used by all implementations
	 *
	 * @param isLeaf
	 */
	public abstract void setLeaf(V parentNode, boolean isLeaf);
}
