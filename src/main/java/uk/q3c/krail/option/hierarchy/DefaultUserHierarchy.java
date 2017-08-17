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

package uk.q3c.krail.option.hierarchy;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.UserHierarchy;

import javax.annotation.concurrent.ThreadSafe;

import static com.google.common.base.Preconditions.*;

/**
 * To do anything useful with {@link Option}, this class will need to be replaced by something meaningful.  Its only purpose is to enable code to run
 * <p>
 * Created by David Sowerby on 18/02/15.
 */
@ThreadSafe
public class DefaultUserHierarchy implements UserHierarchy {


    public static final String SYSTEM = "system";

    @Inject
    public DefaultUserHierarchy() {
    }

    /**
     * The descriptive name for this hierarchy, usually for use in the user interface. Should be Locale sensitive
     *
     * @return The descriptive name for this hierarchy
     */
    @Override

    public synchronized String displayName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Returns the identifier of the {@code hierarchyRank}, for the current user.  From the example given in the
     * {@link Option} javadoc, above that could be 'Development' if an implementation represented an Organisation,
     * and the current user is Emily
     *
     * @param hierarchyRank the rank (index) which is required.
     * @return the rank name, for the current user, at the rank specified by {@code hierarchyRank}.
     * @throws IllegalArgumentException if {@code hierarchyRank} is out of bounds
     */
    @Override
    public synchronized String rankName(int hierarchyRank) {
        checkArgument(hierarchyRank >= 0, "hierarchyRank must be 0 or greater");
        ImmutableList<String> ranks = ranksForCurrentUser();
        try {
            return ranks.get(hierarchyRank);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Hierarchy level of " + hierarchyRank + " is too high", e);
        }
    }

    @Override

    public synchronized ImmutableList<String> ranksForCurrentUser() {

        return ImmutableList.of("me", SYSTEM);

    }

    @Override
    public synchronized String highestRankName() {
        ImmutableList<String> ranks = ranksForCurrentUser();
        return ranks.get(0);
    }

    @Override
    public synchronized String lowestRankName() {
        ImmutableList<String> ranks = ranksForCurrentUser();
        return ranks.get(ranks.size() - 1);
    }

    @Override
    public synchronized int lowestRank() {
        ImmutableList<String> ranks = ranksForCurrentUser();
        return ranks.size() - 1;
    }
}
