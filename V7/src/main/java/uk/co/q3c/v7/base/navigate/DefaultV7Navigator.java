package uk.co.q3c.v7.base.navigate;

import java.util.LinkedList;
import java.util.List;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapException;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapService;
import uk.co.q3c.v7.base.shiro.PageAccessController;
import uk.co.q3c.v7.base.shiro.SubjectProvider;
import uk.co.q3c.v7.base.shiro.UnauthorizedExceptionHandler;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.view.ErrorView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@UIScoped
public class DefaultV7Navigator implements V7Navigator {

	private static Logger log = LoggerFactory.getLogger(DefaultV7Navigator.class);

	private final List<V7ViewChangeListener> listeners = new LinkedList<V7ViewChangeListener>();
	private final Provider<ErrorView> errorViewProvider;
	private final URIFragmentHandler uriHandler;

	private final Sitemap sitemap;
	private final Provider<Subject> subjectProvider;
	private final Injector injector;
	private NavigationState currentNavigationState;
	private NavigationState previousNavigationState;
	private SitemapNode previousNode;
	private SitemapNode currentNode;
	private V7View currentView = null;

	private V7View previousView;

	private final PageAccessController pageAccessController;

	@Inject
	protected DefaultV7Navigator(Injector injector, Provider<ErrorView> errorViewProvider,
			URIFragmentHandler uriHandler, SitemapService sitemapService, SubjectProvider subjectProvider,
			PageAccessController pageAccessController) {
		super();
		this.errorViewProvider = errorViewProvider;
		this.uriHandler = uriHandler;

		this.subjectProvider = subjectProvider;
		this.injector = injector;
		this.pageAccessController = pageAccessController;

		try {
			sitemapService.start();
			sitemap = sitemapService.getSitemap();
		} catch (Exception e) {
			String msg = "Sitemap service failed to start, application will have no pages";
			log.error(msg);
			throw new IllegalStateException(msg, e);
		}

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
		// set up the navigation state
		NavigationState navigationState = uriHandler.navigationState(fragment);
		navigateTo(navigationState);
	}

	/**
	 * Checks {@code fragment} to see whether the {@link Sitemap} defines this as a page which should be redirected. If
	 * it is, the full fragment is returned, but modified for the redirected page. If not, the {@code fragment} is
	 * returned unchanged.
	 * 
	 * @param fragment
	 * @return
	 */
	private NavigationState redirectIfNeeded(NavigationState navigationState) {
		// sitemapCheck();

		String page = navigationState.getVirtualPage();
		String redirection = sitemap.getRedirectPageFor(page);
		// if no redirect found, page is returned
		if (redirection == page) {
			return navigationState;
		} else {
			navigationState.setVirtualPage(redirection);
			navigationState.setFragment(uriHandler.fragment(navigationState));
			return navigationState;
		}
	}

	/**
	 * Internal method activating a view, setting its parameters and calling listeners.
	 * 
	 * @param view
	 *            view to activate
	 * @param navigationState
	 *            the object representing the navigation state which will be correct when the change of view is
	 *            completed
	 * 
	 */
	private void changeView(V7View view) {
		V7ViewChangeEvent event = new V7ViewChangeEvent(view, currentNavigationState);
		if (!fireBeforeViewChange(event)) {
			return;
		}
		getUI().changeView(view);
		view.enter(event);

		previousView = currentView;
		currentView = view;

		getUI().getPage().setUriFragment(sitemap.uri(currentNode), false);
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
	public NavigationState getCurrentNavigationState() {
		return currentNavigationState;
	}

	@Override
	public List<String> getNavigationParams() {
		return currentNavigationState.getParameterList();
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
	 * page. If they have gone straight to the login page (maybe they bookmarked it), or they were on the logout page,
	 * they will be routed to the 'private home page' (the StandardPage for StandardPageKey_Private_Home)
	 * 
	 * @see uk.co.q3c.v7.base.navigate.V7Navigator#loginSuccessful()
	 */
	@Override
	public void loginSuccessful() {
		log.debug("user logged in successfully, navigating to appropriate view");
		if (previousNode != null && previousNode != sitemap.standardPageNode(StandardPageKey.Logout)) {
			navigateTo(previousNavigationState);
		} else {
			navigateTo(StandardPageKey.Private_Home);
		}
	}

	@Override
	public V7View getCurrentView() {
		return currentView;
	}

	@Override
	public void navigateTo(StandardPageKey pageKey) {
		// sitemapCheck();
		String page = sitemap.standardPageURI(pageKey);
		if (page == null) {
			throw new SitemapException(pageKey + " cannot have a null path\n" + sitemap.getReport());
		}
		navigateTo(page);
	}

	// private void sitemapCheck() {
	// if (sitemap == null) {
	// throw new SitemapException("Sitemap has failed to load");
	// }
	// if (sitemap.hasErrors()) {
	// throw new SitemapException("Unable to navigate, site map has errors\n" + sitemap.getReport());
	// }
	// }

	@Override
	public void loginStatusChange(boolean authenticated, Subject subject) {
		if (authenticated) {
			loginSuccessful();
		}
	}

	/**
	 * Returns the Sitemap node representing the previous position of the navigator
	 * 
	 * @return
	 */
	public NavigationState getPreviousNavigationState() {
		return previousNavigationState;
	}

	@Override
	public void clearHistory() {
		previousNode = null;
		previousView = null;
		previousNavigationState = null;
	}

	/**
	 * Navigates to a the location represented by {@code node}, instantiating a View, and calling for the view to be
	 * made current via {@link #changeView(V7View)}. If the user is not authorised, a {@link AuthorizationException} is
	 * thrown. This would be caught by the the implementation bound to {@link UnauthorizedExceptionHandler}.
	 * <p>
	 * 
	 * @param node
	 *            The node to navigate to. The node is assumed to be valid, and no check is made for redirects. A node
	 *            is not, however, a complete navigation reference, as it contains no parameters. These are provided by
	 *            the {@link #navigationState}.
	 * @see uk.co.q3c.v7.base.navigate.V7Navigator#navigateTo(uk.co.q3c.v7.base.navigate.sitemap.SitemapNode)
	 */

	private void navigateTo(SitemapNode node, NavigationState navigationState) {
		// get a view instance
		Class<? extends V7View> viewClass = node.getViewClass();
		V7View view = injector.getInstance(viewClass);

		Subject subject = subjectProvider.get();
		boolean authorised = pageAccessController.isAuthorised(subject, node);
		if (authorised) {
			previousNode = currentNode;
			previousNavigationState = currentNavigationState;

			currentNode = node;
			currentNavigationState = navigationState;
			changeView(view);
		} else {
			throw new UnauthorizedException(navigationState.getVirtualPage());
		}

		// check permissions, raise exception if not allowed
		// URIViewPermission permission = new URIViewPermission(navigationState);
		// if (subjectProvider.get().isPermitted(permission)) {
		//
		// } else {
		//
		// }

	}

	/**
	 * 
	 @see uk.co.q3c.v7.base.navigate.V7Navigator#navigateTo(uk.co.q3c.v7.base.navigate.sitemap.SitemapNode)
	 */
	@Override
	public void navigateTo(SitemapNode node) {
		NavigationState navigationState = uriHandler.navigationState(sitemap.uri(node));
		navigateTo(node, navigationState);
	}

	@Override
	public void navigateTo(NavigationState navigationState) {

		String fragment = navigationState.getFragment();
		// this is partly to stop unnecessary changes, but also to prevent UserNavigationTree and other navigation aware
		// components from causing a loop by responding to a change of URI
		if ((fragment != null) && (currentNavigationState != null)
				&& (fragment.equals(currentNavigationState.getFragment()))) {
			log.debug("fragment unchanged, no navigation required");
			return;
		}

		// sitemapCheck();

		// https://sites.google.com/site/q3cjava/sitemap#emptyURI
		if (navigationState.getVirtualPage().isEmpty()) {
			navigationState.setVirtualPage(sitemap.standardPageURI(StandardPageKey.Public_Home));
			uriHandler.updateFragment(navigationState);
		}

		// fragment needs to be revised if redirected
		redirectIfNeeded(navigationState);

		log.debug("fragment after redirect check is {}", navigationState.getFragment());
		String viewKey = navigationState.getVirtualPage();
		log.debug("looking up View for page '{}'", viewKey);

		SitemapNode nodeForUri = sitemap.nodeFor(navigationState);
		if (nodeForUri == null) {
			String msg = "Sitemap node not found for page '" + navigationState.getVirtualPage() + "'";
			log.debug(msg);
			throw new InvalidURIException(msg);
		}

		navigateTo(nodeForUri, navigationState);

	}

	@Override
	public void error() {
		changeView(errorViewProvider.get());
	}

	@Override
	public SitemapNode getCurrentNode() {
		return currentNode;
	}

	public V7View getPreviousView() {
		return previousView;
	}

}
