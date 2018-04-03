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

package uk.q3c.krail.core.navigate;

import uk.q3c.krail.core.user.status.UserStatusChangeSource;

import java.io.Serializable;
import java.util.Optional;

/**
 * Created by David Sowerby on 08/02/15.
 */
public interface LogoutNavigationRule extends Serializable {
    /**
     * Optionally provides a new NavigationState to be navigated to in response to a user logout.
     *
     * @param navigator
     *         the navigator invoking the rule
     * @param source
     *         source the source of the change (usually a login form or component, but could also be a federated source,
     *         single sign-on etc)
     *
     * @return a NavigationState to navigate to, or Optional.empty() if no change needed.
     */
    Optional<NavigationState> changedNavigationState(Navigator navigator, UserStatusChangeSource source);
}
