/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.navigate;

import com.vaadin.server.Page.UriFragmentChangedListener;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.ui.ScopedUI;
import uk.q3c.krail.core.user.status.UserStatusListener;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.core.view.KrailViewChangeListener;

import java.util.List;

/**
 * Uses the {@link UserSitemap} to control navigation from one 'page' to another, using a uri String, or a
 * {@link StandardPageKey} or a {@link UserSitemapNode} to identify a page.<br>
 * <br>
 * Even though {@link UserSitemapNode} should have already been verified for authorisation, all page navigation is
 * checked for authorisation. <br>
 * <br>
 * Looks up the view for the supplied URI, or {@link UserSitemapNode} and calls on {@link ScopedUI} to present that
 * view. Listeners are notified before and after a change of view occurs. The {@link #loginSuccessful()} method is
 * called after a successful user login - this allows the navigator to change views appropriately (according to the
 * implementation). Typically this would be to either return to the view where the user was before they went to the
 * login page, or perhaps to a specified landing page (Page here refers really to a KrailView - a "virtual page"). <br>
 * <br>
 * The navigator must also respond to a change in user status (logged in or out) - logging out just navigates to the
 * logout page, while logging in applies some logic, see {@link #userStatusChanged()}
 *
 * @author David Sowerby 20 Jan 2013
 */
public interface Navigator extends UriFragmentChangedListener, UserStatusListener {

    void navigateTo(String navigationState);

    /**
     * A convenience method to look up the URI fragment for the {@link StandardPageKey} and navigate to it
     *
     * @param pageKey
     */
    void navigateTo(StandardPageKey pageKey);

    NavigationState getCurrentNavigationState();

    List<String> getNavigationParams();

    void addViewChangeListener(KrailViewChangeListener listener);

    void removeViewChangeListener(KrailViewChangeListener listener);

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

    UserSitemapNode getCurrentNode();

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

}
