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

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import uk.q3c.krail.core.shiro.SubjectIdentifier;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.Translate;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The most basic of {@link UserHierarchy} implementations, with just two layers, the 'system' layer and the 'user'
 * layer.  The latter is represented by {@link SubjectIdentifier#userId()}
 * <p>
 * Created by David Sowerby on 18/02/15.
 */
public class SimpleUserHierarchy implements UserHierarchy {


    public static final String SYSTEM = "system";
    private final SubjectProvider subjectProvider;
    private final SubjectIdentifier subjectIdentifier;
    private Translate translate;

    @Inject
    protected SimpleUserHierarchy(SubjectProvider subjectProvider, SubjectIdentifier subjectIdentifier, Translate
            translate) {
        this.subjectProvider = subjectProvider;
        this.subjectIdentifier = subjectIdentifier;
        this.translate = translate;
    }

    /**
     * Returns the layers for the current user, for this option hierarchy.
     *
     * @return if the user is authenticated, the userId and 'system'.  If the user is not authenticated, 'system' only
     */
    @Override
    @Nonnull
    public List<String> layersForCurrentUser() {

        if (subjectProvider.get()
                           .isAuthenticated()) {
            return Lists.newArrayList(subjectIdentifier.userId(), SYSTEM);

        } else {
            //not authenticated, can only use system level
            return Lists.newArrayList(SYSTEM);
        }
    }

    /**
     * The descriptive name for this hierarchy, usually for use in the user interface. Should be Locale sensitive
     *
     * @return
     */
    @Override
    public String displayName() {
        return translate.from(LabelKey.Simple_User_Hierarchy);
    }

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
    @Override
    public String layerForCurrentUser(int hierarchyLevel) {
        switch (hierarchyLevel) {
            case 0:
                return subjectIdentifier.userId();
            case 1:
                return SYSTEM;
            default:
                throw new UserHierarchyException("Hierarchy level of " + hierarchyLevel + " is out of bounds ");
        }
    }
}
