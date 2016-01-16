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

package uk.q3c.krail.core.user.opt.cache;

import org.apache.shiro.authz.permission.WildcardPermission;
import uk.q3c.krail.core.shiro.SubjectIdentifier;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.user.opt.OptionKey;
import uk.q3c.krail.core.user.profile.UserHierarchy;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a Shiro permission for {@link Option}.  The permission is structured:
 * <p>
 * <b>option:[action]:[hierarchy]:[userId]:[hierarchy level index]:[context]:[option name]:[qualifier]:[qualifier]</b>
 * <p>
 * As many qualifiers as necessary can be appended.<ol>
 * <p>
 * <li>[action] is either 'edit' or 'view'</li>
 * <li>[hierarchy] is represented by {@link UserHierarchy#persistenceName()}</li>
 * <li>[userId] is the userId obtained from {@link SubjectIdentifier#userId()}</li>
 * <li>[hierarchy level index]: is the level index for the hierarchy, so level 0 is at user level.  See {@link UserHierarchy} for more information about
 * levels</li>
 * <li>[context]:[option name]:[qualifier] are taken from {@link OptionKey}</li>
 * <p>
 * </ol>
 * <p>Example:<br><br>
 * <em>option:edit:SimpleUserHierarchy:ds:0:LocaleContainer:Error:q1:q2</em>
 * <p>
 * <p>
 * Created by David Sowerby on 01/06/15.
 */
public class OptionPermission extends WildcardPermission {

    public enum Action {
        EDIT, VIEW
    }

    private final String permissionString;

    /**
     * Construct a permission object for the current subject
     *
     * @param hierarchy
     */
    public OptionPermission(@Nonnull Action action, @Nonnull UserHierarchy hierarchy, int index, @Nonnull OptionKey optionKey, @Nonnull String userId) {
        checkNotNull(action);
        checkNotNull(hierarchy);
        checkNotNull(optionKey);
        checkNotNull(userId);
        checkArgument(!"".equals(userId));
        checkArgument(index >= 0);
        StringBuilder buf = new StringBuilder("option:");
        buf.append(action.name()
                         .toLowerCase());
        buf.append(':');
        buf.append(hierarchy.persistenceName());
        buf.append(':');
        buf.append(userId);
        buf.append(':');
        buf.append(index);
        buf.append(':');
        buf.append(optionKey.compositeKey()
                            .replace('-', ':'));
        permissionString = buf.toString();
        setParts(permissionString);
    }

    public String getPermissionString() {
        return permissionString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OptionPermission that = (OptionPermission) o;

        return permissionString.equals(that.permissionString);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        return 31 * result + permissionString.hashCode();
    }
}
