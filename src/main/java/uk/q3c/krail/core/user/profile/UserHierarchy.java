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

import uk.q3c.krail.core.user.opt.OptionStore;

import javax.annotation.Nonnull;
import java.util.List;

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
 * to set good defaults.  You could use a {@link UserHierarchy} to do that by passing it to the {@link OptionStore}
 * when retrieving option values.  By default, Emily could then see information related to her development team, and
 * information about the facilities in Sheffield, while Franck would see information related to Finance, and the
 * facilities of Frankfurt.
 * <p>
 * When they have chosen their own options, however, that selection goes to the "top" of the hierarchy for them,
 * only, and will override any values set at lower levels in the hierarchy.
 * <p>
 * Alternatively, you may for some options only allow them to be changed at the department or even system level - you
 * can do that by not allowing values to be set at any higher level in the hierarchy.
 * <p>
 * {@link UserHierarchy} implementations will often take their information from other systems - an HR or Identity
 * Management system may contain the Organisation hierarchy
 * <p>
 * Created by David Sowerby on 18/02/15.
 */
public interface UserHierarchy {

    /**
     * Returns a list of layers (just String identifiers) for this hierarchy, for the current user.
     * <p>
     * Implementations must ensure that a valid result is returned even if there is no currently authenticated user -
     * the user is 'anonymous'
     * <p>
     * The list is returned indexed in the correct ordering or layers, with the user layer at index 0, and the system
     * layer at the highest list index.<p>
     *
     * @return Returns a list of layers (just String identifiers) for this hierarchy, for the current user.
     */
    @Nonnull
    List<String> layersForCurrentUser();

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
     * @return
     */
    String displayName();

    /**
     * Returns the layer, for the current user, at the level specified by {@code hierarchyLevel}.
     *
     * @param hierarchyLevel
     *         the level (index) which is required.
     *
     * @return he layer, for the current user, at the level specified by {@code hierarchyLevel}.
     *
     * @throws UserHierarchyException
     *         if {@code hierarchy} is out of bounds
     */
    String layerForCurrentUser(int hierarchyLevel);
}
