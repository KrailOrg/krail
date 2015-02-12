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

package uk.q3c.krail.core.navigate.sitemap;

import uk.q3c.krail.core.navigate.NavigationState;

import javax.annotation.Nonnull;

/**
 * A user specific view of the {@link MasterSitemap}
 * <p>
 * The standard page nodes are sometimes not in the user sitemap (for example, the login node is not there after
 * login). Use the isxxxUri methods to test a uri for a match to a standard page
 */
public interface UserSitemap extends Sitemap<UserSitemapNode> {

    public abstract UserSitemapNode userNodeFor(SitemapNode masterNode);

    public abstract void buildUriMap();

    void addListener(UserSitemapChangeListener listener);

    void removeListener(UserSitemapChangeListener listener);

    boolean isLoginUri(NavigationState navigationState);

    boolean isLogoutUri(@Nonnull NavigationState navigationState);

    boolean isPrivateHomeUri(@Nonnull NavigationState navigationState);

    boolean isPublicHomeUri(@Nonnull NavigationState navigationState);
}
