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

import java.util.Comparator;

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
public interface TargetTreeWrapper<S, T> {

	/**
	 * Creates a new target node based on {@code sourceChildNode}, and adds to to the {@code parentNode}. Returns the
	 * new child target node. Delegates to {@link NodeModifier#create(Object, Object)} if one is provided.
	 *
	 * @param parentNode
	 *            the parent target node which will be the parent of the created target node. This can be null, if the
	 *            new target node is to be a root.
	 * @param sourceChildNode
	 *            the source node on which the new target node is to be based. Cannot be null.
	 * @return the new child target node. Can be null, in which case {@link TreeCopy} will ignore it. However, if you
	 *         want to omit certain source nodes, it may be better to use a {@link TreeCopyFilter}
	 */
	public T createNode(T parentNode, S sourceChildNode);

	/**
	 * Called by {@link TreeCopy} to offer the chance to mark this node as not having any children. Not used by all
	 * implementations
	 *
	 * @param isLeaf
	 */
	public abstract void setLeaf(T node, boolean isLeaf);

	public abstract void setCaptionReader(CaptionReader<S> captionReader);

	CaptionReader<S> getCaptionReader();

	void setNodeModifier(NodeModifier<S, T> nodeModifier);

	NodeModifier<S, T> getNodeModifier();

	/**
	 * Sorts the children of {@code parentNode}. {@code parentNode} can not be null, {@code comparator} may be null
	 *
	 * @param parentNode
	 */
	public void sort(T parentNode, Comparator<T> comparator);

	void addChild(T parentNode, T childNode);

}
