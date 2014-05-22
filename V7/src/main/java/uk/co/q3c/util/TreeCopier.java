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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copies source tree to target tree, to a depth specified by {@link #setMaxDepth(int)}. Trees must implement
 * {@link TreeWrapper}, but this allows different tree implementations to be copied. Note that nodes are NOT cloned, the
 * copy is by reference. Sort order of the target nodes can be set using {@link #setSortComparator(Comparator)}. If a
 * target node is of a different type to the source node, {@link #setNodeCreator(NodeCreator)} with an implementation to
 * make the conversion. The default is simply returns a reference to the source node;
 *
 * @author David Sowerby
 * @date 27 May 2014
 * @param <S>
 * @param <T>
 */
public class TreeCopier<S, T> {

	private static Logger log = LoggerFactory.getLogger(TreeCopier.class);

	private final TreeWrapper<S> source;
	private final TreeWrapper<T> target;
	private int maxDepth = Integer.MAX_VALUE;
	private boolean limitedDepth = false;
	private Comparator<T> sortComparator;
	private NodeCreator<S, T> nodeCreator;
	private final LinkedList<TreeCopierFilter<S>> sourceFilters = new LinkedList<>();

	public TreeCopier(TreeWrapper<S> source, TreeWrapper<T> target) {
		super();
		this.source = source;
		this.target = target;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void addSourceFilter(TreeCopierFilter<S> filter) {
		sourceFilters.add(filter);
	}

	public void removeSourceFilter(TreeCopierFilter<S> filter) {
		sourceFilters.remove(filter);
	}

	/**
	 * Sets the depth of the copy required. This automatically sets {@link #limitedDepth} to true.
	 *
	 * @param maxDepth
	 */
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
		limitedDepth = true;
	}

	public boolean isLimitedDepth() {
		return limitedDepth;
	}

	/**
	 * Once a maximum depth has been specified, that maximum is enabled or disabled by this property.
	 * {@link #setMaxDepth(int)} will automatically set this property to 'true'
	 *
	 * @param limitedDepth
	 */
	public void setLimitedDepth(boolean limitedDepth) {
		this.limitedDepth = limitedDepth;
	}

	public TreeWrapper<?> getSource() {
		return source;
	}

	public TreeWrapper<?> getTarget() {
		return target;
	}

	public void copy() {
		loadNodeList(null, source.getRoots(), 1);
	}

	private void loadNodeList(T parentNode, List<S> sourceNodeList, int level) {
		List<T> targetNodeList = new LinkedList<>();
		for (S node : sourceNodeList) {
			if (passesFilter(node)) {
				T targetNode = nodeCreator.create(node);
				if (targetNode != null) {
					targetNodeList.add(targetNode);
				}
			}
		}
		// sort the list into the order determined by the comparator
		if (sortComparator != null) {
			log.debug("sorting list of nodes, using comparator");
			Collections.sort(targetNodeList, sortComparator);
		}

		// then add them to the forest
		for (T childNode : targetNodeList) {
			target.addChild(parentNode, childNode);
			List<S> subNodeList = source.getChildren(nodeCreator.sourceNodeFor(childNode));
			// drill down to next level
			if (subNodeList.size() > 0) {
				if (!isLimitedDepth() || level < maxDepth) {
					loadNodeList(childNode, subNodeList, level + 1);
				} else {
					target.setLeaf(childNode, true);
				}
			} else {
				target.setLeaf(childNode, true);
			}
		}
	}

	private boolean passesFilter(S node) {
		boolean accept = true;
		for (TreeCopierFilter<S> filter : sourceFilters) {
			if (!filter.accept(node)) {
				accept = false;
				break;
			}
		}
		return accept;
	}

	public Comparator<T> getSortComparator() {
		return sortComparator;
	}

	public void setSortComparator(Comparator<T> sortComparator) {
		this.sortComparator = sortComparator;
	}

	public NodeCreator<S, T> getNodeCreator() {
		return nodeCreator;
	}

	public void setNodeCreator(NodeCreator<S, T> nodeCreator) {
		this.nodeCreator = nodeCreator;
	}

}
