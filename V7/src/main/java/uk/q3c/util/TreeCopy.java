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
package uk.q3c.util;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Copies source tree to target tree, to a depth specified by {@link #setMaxDepth(int)} with the option to provide
 * filters to select which source nodes to exclude. Source and target trees are wrapped in implementations of
 * {@link SourceTreeWrapper} and {@link TargetTreeWrapper}, to allow different tree implementations to be copied. Note
 * that by default nodes are not cloned, the copy is by reference, but you can determine exactly how target nodes are
 * created by implementing {@link NodeModifier}, and injecting it your the {@link TargetTreeWrapper} <br>
 * <br>
 * {@link TargetTreeWrapper} implementations are provided for the Vaadin {@link Tree}, {@link MenuBar} and
 * {@link BasicForest} <br>
 * {@link SourceTreeWrapper} implementations are provided for Vaadin {@link Tree} and {@link BasicForest} <br>
 * <br>
 * The way in which target nodes are constructed has a bearing on how a sort of nodes can be performed. For example,
 * linked node trees may require that a child is constructed from a parent or the sort field may be required at the
 * time
 * of node construction. This means that a sort must be performed before the target nodes are created. This utility
 * supports several variations by the use of {@link #sortOption} (the default is to sort the target nodes after
 * creation
 * but before before adding them to the target). If target nodes are the same as the source nodes, however, it makes no
 * difference whether the sort is performed on source or target nodes.<br>
 * <br>
 * A source or target node sort comparator is set to determine sort order using
 * {@link #setSourceSortComparator(Comparator) or {@link #setTargetSortComparator(Comparator)} - but note that if no
 * comparator is provided, nodes must implement {@link Comparable} to provide a natural ordering. <br>
 * <br>
 * An extension may be injected using {@link #setExtension(TreeCopyExtension)}, to enable post-processing. <br>
 * <br>
 * If a target node is of a different type to the source node, call
 * {@link TargetTreeWrapper#setNodeModifier(NodeModifier)} with an implementation to make the conversion. The default
 * is
 * simply returns a reference to the source node;
 *
 * @param <S>
 *         source node type
 * @param <T>
 *         target node type
 *
 * @author David Sowerby
 * @date 27 May 2014
 */
public class TreeCopy<S, T> {

    private static Logger log = LoggerFactory.getLogger(TreeCopy.class);
    ;
    private final SourceTreeWrapper<S> source;
    private final TargetTreeWrapper<S, T> target;
    // We need this to keep a lookup - the target list is sorted before being
    private final Map<S, T> sourceToTargetNodeMap = new HashMap<>();
    private final LinkedList<NodeFilter<S>> sourceFilters = new LinkedList<>();
    private TreeCopyExtension<S, T> extension;
    private boolean limitedDepth = false;
    private int maxDepth = Integer.MAX_VALUE;
    private SortOption sortOption = SortOption.SORT_TARGET_NODES_BEFORE_ADD;
    private boolean sorted = true;
    private Comparator<S> sourceSortComparator;
    private Comparator<T> targetSortComparator;

    public TreeCopy(SourceTreeWrapper<S> source, TargetTreeWrapper<S, T> target) {
        super();
        this.source = source;
        this.target = target;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Sets the depth of the copy required. This automatically sets {@link #limitedDepth} to true.
     *
     * @param maxDepth
     */
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        limitedDepth = true;
        log.debug("maxDepth set to {}", maxDepth);
    }

    public void addSourceFilter(NodeFilter<S> filter) {
        sourceFilters.add(filter);
    }

    public void removeSourceFilter(NodeFilter<S> filter) {
        sourceFilters.remove(filter);
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
            extension.invoke(source, target, sourceToTargetNodeMap);
        }
    }

    private void loadNodeList(T parentNode, List<S> sourceNodeList, int level) {

        Map<T, S> targetToSourceNodeMap = null;
        switch (sortOption) {
            // some target nodes can only be created from their parent, and cannot be sorted after creation
            // so the sort order must be determined before constructing target nodes. This could be used as default
            // as it
            // will fit most cases, but occasionally a target node may use a different sort key
            case SORT_SOURCE_NODES:
                log.debug("sorting list of source nodes, using comparator");
                if (sorted) {
                    Collections.sort(sourceNodeList, sourceSortComparator);
                }
                targetToSourceNodeMap = createTargetNodes(parentNode, sourceNodeList, sortOption);

                addNodesToTarget(parentNode, targetToSourceNodeMap);
                drillDown(targetToSourceNodeMap, level);
                break;
            // This is the default option, create the child nodes for a given parent,
            // then sort them before adding them to
            // the target
            case SORT_TARGET_NODES_BEFORE_ADD:
                targetToSourceNodeMap = createTargetNodes(parentNode, sourceNodeList, sortOption);
                addNodesToTarget(parentNode, targetToSourceNodeMap);
                drillDown(targetToSourceNodeMap, level);
                break;
            // There are some cases, usually with linked node trees, where a child has to be created with the sort field
            // present, for example, a caption for MenuItem. Depending on the implementation,
            // it may be easier to sort the
            // children after they have been added to the parent (if the implementation allows it, as MenuItem does)
            case SORT_TARGET_NODES_AFTER_ADD:
                targetToSourceNodeMap = createTargetNodes(parentNode, sourceNodeList, sortOption);
                addNodesToTarget(parentNode, targetToSourceNodeMap);
                if (sorted) {
                    log.debug("sorting child target nodes after they have been added to target parent node");
                    target.sortChildren(parentNode, targetSortComparator);
                }
                drillDown(targetToSourceNodeMap, level);

                break;
        }

        if (extension != null) {
            extension.invoke(source, target, sourceToTargetNodeMap);
        }

        // sort the list into the order determined by the comparator
        // if (sortComparator != null) {
        //
        // Collections.sort(targetNodeMap, sortComparator);
        // }

        // Nodes are sorted, add to the target in sort order
        // It is up to the target implementation to maintain the order
        // for (T childNode : targetToSourceNodeMap.keySet()) {
        // target.addNode(parentNode, sourceChildNode);
        // }

    }

    /**
     * If nodes are not been added during creation, adds the target nodes to the target (attaching them to their parent
     * as required)
     *
     * @param parentNode
     * @param targetToSourceNodeMap
     */
    private void addNodesToTarget(T parentNode, Map<T, S> targetToSourceNodeMap) {
        if (target.getNodeModifier() == null || !target.getNodeModifier()
                                                       .attachOnCreate()) {
            log.debug("adding {} nodes to target", targetToSourceNodeMap.size());
            for (T node : targetToSourceNodeMap.keySet()) {
                target.addChild(parentNode, node);
            }
        }
    }

    /**
     * Now iterate over this level and drill down to next level If a node has no children call setLeaf (which the
     * target
     * will implement if used}
     *
     * @param targetToSourceNodeMap
     */

    private void drillDown(Map<T, S> targetToSourceNodeMap, int level) {
        for (T childNode : targetToSourceNodeMap.keySet()) {
            List<S> subNodeList = source.getChildren(targetToSourceNodeMap.get(childNode));
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

    /**
     * create the source nodes using target.addNode for this set of children. Target nodes are created using a map
     * which
     * will honour the sort requirements.
     *
     * @param sourceNodeList
     *
     * @return
     */
    private Map<T, S> createTargetNodes(T parentNode, List<S> sourceNodeList, SortOption sortOption) {
        Map<T, S> targetToSourceNodeMap = null;

        switch (sortOption) {

            case SORT_SOURCE_NODES:
                // nodes has already been sorted (if required), so using LinkedHashMap to keep current order
                targetToSourceNodeMap = new LinkedHashMap<>();
                break;

            case SORT_TARGET_NODES_BEFORE_ADD:
                // use the TreeMap to sort if sorted is true

                if (sorted) {
                    log.debug("using SortedMap with comparator to sort target nodes");
                    targetToSourceNodeMap = new TreeMap<>(targetSortComparator);
                } else {
                    log.debug("sorting not required, use LinkedHashMap for target nodes");
                    targetToSourceNodeMap = new LinkedHashMap<>();
                }
                break;

            case SORT_TARGET_NODES_AFTER_ADD:
                // the map implementation does not really matter
                targetToSourceNodeMap = new LinkedHashMap<>();
                break;

            default:
                break;
        }

        // create the target nodes using target.addNode for this set of children
        // parentNode is supplied so that the new child can be attached to its parent
        for (S sourceNode : sourceNodeList) {
            if (passesFilter(sourceNode)) {
                T targetNode = target.createNode(parentNode, sourceNode);
                if (targetNode != null) {
                    targetToSourceNodeMap.put(targetNode, sourceNode);
                    // we need an easy way to get from source node to target node
                    // especially for the extension (if there is one)
                    sourceToTargetNodeMap.put(sourceNode, targetNode);
                }
            }
        }

        return targetToSourceNodeMap;
    }

    private boolean passesFilter(S node) {
        boolean accept = true;
        for (NodeFilter<S> filter : sourceFilters) {
            if (!filter.accept(node)) {
                accept = false;
                break;
            }
        }
        return accept;
    }

    public Comparator<S> getSourceSortComparator() {
        return sourceSortComparator;
    }

    public void setSourceSortComparator(Comparator<S> sortComparator) {
        this.sourceSortComparator = sortComparator;
    }

    public TreeCopyExtension<S, T> getExtension() {
        return extension;
    }

    public void setExtension(TreeCopyExtension<S, T> extension) {
        this.extension = extension;
    }

    public Comparator<T> getTargetSortComparator() {
        return targetSortComparator;
    }

    public void setTargetSortComparator(Comparator<T> targetSortComparator) {
        this.targetSortComparator = targetSortComparator;
    }

    public SortOption getSortOption() {
        return sortOption;
    }

    public void setSortOption(SortOption sortOption) {
        this.sortOption = sortOption;
    }

    public boolean isSorted() {
        return sorted;
    }

    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    public enum SortOption {
        SORT_SOURCE_NODES, SORT_TARGET_NODES_AFTER_ADD, SORT_TARGET_NODES_BEFORE_ADD
    }

}
