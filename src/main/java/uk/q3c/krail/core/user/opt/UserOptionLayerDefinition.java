package uk.q3c.krail.core.user.opt;


import java.util.List;
import java.util.Optional;

/**
 * User options are structured in layers, so that if desired, a hierarchy can be created.  For example, imagine you
 * have an application which puts company news on a user's home page.  It is a large company, with a number of
 * divisions, and a number of departments within those divisions.  You want users to be able to select their source
 * of company news, but you also need sensible defaults for the initial rollout, and also for new users. You could
 * give them all the same defaults, but using an implementation of this class, you can make those defaults sensitive
 * to the user.
 * <p>
 * A user is passed to an implementation of this class, and returns an ordered list of layers relevant to that user -
 * in the example above, perhaps it would be "South-East,Finance"
 * <p>
 * For a full explanation of UserOptions and layers see https://sites.google.com/site/q3cjava/user-options
 * Created by David Sowerby on 04/12/14.
 */
public interface UserOptionLayerDefinition {

    /**
     * Returns a list of layers for {@code userId}, and an optional {@code hierarchy}.  If {@code hierarchy} is not
     * present, then the default hierarchy is used
     *
     * @param userId
     *         id for the user, used to lookup layer definitions
     * @param hierarchy
     *         an optional hierarchy, so that options could be from, for example, a geography hierarchy and a company
     *         structure hierarchy
     *
     * @return
     */
    List<String> getLayers(String userId, Optional<String> hierarchy);
}
