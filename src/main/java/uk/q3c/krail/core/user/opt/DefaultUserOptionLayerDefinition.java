package uk.q3c.krail.core.user.opt;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.List;

/**
 * This default implementation has no knowledge of the application being developed.  If hierarchies are
 * required, this implementation will need to be replaced by your own implementation.  A sub-class of {@link
 * UserOptionModule} should then contain a binding for your new implementation
 * <p>
 * Created by David Sowerby on 05/12/14.
 */
public class DefaultUserOptionLayerDefinition implements UserOptionLayerDefinition {
    /**
     * This implementation simply returns an empty list as there is no way of knowing what an application would need to
     * implement
     *
     * @param userId
     *         id for the user, used to lookup layer definitions
     * @param hierarchy
     *         an optional hierarchy, so that options could be from, for example, a geography hierarchy and a company
     *         structure hierarchy
     *
     * @return
     */
    @Override
    public List<String> getLayers(String userId, Optional<String> hierarchy) {
        return new ArrayList<>();
    }
}
