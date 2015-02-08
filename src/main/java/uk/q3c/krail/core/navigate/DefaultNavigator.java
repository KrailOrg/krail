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
package uk.q3c.krail.core.navigate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.navigate.sitemap.*;
import uk.q3c.krail.core.shiro.PageAccessController;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.shiro.UnauthorizedExceptionHandler;
import uk.q3c.krail.core.ui.ScopedUI;
import uk.q3c.krail.core.ui.ScopedUIProvider;
import uk.q3c.krail.core.user.UserStatusChangeSource;
import uk.q3c.krail.core.user.status.UserStatus;
import uk.q3c.krail.core.view.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The navigator is at the heart of navigation process, and provides navigation form a number of data types (for
 * example, String, {@link NavigationState} and {@link UserSitemapNode}. Because the USerSitemap only holds pages
 * authorised for the current Subject, there is not need to check for authorisation before navigating (there is still
 * some old code in here which does, but that will be removed)
 * <p>
 * There is no need to register as a listener with {@link UserStatus}, the navigator is always called after all other
 * listeners - this is so that navigation components are set up before the navigator moves to a page (which might not
 * be
 * displayed in a navigation component if it is not up to date)
 *
 * @author David Sowerby
 * @date 18 Apr 2014
 */
public class DefaultNavigator implements Navigator {
    private static Logger log = LoggerFactory.getLogger(DefaultNavigator.class);

    private final List<KrailViewChangeListener> viewChangeListeners = new LinkedList<>();
    private final URIFragmentHandler uriHandler;
    private final Provider<Subject> subjectProvider;
    private final PageAccessController pageAccessController;
    private final ScopedUIProvider uiProvider;
    private final DefaultViewFactory viewFactory;
    private final SitemapService sitemapService;
    private final UserSitemapBuilder userSitemapBuilder;
    private final LoginNavigationRule loginNavigationRule;
    private final LogoutNavigationRule logoutNavigationRule;
    private NavigationState currentNavigationState;
    private KrailView currentView = null;
    private NavigationState previousNavigationState;
    private UserSitemap userSitemap;

    @Inject
    public DefaultNavigator(URIFragmentHandler uriHandler, SitemapService sitemapService, SubjectProvider
            subjectProvider, PageAccessController pageAccessController, ScopedUIProvider uiProvider,
                            DefaultViewFactory viewFactory, UserSitemapBuilder userSitemapBuilder,
                            LoginNavigationRule loginNavigationRule, LogoutNavigationRule logoutNavigationRule) {
        super();
        this.uriHandler = uriHandler;
        this.uiProvider = uiProvider;
        this.sitemapService = sitemapService;
        this.subjectProvider = subjectProvider;
        this.pageAccessController = pageAccessController;
        this.viewFactory = viewFactory;
        this.userSitemapBuilder = userSitemapBuilder;

        this.loginNavigationRule = loginNavigationRule;
        this.logoutNavigationRule = logoutNavigationRule;
    }

    @Override
    public void init() {
        try {
            sitemapService.start();
            userSitemapBuilder.build();
            userSitemap = userSitemapBuilder.getUserSitemap();

        } catch (Exception e) {
            String msg = "Sitemap service failed to start, application will have no pages";
            log.error(msg);
            throw new IllegalStateException(msg, e);
        }
    }

    /**
     * Listen to changes of the active view.
     * <p>
     * Registered listeners are invoked in registration order before (
     * {@link ViewChangeListener#beforeViewChange(ViewChangeEvent) beforeViewChange()}) and after (
     * {@link ViewChangeListener#afterViewChange(ViewChangeEvent) afterViewChange()}) a view change occurs.
     *
     * @param listener
     *         Listener to invoke during a view change.
     */
    @Override
    public void addViewChangeListener(KrailViewChangeListener listener) {
        viewChangeListeners.add(listener);
    }

    /**
     * Removes a view change listener.
     *
     * @param listener
     *         Listener to remove.
     */
    @Override
    public void removeViewChangeListener(KrailViewChangeListener listener) {
        viewChangeListeners.remove(listener);
    }

    @Override
    public void uriFragmentChanged(UriFragmentChangedEvent event) {
        navigateTo(event.getUriFragment());
    }

    /**
     * Takes a URI fragment, checks for any redirects defined by the {@link Sitemap}, then calls
     * {@link #navigateTo(NavigationState)} to change the view
     *
     * @see Navigator#navigateTo(java.lang.String)
     */
    @Override
    public void navigateTo(String fragment) {
        log.debug("Navigating to fragment: {}", fragment);

        // set up the navigation state
        NavigationState navigationState = uriHandler.navigationState(fragment);
        navigateTo(navigationState);
    }

    /**
     * Navigates to a the location represented by {@code navigationState}. If the {@link Sitemap} holds a redirect for
     * the URI represented by {@code navigationState}, navigation will be directed to the redirect target. An
     * unrecognised URI will throw a {@link SitemapException}. If the view for the URI is found, the user's
     * authorisation is checked. If the user is not authorised, a {@link AuthorizationException} is thrown. This would
     * be caught by the the implementation bound to {@link UnauthorizedExceptionHandler}. If the user is authorised,
     * the
     * View is instantiated, and made the current view in the UI via {@link ScopedUI#changeView(KrailView)}.<br>
     * <br>
     * Events are fired before and after the view change, to the {@link #viewChangeListeners}. Listeners have the
     * option
     * to block the view change by returning false (see {@link #fireBeforeViewChange(KrailViewChangeEvent)}
     * <p>
     *
     * @param navigationState
     *         The navigationState to navigate to. May not be null.
     */
    @Override
    public void navigateTo(NavigationState navigationState) {
        checkNotNull(navigationState);
        redirectIfNeeded(navigationState);

        // stop unnecessary changes, but also to prevent navigation aware
        // components from causing a loop by responding to a change of URI (they should suppress events when they do,
        // but may not)
        if (navigationState == currentNavigationState) {
            log.debug("fragment unchanged, no navigation required");
            return;
        }

        // https://sites.google.com/site/q3cjava/sitemap#emptyURI
        if (navigationState.getVirtualPage()
                           .isEmpty()) {
            navigationState.setVirtualPage(userSitemap.standardPageURI(StandardPageKey.Public_Home));
            uriHandler.updateFragment(navigationState);
        }

        log.debug("obtaining view for '{}'", navigationState.getVirtualPage());

        UserSitemapNode node = userSitemap.nodeFor(navigationState);
        if (node == null) {
            InvalidURIException exception = new InvalidURIException("URI not found");
            exception.setTargetURI(navigationState.getVirtualPage());
            throw exception;
        }

        Subject subject = subjectProvider.get();
        boolean authorised = pageAccessController.isAuthorised(subject, node);
        if (authorised) {

            // need this in case the change is blocked by a listener
            NavigationState previousPreviousNavigationState = previousNavigationState;
            previousNavigationState = currentNavigationState;
            currentNavigationState = navigationState;

            KrailViewChangeEvent event = new KrailViewChangeEvent(previousNavigationState, navigationState);
            // if change is blocked revert to previous state
            if (!fireBeforeViewChange(event)) {
                currentNavigationState = previousNavigationState;
                previousNavigationState = previousPreviousNavigationState;
                return;
            }

            // make sure the page uri is updated if necessary, but do not fire any change events
            // as we have already responded to the change
            ScopedUI ui = uiProvider.get();
            Page page = ui.getPage();
            if (!navigationState.getFragment()
                                .equals(page.getUriFragment())) {
                page.setUriFragment(navigationState.getFragment(), false);
            }
            // now change the view
            KrailView view = viewFactory.get(node.getViewClass());
            changeView(view, event);
            // and tell listeners its changed
            fireAfterViewChange(event);
        } else {
            throw new UnauthorizedException(navigationState.getVirtualPage());
        }

    }

    /**
     * Checks {@code navigationState} to see whether the {@link Sitemap} defines this as a page which should be
     * redirected. If it is, a {@link NavigationState} is returned, modified for the redirected page. If no
     * redirection is required, the {@code navigationState} is returned unchanged.
     *
     * @param navigationState the proposed navigation state before considering redirection
     *
     * @return navigationState reflecting the correct navigation state after considering a possible redirection
     */
    private NavigationState redirectIfNeeded(NavigationState navigationState) {

        String page = navigationState.getVirtualPage();
        String redirection = userSitemap.getRedirectPageFor(page);
        // if no redirect found, page is returned
        if (redirection.equals(page)) {
            return navigationState;
        } else {
            navigationState.setVirtualPage(redirection);
            navigationState.setFragment(uriHandler.fragment(navigationState));
            return navigationState;
        }
    }

    protected void changeView(KrailView view, KrailViewChangeEvent event) {
        ScopedUI ui = uiProvider.get();
        log.debug("calling view.beforeBuild(event) for {}", view.getClass()
                                                                .getName());
        view.beforeBuild(event);
        log.debug("calling view.buildView(event) {}", view.getClass()
                                                          .getName());
        view.buildView(event);
        ui.changeView(view);
        log.debug("calling view.afterBuild(event) {}", view.getClass()
                                                           .getName());
        view.afterBuild(event);
        currentView = view;
    }

    /**
     * Fires an event before an imminent view change.  At this point the event:<ol> <
     * <li><{@code fromState} represents the current navigation state/li>
     * li>{@code toState} represents the navigation state which will be moved to if the change is successful.</li></ol>
     * <p>
     * Listeners are called in registration order. If any listener cancels the event, {@link
     * KrailViewChangeEvent#cancel()}, the rest of the
     * listeners are not called and the view change is blocked.
     *
     * @param event
     *         view change event (not null, view change not yet performed)
     *
     * @return true if the view change should be allowed, false to silently block the navigation operation
     */
    protected boolean fireBeforeViewChange(KrailViewChangeEvent event) {
        for (KrailViewChangeListener l : viewChangeListeners) {
            l.beforeViewChange(event);
            if (event.isCancelled()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fires an event after the current view has changed.
     * <p>
     * Listeners are called in registration order.
     *
     * @param event
     *         view change event (not null)
     */
    protected void fireAfterViewChange(KrailViewChangeEvent event) {
        for (KrailViewChangeListener l : viewChangeListeners) {
            l.afterViewChange(event);
        }
    }

    @Override
    public NavigationState getCurrentNavigationState() {
        return currentNavigationState;
    }

    @Override
    public List<String> getNavigationParams() {
        return currentNavigationState.getParameterList();
    }

    @Override
    public KrailView getCurrentView() {
        return currentView;
    }

    /**
     * Returns the NavigationState representing the previous position of the navigator
     *
     * @return the NavigationState representing the previous position of the navigator
     */
    public NavigationState getPreviousNavigationState() {
        return previousNavigationState;
    }

    @Override
    public void clearHistory() {
        previousNavigationState = null;
    }

    @Override
    public void error(Throwable error) {
        log.debug("A {} Error has been thrown, reporting via the Error View", error.getClass()
                                                                                   .getName());
        NavigationState navigationState = uriHandler.navigationState("error");
        KrailViewChangeEvent event = new KrailViewChangeEvent(previousNavigationState, navigationState);
        ErrorView view = viewFactory.get(ErrorView.class);
        view.setError(error);
        changeView(view, event);
    }

    /**
     * Navigates to a the location represented by {@code node}
     */
    @Override
    public void navigateTo(UserSitemapNode node) {
        navigateTo(userSitemap.uri(node));
    }

    @Override
    public UserSitemapNode getCurrentNode() {
        return userSitemap.nodeFor(currentNavigationState);
    }

    @Override
    public UserSitemapNode getPreviousNode() {
        return userSitemap.nodeFor(previousNavigationState);
    }

    /**
     * Applies the login navigation rule to change page if required
     * @param source
     */
    @Override
    public void userHasLoggedIn(UserStatusChangeSource source) {
        log.info("user logged in successfully, applying login navigation rule");
        applyLoginNavigationRule(source);
    }

    protected void applyLoginNavigationRule(UserStatusChangeSource source) {
        Optional<NavigationState> newState = loginNavigationRule.changedNavigationState(this, source);
        if (newState.isPresent()) {
            navigateTo(newState.get());
        }

    }

    /**
     * Applies the logout navigation rule to change page if required
     *
     * @param source
     */
    @Override
    public void userHasLoggedOut(UserStatusChangeSource source) {
        log.info("user logged out, applying logout navigation rule");
        applyLogoutNavigationRule(source);
    }

    protected void applyLogoutNavigationRule(UserStatusChangeSource source) {
        Optional<NavigationState> newState = logoutNavigationRule.changedNavigationState(this, source);
        if (newState.isPresent()) {
            navigateTo(newState.get());
        }
    }

    @Override
    public void navigateTo(StandardPageKey pageKey) {
        navigateTo(userSitemap.standardPageURI(pageKey));
    }

}
