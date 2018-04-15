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

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.Page;
import fixture.ReferenceUserSitemap;
import fixture.testviews2.ViewB;
import net.engio.mbassy.bus.common.PubSubSupport;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.config.KrailApplicationConfigurationModule;
import uk.q3c.krail.core.eventbus.UIBus;
import uk.q3c.krail.core.eventbus.UIBusProvider;
import uk.q3c.krail.core.eventbus.VaadinEventBusModule;
import uk.q3c.krail.core.guice.InjectorHolder;
import uk.q3c.krail.core.guice.ServletEnvironmentModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.MessageKey;
import uk.q3c.krail.core.navigate.sitemap.DefaultMasterSitemap;
import uk.q3c.krail.core.navigate.sitemap.DefaultUserSitemap;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.SitemapModule;
import uk.q3c.krail.core.navigate.sitemap.SitemapService;
import uk.q3c.krail.core.navigate.sitemap.UserSitemap;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapBuilder;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.set.MasterSitemapQueue;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.shiro.PageAccessController;
import uk.q3c.krail.core.shiro.PagePermission;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.ui.ScopedUI;
import uk.q3c.krail.core.ui.ScopedUIProvider;
import uk.q3c.krail.core.user.LoginView;
import uk.q3c.krail.core.user.UserSitemapRebuilt;
import uk.q3c.krail.core.user.notify.UserNotifier;
import uk.q3c.krail.core.user.status.UserStatusChangeSource;
import uk.q3c.krail.core.view.BeforeViewChangeBusMessage;
import uk.q3c.krail.core.view.DefaultErrorView;
import uk.q3c.krail.core.view.DefaultViewFactory;
import uk.q3c.krail.core.view.ErrorView;
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage;
import uk.q3c.krail.core.view.component.ComponentIdGenerator;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.eventbus.SubscribeTo;
import uk.q3c.krail.eventbus.mbassador.EventBusModule;
import uk.q3c.krail.i18n.test.TestI18NModule;
import uk.q3c.krail.option.mock.MockOption;
import uk.q3c.krail.option.mock.TestOptionModule;
import uk.q3c.krail.persist.inmemory.InMemoryModule;
import uk.q3c.krail.testutil.guice.uiscope.TestUIScopeModule;
import uk.q3c.krail.util.UtilsModule;
import uk.q3c.util.UtilModule;
import uk.q3c.util.guice.SerializationSupportModule;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({VaadinSessionScopeModule.class, TestI18NModule.class, InMemoryModule.class, TestOptionModule.class, VaadinEventBusModule.class,
        TestUIScopeModule.class, ServletEnvironmentModule.class, SerializationSupportModule.class, EventBusModule.class, SitemapModule.class, UtilModule.class, UtilsModule.class, KrailApplicationConfigurationModule.class})
public class DefaultNavigatorTest {

    @Mock
    UserSitemapBuilder builder;
    @Inject
    TestViewChangeListener changeListener;
    @Mock
    BeforeViewChangeBusMessage event;
    @Mock
    UserStatusChangeSource source;
    @Inject
    MockOption option;
    @Inject
    DefaultViewChangeRule defaultViewChangeRule;
    @Mock
    UserNotifier userNotifier;
    InvalidURIHandler invalidURIHandler;
    @Mock
    MasterSitemapQueue masterSitemapQueue;
    @Inject
    MasterSitemap masterSitemap;
    @Mock
    private Page browserPage;
    @Mock
    private ErrorView errorView;
    @Mock
    private Provider<ErrorView> errorViewProvider;
    @Inject
    private UIBusProvider eventBusProvider;
    @Inject
    @UIBus
    private PubSubSupport<BusMessage> eventBus2;
    @Inject
    private FakeListener listener1;
    @Inject
    private FakeListener listener2;
    @Inject
    private FakeListener listener3;
    @Inject
    private MockListener listener4;
    @Mock
    private LoginNavigationRule loginNavigationRule;
    @Mock
    private UserStatusChangeSource loginSource;
    @Mock
    private LogoutNavigationRule logoutNavigationRule;
    @Mock
    private UserStatusChangeSource logoutSource;
    private DefaultNavigator navigator;
    @Inject
    private PageAccessController pageAccessController;
    @Mock
    private ScopedUI scopedUI;
    @Mock
    private SitemapService sitemapService;
    @Mock
    private Subject subject;
    @Mock
    private SubjectProvider subjectProvider;
    @Mock
    private ScopedUIProvider uiProvider;
    @Inject
    private StrictURIFragmentHandler uriHandler;
    @Inject
    private ReferenceUserSitemap userSitemap;
    @Mock
    private Provider<UserSitemap> userSitemapProvider;
    @Inject
    private DefaultViewFactory viewFactory;

    @Inject
    private ComponentIdGenerator componentIdGenerator;

    @Inject
    Injector injector;

    @Before
    public void setup() {
        InjectorHolder.setInjector(injector);
        userSitemap.populate();
        when(builder.getUserSitemap()).thenReturn(userSitemap);
        when(uiProvider.get()).thenReturn(scopedUI);
        when(scopedUI.getPage()).thenReturn(browserPage);
        when(errorViewProvider.get()).thenReturn(errorView);
        when(subjectProvider.get()).thenReturn(subject);
        when(userSitemapProvider.get()).thenReturn(userSitemap);
        when(masterSitemapQueue.getCurrentModel()).thenReturn(masterSitemap);
        invalidURIHandler = new DefaultInvalidURIHandler(userNotifier);


    }

    @After
    public void tearDown() {

    }

    @Test
    public void init() {

        // given

        // when
        navigator = createNavigator();
        // then
        verify(sitemapService).start();
        verify(builder).build();
    }

    private DefaultNavigator createNavigator() {
        navigator = new DefaultNavigator(uriHandler, sitemapService, subjectProvider, pageAccessController, uiProvider, viewFactory, builder,
                loginNavigationRule, logoutNavigationRule, eventBusProvider, defaultViewChangeRule, invalidURIHandler, masterSitemapQueue, componentIdGenerator);
        navigator.init();
        return navigator;
    }


    @Test
    public void login() {
        // given
        navigator = createNavigator();
        // when
        navigator.navigateTo(userSitemap.loginFragment);
        // then
        assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.loginViewClass);
        verify(scopedUI).changeView(any(LoginView.class));
    }


    @Test
    public void logout_rule_invoked() {

        // given
        UserSitemapRebuilt userHasLoggedOut = new UserSitemapRebuilt(false, logoutSource);
        navigator = createNavigator();
        when(logoutNavigationRule.changedNavigationState(navigator, logoutSource)).thenReturn(Optional.empty());
        // when
        navigator.handleUserSitemapRebuilt(userHasLoggedOut);
        // then
        verify(logoutNavigationRule).changedNavigationState(navigator, logoutSource);
    }


    @Test
    public void login_rule_invoked() {

        // given
        UserSitemapRebuilt userHasLoggedIn = new UserSitemapRebuilt(true, loginSource);
        //        assertThat(loginNavigationRule.changedNavigationState(navigator,loginSource)).isNotNull();
        navigator = createNavigator();
        when(loginNavigationRule.changedNavigationState(navigator, loginSource)).thenReturn(Optional.empty());
        // when
        navigator.handleUserSitemapRebuilt(userHasLoggedIn);
        // then
        verify(loginNavigationRule).changedNavigationState(navigator, loginSource);
    }

    @Test
    public void navigateTo() {

        // given
        navigator = createNavigator();
        // when
        navigator.navigateTo(userSitemap.a11Fragment);
        // then
        assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.a11ViewClass);
        assertThat(navigator.getCurrentNode()).isEqualTo(userSitemap.a11Node());
        assertThat(navigator.getCurrentNavigationState()
                .getFragment()).isEqualTo(userSitemap.a11Fragment);
    }

    @Test
    public void navigateToEmptyPageWithParams() {

        // given
        navigator = createNavigator();
        String page1 = "";
        String fragment1 = page1 + "/id=2/age=5";

        // when
        navigator.navigateTo(fragment1);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo("public/home/id=2/age=5");

    }

    @Test
    public void navigateFromPageEvent() {

        // given
        Page page = mock(Page.class);
        Page.PopStateEvent event = new Page.PopStateEvent(page, "https://localhost:8080/krailapp/#" + userSitemap.a11Fragment);
        navigator = createNavigator();

        // when
        navigator.uriChanged(event);
        // then
        assertThat(navigator.getCurrentNavigationState()
                .getFragment()).isEqualTo(userSitemap.a11Fragment);

    }

    @Test
    public void navigateTo_invalidURI() {

        // given
        navigator = createNavigator();
        String page = "public/view3";

        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getVirtualPage()).isEqualTo("public/home");

    }


    @Test
    public void navigate_to_invalid_URI() {
        //given
        navigator = createNavigator();
        String page = "public/view3";
        // when
        navigator.navigateTo(page);
        //then
        verify(userNotifier).notifyInformation(MessageKey.Invalid_URI, page);

    }


    @Test
    public void getNavigationState() {

        // given
        navigator = createNavigator();
        // when
        navigator.navigateTo(userSitemap.a1Fragment);
        // then
        assertThat(navigator.getCurrentNavigationState()
                .getFragment()).isEqualTo(userSitemap.a1Fragment);

    }

    @Test
    public void getNavigationParams() {

        // given
        navigator = createNavigator();
        String page1 = userSitemap.a1Fragment;
        String fragment1 = page1 + "/id=2/age=5";
        // when
        navigator.navigateTo(fragment1);
        // then
        assertThat(navigator.getNavigationParams()).containsOnly("id=2", "age=5");

    }

    @Test
    public void navigateToNode() {

        // given
        navigator = createNavigator();
        // when
        navigator.navigateTo(userSitemap.a11Node());
        // then
        assertThat(navigator.getCurrentNavigationState()
                .getFragment()).isEqualTo(userSitemap.a11Fragment);

    }

    @Test
    public void currentAndPreviousViews_andClearHistory() {

        // given
        navigator = createNavigator();
        String page1 = userSitemap.a1Fragment;
        String fragment1 = page1 + "/id=1";

        String page2 = userSitemap.a11Fragment;
        String fragment2 = page2 + "/id=2";

        // when

        // then
        // start position
        assertThat(navigator.getCurrentView()).isNull();
        assertThat(navigator.getCurrentNavigationState()).isNull();

        assertThat(navigator.getPreviousNavigationState()).isNull();

        // when
        navigator.navigateTo(fragment1);

        // then
        assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.a1ViewClass);
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(fragment1);

        assertThat(navigator.getPreviousNavigationState()).isNull();

        // when
        navigator.navigateTo(fragment2);

        // then
        assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.a11ViewClass);
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(fragment2);

        assertThat(navigator.getPreviousNavigationState()
                            .getFragment()).isEqualTo(fragment1);

        // when
        navigator.clearHistory();

        // then
        assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.a11ViewClass);
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(fragment2);

        assertThat(navigator.getPreviousNavigationState()).isNull();
    }

    /**
     * Checks add and remove listeners
     */
    @Test
    public void listeners_allRespond() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a11Fragment;

        // when
        NavigationState startState = navigator.getCurrentNavigationState();
        navigator.navigateTo(page);
        NavigationState endState = navigator.getCurrentNavigationState();
        // then
        assertThat(endState).isNotEqualTo(startState);
    }

    @Test
    public void listener_blocked() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a11Fragment;
        listener4.cancelBefore = true;

        // when
        NavigationState startState = navigator.getCurrentNavigationState();
        navigator.navigateTo(page);
        NavigationState endState = navigator.getCurrentNavigationState();
        // then
        assertThat(endState).isEqualTo(startState);
    }


    @Test
    public void redirection() {

        // given
        navigator = createNavigator();
        String page = "wiggly";
        String page2 = userSitemap.a1Fragment;

        userSitemap.addRedirect(page, page2);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(page2);
    }

    @Test
    public void navigateToNavState() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1Fragment;
        NavigationState navigationState = uriHandler.navigationState(page);

        // when
        navigator.navigateTo(navigationState);
        // then
        assertThat(navigator.getCurrentNavigationState()).isEqualTo(navigationState);
        assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.a1ViewClass);
    }

    @Test
    public void error() {

        // given
        navigator = createNavigator();
        // when
        navigator.error(new NullPointerException("test"));
        // then
        assertThat(navigator.getCurrentView()).isInstanceOf(ErrorView.class);
    }

    @Test
    public void UAC_Public() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1Fragment;
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                .getFragment()).isEqualTo(userSitemap.a1Fragment);
    }

    /**
     * 'user' is required to be either authenticated or remembered
     */
    @Test
    public void UAC_User() {

        // given authenticated
        navigator = createNavigator();
        String page = userSitemap.a1Fragment;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node()
                   .getMasterNode()
                   .modifyPageAccessControl(PageAccessControl.USER);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                .getFragment()).isEqualTo(userSitemap.a1Fragment);

        // given remembered
        page = userSitemap.a11Fragment;
        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.isRemembered()).thenReturn(true);
        userSitemap.a11Node()
                   .getMasterNode()
                   .modifyPageAccessControl(PageAccessControl.USER);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                .getFragment()).isEqualTo(userSitemap.a11Fragment);

    }

    @Test(expected = UnauthorizedException.class)
    public void UAC_User_fail() {
        // given
        navigator = createNavigator();
        String page = userSitemap.a1Fragment;

        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.isRemembered()).thenReturn(false);
        updatePermissionForA1Node(PageAccessControl.USER);
        // when
        navigator.navigateTo(page);
        // then
        // exception thrown
    }

    /**
     * Changes the master node permission
     */
    private void updatePermissionForA1Node(PageAccessControl pageAccessControl) {
        MasterSitemapNode masterSitemapNode = userSitemap.a1Node()
                                                         .getMasterNode()
                                                         .modifyPageAccessControl(pageAccessControl);
        UserSitemapNode newNode = new UserSitemapNode(masterSitemapNode);
        newNode.setCollationKey(userSitemap.a1Node()
                                           .getCollationKey());
        newNode.setLabel(userSitemap.a1Node()
                                    .getLabel());
        userSitemap.replaceNode(userSitemap.a1Node(), newNode);
        userSitemap.setA1Node(newNode);
    }

    @Test
    public void UAC_Guest() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1Fragment;

        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node()
                   .getMasterNode()
                   .modifyPageAccessControl(PageAccessControl.GUEST);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                .getFragment()).isEqualTo(userSitemap.a1Fragment);

    }

    @Test(expected = UnauthorizedException.class)
    public void UAC_Guest_Fail_remembered() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1Fragment;

        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.isRemembered()).thenReturn(true);
        updatePermissionForA1Node(PageAccessControl.GUEST);
        // when
        navigator.navigateTo(page);
        // then
    }

    @Test(expected = UnauthorizedException.class)
    public void UAC_Guest_Fail_authenticated() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1Fragment;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        updatePermissionForA1Node(PageAccessControl.GUEST);
        // when
        navigator.navigateTo(page);
        // then
    }

    @Test
    public void UAC_Authenticate() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1Fragment;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node()
                   .getMasterNode()
                   .modifyPageAccessControl(PageAccessControl.AUTHENTICATION);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                .getFragment()).isEqualTo(userSitemap.a1Fragment);

    }

    @Test(expected = UnauthorizedException.class)
    public void UAC_Authenticate_Fail() {
        // given
        navigator = createNavigator();
        String page = userSitemap.a1Fragment;

        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.isRemembered()).thenReturn(false);
        updatePermissionForA1Node(PageAccessControl.AUTHENTICATION);
        // when
        navigator.navigateTo(page);
        // then
    }

    @Test
    public void UAC_Permission() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1Fragment;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node()
                   .getMasterNode()
                   .modifyPageAccessControl(PageAccessControl.PERMISSION);
        when(subject.isPermitted(any(PagePermission.class))).thenReturn(true);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                .getFragment()).isEqualTo(userSitemap.a1Fragment);

    }

    @Test(expected = UnauthorizedException.class)
    public void UAC_Permission_Failed() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1Fragment;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);

        updatePermissionForA1Node(PageAccessControl.PERMISSION);
        when(subject.isPermitted(any(PagePermission.class))).thenReturn(false);
        // when
        navigator.navigateTo(page);
        // then
    }

    @Test
    public void UAC_roles() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1Fragment;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);


        updatePermissionForA1Node(PageAccessControl.ROLES, Lists.newArrayList("admin", "beast"));

        List<String> permissions = userSitemap.a1Node()
                                              .getMasterNode()
                                              .getRoles();
        when(subject.hasAllRoles(permissions)).thenReturn(true);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(page);

    }

    /**
     * Changes the master node permission
     */
    private void updatePermissionForA1Node(PageAccessControl pageAccessControl, List<String> roles) {
        MasterSitemapNode oldMaster = userSitemap.a1Node()
                                                 .getMasterNode();
        MasterSitemapNode masterSitemapNode = new MasterSitemapNode(oldMaster.getId(), oldMaster.getUriSegment(), oldMaster.getViewClass(), oldMaster
                .getLabelKey(), oldMaster.getPositionIndex(), pageAccessControl, roles);

        UserSitemapNode newNode = new UserSitemapNode(masterSitemapNode);
        newNode.setCollationKey(userSitemap.a1Node()
                                           .getCollationKey());
        newNode.setLabel(userSitemap.a1Node()
                                    .getLabel());
        userSitemap.replaceNode(userSitemap.a1Node(), newNode);
    }

    @Test(expected = UnauthorizedException.class)
    public void UAC_roles_failed() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1Fragment;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        updatePermissionForA1Node(PageAccessControl.ROLES, Lists.newArrayList("admin", "beast"));

        List<String> permissions = userSitemap.a1Node()
                                              .getMasterNode()
                                              .getRoles();
        when(subject.hasAllRoles(permissions)).thenReturn(false);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(page);

    }

    @Test
    public void callOrder() {
        //given
        navigator = createNavigator();
        String page = userSitemap.bFragment;
        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        when(subject.isPermitted(any(PagePermission.class))).thenReturn(true);
        //when
        navigator.navigateTo(page);
        //then
        assertThat(navigator.getCurrentView()).isInstanceOf(ViewB.class);
        ViewB view = (ViewB) navigator.getCurrentView();
        assertThat(changeListener.getCalls()).containsExactly("beforeViewChange", "readFromEnvironment",
                "beforeBuild", "buildView", "afterBuild", "afterViewChange");

    }

    @Test
    public void eventContent() {
        navigator = createNavigator();
        String page = userSitemap.bFragment;
        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        when(subject.isPermitted(any(PagePermission.class))).thenReturn(true);
        //when
        navigator.navigateTo(page);

        //then
        ViewChangeBusMessage event = getEvent("beforeViewChange");
        assertThat(event.getFromState()).isNull();
        assertThat(event.getToState()
                .getFragment()).isEqualTo(userSitemap.bFragment);

        event = getEvent("afterViewChange");
        assertThat(event.getFromState()).isNull();
        assertThat(event.getToState()
                .getFragment()).isEqualTo(userSitemap.bFragment);


        //given
        String page2 = userSitemap.b1Fragment;
        changeListener.clear();
        //when

        navigator.navigateTo(page2);
        //then

        event = getEvent("beforeViewChange");
        assertThat(event.getFromState()
                .getFragment()).isEqualTo(userSitemap.bFragment);

        assertThat(event.getToState()
                .getFragment()).isEqualTo(userSitemap.b1Fragment);

        event = getEvent("afterViewChange");
        assertThat(event.getFromState()
                .getFragment()).isEqualTo(userSitemap.bFragment);

        assertThat(event.getToState()
                .getFragment()).isEqualTo(userSitemap.b1Fragment);


    }

    private ViewChangeBusMessage getEvent(String eventKey) {
        return changeListener.getMessage(eventKey);
    }

    /**
     * https://github.com/davidsowerby/krail/issues/382
     */
    @Test
    public void navStateNotUpdated() {
        //given
        navigator = createNavigator();
        NavigationState navState = new NavigationState();
        navState.virtualPage(userSitemap.aFragment);
        //when
        navigator.navigateTo(navState);
        //then
        assertThat(navigator.getCurrentNavigationState()).isEqualTo(navState);
    }

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(ErrorView.class).to(DefaultErrorView.class);
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(MasterSitemap.class).to(DefaultMasterSitemap.class);
                bind(UserSitemap.class).to(DefaultUserSitemap.class);
            }

        };
    }

    @Listener
    @SubscribeTo(UIBus.class)
    static class MockListener {
        boolean cancelBefore = false;
        boolean cancelAfter = false;

        @Handler
        public void beforeViewChange(BeforeViewChangeBusMessage event) {
            if (cancelBefore) {
                event.cancel();
            }
        }

        @Handler
        public void afterViewChange(BeforeViewChangeBusMessage event) {
            if (cancelAfter) {
                event.cancel();
            }
        }
    }

    @Singleton
    @Listener
    @SubscribeTo(UIBus.class)
    public static class TestViewChangeListener {

        Map<String, ViewChangeBusMessage> calls = new LinkedHashMap<>();

        public Set<String> getCalls() {
            return calls.keySet();
        }

        @Handler
        public void beforeViewChange(BeforeViewChangeBusMessage busMessage) {
            calls.put("beforeViewChange", busMessage);
        }

        /**
         * Invoked after the view is changed. If a <code>beforeViewChange</code>
         * method blocked the view change, this method is not called. Be careful of
         * unbounded recursion if you decide to change the view again in the
         * listener.
         *
         * @param busMessage view change event
         */
        @Handler
        public void afterViewChange(AfterViewChangeBusMessage busMessage) {
            calls.put("afterViewChange", busMessage);
        }

        public void addCall(String call, ViewChangeBusMessage busMessage) {
            calls.put(call, busMessage);
        }


        public ViewChangeBusMessage getMessage(String eventKey) {
            return calls.get(eventKey);
        }

        public void clear() {
            calls.clear();
        }
    }

    @Listener
    @SubscribeTo(UIBus.class)
    public static class FakeListener {

        int callsBefore;
        int callsAfter;

        @Handler
        public void beforeViewChange(BeforeViewChangeBusMessage event) {
            callsBefore++;
        }

        @Handler
        public void afterViewChange(BeforeViewChangeBusMessage event) {
            callsAfter++;
        }
    }

}
