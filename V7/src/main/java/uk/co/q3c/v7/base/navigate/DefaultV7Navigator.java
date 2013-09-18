package uk.co.q3c.v7.base.navigate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.sitemap.SiteMapException;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.shiro.LoginStatusListener;
import uk.co.q3c.v7.base.shiro.V7SecurityManager;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;

import com.google.inject.Provider;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@UIScoped
public class DefaultV7Navigator implements V7Navigator, LoginStatusListener {
	private static Logger log = LoggerFactory
			.getLogger(DefaultV7Navigator.class);

	private final Sitemap sitemap;
	private NavigationState previousNavigationState = null;
	private NavigationState currentNavigationState = null;
	private final List<V7ViewChangeListener> listeners = new LinkedList<V7ViewChangeListener>();
	private final Map<String, Provider<V7View>> viewProvidersMap;

	private final Provider<Subject> subjectProvider;
	private UriFragmentFactory uriFragmentFactory;

	@Inject
	protected DefaultV7Navigator(Sitemap sitemap,
			Map<String, Provider<V7View>> viewProMap,
			UriFragmentFactory uriFragmentFactory,
			V7SecurityManager securityManager, Provider<Subject> securityContext) {
		super();
		this.viewProvidersMap = viewProMap;
		this.sitemap = sitemap;
		this.subjectProvider = securityContext;
		this.uriFragmentFactory = uriFragmentFactory;
		securityManager.addListener(this);
	}

	@Override
	public void navigateTo(String uri) {
		assert uri != null;

		navigateTo(uriFragmentFactory.getUriFragment(uri));
	}

	/**
	 * Takes a URI fragment, checks for any redirects defined by the
	 * {@link Sitemap}, then calls {@link #navigateTo(V7View, String, String)}
	 * to change the view
	 * 
	 * @see uk.co.q3c.v7.base.navigate.V7Navigator#navigateTo(java.lang.String)
	 */
	public void navigateTo(URIFragment uriFragment) {

		log.debug("Navigating to uri: {}", uriFragment.getUri());

		sitemapCheck();
		if (sitemap.hasErrors()) {
			throw new SiteMapException(
					"Unable to navigate, site map has errors\n"
							+ sitemap.getReport());
		}

		// check permissions
		try {
			sitemap.checkPermissions(uriFragment.getVirtualPage(),
					subjectProvider.get());
		} catch (UnauthenticatedException e) {
			onUnauthenticatedException(e);
			return;
		} catch (UnauthorizedException e) {
			// TODO handle
			throw e;
		}

		// fragment needs to be revised if redirected
		if (checkRedirects(uriFragment)) {
			log.debug("fragment after redirect check is {}",
					uriFragment.getUri());

			// if redirected it need to begin the navigation again (this way i
			// can check permissions and redirects on the new uri)
			navigateTo(uriFragment);
			return;
		}

		// no redirects, and the navigation is permitted
		String viewUriFragment = uriFragment.getVirtualPage();
		log.debug("page to look up View is {}", viewUriFragment);
		Provider<V7View> provider = viewProvidersMap.get(viewUriFragment);
		V7View view = null;
		if (provider == null) {
			String msg = "View not found for page '" + uriFragment.getUri()
					+ "'";
			log.debug(msg);
			throw new InvalidURIException(msg);
		} else {
			view = provider.get();
		}

		navigateTo(new NavigationState(uriFragment, view));
	}

	/**
	 * Checks {@code fragment} to see whether it has been redirected. If it has
	 * the fragment will be modified for the redirected page.
	 * 
	 * @param uriFragment
	 * @return true if redirected, false otherwise
	 */
	private boolean checkRedirects(URIFragment uriFragment) {
		String page = uriFragment.getVirtualPage();
		String redirection = sitemap.getRedirectFor(page);
		// if no redirect found, page is returned
		if (redirection == null) {
			return false;
		} else {
			// TODO support apache mod-rewrite like redirects : if one set a
			// redirect for /foo -> /bar, then /foo/moo/a would be redirected to
			// /bar/moo/a
			uriFragment.setFragment(redirection);
			return true;
		}
	}

	protected void navigateTo(NavigationState navigationState) {
		changeView(navigationState.getFragment(), navigationState.getView());
	}

	protected void onUnauthenticatedException(UnauthenticatedException e) {
		log.trace("UnauthenticatedException");
		// reditect to login
		navigateTo(StandardPageKey.Login);
	}

	/**
	 * Internal method activating a view, setting its parameters and calling
	 * listeners.
	 * 
	 * @param view
	 *            view to activate
	 * @param fragment
	 */
	private void changeView(URIFragment fragment, V7View view) {
		final NavigationState newNavigationState = new NavigationState(
				fragment, view);

		V7ViewChangeEvent event = new V7ViewChangeEvent(this,
				currentNavigationState, newNavigationState);
		if (!fireBeforeViewChange(event)) {
			// aborted
			return;
		}
		V7View currentView = currentNavigationState != null ? currentNavigationState
				.getView() : null;
		getUI().changeView(currentView, view);
		view.enter(event);

		setCurrentNavigationState(newNavigationState);
		fireAfterViewChange(event);
	}

	/**
	 * Fires an event before an imminent view change.
	 * <p>
	 * Listeners are called in registration order. If any listener returns
	 * <code>false</code>, the rest of the listeners are not called and the view
	 * change is blocked.
	 * <p>
	 * The view change listeners may also e.g. open a warning or question dialog
	 * and save the parameters to re-initiate the navigation operation upon user
	 * action.
	 * 
	 * @param event
	 *            view change event (not null, view change not yet performed)
	 * @return true if the view change should be allowed, false to silently
	 *         block the navigation operation
	 */
	protected boolean fireBeforeViewChange(V7ViewChangeEvent event) {
		for (V7ViewChangeListener l : listeners) {
			if (!l.beforeViewChange(event)) {
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
	 *            view change event (not null)
	 */
	protected void fireAfterViewChange(V7ViewChangeEvent event) {
		for (V7ViewChangeListener l : listeners) {
			l.afterViewChange(event);
		}
	}

	/**
	 * Listen to changes of the active view.
	 * <p>
	 * Registered listeners are invoked in registration order before (
	 * {@link ViewChangeListener#beforeViewChange(ViewChangeEvent)
	 * beforeViewChange()}) and after (
	 * {@link ViewChangeListener#afterViewChange(ViewChangeEvent)
	 * afterViewChange()}) a view change occurs.
	 * 
	 * @param listener
	 *            Listener to invoke during a view change.
	 */
	@Override
	public void addViewChangeListener(V7ViewChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a view change listener.
	 * 
	 * @param listener
	 *            Listener to remove.
	 */
	@Override
	public void removeViewChangeListener(V7ViewChangeListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void uriFragmentChanged(UriFragmentChangedEvent event) {
		String uri = event.getPage().getUriFragment();
		navigateTo(uri != null ? uri : "");
	}

	@Override
	public String getCurrentUri() {
		return getCurrentNavigationState().getFragment().getUri();
	}

	public ScopedUI getUI() {
		/**
		 * TODO This should be injected, with a UIScoped UI!
		 */
		UI ui = CurrentInstance.get(UI.class);
		ScopedUI scopedUi = (ScopedUI) ui;
		return scopedUi;
	}

	/**
	 * When a user has successfully logged in, they are routed back to the page
	 * they were on before going to the login page. If they have gone straight
	 * to the login page (maybe they bookmarked it), they will be routed to the
	 * 'authenticated landing page' instead (see
	 * 
	 * @see uk.co.q3c.v7.base.navigate.V7Navigator#loginSuccessful()
	 */
	@Override
	public void loginSuccessful() {
		if (previousNavigationState != null
				&& !(previousNavigationState.getView() instanceof LoginView)) {
			assert previousNavigationState.getView().getUiComponent().getUI() == null : "the navigation state view should not be attached";
			navigateTo(previousNavigationState);
		} else {
			navigateTo(StandardPageKey.Private_Home);
		}
	}

	protected NavigationState getCurrentNavigationState() {
		return this.currentNavigationState;
	}

	protected void setCurrentNavigationState(NavigationState newNavigationState) {
		assert newNavigationState.getView().getUiComponent().getUI() == UI.getCurrent() : "the navigation state view must belong to the current UI";
		previousNavigationState = currentNavigationState;
		currentNavigationState = newNavigationState;

		getUI().getPage().setUriFragment(
				currentNavigationState.getFragment().getUri(), false);
	}

	@Override
	public void navigateTo(PageKey pageKey) {
		sitemapCheck();
		String uri = sitemap.pageUri(pageKey);
		if (uri == null) {
			throw new SiteMapException(pageKey + " cannot have a null path\n"
					+ sitemap.getReport());
		}
		navigateTo(uri);
	}

	private void sitemapCheck() {
		if (sitemap == null) {
			throw new SiteMapException("Sitemap has failed to load");
		}
	}

	@Override
	public void updateStatus() {
		Subject subject = subjectProvider.get();
		if (subject.isAuthenticated()) {
			loginSuccessful();
		}
	}

	@Override
	public void clearHistory() {
		previousNavigationState = null;
	}

	@Override
	public void navigateTo(SitemapNode node) {
		sitemapCheck();
		node = sitemap.getNode(node);

		if (node == null) {
			throw new SiteMapException(
					"The sitemap does not containt the node : " + node);
		}

		String uri = node.getUri();
		navigateTo(uri);
	}

	@Override
	public void error() {
		navigateTo(StandardPageKey.Error);
	}
}
