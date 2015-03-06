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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.navigate.sitemap.SitemapNode;
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.user.UserStatusChangeSource;

import java.util.Optional;

/**
 * Created by David Sowerby on 08/02/15.
 */
public class DefaultLoginNavigationRule implements LoginNavigationRule {
    private static Logger log = LoggerFactory.getLogger(DefaultLoginNavigationRule.class);
    private URIFragmentHandler uriFragmentHandler;
    private UserSitemap userSitemap;

    @Inject
    protected DefaultLoginNavigationRule(UserSitemap userSitemap, URIFragmentHandler uriFragmentHandler) {

        this.userSitemap = userSitemap;
        this.uriFragmentHandler = uriFragmentHandler;
    }

    /**
     * If there is a previous page (before the login page that is), and it is not the logout page, then
     * return the state for that previous page, otherwise return the state for the Standard private home page
     *
     * @param source
     *         the source of the change (usually a login form or component, but could also be a federated source, single
     *         sign-on etc)
     *
     * @return If there is a previous page (before the login page that is), and it is not the logout page, then
     * return the state for that previous page, otherwise return the state for the Standard private home page
     */
    @Override
    public Optional<NavigationState> changedNavigationState(Navigator navigator, UserStatusChangeSource source) {



        //if we are not on the login page we do not need to move
        if (!userSitemap.isLoginUri(navigator.getCurrentNavigationState())) {
            log.debug("Not on the login page so no navigation change required");
            return Optional.empty();
        }

        SitemapNode previousNode = navigator.getPreviousNode();

        //We are on the login page
        //there is no previous node, then they must have gone straight to the login page - send them to private home
        if (previousNode == null) {
            log.debug("There is no recorded previous node (probably gone straight to login) so default to private home");
            return Optional.of(uriFragmentHandler.navigationState(userSitemap.standardPageURI(StandardPageKey
                    .Private_Home)));
        }

        //The user was on the logout page before logging in - no point in going back there, send to private home
        UserSitemapNode logoutNode = userSitemap.standardPageNode(StandardPageKey.Log_Out);
        if (previousNode.equals(logoutNode)) {
            log.debug("logged in from logout page, so default to private home");
            return Optional.of(uriFragmentHandler.navigationState(userSitemap.standardPageURI(StandardPageKey
                    .Private_Home)));
        }

        // Got to the page they were on before login
        return Optional.of(navigator.getPreviousNavigationState());
    }
}
