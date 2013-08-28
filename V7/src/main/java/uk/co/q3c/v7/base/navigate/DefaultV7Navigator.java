package uk.co.q3c.v7.base.navigate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.shiro.LoginStatusListener;
import uk.co.q3c.v7.base.shiro.URIPermissionFactory;
import uk.co.q3c.v7.base.shiro.URIViewPermission;
import uk.co.q3c.v7.base.shiro.UnauthorizedExceptionHandler;
import uk.co.q3c.v7.base.shiro.V7SecurityManager;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.view.ErrorView;
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
	private NavigationState currentNavigationState = new NavigationState(null, null, null);
	private final List<V7ViewChangeListener> listeners = new LinkedList<V7ViewChangeListener>();
	private final Provider<ErrorView> errorViewPro;
	private final URIFragmentHandler uriHandler;
	private final Map<String, Provider<V7View>> viewProvidersMap;

	private final Provider<Subject> subjectProvider;

	@Inject
	protected DefaultV7Navigator(Provider<ErrorView> errorViewPro,
			URIFragmentHandler uriHandler, Sitemap sitemap,
			Map<String, Provider<V7View>> viewProMap,
			V7SecurityManager securityManager,
			Provider<Subject> securityContext) {
		super();
		this.errorViewPro = errorViewPro;
		this.viewProvidersMap = viewProMap;
		this.uriHandler = uriHandler;
		this.sitemap = sitemap;
		this.subjectProvider = securityContext;
		securityManager.addListener(this);
	}

	/**
	 * Takes a URI fragment, checks for any redirects defined by the
	 * {@link Sitemap}, then calls {@link #navigateTo(V7View, String, String)}
	 * to change the view
	 * 
	 * @see uk.co.q3c.v7.base.navigate.V7Navigator#navigateTo(java.lang.String)
	 */
	@Override
	public void navigateTo(String uri) {

		assert uri != null;

		log.debug("Navigating to uri: {}", uri);
		sitemapCheck();
		if (sitemap.hasErrors()) {
			throw new SiteMapException(
					"Unable to navigate, site map has errors\n"
							+ sitemap.getReport());
		}

		sitemapCheck();
		uriHandler.setFragment(uri);

		// fragment needs to be revised if redirected
		String revisedUri = checkRedirects(uri);
		log.debug("fragment after redirect check is {}", revisedUri);

		String viewUriFragment = sitemap.uri(uriHandler.virtualPage());
		log.debug("page to look up View is {}", viewUriFragment);
		Provider<V7View> provider = viewProvidersMap.get(viewUriFragment);
		V7View view = null;
		if (provider == null) {
			String msg = "View not found for page '" + revisedUri + "'";
			log.debug(msg);
			throw new InvalidURIException(msg);
		} else {
			view = provider.get();
		}

		navigateTo(view, viewUriFragment, revisedUri);
	}

	/**
	 * Checks {@code fragment} to see whether it has been redirected. If it has
	 * the full fragment is returned, but modified for the redirected page. If
	 * not, the {@code fragment} is returned unchanged.
	 * 
	 * @param fragment
	 * @return
	 */
	private String checkRedirects(String fragment) {
		String page = uriHandler.virtualPage();
		String redirection = sitemap.getRedirectFor(page);
		// if no redirect found, page is returned
		if (redirection == page) {
			return fragment;
		} else {
			String newFragment = fragment.replaceFirst(page, redirection);
			uriHandler.setFragment(newFragment);
			return newFragment;
		}
	}
	
	protected void navigateTo(NavigationState navigationState) {
		navigateTo(navigationState.getView(), navigationState.getViewName(), navigationState.getUri());
	}

	/**
	 * Navigates to a view, setting its parameters and calling listeners. If a
	 * page is public then any user (even unauthenticated) can navigate to it.
	 * If it is not public then permissions are checked, and if the user is not
	 * authorised, a {@link AuthorizationException} is thrown. This would be
	 * caught by the the implementation bound to
	 * {@link UnauthorizedExceptionHandler}
	 * 
	 * @param view
	 *            view to activate
	 * @param viewName
	 *            (optional) name of the view or null not to change the
	 *            navigation state
	 * @param uri
	 *            parameters passed in the navigation state to the view. In this
	 *            context, the parameters are all the parameters, which include
	 *            the part which forms the pseudo URI. For example,
	 *            private/transfers/id=23
	 */
	protected void navigateTo(V7View view, String viewName, String uri) {

		try {
			sitemap.checkPermissions(uri, subjectProvider.get());
		} catch (UnauthorizedException e) {
			throw new UnauthorizedException(uri);
		} catch (UnauthenticatedException e) {
			throw new UnauthenticatedException(uri);
		}

		// is permitted, proceed
		changeView(view, viewName, uri);
	}

	/**
	 * Internal method activating a view, setting its parameters and calling
	 * listeners.
	 * 
	 * @param view
	 *            view to activate
	 * @param viewName
	 *            (optional) name of the view or null not to change the
	 *            navigation state
	 * @param uri
	 *            parameters passed in the navigation state to the view. In this
	 *            context, the parameters are all the parameters, which include
	 *            the part which forms the pseudo URI. For example,
	 *            private/transfers/id=23
	 */
	private void changeView(V7View view, String viewName, String uri) {
		V7ViewChangeEvent event = new V7ViewChangeEvent(this, currentNavigationState,
				view, viewName, uri);
		if (!fireBeforeViewChange(event)) {
			//aborted
			return;
		}
		getUI().changeView(currentNavigationState.getView(), view);
		view.enter(event);

		setCurrentView(view, viewName, uri);
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
		navigateTo(event.getPage().getUriFragment());
	}

	@Override
	public String getNavigationState() {
		return uriHandler.fragment();
	}

	@Override
	public List<String> getNavigationParams() {
		return uriHandler.parameterList();
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
		if (previousNavigationState != null) {
			navigateTo(previousNavigationState);
		} else {
			navigateTo(StandardPageKey.Private_Home);
		}
	}

	@Override
	public V7View getCurrentView() {
		return currentNavigationState.getView();
	}

	public V7View getPreviousView() {
		return previousNavigationState.getView();
	}

	protected void setCurrentView(V7View newView, String viewName,
			String fragment) {
		previousNavigationState = currentNavigationState;
		currentNavigationState = new NavigationState(newView, viewName, fragment);

		uriHandler.setFragment(fragment);
		getUI().getPage().setUriFragment(fragment, false);
	}

	protected void setPreviousView(V7View previousView) {
		this.previousNavigationState.setView(previousView);
	}

	public String getPreviousViewName() {
		return previousNavigationState.getViewName();
	}

	public String getCurrentViewName() {
		return currentNavigationState.getViewName();
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

	public String getPreviousFragment() {
		return previousNavigationState.getUri();
	}

	@Override
	public void clearHistory() {
		previousNavigationState = null;
	}

	@Override
	public void navigateTo(SitemapNode node) {
		sitemapCheck();
		node = sitemap.getNode(node);
		
		if(node == null){
			throw new SiteMapException("The sitemap does not containt the node : "+ node);
		}
		
		String uri = node.getUri();
		navigateTo(uri);
	}

	@Override
	public void error() {
		changeView(errorViewPro.get(), "ErrorView", "error");
	}

}
