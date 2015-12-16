package uk.q3c.krail.core.services;

import com.google.common.collect.ImmutableList;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
import org.slf4j.Logger;
import uk.q3c.util.CycleDetectedException;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Default implementation of {@link ServicesGraph}
 * <p>
 * Created by David Sowerby on 03 Dec 2015
 */
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
     * @return list of vertices at the opposite end of the edges from {@code referenceVertex}
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
    public List<T> findDependencies(@Nonnull T dependant, @Nonnull Selection selection) {
        checkNotNull(dependant);
        checkNotNull(selection);
        return findRelations(
                dependant, true, selection);
    }

    @Override
    public List<T> findDependants(@Nonnull T dependency, @Nonnull Selection selection) {
        checkNotNull(dependency);
        checkNotNull(selection);
        return findRelations(dependency, false, selection);
    }

    private Collection<ServiceEdge> getDependenciesEdges(@Nonnull T dependant) {
        Collection<ServiceEdge> edges = graph.getOutEdges(dependant);
        return (edges == null) ? new ArrayList<ServiceEdge>() : edges;
    }


    private Collection<ServiceEdge> getDependantsEdges(@Nonnull T dependency) {
        Collection<ServiceEdge> edges = graph.getInEdges(dependency);
        return (edges == null) ? new ArrayList<ServiceEdge>() : edges;
    }

    @Override
    public boolean addService(@Nonnull T service) {
        checkNotNull(service);
        log.debug("adding service");
        if (graph.containsVertex(service)) {
            return false;
        }
        return graph.addVertex(service);
    }

    @Override
    public boolean removeService(@Nonnull T service) {
        checkNotNull(service);
        log.debug("removing service");
        return graph.removeVertex(service);
    }

    @Override
    public boolean contains(T service) {
        return graph
                .getVertices()
                .contains(service);
    }

    @Override
    public Optional<ServiceEdge> getEdge(T dependant, T dependency) {
        ServiceEdge result = graph.findEdge(dependant, dependency);
        return result == null ? Optional.empty() : Optional.of(result);
    }

    @Override
    public void createDependency(@Nonnull T dependant, @Nonnull T dependency, @Nonnull Dependency.Type type) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        checkNotNull(type);
        if (hasDependency(dependant, dependency)) {
            throw new DuplicateDependencyException("There can only be one dependency between the two services");
        }
        graph.addVertex(dependant);
        graph.addVertex(dependency);
        ServiceEdge edge = new ServiceEdge(type);
        graph.addEdge(edge, dependant, dependency);
        if (detectCycle(dependant, dependency)) {
            throw new CycleDetectedException("Creating dependency from " + dependant + " to " + dependency + " has caused a loop");
        }
    }

    @Override
    public ImmutableList<T> getServices() {
        return ImmutableList.copyOf(graph.getVertices());
    }

    @Override
    public boolean hasDependency(@Nonnull T dependant, @Nonnull T dependency) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        return findDependencies(dependant, Selection.ALL).contains(dependency);
    }

    @Override
    public boolean hasDependant(@Nonnull T dependency, @Nonnull T dependant) {
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
    public int size() {
        return graph.getVertexCount();
    }

    @Override
    public boolean isOptionalDependency(@Nonnull T dependency, @Nonnull T dependant) {
        checkNotNull(dependant);
        checkNotNull(dependency);
        List<T> optionalDependencies = findRelations(dependant, true, Selection.OPTIONAL);
        return optionalDependencies.contains(dependency);
    }
}
