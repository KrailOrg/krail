package uk.q3c.krail.core.service;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

/**
 * A wrapper on a Jung graph used specifically for modelling relationships between either {@link Service} classes (represented by a {@link ServiceKey}) or
 * Service instances in
 * {@link ServicesModel}
 * <p>
 * <p>
 * <T>  is expected to be either {@link ServiceKey}  or a {@link Service} instance
 * <p>
 * <p>
 * Created by David Sowerby on 03/12/15.
 */
public interface ServicesGraph<T> {

    enum Selection {ONLY_REQUIRED_AT_START, REQUIRED_AT_START, ALWAYS_REQUIRED, OPTIONAL, ALL}

    /**
     * Returns a list of dependencies for {@code dependant} which are of the {@link Dependency.Type} defined by {@code selection}
     *
     * @param dependant the dependant for which dependencies are requested
     * @param selection which {@link Dependency.Type}s to select
     * @return List of vertices on which {@code dependant} depends and meet the selection criteria given by meeting the selection
     */
    List<T> findDependencies(T dependant, Selection selection);

    List<T> findDependants(T dependency, Selection selection);

    /**
     * Adds a service to this graph.  Returns true if {@code service} is added, false if not added (because {@code service} is already in the graph)
     *
     * @param service the service to be added
     */
    boolean addService(T service);


    /**
     * Removes a service from this graph
     *
     * @param service the service to be removed
     */
    boolean removeService(T service);

    boolean contains(T service);

    /**
     * Returns the edge (and therefore the dependency type) between two service.  If you get the dependant
     * and dependency the wrong way round the edge will not be found
     *
     * @return the edge if found, Optional.empty() if not
     */
    Optional<ServiceEdge> getEdge(T dependant, T dependency);

    /**
     * Creates a dependency from {@code dependant} to {@code dependency} of type {@code type}
     *
     * @param dependant  the Service which depends on {@code dependency}. Will be added to the graph if not already
     *                   added.
     * @param dependency the Service on which {@code dependant} depends.  Will be added to the graph if not already
     *                   added.
     * @param type       the type of {@link Dependency)
     * @throws CycleDetectedException       if a loop is created by forming this dependency
     * @throws DuplicateDependencyException if a dependency is created twice between the same Services
     */
    void createDependency(T dependant, T dependency, Dependency.Type type);

    /**
     * Returns a list of the service contained ion this graph
     *
     * @return a list of the service contained ion this graph
     */
    ImmutableList<T> getServices();

    /**
     * Returns true if {@code dependant} has {@code dependency}
     *
     * @return true if {@code dependant} has {@code dependency}
     */
    boolean hasDependency(T dependant, T dependency);

    /**
     * Returns true if {@code dependency} has {@code dependant}
     *
     * @return true if {@code dependency} has {@code dependant}
     */
    boolean hasDependant(T dependency, T dependant);

    /**
     * Returns the number of vertices in the graph
     * @return the number of vertices in the graph
     */
    int size();


    /**
     * Returns true only if {@code dependency} is an optional dependency of {@code dependant}
     *
     * @param dependency the dependency to check
     * @param dependant  the dependant which may have the dependency
     * @return true if {@code dependency} is an optional dependency of {@code dependant}, false otherwise
     */
    boolean isOptionalDependency(T dependency, T dependant);
}
