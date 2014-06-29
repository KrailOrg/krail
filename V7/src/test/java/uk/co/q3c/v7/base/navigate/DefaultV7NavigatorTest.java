package uk.co.q3c.v7.base.navigate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultMasterSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultUserSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.MasterSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapService;
import uk.co.q3c.v7.base.navigate.sitemap.StandardPageKey;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemap;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.shiro.PageAccessController;
import uk.co.q3c.v7.base.shiro.PagePermission;
import uk.co.q3c.v7.base.shiro.SubjectProvider;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.ui.ScopedUIProvider;
import uk.co.q3c.v7.base.user.opt.DefaultUserOption;
import uk.co.q3c.v7.base.user.opt.DefaultUserOptionStore;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.opt.UserOptionStore;
import uk.co.q3c.v7.base.view.DefaultErrorView;
import uk.co.q3c.v7.base.view.DefaultViewFactory;
import uk.co.q3c.v7.base.view.ErrorView;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.Page;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

import fixture.ReferenceUserSitemap;
import fixture.testviews2.TestLoginView;
import fixture.testviews2.ViewA1;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class DefaultV7NavigatorTest {

	private DefaultV7Navigator navigator;

	@Mock
	private Provider<ErrorView> errorViewProvider;

	@Inject
	private StrictURIFragmentHandler uriHandler;

	@Mock
	private ScopedUI scopedUI;

	@Mock
	private Page browserPage;

	@Mock
	private ErrorView errorView;

	@Mock
	private V7ViewChangeListener listener1;

	@Mock
	private V7ViewChangeListener listener2;

	@Mock
	private V7ViewChangeListener listener3;

	@Mock
	private SitemapService sitemapService;

	@Mock
	private SubjectProvider subjectProvider;

	@Mock
	private Subject subject;

	@Inject
	private PageAccessController pageAccessController;

	@Mock
	private ScopedUIProvider uiProvider;

	@Inject
	private DefaultViewFactory viewFactory;

	@Mock
	private Provider<UserSitemap> userSitemapProvider;

	@Mock
	private UserOption userOption;

	@Inject
	private ReferenceUserSitemap userSitemap;

	@Before
	public void setup() {
		userSitemap.populate();
		when(uiProvider.get()).thenReturn(scopedUI);
		when(scopedUI.getPage()).thenReturn(browserPage);
		when(errorViewProvider.get()).thenReturn(errorView);
		when(subjectProvider.get()).thenReturn(subject);
		when(userSitemapProvider.get()).thenReturn(userSitemap);

		CurrentInstance.set(UI.class, scopedUI);
		navigator = createNavigator();
	}

	@Test
	public void logout() {

		// given

		// when
		navigator.navigateTo(StandardPageKey.Logout);
		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.logoutViewClass);
		verify(scopedUI).changeView(any(LogoutView.class));
	}

	@Test
	public void login() {
		// given
		// when
		navigator.navigateTo(userSitemap.loginURI);
		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.loginViewClass);
		verify(scopedUI).changeView(any(LoginView.class));
	}

	@Test
	public void loginSuccessFul_toPreviousView() {

		// given

		// when
		navigator.navigateTo(userSitemap.a1URI);
		navigator.navigateTo(StandardPageKey.Login);
		assertThat(navigator.getCurrentView()).isInstanceOf(TestLoginView.class);
		// // when
		when(subject.isAuthenticated()).thenReturn(true);
		navigator.userStatusChanged();
		// // then
		assertThat(navigator.getCurrentView()).isInstanceOf(ViewA1.class);
	}

	@Test
	public void loginSuccessFul_noPreviousView() {

		// given

		// when
		navigator.navigateTo(StandardPageKey.Login);
		assertThat(navigator.getCurrentView()).isInstanceOf(TestLoginView.class);
		verify(scopedUI).changeView(any(V7View.class));
		// // when
		when(subject.isAuthenticated()).thenReturn(true);
		navigator.userStatusChanged();
		// // then
		verify(scopedUI, times(2)).changeView(any(V7View.class));
		assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.privateHomeViewClass);

	}

	@Test
	public void navigateTo() {

		// given
		// when
		navigator.navigateTo(userSitemap.a11URI);
		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.a11ViewClass);
		assertThat(navigator.getCurrentNode()).isEqualTo(userSitemap.a11Node);
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(userSitemap.a11URI);
	}

	@Test
	public void navigateToEmptyPageWithParams() {

		// given
		String page1 = "";
		String fragment1 = page1 + "/id=2/age=5";

		// when
		navigator.navigateTo(fragment1);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo("public/home/id=2/age=5");

	}

	@Test(expected = InvalidURIException.class)
	public void navigateTo_invalidURI() {

		// given
		String page = "public/view3";

		// when
		navigator.navigateTo(page);
		// then

	}

	public void navigateTo_invalidURI_checkView() {

		// given
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
		// when
		navigator.navigateTo(userSitemap.a1URI);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(userSitemap.a1URI);

	}

	@Test
	public void getNavigationParams() {

		// given
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
		// when
		navigator.navigateTo(userSitemap.a11Node);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(userSitemap.a11URI);

	}

	@Test
	public void currentAndPreviousViews_andClearHistory() {

		// given

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
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(fragment1);

		assertThat(navigator.getPreviousNavigationState()).isNull();

		// when
		navigator.navigateTo(fragment2);

		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.a11ViewClass);
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(fragment2);

		assertThat(navigator.getPreviousNavigationState().getFragment()).isEqualTo(fragment1);

		// when
		navigator.clearHistory();

		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(userSitemap.a11ViewClass);
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(fragment2);

		assertThat(navigator.getPreviousNavigationState()).isNull();
	}

	/**
	 * Checks add and remove listeners
	 */
	@Test
	public void listeners_allRespond() {

		// given
		String page = userSitemap.a11URI;

		// need to return true, or first listener will block the second
		when(listener1.beforeViewChange(any(V7ViewChangeEvent.class))).thenReturn(true);
		navigator.addViewChangeListener(listener1);
		navigator.addViewChangeListener(listener2);
		navigator.addViewChangeListener(listener3);
		// when
		navigator.removeViewChangeListener(listener3);
		navigator.navigateTo(page);
		// then
		verify(listener1, times(1)).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener2, times(1)).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener3, never()).beforeViewChange(any(V7ViewChangeEvent.class));
	}

	@Test
	public void listener_blocked() {

		// given
		String page = userSitemap.a11URI;

		// to block second and subsequent
		when(listener1.beforeViewChange(any(V7ViewChangeEvent.class))).thenReturn(false);
		navigator.addViewChangeListener(listener1);
		navigator.addViewChangeListener(listener2);
		navigator.addViewChangeListener(listener3);
		// when
		navigator.navigateTo(page);
		// then
		verify(listener1, times(1)).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener2, never()).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener3, never()).beforeViewChange(any(V7ViewChangeEvent.class));
	}

	@Test
	public void redirection() {

		// given
		String page = "wiggly";
		String page2 = userSitemap.a1URI;

		userSitemap.addRedirect(page, page2);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(page2);
	}

	@Test
	public void navigateToNavState() {

		// given
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
		// when
		navigator.error(new NullPointerException("test"));
		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(ErrorView.class);

	}

	@Test
	public void UAC_Public() {

		// given
		String page = userSitemap.a1URI;
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(userSitemap.a1URI);
	}

	/**
	 * 'user' is required to be either authenticated or remembered
	 */
	@Test
	public void UAC_User() {

		// given authenticated
		String page = userSitemap.a1URI;

		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);
		userSitemap.a1Node.getMasterNode().setPageAccessControl(PageAccessControl.USER);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(userSitemap.a1URI);

		// given remembered
		page = userSitemap.a11URI;
		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.isRemembered()).thenReturn(true);
		userSitemap.a11Node.getMasterNode().setPageAccessControl(PageAccessControl.USER);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(userSitemap.a11URI);

	}

	@Test(expected = UnauthorizedException.class)
	public void UAC_User_fail() {
		// given
		String page = userSitemap.a1URI;

		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.isRemembered()).thenReturn(false);
		userSitemap.a1Node.getMasterNode().setPageAccessControl(PageAccessControl.USER);
		// when
		navigator.navigateTo(page);
		// then
		// exception thrown
	}

	@Test
	public void UAC_Guest() {

		// given
		String page = userSitemap.a1URI;

		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.isRemembered()).thenReturn(false);
		userSitemap.a1Node.getMasterNode().setPageAccessControl(PageAccessControl.GUEST);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(userSitemap.a1URI);

	}

	@Test(expected = UnauthorizedException.class)
	public void UAC_Guest_Fail_remembered() {

		// given
		String page = userSitemap.a1URI;

		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.isRemembered()).thenReturn(true);
		userSitemap.a1Node.getMasterNode().setPageAccessControl(PageAccessControl.GUEST);
		// when
		navigator.navigateTo(page);
		// then
	}

	@Test(expected = UnauthorizedException.class)
	public void UAC_Guest_Fail_authenticated() {

		// given
		String page = userSitemap.a1URI;

		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);
		userSitemap.a1Node.getMasterNode().setPageAccessControl(PageAccessControl.GUEST);
		// when
		navigator.navigateTo(page);
		// then
	}

	@Test
	public void UAC_Authenticate() {

		// given
		String page = userSitemap.a1URI;

		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);
		userSitemap.a1Node.getMasterNode().setPageAccessControl(PageAccessControl.AUTHENTICATION);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(userSitemap.a1URI);

	}

	@Test(expected = UnauthorizedException.class)
	public void UAC_Authenticate_Fail() {
		// given
		String page = userSitemap.a1URI;

		when(subject.isAuthenticated()).thenReturn(false);
		when(subject.isRemembered()).thenReturn(false);
		userSitemap.a1Node.getMasterNode().setPageAccessControl(PageAccessControl.AUTHENTICATION);
		// when
		navigator.navigateTo(page);
		// then
	}

	@Test
	public void UAC_Permission() {

		// given
		String page = userSitemap.a1URI;

		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);
		userSitemap.a1Node.getMasterNode().setPageAccessControl(PageAccessControl.PERMISSION);
		when(subject.isPermitted(any(PagePermission.class))).thenReturn(true);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(userSitemap.a1URI);

	}

	@Test(expected = UnauthorizedException.class)
	public void UAC_Permission_Failed() {

		// given
		String page = userSitemap.a1URI;

		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);
		userSitemap.a1Node.getMasterNode().setPageAccessControl(PageAccessControl.PERMISSION);
		when(subject.isPermitted(any(PagePermission.class))).thenReturn(false);
		// when
		navigator.navigateTo(page);
		// then
	}

	@Test
	public void UAC_roles() {

		// given
		String page = userSitemap.a1URI;

		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);
		userSitemap.a1Node.getMasterNode().setPageAccessControl(PageAccessControl.ROLES);
		userSitemap.a1Node.getMasterNode().addRole("admin");
		userSitemap.a1Node.getMasterNode().addRole("beast");
		List<String> permissions = userSitemap.a1Node.getMasterNode().getRoles();
		when(subject.hasAllRoles(permissions)).thenReturn(true);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(page);

	}

	@Test(expected = UnauthorizedException.class)
	public void UAC_roles_failed() {

		// given
		String page = userSitemap.a1URI;

		when(subject.isAuthenticated()).thenReturn(true);
		when(subject.isRemembered()).thenReturn(false);
		userSitemap.a1Node.getMasterNode().setPageAccessControl(PageAccessControl.ROLES);
		userSitemap.a1Node.getMasterNode().addRole("admin");
		userSitemap.a1Node.getMasterNode().addRole("beast");
		List<String> permissions = userSitemap.a1Node.getMasterNode().getRoles();
		when(subject.hasAllRoles(permissions)).thenReturn(false);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentNavigationState().getFragment()).isEqualTo(page);

	}

	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
				bind(ErrorView.class).to(DefaultErrorView.class);
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
				bind(MasterSitemap.class).to(DefaultMasterSitemap.class);
				bind(UserSitemap.class).to(DefaultUserSitemap.class);
				bind(UserOption.class).to(DefaultUserOption.class);
				bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
			}

		};
	}

	private DefaultV7Navigator createNavigator() {
		return new DefaultV7Navigator(uriHandler, sitemapService, subjectProvider, pageAccessController, uiProvider,
				viewFactory, userSitemapProvider);
	}

}
