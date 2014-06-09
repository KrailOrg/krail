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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copies source tree to target tree, to a depth specified by {@link #setMaxDepth(int)}. Trees must implement
 * {@link SourceTreeWrapper}, but this allows different tree implementations to be copied. Note that nodes are NOT
 * cloned, the copy is by reference. Sort order of the target nodes can be set using
 * {@link #setSortComparator(Comparator)}. If a target node is of a different type to the source node,
 * {@link #setNodeModifier(NodeModifier)} with an implementation to make the conversion. The default is simply returns a
 * reference to the source node;
 *
 * @author David Sowerby
 * @date 27 May 2014
 * @param <S>
 *            source node type
 * @param <T>
 *            target node type
 */
public class TreeCopier<S, T> {

	private static Logger log = LoggerFactory.getLogger(TreeCopier.class);

	private final SourceTreeWrapper<S> source;
	private final TargetTreeWrapper<S, T> target;
	// We need this to keep a lookup - the target list is sorted before being
	private final Map<S, T> nodeMap = new HashMap<>();
	private int maxDepth = Integer.MAX_VALUE;
	private boolean limitedDepth = false;
	private Comparator<T> sortComparator;
	private final LinkedList<TreeCopierFilter<S>> sourceFilters = new LinkedList<>();
	private TreeCopierExtension<S, T> extension;

	public TreeCopier(SourceTreeWrapper<S> source, TargetTreeWrapper<S, T> target) {
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

	public void copy() {
		loadNodeList(null, source.getRoots(), 1);
		if (extension != null) {
			extension.invoke(source, target, nodeMap);
		}
	}

	private void loadNodeList(T parentNode, List<S> sourceNodeList, int level) {

		// create the source nodes using target.addNode for this set of children
		// these are sorted according to sortComparator
		SortedMap<T, S> targetNodeMap = new TreeMap<>(sortComparator);
		for (S sourceNode : sourceNodeList) {
			if (passesFilter(sourceNode)) {
				T targetNode = target.addNode(parentNode, sourceNode);
				if (targetNode != null) {
					targetNodeMap.put(targetNode, sourceNode);
					// we need an easy way to get from source node to source node
					// especially for the extension (if there is one)
					nodeMap.put(sourceNode, targetNode);
				}
			}
		}
		// sort the list into the order determined by the comparator
		// if (sortComparator != null) {
		// log.debug("sorting list of nodes, using comparator");
		// Collections.sort(targetNodeMap, sortComparator);
		// }

		// now they are sorted, drill down each, and add to the forest
		for (T childNode : targetNodeMap.keySet()) {
			List<S> subNodeList = source.getChildren(targetNodeMap.get(childNode));
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

	public TreeCopierExtension<S, T> getExtension() {
		return extension;
	}

	public void setExtension(TreeCopierExtension<S, T> extension) {
		this.extension = extension;
	}

}
