/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.user.profile;

import com.google.common.collect.ImmutableList;
import uk.q3c.krail.core.user.opt.Option;

import javax.annotation.Nonnull;

/**
 * There are many reasons for having hierarchies which depend on some aspect of a user's profile.  For example, on
 * their home page, you may wish to present different company information dependent on their location, the job they
 * do in the company, or both.
 * <p>
 * Imagine then that you have two implementations of this interface, an OrganisationHierarchy and a LocationHierarchy
 * . Also imagine you have a user, Emily (userId 'equick'), a developer in Sheffield, England and Franck (userId
 * 'fbaton') in the Finance team in Frankfurt, Germany.
 * <p>
 * Ultimately you probably want to give them the choice of what they see on their home page, but you will also want
 * to set good defaults.  You could use a {@link UserHierarchy} to do that in conjunction with {@link Option}
 * <p>
 * Initially, if option values have been set at department level, Emily would see information related to her
 * development team, and information about the facilities in Sheffield, while Franck would see information related to
 * Finance, and the facilities of Frankfurt.
 * <p>
 * When they have chosen their own options, however, that you would probably want those values to override those set at
 * department level.
 * <p>
 * This is done by ranking the hierarchy so that in this case, the option values assigned at the user level will
 * override any assigned at lower ranks - department etc.
 * <p>
 * How you the ranks are ordered is entirely up to the implementation, but should always be returned with the highest
 * rank at index 0.
 * <p>
 * When used with {@link Option}, there may be some option values which should be set only at the department level, for
 * example.  That is achieved simply by not allowing anyone access to change their own option values, and for example,
 * only allowing those with a department admin role to change the department option values.  That of course would be
 * easily achieved with the Shiro integration within Krail
 * <p>
 * {@link UserHierarchy} implementations will often take their information from other systems - for example, an HR or
 * Identity Management system may contain the Organisation hierarchy (sometimes even different ones for the same
 * organisation!)
 * <p>
 *     Implementations must be thread safe
 *
 * Created by David Sowerby on 18/02/15.
 */
public interface UserHierarchy {

    /**
     * Returns a list of values for the hierarchy ranks, for the current user, for this {@link UserHierarchy}
     * implementation.
     * <p>
     * Implementations must ensure that a valid result is returned even if there is no currently authenticated user -
     * that is, when the user is 'anonymous'
     * <p>
     * The returned list is ordered by rank,  with the highest rank at index 0
     *
     * @return a list of String identifiers for this hierarchy, for the current user, ordered by rank, with the highest
     * rank at index 0.
     */
    @Nonnull
    ImmutableList<String> ranksForCurrentUser();

    /**
     * The name to be used when for this hierarchy when stored in persistence (which should not therefore change, or
     * values will be lost)
     *
     * @return The name to be used when for this hierarchy when stored in persistence (which should not therefore
     * change, or values will be lost)
     */
    default String persistenceName() {
        return this.getClass()
                   .getSimpleName();
    }

    /**
     * The descriptive name for this hierarchy, usually for use in the user interface. Should be Locale sensitive
     *
     * @return The descriptive name for this hierarchy, usually for use in the user interface. Should be Locale
     * sensitive
     */
    String displayName();

    /**
     * Returns the identifier of the {@code hierarchyRank}, for the current user.  From the example above that could
     * be 'Development' if an implementation represented an Organisation, and the current user is Emily
     *
     * @param hierarchyRank
     *         the rank (index) which is required.
     *
     * @return the layer, for the current user, at the rank specified by {@code hierarchyRank}.
     *
     * @throws UserHierarchyException
     *         if {@code hierarchy} is out of bounds
     */
    String rankName(int hierarchyRank);

    String highestRankName();

    String lowestRankName();
}
