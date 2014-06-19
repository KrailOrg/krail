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

import com.vaadin.ui.MenuBar.MenuItem;

/**
 * Create a source node of type '<S>' from a target node of type '<T>'
 *
 * @author David Sowerby
 * @date 27 May 2014
 * @param <S>
 * @param <T>
 */
public interface NodeModifier<S, T> {

	/**
	 * Create a target node from a source node. May return null if creating the target node is invalid in some
	 * implementation specific way. The parent node for the target is supplied because linked node implementations (
	 * {@link MenuItem} for example) only allow creation of children from the parent. <br>
	 * <br>
	 * If you have a choice, it is usually better not to attach a new child to its parent during create, as it allows
	 * target nodes to be sorted before being attached to the parent. <br>
	 * <br>
	 * The return value for {@link #attachOnCreate()} must return true if a node is attached to its parent during
	 * create.
	 *
	 * @param source
	 * @return
	 */
	T create(T parentNode, S sourceNode);

	/**
	 * Return true if a node is attached to its parent on create, otherwise false. If true, consider whether the
	 * {@link TreeCopy#setSortOption()} should sort the source nodes, or sort after adding the target nodes - it will
	 * not be possible to sort the target nodes before they are added.
	 *
	 * @return
	 */
	boolean attachOnCreate();

	/**
	 * Returns a source node given a target node. The easiest way to implement this is to have the target node contain a
	 * reference to its source node. If the target node is the same as the source node, simply return {@code targetNode}
	 *
	 * @param target
	 * @return
	 */

	S sourceNodeFor(T targetNode);

	/**
	 * Some implementations need to mark a node as a leaf. If not needed, implement as an empty method
	 *
	 * @param Target
	 */
	void setLeaf(T targetNode, boolean isLeaf);

	/**
	 * Some implementations, usually in the user interface, require a caption or label. If not required, implement as an
	 * empty method
	 *
	 * @param targetNode
	 * @param caption
	 */
	void setCaption(T targetNode, String caption);

}
