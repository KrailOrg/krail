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

package uk.q3c.krail.core.services;

import com.google.common.collect.ImmutableList;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
import org.slf4j.Logger;
import uk.q3c.util.dag.CycleDetectedException;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;
import static org.slf4j.LoggerFactory.*;

/**
 * Default implementation of {@link ServicesGraph}
 * <p>
 * Created by David Sowerby on 03 Dec 2015
 */
@ThreadSafe
public abstract class AbstractServicesGraph<T> implements ServicesGraph<T> {
    private static final EnumMap<Selection, EnumSet<Dependency.Type>> selectionCriteria;
    private static Logger log = getLogger(AbstractServicesGraph.class);

    static {
        selectionCriteria = new EnumMap<>(Selection.class);
        selectionCriteria.put(Selection.ALL, EnumSet.allOf(Dependency.Type.class));
        selectionCriteria.put(Selection.ALWAYS_REQUIRED, EnumSet.of(Dependency.Type.ALWAYS_REQUIRED));
        selectionCriteria.put(Selection.REQUIRED_AT_START, EnumSet.of(Dependency.Type.ALWAYS_REQUIRED, Dependency.Type.REQUIRED_ONLY_AT_START));
        selectionCriteria.put(Selection.ONLY_REQUIRED_AT_START, EnumSet.of(Dependency.Type.REQUIRED_ONLY_AT_START));
        selectionCriteria.put(Selection.OPTIONAL, EnumSet.of(Dependency.Type.OPTIONAL));
    }

    private Forest<T, ServiceEdge> graph = new DelegateForest<>(new DirectedOrderedSparseMultigraph<>());


    /**
     * Finds all the vertices at the opposite end of the edges from {@code referenceVertex}, filtered to include only those identified in {@code selection}
     *
     * @param referenceVertex  the vertex at the centre of the 'query'
     * @param wantDependencies if true, select dependency edges, otherwise select dependant edges
     * @param selection        the Dependency.Type(s) to include
     * @return list of vertices at the opposite end of the edges from {@code referenceVertex}, or an empty list if there are no edges
     */
    private List<T> findRelations(T referenceVertex, boolean wantDependencies, Selection selection) {

        Collection<ServiceEdge> edges = wantDependencies ? getDependenciesEdges(referenceVertex) : getDependantsEdges(referenceVertex);

        EnumSet<Dependency.Type> selectionSet = selectionCriteria.get(selection);
        return edges.stream()
                    .filter(edge -> selectionSet.contains(edge.getType()))
                    .map(edge -> graph.getOpposite(referenceVertex, edge))
                    .collect(Collectors.toList());

    }


    @Override
    public synchronized List<T> findDependencies(T dependant, Selection selection) {
        checkNotNull(dependant);
        checkNotNull(selection);
        return findRelations(
                dependant, true, selection);
    }

    @Override
    public synchronized List<T> findDependants(T dependency, Selection selection) {
        checkNotNull(dependency);
        checkNotNull(selection);
        return findRelations(dependency, false, selection);
    }

    private Collection<ServiceEdge> getDependenciesEdges(T dependant) {
        Collection<ServiceEdge> edges = graph.getOutEdges(dependant);
        return (edges == null) ? new ArrayList<ServiceEdge>() : edges;
    }


    private Collection<ServiceEdge> getDependantsEdges(T dependency) {
        Collection<ServiceEdge> edges = graph.getInEdges(dependency);
        return (edges == null) ? new ArrayList<ServiceEdge>() : edges;
    }

    @Override
    public synchronized boolean addService(T service) {
        checkNotNull(service);
        log.debug("adding service");
        if (graph.containsVertex(service)) {
            return false;
        }
        return graph.addVertex(service);
    }

    @Override
    public synchronized boolean removeService(T service) {
        checkNotNull(service);
        log.debug("removing service");
        return graph.removeVertex(service);
    }

    @Override
    public synchronized boolean contains(T service) {
        checkNotNull(service);
        return graph
                .getVertices()
                .contains(service);
    }

    @Override
    public synchronized Optional<ServiceEdge> getEdge(T dependant, T dependency) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        ServiceEdge result = graph.findEdge(dependant, dependency);
        return result == null ? Optional.empty() : Optional.of(result);
    }

    @Override
    public synchronized void createDependency(T dependant, T dependency, Dependency.Type type) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        checkNotNull(type);
        if (hasDependency(dependant, dependency)) {
            throw new DuplicateDependencyException("There can only be one dependency between the same two services");
        }
        if (!graph.containsVertex(dependant)) {
            graph.addVertex(dependant);
        }
        if (!graph.containsVertex(dependency)) {
            graph.addVertex(dependency);
        }
        ServiceEdge edge = new ServiceEdge(type);
        graph.addEdge(edge, dependant, dependency);
        if (detectCycle(dependant, dependency)) {
            throw new CycleDetectedException("Creating dependency from " + dependant + " to " + dependency + " has caused a loop");
        }
    }

    @Override
    public synchronized ImmutableList<T> getServices() {
        return ImmutableList.copyOf(graph.getVertices());
    }

    @Override
    public synchronized boolean hasDependency(T dependant, T dependency) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        return findDependencies(dependant, Selection.ALL).contains(dependency);
    }

    @Override
    public synchronized boolean hasDependant(T dependency, T dependant) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        return findDependants(dependency, Selection.ALL).contains(dependant);
    }

    /**
     * Checks the proposed connection between parent and child nodes, and returns true if a cycle would be created by
     * adding the child to the parent, or false if not
     *
     * @param parentNode dependant
     * @param childNode  dependency
     * @return true if a cycle detected
     */
    protected boolean detectCycle(T parentNode, T childNode) {
        if (parentNode.equals(childNode)) {
            return true;
        }
        Stack<T> stack = new Stack<>();
        stack.push(parentNode);
        while (!stack.isEmpty()) {
            T node = stack.pop();
            Collection<T> predecessors = graph.getPredecessors(node);
            if (predecessors != null) {
                for (T pred : predecessors) {
                    if (pred == childNode) {
                        return true;
                    }
                }
                stack.addAll(predecessors);
            }
        }
        return false;
    }

    @Override
    public synchronized int size() {
        return graph.getVertexCount();
    }

    @Override
    public synchronized boolean isOptionalDependency(T dependency, T dependant) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        List<T> optionalDependencies = findRelations(dependant, true, Selection.OPTIONAL);
        return optionalDependencies.contains(dependency);
    }
}
