/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.shiro;

import org.apache.shiro.authz.permission.WildcardPermission;
import uk.q3c.krail.core.navigate.NavigationState;

public class PagePermission extends WildcardPermission {

    public PagePermission(NavigationState navigationState) {
        super();
        construct(navigationState, false, false);
    }

    /**
     * Creates a Permission object from the uri fragment held in {@code navigationState}. The '/' characters are
     * changed
     * to ':' to facilitate use of Shiro WildcardPermission. If {@code appendWildCard} is true, a final ':*' is added.
     * The fill translation is, for example, for a URI of:<br>
     * <br>
     * <i>private/deptx/teamy/current projects</i> becomes a Shiro permission of <br>
     * <br>
     * <i>uri:view:private:deptx:teamy:current projects</i> with no wildcard, or <br>
     * <br>
     * <i>uri:view:private:deptx:teamy:current projects:*</i> with a wildcard
     *
     * @param uri
     * @param appendWildcard
     *
     * @return
     */
    protected void construct(NavigationState navigationState, boolean appendWildcard, boolean edit) {
        construct(navigationState.getVirtualPage(), appendWildcard, edit);
    }

    private void construct(String virtualPage, boolean appendWildcard, boolean edit) {
        String action = edit ? "edit:" : "view:";
        String prefix = "page:";
        String pagePerm = virtualPage.replace("/", ":");
        String wildcard = appendWildcard ? ":*" : "";

        String permissionString = prefix + action + pagePerm + wildcard;
        setParts(permissionString);

    }

    /**
     * Creates a Permission object from the uri fragment held in {@code navigationState}. The '/' characters are
     * changed
     * to ':' to facilitate use of Shiro WildcardPermission
     *
     * @param uri
     *
     * @return
     */
    public PagePermission(NavigationState navigationState, boolean appendWildcard) {
        super();
        construct(navigationState, appendWildcard, false);
    }

    public PagePermission(String virtualPage, boolean appendWildcard) {
        super();
        construct(virtualPage, appendWildcard, false);
    }

    public PagePermission(String virtualPage, boolean appendWildcard, boolean edit) {
        super();
        construct(virtualPage, appendWildcard, edit);
    }

    public PagePermission(NavigationState navigationState, boolean appendWildcard, boolean edit) {
        super();
        construct(navigationState, appendWildcard, edit);
    }

    public PagePermission(String virtualPage) {
        super();
        construct(virtualPage, false, false);
    }

}
