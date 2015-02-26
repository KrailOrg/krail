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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.Page;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import fixture.ReferenceUserSitemap;
import fixture.TestI18NModule;
import fixture.testviews2.ViewB;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.sitemap.*;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.shiro.PageAccessController;
import uk.q3c.krail.core.shiro.PagePermission;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.ui.ScopedUI;
import uk.q3c.krail.core.ui.ScopedUIProvider;
import uk.q3c.krail.core.user.UserStatusChangeSource;
import uk.q3c.krail.core.view.*;
import uk.q3c.krail.testutil.MockOption;
import uk.q3c.krail.testutil.TestOptionModule;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({VaadinSessionScopeModule.class, TestI18NModule.class, TestOptionModule.class})
public class DefaultNavigatorTest {

    @Mock
    UserSitemapBuilder builder;
    @Inject
    TestViewChangeListener changeListener;
    @Mock
    KrailViewChangeEvent event;
    @Mock
    UserStatusChangeSource source;
    @Inject
    MockOption option;
    @Mock
    private Page browserPage;
    @Mock
    private ErrorView errorView;
    @Mock
    private Provider<ErrorView> errorViewProvider;
    @Mock
    private KrailViewChangeListener listener1;
    @Mock
    private KrailViewChangeListener listener2;
    @Mock
    private KrailViewChangeListener listener3;
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

    @Before
    public void setup() {
        userSitemap.populate();
        when(builder.getUserSitemap()).thenReturn(userSitemap);
        when(uiProvider.get()).thenReturn(scopedUI);
        when(scopedUI.getPage()).thenReturn(browserPage);
        when(errorViewProvider.get()).thenReturn(errorView);
        when(subjectProvider.get()).thenReturn(subject);
        when(userSitemapProvider.get()).thenReturn(userSitemap);

        CurrentInstance.set(UI.class, scopedUI);


    }

    @Test
    public void init() throws Exception {

        // given

        // when
        navigator = createNavigator();
        // then
        verify(sitemapService).start();
        verify(builder).build();
    }

    private DefaultNavigator createNavigator() {
        navigator = new DefaultNavigator(uriHandler, sitemapService, subjectProvider, pageAccessController, uiProvider, viewFactory, builder, loginNavigationRule, logoutNavigationRule);
        navigator.init();
        return navigator;
    }



    @Test
    public void login() {
        // given
        navigator = createNavigator();
        // when
        navigator.navigateTo(userSitemap.loginURI);
        // then
        assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.loginViewClass);
        verify(scopedUI).changeView(any(LoginView.class));
    }


    @Test
    public void logout_rule_invoked() {

        // given
        navigator = createNavigator();
        when(logoutNavigationRule.changedNavigationState(navigator, logoutSource)).thenReturn(Optional.empty());
        // when
        navigator.userHasLoggedOut(logoutSource);
        // then
        verify(logoutNavigationRule).changedNavigationState(navigator, logoutSource);
    }


    @Test
    public void login_rule_invoked() {

        // given

        //        assertThat(loginNavigationRule.changedNavigationState(navigator,loginSource)).isNotNull();
        navigator = createNavigator();
        when(loginNavigationRule.changedNavigationState(navigator, loginSource)).thenReturn(Optional.empty());
        // when
        navigator.userHasLoggedIn(loginSource);
        // then
        verify(loginNavigationRule).changedNavigationState(navigator, loginSource);
    }

    @Test
    public void navigateTo() {

        // given
        navigator = createNavigator();
        // when
        navigator.navigateTo(userSitemap.a11URI);
        // then
        assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.a11ViewClass);
        assertThat(navigator.getCurrentNode()).isEqualTo(userSitemap.a11Node);
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(userSitemap.a11URI);
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

    @Test(expected = InvalidURIException.class)
    public void navigateTo_invalidURI() {

        // given
        navigator = createNavigator();
        String page = "public/view3";

        // when
        navigator.navigateTo(page);
        // then

    }

    @Test
    public void navigate_to_invalid_URI_exception_target_uri() {
        //given
        navigator = createNavigator();
        String page = "public/view3";
        // when
        try {
            navigator.navigateTo(page);
        } catch (InvalidURIException iue) {
            //then
            assertThat(iue.getTargetURI()).isEqualTo(page);
        }

    }

    public void navigateTo_invalidURI_checkView() {

        // given
        navigator = createNavigator();
        String page = "public/view3";
        // when
        try {
            navigator.navigateTo(page);
        } catch (Exception e) {
            // then
            assertThat(navigator.getCurrentView()).isEqualTo(errorView);
        }

    }

    @Test
    public void getNavigationState() {

        // given
        navigator = createNavigator();
        // when
        navigator.navigateTo(userSitemap.a1URI);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(userSitemap.a1URI);

    }

    @Test
    public void getNavigationParams() {

        // given
        navigator = createNavigator();
        String page1 = userSitemap.a1URI;
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
        navigator.navigateTo(userSitemap.a11Node);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(userSitemap.a11URI);

    }

    @Test
    public void currentAndPreviousViews_andClearHistory() {

        // given
        navigator = createNavigator();
        String page1 = userSitemap.a1URI;
        String fragment1 = page1 + "/id=1";

        String page2 = userSitemap.a11URI;
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
        String page = userSitemap.a11URI;

        // need to return true, or first listener will block the second
        navigator.addViewChangeListener(listener1);
        navigator.addViewChangeListener(listener2);
        navigator.addViewChangeListener(listener3);
        // when
        navigator.removeViewChangeListener(listener3);
        navigator.navigateTo(page);
        // then
        verify(listener1, times(1)).beforeViewChange(any(KrailViewChangeEvent.class));
        verify(listener2, times(1)).beforeViewChange(any(KrailViewChangeEvent.class));
        verify(listener3, never()).beforeViewChange(any(KrailViewChangeEvent.class));
    }

    @Test
    public void listener_blocked() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a11URI;
        MockListener listener4 = new MockListener();
        listener4.cancelBefore = true;

        // to block second and subsequent
        navigator.addViewChangeListener(listener4);
        navigator.addViewChangeListener(listener2);
        navigator.addViewChangeListener(listener3);
        // when
        navigator.navigateTo(page);
        // then
        verify(listener2, never()).beforeViewChange(any(KrailViewChangeEvent.class));
        verify(listener3, never()).beforeViewChange(any(KrailViewChangeEvent.class));
    }

    @Test
    public void attemptToBlockAfter() {
        //given
        navigator = createNavigator();
        String page = userSitemap.a11URI;
        MockListener listener4 = new MockListener();
        listener4.cancelAfter = true;
        navigator.addViewChangeListener(listener4);
        navigator.addViewChangeListener(listener2);
        navigator.addViewChangeListener(listener3);
        //when
        navigator.navigateTo(page);
        //then
        verify(listener2, times(1)).beforeViewChange(any(KrailViewChangeEvent.class));
        verify(listener3, times(1)).beforeViewChange(any(KrailViewChangeEvent.class));
    }

    @Test
    public void redirection() {

        // given
        navigator = createNavigator();
        String page = "wiggly";
        String page2 = userSitemap.a1URI;

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
        String page = userSitemap.a1URI;
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
        String page = userSitemap.a1URI;
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(userSitemap.a1URI);
    }

    /**
     * 'user' is required to be either authenticated or remembered
     */
    @Test
    public void UAC_User() {

        // given authenticated
        navigator = createNavigator();
        String page = userSitemap.a1URI;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node.getMasterNode()
                          .setPageAccessControl(PageAccessControl.USER);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(userSitemap.a1URI);

        // given remembered
        page = userSitemap.a11URI;
        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.isRemembered()).thenReturn(true);
        userSitemap.a11Node.getMasterNode()
                           .setPageAccessControl(PageAccessControl.USER);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(userSitemap.a11URI);

    }

    @Test(expected = UnauthorizedException.class)
    public void UAC_User_fail() {
        // given
        navigator = createNavigator();
        String page = userSitemap.a1URI;

        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node.getMasterNode()
                          .setPageAccessControl(PageAccessControl.USER);
        // when
        navigator.navigateTo(page);
        // then
        // exception thrown
    }

    @Test
    public void UAC_Guest() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1URI;

        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node.getMasterNode()
                          .setPageAccessControl(PageAccessControl.GUEST);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(userSitemap.a1URI);

    }

    @Test(expected = UnauthorizedException.class)
    public void UAC_Guest_Fail_remembered() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1URI;

        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.isRemembered()).thenReturn(true);
        userSitemap.a1Node.getMasterNode()
                          .setPageAccessControl(PageAccessControl.GUEST);
        // when
        navigator.navigateTo(page);
        // then
    }

    @Test(expected = UnauthorizedException.class)
    public void UAC_Guest_Fail_authenticated() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1URI;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node.getMasterNode()
                          .setPageAccessControl(PageAccessControl.GUEST);
        // when
        navigator.navigateTo(page);
        // then
    }

    @Test
    public void UAC_Authenticate() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1URI;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node.getMasterNode()
                          .setPageAccessControl(PageAccessControl.AUTHENTICATION);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(userSitemap.a1URI);

    }

    @Test(expected = UnauthorizedException.class)
    public void UAC_Authenticate_Fail() {
        // given
        navigator = createNavigator();
        String page = userSitemap.a1URI;

        when(subject.isAuthenticated()).thenReturn(false);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node.getMasterNode()
                          .setPageAccessControl(PageAccessControl.AUTHENTICATION);
        // when
        navigator.navigateTo(page);
        // then
    }

    @Test
    public void UAC_Permission() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1URI;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node.getMasterNode()
                          .setPageAccessControl(PageAccessControl.PERMISSION);
        when(subject.isPermitted(any(PagePermission.class))).thenReturn(true);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(userSitemap.a1URI);

    }

    @Test(expected = UnauthorizedException.class)
    public void UAC_Permission_Failed() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1URI;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node.getMasterNode()
                          .setPageAccessControl(PageAccessControl.PERMISSION);
        when(subject.isPermitted(any(PagePermission.class))).thenReturn(false);
        // when
        navigator.navigateTo(page);
        // then
    }

    @Test
    public void UAC_roles() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1URI;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node.getMasterNode()
                          .setPageAccessControl(PageAccessControl.ROLES);
        userSitemap.a1Node.getMasterNode()
                          .addRole("admin");
        userSitemap.a1Node.getMasterNode()
                          .addRole("beast");
        List<String> permissions = userSitemap.a1Node.getMasterNode()
                                                     .getRoles();
        when(subject.hasAllRoles(permissions)).thenReturn(true);
        // when
        navigator.navigateTo(page);
        // then
        assertThat(navigator.getCurrentNavigationState()
                            .getFragment()).isEqualTo(page);

    }

    @Test(expected = UnauthorizedException.class)
    public void UAC_roles_failed() {

        // given
        navigator = createNavigator();
        String page = userSitemap.a1URI;

        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        userSitemap.a1Node.getMasterNode()
                          .setPageAccessControl(PageAccessControl.ROLES);
        userSitemap.a1Node.getMasterNode()
                          .addRole("admin");
        userSitemap.a1Node.getMasterNode()
                          .addRole("beast");
        List<String> permissions = userSitemap.a1Node.getMasterNode()
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
        navigator.addViewChangeListener(changeListener);
        String page = userSitemap.bURI;
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
        navigator.addViewChangeListener(changeListener);
        String page = userSitemap.bURI;
        when(subject.isAuthenticated()).thenReturn(true);
        when(subject.isRemembered()).thenReturn(false);
        when(subject.isPermitted(any(PagePermission.class))).thenReturn(true);
        //when
        navigator.navigateTo(page);

        //then
        KrailViewChangeEvent event = getEvent("beforeViewChange");
        assertThat(event.getFromState()).isNull();
        assertThat(event.getToState()
                        .getFragment()).isEqualTo(userSitemap.bURI);

        event = getEvent("afterViewChange");
        assertThat(event.getFromState()).isNull();
        assertThat(event.getToState()
                        .getFragment()).isEqualTo(userSitemap.bURI);


        //given
        String page2 = userSitemap.b1URI;
        changeListener.clear();
        //when

        navigator.navigateTo(page2);
        //then

        event = getEvent("beforeViewChange");
        assertThat(event.getFromState()
                        .getFragment()).isEqualTo(userSitemap.bURI);

        assertThat(event.getToState()
                        .getFragment()).isEqualTo(userSitemap.b1URI);

        event = getEvent("afterViewChange");
        assertThat(event.getFromState()
                        .getFragment()).isEqualTo(userSitemap.bURI);

        assertThat(event.getToState()
                        .getFragment()).isEqualTo(userSitemap.b1URI);


    }

    private KrailViewChangeEvent getEvent(String eventKey) {
        return changeListener.getEvent(eventKey);
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

    static class MockListener implements KrailViewChangeListener {
        boolean cancelBefore = false;
        boolean cancelAfter = false;

        @Override
        public void beforeViewChange(KrailViewChangeEvent event) {
            if (cancelBefore) {
                event.cancel();
            }
        }

        @Override
        public void afterViewChange(KrailViewChangeEvent event) {
            if (cancelAfter) {
                event.cancel();
            }
        }
    }

    @Singleton
    public static class TestViewChangeListener implements KrailViewChangeListener {

        Map<String, KrailViewChangeEvent> calls = new LinkedHashMap<>();

        public Set<String> getCalls() {
            return calls.keySet();
        }

        @Override
        public void beforeViewChange(KrailViewChangeEvent event) {
            calls.put("beforeViewChange", event);
        }

        /**
         * Invoked after the view is changed. If a <code>beforeViewChange</code>
         * method blocked the view change, this method is not called. Be careful of
         * unbounded recursion if you decide to change the view again in the
         * listener.
         *
         * @param event
         *         view change event
         */
        @Override
        public void afterViewChange(KrailViewChangeEvent event) {
            calls.put("afterViewChange", event);
        }

        public void addCall(String call, KrailViewChangeEvent event) {
            calls.put(call, event);
        }


        public KrailViewChangeEvent getEvent(String eventKey) {
            return calls.get(eventKey);
        }

        public void clear() {
            calls.clear();
        }
    }

}
