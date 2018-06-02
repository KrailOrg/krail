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
package uk.q3c.krail.core.navigate;

import com.google.inject.Inject;
import com.vaadin.server.Page;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.eventbus.UIBusProvider;
import uk.q3c.krail.core.guice.uiscope.UIScoped;
import uk.q3c.krail.core.monitor.PageLoadingMessage;
import uk.q3c.krail.core.monitor.PageReadyMessage;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap;
import uk.q3c.krail.core.navigate.sitemap.Sitemap;
import uk.q3c.krail.core.navigate.sitemap.SitemapException;
import uk.q3c.krail.core.navigate.sitemap.SitemapService;
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapBuilder;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.shiro.PageAccessController;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.shiro.UnauthorizedExceptionHandler;
import uk.q3c.krail.core.ui.ScopedUI;
import uk.q3c.krail.core.ui.ScopedUIProvider;
import uk.q3c.krail.core.user.UserSitemapRebuilt;
import uk.q3c.krail.core.view.BeforeViewChangeBusMessage;
import uk.q3c.krail.core.view.ErrorView;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.core.view.ViewFactory;
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage;
import uk.q3c.krail.core.view.component.ComponentIdGenerator;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.eventbus.MessageBus;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.util.guice.SerializationSupport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The navigator is at the heart of navigation process, and provides navigation for a number of data types (for
 * example, String, {@link NavigationState} and {@link UserSitemapNode}.
 * <p>
 * The navigator implements {@link Page.PopStateListener }s to detect changes in URI.
 * <p>
 * Although the {@link UserSitemap} contains only authorised pages, an additional level of security is added by checking that a user is authorised before
 * moving
 * to another page
 * <p>
 * The {@link #uiBusProvider} is used to manage view changes - note that the eventBus must be synchronous for the view change cancellation to work (see {@link
 * #publishBeforeViewChange(BeforeViewChangeBusMessage)}
 *
 * @author David Sowerby
 * @date 18 Apr 2014
 */
@UIScoped
@Listener
@SubscribeTo(SessionBus.class)
public class DefaultNavigator implements Navigator {
    private static Logger log = LoggerFactory.getLogger(DefaultNavigator.class);

    private final URIFragmentHandler uriHandler;
    private final SubjectProvider subjectProvider;
    private final PageAccessController pageAccessController;
    private final ScopedUIProvider uiProvider;
    private final ViewFactory viewFactory;
    private final SitemapService sitemapService;
    private final UserSitemapBuilder userSitemapBuilder;
    private final LoginNavigationRule loginNavigationRule;
    private final LogoutNavigationRule logoutNavigationRule;
    private final InvalidURIHandler invalidURIHandler;
    private final MasterSitemap masterSitemap;
    private NavigationState currentNavigationState;
    private final UIBusProvider uiBusProvider;
    private NavigationState previousNavigationState;
    private UserSitemap userSitemap;
    private final ViewChangeRule viewChangeRule;
    private final ComponentIdGenerator idGenerator;
    private final transient MessageBus messageBus;
    private SerializationSupport serializationSupport;


    @Inject
    public DefaultNavigator(URIFragmentHandler uriHandler, SitemapService sitemapService, SubjectProvider subjectProvider, PageAccessController
            pageAccessController, ScopedUIProvider uiProvider, ViewFactory viewFactory, UserSitemapBuilder userSitemapBuilder, LoginNavigationRule
                                    loginNavigationRule, LogoutNavigationRule logoutNavigationRule, UIBusProvider uiBusProvider, ViewChangeRule
                                    viewChangeRule, InvalidURIHandler invalidURIHandler, ComponentIdGenerator idGenerator, MasterSitemap masterSitemap, MessageBus messageBus, SerializationSupport serializationSupport) {
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
        this.invalidURIHandler = invalidURIHandler;
        this.masterSitemap = masterSitemap;

        this.uiBusProvider = uiBusProvider;
        this.viewChangeRule = viewChangeRule;


        this.idGenerator = idGenerator;
        this.messageBus = messageBus;
        this.serializationSupport = serializationSupport;
    }

    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        inputStream.defaultReadObject();
        serializationSupport.deserialize(this);
    }

    @SuppressFBWarnings("EXS_EXCEPTION_SOFTENING_NO_CHECKED")
    @Override
    public void init() {
        log.debug("initialising DefaultNavigator");
        try {
            sitemapService.start();
            //take a reference and keep it in case current model changes
            userSitemapBuilder.setMasterSitemap(masterSitemap);
            userSitemapBuilder.build();
            userSitemap = userSitemapBuilder.getUserSitemap();

        } catch (Exception e) {
            String msg = "Sitemap service failed to start, application will have no pages";
            log.error(msg);
            throw new IllegalStateException(msg, e);
        }
    }

    /**
     * Replaces uriFragmentChanged. This was required by Vaadin 8, as the {@link Page.PopStateEvent} returns the full uri as a String, whereas its predecessor returned only the fragment
     *
     * @param event
     */
    @Override
    public void uriChanged(Page.PopStateEvent event) {
        log.debug("URI change received from Vaadin Page, with event.uri = {}", event.getUri());
        URI uri = URI.create(event.getUri());
        NavigationState navigationState = uriHandler.navigationState(uri);
        navigateTo(navigationState);
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
        publishPageLoadingMessage();

        // set up the navigation state
        NavigationState navigationState = uriHandler.navigationState(fragment);
        navigateTo(navigationState);
    }

    /**
     * Navigates to the location represented by {@code navigationState}. If the {@link Sitemap} holds a redirect for
     * the URI represented by {@code navigationState}, navigation will be directed to the redirect target. An
     * unrecognised URI will throw a {@link SitemapException}. If the view for the URI is found, the user's
     * authorisation is checked. If the user is not authorised, a {@link AuthorizationException} is thrown. This would
     * be caught by the the implementation bound to {@link UnauthorizedExceptionHandler}. If the user is authorised,
     * the
     * View is instantiated, and made the current view in the UI via {@link ScopedUI#changeView(KrailView)}.<br>
     * <br>
     * Messages are published to the {{@link #uiBusProvider}} before and after the view change. Message handlers have the
     * option to block the view change by returning false (see {@link #publishBeforeViewChange(BeforeViewChangeBusMessage)}
     * <p>
     *
     * @param navigationState The navigationState to navigate to. May not be null.
     */
    @Override
    public void navigateTo(NavigationState navigationState) {
        checkNotNull(navigationState);
        publishPageLoadingMessage();
        //computer says no
        if (!viewChangeRule.changeIsAllowed(this, getCurrentView())) {
            return;
        }
        //makes sure the navigation state is up to date, removes the need to do this externally
        uriHandler.updateFragment(navigationState);
        log.debug("Navigating to navigation state: {}", navigationState.getFragment());

        redirectIfNeeded(navigationState);

        // stop unnecessary changes, but also to prevent navigation aware
        // components from causing a loop by responding to a change of URI (they should suppress events when they do,
        // but may not)
        if (navigationState.equals(currentNavigationState)) {
            log.debug("fragment unchanged, no navigation required");
            return;
        }

        // https://sites.google.com/site/q3cjava/sitemap#emptyURI
        if (navigationState.getVirtualPage()
                .isEmpty()) {
            navigationState.virtualPage(userSitemap.standardPageURI(StandardPageKey.Public_Home));
            uriHandler.updateFragment(navigationState);
        }

        String virtualPage = navigationState.getVirtualPage();
        log.debug("obtaining view for '{}'", virtualPage);

        UserSitemapNode node = userSitemap.nodeFor(navigationState);
        if (node == null) {
            invalidURIHandler.invoke(this, virtualPage);
            return;
        }

        Subject subject = subjectProvider.get();
        boolean authorised = pageAccessController.isAuthorised(subject, masterSitemap, node);
        if (authorised) {

            // need this in case the change is blocked by a listener
            NavigationState previousPreviousNavigationState = previousNavigationState;
            previousNavigationState = currentNavigationState;
            currentNavigationState = navigationState;

            BeforeViewChangeBusMessage beforeMessage = new BeforeViewChangeBusMessage(previousNavigationState, navigationState);
            // if change is blocked revert to previous state
            if (!publishBeforeViewChange(beforeMessage)) {
                currentNavigationState = previousNavigationState;
                previousNavigationState = previousPreviousNavigationState;
                return;
            }

            // make sure the page uri is updated if necessary, but do not fire any change events
            // as we have already responded to the change
            ScopedUI ui = uiProvider.get();
            Page page = ui.getPage();
            String fragment = navigationState.getFragment();
            if (!fragment
                    .equals(page.getUriFragment())) {
                page.setUriFragment(fragment, false);
            }
            // now change the view
            KrailView view = viewFactory.get(node.getViewClass());
            AfterViewChangeBusMessage afterMessage = new AfterViewChangeBusMessage(beforeMessage);
            changeView(view, afterMessage);

            // and tell listeners its changed
            publishAfterViewChange(afterMessage);
        } else {
            throw new UnauthorizedException(navigationState.getVirtualPage());
        }

    }

    /**
     * Checks {@code navigationState} to see whether the {@link Sitemap} defines this as a page which should be
     * redirected. If it is,  {@code navigationState} is modified, modified for the redirected page. If no
     * redirection is required, the {@code navigationState} is returned unchanged.
     *
     * @param navigationState the proposed navigation state before considering redirection
     */
    private void redirectIfNeeded(NavigationState navigationState) {

        String page = navigationState.getVirtualPage();
        String redirection = userSitemap.getRedirectPageFor(page);
        // if no redirect found, do nothing
        if (!redirection.equals(page)) {
            navigationState.virtualPage(redirection)
                    .update(uriHandler);
        }
    }

    protected void changeView(KrailView view, ViewChangeBusMessage busMessage) {
        ScopedUI ui = uiProvider.get();
        log.debug("calling view.beforeBuild(event) for {}", view.getClass()
                .getName());
        view.beforeBuild(busMessage);
        log.debug("calling view.buildView(event) {}", view.getClass()
                .getName());
        view.buildView(busMessage);
        ui.changeView(view);
        log.debug("calling view.afterBuild(event) {}", view.getClass()
                .getName());

        generateAndApplyComponentIds(view);
        generateAndApplyComponentIds(ui);
        view.afterBuild(new AfterViewChangeBusMessage(busMessage));
        HasComponents g;
    }

    private void generateAndApplyComponentIds(Object containingObject) {
        // TODO configuration should allow Ids to be switched off see https://github.com/davidsowerby/krail/issues/662
        idGenerator.generateAndApply(containingObject);
    }

    /**
     * Publishes a message to the {@link #uiBusProvider} before an imminent view change.  At this point the {@code message}:<ol> <
     * <li><{@code fromState} represents the current navigation state/li>
     * li>{@code toState} represents the navigation state which will be moved to if the change is successful.</li></ol>
     * <p>
     * Message Handlers are called in an undefined order unless {@link Handler#priority()} is used to specify an order.  If any handler cancels the event,
     * {@link
     * BeforeViewChangeBusMessage#cancel()}, false is returned.
     *
     * @param busMessage view change message from the bus (view change not yet performed)
     * @return true if the view change should be allowed, false to silently block the navigation operation
     */
    protected boolean publishBeforeViewChange(BeforeViewChangeBusMessage busMessage) {

        // must be a synchronous bus, or the blocking mechanism will not work
        uiBusProvider.get().publish(busMessage);
        return !busMessage.isCancelled();

    }

    /**
     * Publishes a message to the {@link #uiBusProvider} immediately after a view change.
     * <p>
     * Message Handlers are called in an undefined order unless {@link Handler#priority()} is used to specify an order.
     *
     * @param busMessage view change message from the bus
     */
    protected void publishAfterViewChange(AfterViewChangeBusMessage busMessage) {
        uiBusProvider.get().publish(busMessage);
        ScopedUI ui = uiProvider.get();
        messageBus.publishASync(new PageReadyMessage(ui.getInstanceKey(), ui.getUIId()));
    }

    protected void publishPageLoadingMessage() {
        ScopedUI ui = uiProvider.get();
        messageBus.publishASync(new PageLoadingMessage(ui.getInstanceKey(), ui.getUIId()));
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
        return ((ScopedUI) UI.getCurrent()).getView();
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
        ViewChangeBusMessage viewChangeBusMessage = new ViewChangeBusMessage(previousNavigationState, navigationState);
        ErrorView view = viewFactory.get(ErrorView.class);
        view.setError(error);
        changeView(view, viewChangeBusMessage);
    }

    /**
     * Navigates to a the location represented by {@code node}
     */
    @Override
    public void navigateTo(UserSitemapNode node) {
        navigateTo(userSitemap.uri(node));
    }

    /**
     * Returns the node for the current navigation state.  If the node is not fond in the map, a check is also made to
     * see whether it is the login node (which will not appear in the map once the user has logged in)
     *
     * @return
     */
    @Override
    public UserSitemapNode getCurrentNode() {
        UserSitemapNode node = userSitemap.nodeFor(currentNavigationState);
        if (node == null) {
            if (userSitemap.isLoginUri(currentNavigationState)) {
                return userSitemap.standardPageNode(StandardPageKey.Log_In);
            } else {
                return null;
            }
        } else {
            return node;
        }
    }

    @Override
    public UserSitemapNode getPreviousNode() {
        return userSitemap.nodeFor(previousNavigationState);
    }

    /**
     * Applies the login / logout navigation rules after a user has logged in / out.  The {@link UserSitemap} rebuilds
     * in response to the login / logout, (which must complete before trying to change page) and then sends a
     * {@link UserSitemapRebuilt}, which this method then responds to
     *
     * @param event message from the event bus
     */
    @Handler
    public void handleUserSitemapRebuilt(UserSitemapRebuilt event) {
        log.debug("UserSitemapRebuilt received");
        if (event.getLoggedIn()) {
            log.info("user logged in, applying login navigation rule");
            Optional<NavigationState> newState = loginNavigationRule.changedNavigationState(this, event.getSource());
            newState.ifPresent(this::navigateTo);
        } else {
            log.info("user logged out, applying logout navigation rule");
            Optional<NavigationState> newState = logoutNavigationRule.changedNavigationState(this, event.getSource());
            newState.ifPresent(this::navigateTo);
        }
    }


    @Override
    public void navigateTo(StandardPageKey pageKey) {
        navigateTo(userSitemap.standardPageURI(pageKey));
    }

}
