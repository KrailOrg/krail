package uk.q3c.krail.core.services;

/**
 * Scans a {@link Service} for {@link Dependency} annotations, and adds them to the {@link ServicesGraph}
 * <p>
 * <p>
 * Created by David Sowerby on 11/11/15.
 */
public interface ServiceDependencyScanner {

    void scan(Service service);

}
