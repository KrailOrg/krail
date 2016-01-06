/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.util;

import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Tree;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A very simple semantic wrapper for the <a href=http://jung.sourceforge.net/site/index.html> Jung</a> library, to
 * use the more familiar language of trees. Underneath is it a proper implementation of a graph - there are many
 * methods not exposed through this wrapper, but you can access those via {@link #getGraph()}.  Uses a {@link
 * DirectedOrderedSparseMultigraph} to maintain insertion order
 * <p>
 * The E (edge) parameter for the underlying graph is a simple Integer.
 * <p>
 * Originally this implementation used a default DelegateForest which in turn uses a DirectedSparseGraph - this uses
 * hash maps, so it would appear that a different hash algorithm is being in Java 8 to Java 7 used, yielding a
 * different order for this test case.
 * BasicForest previously made no commitment to maintaining insertion order; however, the failure of this case under
 * Java 8 suggests that it might be expected to do so.  The implementation has therefore been changed to use a
 * {@link DirectedOrderedSparseMultigraph} (this uses LinkedHashMaps) to maintain insertion order.
 *
 * @param <V>
 *         the type of object to be contained (the 'node'). Must implement equals
 */
public class BasicForest<V> implements Serializable {

    private int edgeCount = 0;
    private Forest<V, Integer> graph;

    public BasicForest() {
        graph = new DelegateForest<V, Integer>(new DirectedOrderedSparseMultigraph<>());
    }

    public boolean hasChild(V parentNode, V childNode) {
        return graph.getParent(childNode)
                    .equals(parentNode);
    }

    public V getParent(V childNode) {
        return graph.getParent(childNode);
    }

    /**
     * First step is to identify where this branch should join the tree - this is the last node in the branch which is
     * already in the tree. The remainder of the branch is then appended to that node. If none of the nodes already
     * exist, the first node of the branch is taken as a root node (that is, it has no parent).
     *
     * @param branch
     *
     * @return
     */
    public V addBranch(List<V> branch) {
        if ((branch == null) || (branch.isEmpty())) {
            return null;
        }
        int startIndex = -1;
        for (int i = 0; i < branch.size(); i++) {
            if (!this.containsNode(branch.get(i))) {
                startIndex = i - 1;
                break;
            }
        }
        // no join found
        if (startIndex < 0) {
            // put the first node in as a root
            addNode(branch.get(0));
            startIndex = 1;
        }
        if (startIndex == 0) {
            addChild(null, branch.get(0));
            startIndex++;
        }
        for (int j = startIndex; j < branch.size(); j++) {
            addChild(branch.get(j - 1), branch.get(j));
        }
        return branch.get(0);
    }

    public void addNode(V node) {
        graph.addVertex(node);
    }

    public boolean containsNode(V node) {
        return graph.containsVertex(node);
    }

    /**
     * Adds a {@code childNode} to {@code parentNode}. Note that if {@code parentNode} is not already in the tree, it
     * will be added - which may mean that you no longer have a single root
     *
     * @param parentNode
     * @param childNode
     */
    public void addChild(V parentNode, V childNode) {
        if (parentNode == null) {
            addNode(childNode);
        } else {
            graph.addEdge(newEdge(), parentNode, childNode);
        }
    }

    private Integer newEdge() {
        edgeCount++;
        return edgeCount;
    }

    /**
     * Returns the node contained in the tree which matches (equals) the supplied {@code node}
     *
     * @param node
     *
     * @return
     */
    public V getNode(V node) {
        Collection<V> x = graph.getVertices();
        List<V> list = new ArrayList<V>(x);
        int n = list.indexOf(node);
        if (n < 0) {
            return null;
        }
        V found = list.get(n);
        return found;
    }

    public List<V> getChildren(V parentNode) {
        Collection<V> children = graph.getChildren(parentNode);
        List<V> result = new ArrayList<V>();
        if (children != null) {
            result.addAll(children);
        }
        return result;
    }

    /**
     * Get all the nodes which are below the {@code parentNode},that is children, children's children etc. The returned
     * list includes the {@code parentNode}
     *
     * @param parentNode
     *
     * @return
     */
    public List<V> getSubtreeNodes(V parentNode) {
        Collection<V> children = graph.getChildren(parentNode);
        List<V> list = new ArrayList<V>();
        list.add(parentNode);
        if (children != null) {
            for (V v : children) {
                list.addAll(getSubtreeNodes(v));
            }
        }
        return list;

    }

    /**
     * Finds all the leaves for the specified {@code parentNode}, that is, all those with no children;
     *
     * @param parentNode
     * @param leaves
     */
    private void findLeaves(V parentNode, List<V> leaves) {
        if (leaves == null) {
            return;
        }
        Collection<V> children = graph.getChildren(parentNode);
        if (children == null) {
            return;
        }
        if (children.size() == 0) {
            leaves.add(parentNode);
        } else {
            for (V v : children) {
                findLeaves(v, leaves);
            }
        }
    }

    /**
     * Finds all the leaves for the whole tree, that is, all those with no children, from the root of the tree. Use
     * {@link #findLeaves(Object)} if you want leaves for a subset of the tree
     *
     * @see #findLeaves(Object)
     */
    public List<V> findLeaves() {
        List<V> leaves = new ArrayList<V>();
        findLeaves(getRoot(), leaves);
        return leaves;
    }

    public List<V> findLeaves(V parentNode) {
        List<V> leaves = new ArrayList<V>();
        findLeaves(parentNode, leaves);
        return leaves;
    }

    /**
     * Returns a list of all the entries in the tree
     *
     * @return
     */
    public Collection<V> getEntries() {
        return graph.getVertices();
    }

    public void clear() {
        graph = new DelegateForest<V, Integer>();
        edgeCount = 0;
    }

    /**
     * Returns a list of all the roots - the entry which is at the start of each chain or branch. For the tree to be a
     * tree, there should only be one of these
     *
     * @return
     */
    public List<V> getRoots() {
        Collection<Tree<V, Integer>> t = graph.getTrees();
        List<V> branchRoots = new ArrayList<V>();
        for (Tree<V, Integer> branch : t) {
            branchRoots.add(branch.getRoot());
        }
        return branchRoots;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (V rootNode : getRoots()) {
            buf.append("\n");
            text(rootNode, buf, 0);
        }
        return buf.toString();
    }

    public void text(V node, StringBuilder buf, int level) {
        String indent = StringUtils.repeat("-", level + 1);
        buf.append(indent);
        buf.append(node.toString() + "\n");
        for (V child : getChildren(node)) {
            text(child, buf, level + 1);
        }
    }

    public boolean hasChildren(V parentNode) {
        return getChildCount(parentNode) > 0;
    }

    public int getChildCount(V parentNode) {
        return graph.getChildCount(parentNode);
    }

    /**
     * Assumes this is a genuine tree and that there is only one root, or just takes the first one
     *
     * @return
     */
    public V getRoot() {
        if (getRoots().isEmpty()) {
            return null;
        } else {
            return getRoots().get(0);
        }
    }

    public int getNodeCount() {
        return graph.getVertexCount();
    }

    public Forest<V, Integer> getGraph() {
        return graph;
    }

    /**
     * Uses a list to return all vertices, but no ordering should be implied
     *
     * @return
     */
    public List<V> getAllNodes() {
        return new ArrayList<V>(graph.getVertices());
    }

    public V getRootFor(V node) {
        if (node == null) {
            return null;
        }
        V nut = node;
        while (true) {
            V parent = graph.getParent(nut);
            if (parent == null) {
                break;
            } else {
                nut = parent;
            }
        }
        return nut;
    }

    public void removeNode(V node) {
        graph.removeVertex(node);
    }

    /**
     * Useful for immutable vertices, this method replaced the current vertex with a new vertex.  To do so, it has to deep copy to a subgraph, and then copy
     * back, so not very efficient.  This method is made necessary by Jung's approach to maintaining the integrity of the map - so far I have not found a way
     * to move an edge from one vertex to another without the associated vertices being deleted. https://github.com/davidsowerby/krail/issues/397
     *
     * @param currentVertex
     *         the vertex to be replaced
     * @param newVertex
     *         the vertex to replace it with
     */
    public void replaceNode(@Nonnull V currentVertex, @Nonnull V newVertex) {
        checkNotNull(currentVertex);
        checkNotNull(newVertex);
        V parentVertex = getParent(currentVertex);
        BasicForest<V> subGraph = subGraph(currentVertex, newVertex);
        graph.removeVertex(currentVertex);
        addChild(parentVertex, newVertex);
        mergeSubGraph(subGraph, parentVertex);

    }

    /**
     * Assumes a single root
     *
     * @param sourceSubGraph
     * @param targetParentVertex
     */
    private void mergeSubGraph(BasicForest<V> sourceSubGraph, V targetParentVertex) {
        if (sourceSubGraph.getNodeCount() > 0) {
            addChild(targetParentVertex, sourceSubGraph.getRoot());
            copyChildren(sourceSubGraph, sourceSubGraph.getRoot(), this, sourceSubGraph.getRoot());
        }
    }

    /**
     * Returns a BasicForest with a full depth sub-graph of {@code root}, but with {@code root} itself replaced by {@code newRoot}
     *
     * @param root
     * @param newRoot
     *
     * @return
     */
    private BasicForest<V> subGraph(V root, V newRoot) {
        BasicForest<V> subGraph = new BasicForest<V>();
        copyChildren(this, root, subGraph, newRoot);
        return subGraph;
    }


    private void copyChildren(BasicForest<V> sourceGraph, V sourceParentVertex, BasicForest<V> targetGraph, V targetParentVertex) {
        final List<V> children = sourceGraph.getChildren(sourceParentVertex);
        if (children != null) {
            for (V child : children) {
                targetGraph.addChild(targetParentVertex, child);
                copyChildren(sourceGraph, child, targetGraph, child);
            }
        }
    }
}
