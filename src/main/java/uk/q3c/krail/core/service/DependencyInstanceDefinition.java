package uk.q3c.krail.core.service;

import javax.annotation.concurrent.Immutable;

/**
 * Defines a dependency instance, that is  the type of relationship and the dependency service
 * <p>
 * Created by David Sowerby on 06 Dec 2015
 */
@Immutable
public class DependencyInstanceDefinition extends ServiceEdge {

    private final Service dependency;

    public DependencyInstanceDefinition(Service dependency, Dependency.Type type) {
        super(type);
        this.dependency = dependency;
    }

    public Service getDependency() {
        return dependency;
    }
}
