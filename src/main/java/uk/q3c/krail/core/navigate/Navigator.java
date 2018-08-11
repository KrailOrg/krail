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

import com.vaadin.server.Page;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.ui.ScopedUI;
import uk.q3c.krail.core.view.KrailView;

import java.util.List;

/**
 * Uses the {@link UserSitemap} to control navigation from one 'page' to another, using a uri String, or a
 * {@link StandardPageKey} or a {@link UserSitemapNode} to identify a page.<br>
 * <br>
 * Even though {@link UserSitemapNode} should have already been verified for authorisation, all page navigation is
 * checked for authorisation. <br>
 * <br>
 * Looks up the view for the supplied URI, or {@link UserSitemapNode} and calls on {@link ScopedUI} to present that
 * view. Listeners are notified before and after a change of view occurs.  <br>
 * <br>
 * The navigator must also respond to a change in user status (logged in or out) - the response is determined by an
 * implementation of {@link LoginNavigationRule} and {@link LogoutNavigationRule},
 *
 * @author David Sowerby 20 Jan 2013
 */
public interface Navigator extends Page.PopStateListener {

    void navigateTo(String navigationState);

    /**
     * A convenience method to look up the URI fragment for the {@link StandardPageKey} and navigate to it
     *
     * @param pageKey
     */
    void navigateTo(StandardPageKey pageKey);

    NavigationState getCurrentNavigationState();

    /**
     * Returns the NavigationState representing the page that the user was previously on.  Null if the user is on their
     * first page of the session.
     *
     * @return
     */
    NavigationState getPreviousNavigationState();

    List<String> getNavigationParams();

    /**
     * Removes any historical navigation state
     */
    void clearHistory();

    KrailView getCurrentView();

    /**
     * Navigate to the error view. It is assumed that the view has already been set up with error information, usually
     * via the KrailErrorHandler
     *
     * @param throwable
     */
    void error(Throwable throwable);

    /**
     * Navigates to the location represented by {@code navigationState}, which may include parameters
     *
     * @param navigationState
     */
    void navigateTo(NavigationState navigationState);

    /**
     * Returns the current node. Can be null if the call is  made before navigation to the current node is complete
     *
     * @return the current node. Can be null if the call is  made before navigation to the current node is complete
     */
    UserSitemapNode getCurrentNode();

    /**
     * Returns the UserSitemapNode representing the page that the user was previously on.  Null if the user is on
     * their first page of the session.  If you need the parameters associated with the page, use {@link
     * #getPreviousNavigationState()} instead
     *
     * @return
     */
    UserSitemapNode getPreviousNode();

    /**
     * Navigates to the location represented by {@code node}. Because this is based on a {@link MasterSitemapNode}, no
     * parameters are associated with this, and only navigates to the page associated with the node
     *
     * @param node
     */
    void navigateTo(UserSitemapNode node);

    /**
     * Initialises the navigator by preparing the {@link UserSitemap}
     */
    void init();

    /**
     * Lists the 'path' to the current node
     *
     * @return the path to the current node, or an empty list if the current node is null.  This can happen if the call is
     * made before navigation to the current node is complete
     */
    List<UserSitemapNode> nodeChainForCurrentNode();

    /**
     * Lists the sub-nodes (representing sub-pages) of the current node
     *
     * @return a list of sub-nodes (representing sub-pages) of the current node, or an empty list if the current node is null.
     * This can happen if the call is  made before navigation to the current node is complete
     */
    List<UserSitemapNode> subNodes();

}
