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

import com.google.inject.Inject;
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.user.status.UserStatusChangeSource;

import java.util.Optional;

/**
 * Created by David Sowerby on 08/02/15.
 */
public class DefaultLogoutNavigationRule implements LogoutNavigationRule {

    private URIFragmentHandler uriFragmentHandler;
    private UserSitemap userSitemap;

    @Inject
    protected DefaultLogoutNavigationRule(UserSitemap userSitemap, URIFragmentHandler uriFragmentHandler) {
        this.userSitemap = userSitemap;
        this.uriFragmentHandler = uriFragmentHandler;
    }

    @Override
    public Optional<NavigationState> changedNavigationState(Navigator navigator, UserStatusChangeSource source) {

        return Optional.of(uriFragmentHandler.navigationState(userSitemap.standardPageURI(StandardPageKey.Log_Out)));
    }
}
