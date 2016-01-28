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

package uk.q3c.krail.core.user.profile;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.i18n.Translate;
import uk.q3c.krail.core.option.Option;
import uk.q3c.krail.core.shiro.SubjectIdentifier;
import uk.q3c.krail.core.shiro.SubjectProvider;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The most basic of {@link UserHierarchy} implementations, with just two ranks, the 'system' and the 'user'.  The
 * user is represented by {@link SubjectIdentifier#userId()}, and is a higher rank than 'system'
 * <p>
 * Created by David Sowerby on 18/02/15.
 */
@ThreadSafe
public class SimpleUserHierarchy implements UserHierarchy {


    public static final String SYSTEM = "system";
    private final SubjectProvider subjectProvider;
    private final SubjectIdentifier subjectIdentifier;
    private Translate translate;

    @Inject
    public SimpleUserHierarchy(SubjectProvider subjectProvider, SubjectIdentifier subjectIdentifier, Translate
            translate) {
        this.subjectProvider = subjectProvider;
        this.subjectIdentifier = subjectIdentifier;
        this.translate = translate;
    }

    /**
     * The descriptive name for this hierarchy, usually for use in the user interface. Should be Locale sensitive
     *
     * @return The descriptive name for this hierarchy
     */
    @Override
    @Nonnull
    public synchronized String displayName() {
        return translate.from(LabelKey.Simple_User_Hierarchy);
    }

    /**
     * Returns the identifier of the {@code hierarchyRank}, for the current user.  From the example given in the
     * {@link Option} javadoc, above that could be 'Development' if an implementation represented an Organisation,
     * and the current user is Emily
     *
     * @param hierarchyRank
     *         the rank (index) which is required.
     *
     * @return the rank name, for the current user, at the rank specified by {@code hierarchyRank}.
     *
     * @throws IllegalArgumentException
     *         if {@code hierarchyRank} is out of bounds
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

    /**
     * Returns the values for the hierarchy ranks, for the current user, for this {@link UserHierarchy} implementation.
     *
     * @return if the user is authenticated, the userId and 'system'.  If the user is not authenticated, 'system' only
     */
    @Override
    @Nonnull
    public synchronized ImmutableList<String> ranksForCurrentUser() {

        if (subjectProvider.get()
                           .isAuthenticated()) {
            return ImmutableList.of(subjectIdentifier.userId(), SYSTEM);

        } else {
            //not authenticated, can only use system level
            return ImmutableList.of(SYSTEM);
        }
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
