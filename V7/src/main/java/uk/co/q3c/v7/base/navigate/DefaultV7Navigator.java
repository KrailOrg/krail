package uk.co.q3c.v7.base.navigate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.shiro.authz.AuthorizationException;
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

	private static Logger log = LoggerFactory.getLogger(DefaultV7Navigator.class);
	private String previousViewName = null;
	private V7View previousView = null;
	private String currentViewName = null;
	private V7View currentView = null;
	private final List<V7ViewChangeListener> listeners = new LinkedList<V7ViewChangeListener>();
	private final Provider<ErrorView> errorViewPro;
	private final URIFragmentHandler uriHandler;
	private final Map<String, Provider<V7View>> viewProMap;
	private String previousFragment;
	private String currentFragment;
	private final Sitemap sitemap;
	private final Provider<Subject> subjectPro;
	private final URIPermissionFactory uriPermissionFactory;
	private final SitemapURIConverter sitemapURIConverter;
	private boolean respondToLoginStatusChange;

	@Inject
	protected DefaultV7Navigator(Provider<ErrorView> errorViewPro, URIFragmentHandler uriHandler, Sitemap sitemap,
			Map<String, Provider<V7View>> viewProMap, V7SecurityManager securityManager, Provider<Subject> subjectPro,
			URIPermissionFactory uriPermissionFactory, SitemapURIConverter sitemapURIConverter) {
		super();
		this.errorViewPro = errorViewPro;
		this.viewProMap = viewProMap;
		this.uriHandler = uriHandler;
		this.sitemap = sitemap;
		this.subjectPro = subjectPro;
		this.uriPermissionFactory = uriPermissionFactory;
		this.sitemapURIConverter = sitemapURIConverter;
		securityManager.addListener(this);
	}

	/**
	 * Takes a URI fragment, checks for any redirects defined by the {@link Sitemap}, then calls
	 * {@link #navigateTo(V7View, String, String)} to change the view
	 * 
	 * @see uk.co.q3c.v7.base.navigate.V7Navigator#navigateTo(java.lang.String)
	 */
	@Override
	public void navigateTo(String fragment) {
		log.debug("Navigating to fragment: {}", fragment);
		sitemapCheck();
		if (sitemap.hasErrors()) {
			throw new SiteMapException("Unable to navigate, site map has errors\n" + sitemap.getReport());
		}

		// fragment needs to be revised if redirected
		String revisedFragment = null;
		if (fragment == null) {
			revisedFragment = checkRedirects("");
		} else {
			revisedFragment = checkRedirects(fragment);
		}

		log.debug("fragment after redirect check is {}", revisedFragment);
		String viewName = uriHandler.virtualPage();
		log.debug("page to look up View is {}", viewName);
		Provider<V7View> provider = viewProMap.get(viewName);
		V7View view = null;
		if (provider == null) {
			String msg = "View not found for page '" + revisedFragment + "'";
			log.debug(msg);
			throw new InvalidURIException(msg);
		} else {
			view = provider.get();
		}

		navigateTo(view, viewName, revisedFragment);

	}

	/**
	 * Checks {@code fragment} to see whether it has been redirected. If it has the full fragment is returned, but
	 * modified for the redirected page. If not, the {@code fragment} is returned unchanged.
	 * 
	 * @param fragment
	 * @return
	 */
	private String checkRedirects(String fragment) {
		sitemapCheck();
		uriHandler.setFragment(fragment);
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

	/**
	 * Navigates to a view, setting its parameters and calling listeners. If a page is public then any user (even
	 * unauthenticated) can navigate to it. If it is not public then permissions are checked, and if the user is not
	 * authorised, a {@link AuthorizationException} is thrown. This would be caught by the the implementation bound to
	 * {@link UnauthorizedExceptionHandler}
	 * 
	 * @param view
	 *            view to activate
	 * @param viewName
	 *            (optional) name of the view or null not to change the navigation state
	 * @param fragment
	 *            parameters passed in the navigation state to the view. In this context, the parameters are all the
	 *            parameters, which include the part which forms the pseudo URI. For example, private/transfers/id=23
	 */
	protected void navigateTo(V7View view, String viewName, String fragment) {
		boolean publicPage = sitemapURIConverter.pageIsPublic(fragment);

		// if page is public don't check permissions as they will fail!
		if (publicPage) {
			changeView(view, viewName, fragment);
			return;
		}

		// check permissions, raise exception if not allowed
		URIViewPermission permission = uriPermissionFactory.createViewPermission(fragment);
		if (subjectPro.get().isPermitted(permission)) {
			changeView(view, viewName, fragment);
		} else {
			throw new UnauthorizedException(fragment);
		}

	}

	/**
	 * Internal method activating a view, setting its parameters and calling listeners.
	 * 
	 * @param view
	 *            view to activate
	 * @param viewName
	 *            (optional) name of the view or null not to change the navigation state
	 * @param fragment
	 *            parameters passed in the navigation state to the view. In this context, the parameters are all the
	 *            parameters, which include the part which forms the pseudo URI. For example, private/transfers/id=23
	 */
	private void changeView(V7View view, String viewName, String fragment) {
		V7ViewChangeEvent event = new V7ViewChangeEvent(this, currentView, view, viewName, fragment);
		if (!fireBeforeViewChange(event)) {
			return;
		}
		getUI().changeView(currentView, view);
		view.enter(event);

		// we don't want to record being at the login page
		// if (!(view instanceof LoginView)) {
		setCurrentView(view, viewName, fragment);
		// }
		fireAfterViewChange(event);
	}

	/**
	 * Fires an event before an imminent view change.
	 * <p>
	 * Listeners are called in registration order. If any listener returns <code>false</code>, the rest of the listeners
	 * are not called and the view change is blocked.
	 * <p>
	 * The view change listeners may also e.g. open a warning or question dialog and save the parameters to re-initiate
	 * the navigation operation upon user action.
	 * 
	 * @param event
	 *            view change event (not null, view change not yet performed)
	 * @return true if the view change should be allowed, false to silently block the navigation operation
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
	 * {@link ViewChangeListener#beforeViewChange(ViewChangeEvent) beforeViewChange()}) and after (
	 * {@link ViewChangeListener#afterViewChange(ViewChangeEvent) afterViewChange()}) a view change occurs.
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
	 * When a user has successfully logged in, they are routed back to the page they were on before going to the login
	 * page. If they have gone straight to the login page (maybe they bookmarked it), they will be routed to the
	 * 'authenticated landing page' instead (see
	 * 
	 * @see uk.co.q3c.v7.base.navigate.V7Navigator#loginSuccessful()
	 */
	@Override
	public void loginSuccessful() {
		respondToLoginStatusChange = false;
		if (previousView != null) {
			navigateTo(previousView, previousViewName, previousFragment);
		} else {
			navigateTo(StandardPageKey.Private_Home);
		}
		respondToLoginStatusChange = true;
	}

	@Override
	public V7View getCurrentView() {
		return currentView;
	}

	public V7View getPreviousView() {
		return previousView;
	}

	protected void setCurrentView(V7View newView, String viewName, String fragment) {
		previousView = currentView;
		previousViewName = currentViewName;
		previousFragment = currentFragment;
		currentView = newView;
		currentViewName = viewName;
		currentFragment = fragment;

		uriHandler.setFragment(fragment);
		getUI().getPage().setUriFragment(fragment, false);
	}

	protected void setPreviousView(V7View previousView) {
		this.previousView = previousView;
	}

	public String getPreviousViewName() {
		return previousViewName;
	}

	public String getCurrentViewName() {
		return currentViewName;
	}

	@Override
	public void navigateTo(StandardPageKey pageKey) {
		sitemapCheck();
		String page = sitemap.standardPageURI(pageKey);
		if (page == null) {
			throw new SiteMapException(pageKey + " cannot have a null path\n" + sitemap.getReport());
		}
		navigateTo(page);
	}

	private void sitemapCheck() {
		if (sitemap == null) {
			throw new SiteMapException("Sitemap has failed to load");
		}
	}

	@Override
	public void updateStatus() {
		Subject subject = subjectPro.get();
		if (subject.isAuthenticated()) {
			loginSuccessful();
		}
	}

	public String getPreviousFragment() {
		return previousFragment;
	}

	@Override
	public void clearHistory() {
		previousView = null;
		previousViewName = null;
		previousFragment = null;
	}

	@Override
	public void navigateTo(SitemapNode node) {
		sitemapCheck();
		String url = sitemap.uri(node);
		navigateTo(url);
	}

	@Override
	public void error() {
		changeView(errorViewPro.get(), "ErrorView", "error");
	}

}
