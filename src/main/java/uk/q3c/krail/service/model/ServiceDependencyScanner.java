package uk.q3c.krail.service.model;

import uk.q3c.krail.service.Dependency;
import uk.q3c.krail.service.Service;
import uk.q3c.krail.service.ServiceGraph;

/**
 * Scans a {@link Service} for {@link Dependency} annotations, and adds them to the {@link ServiceGraph}
 * <p>
 * <p>
 * Created by David Sowerby on 11/11/15.
 */
public interface ServiceDependencyScanner {

    void scan(Service service);

}
