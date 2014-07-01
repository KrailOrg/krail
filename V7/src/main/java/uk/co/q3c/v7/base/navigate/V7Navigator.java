package uk.co.q3c.v7.base.navigate;

import java.util.List;

import uk.co.q3c.v7.base.navigate.sitemap.MasterSitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.StandardPageKey;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.user.status.UserStatusListener;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;

import com.vaadin.server.Page.UriFragmentChangedListener;

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
 * login page, or perhaps to a specified landing page (Page here refers really to a V7View - a "virtual page"). <br>
 * <br>
 * The navigator must also respond to a change in user status (logged in or out) - logging out just navigates to the
 * logout page, while logging in applies some logic, see {@link #userStatusChanged()}
 *
 * @author David Sowerby 20 Jan 2013
 *
 */
public interface V7Navigator extends UriFragmentChangedListener, UserStatusListener {

	void navigateTo(String navigationState);

	/**
	 * A convenience method to look up the URI fragment for the {@link StandardPageKey} and navigate to it
	 *
	 * @param pageKey
	 */
	void navigateTo(StandardPageKey pageKey);

	NavigationState getCurrentNavigationState();

	List<String> getNavigationParams();

	void addViewChangeListener(V7ViewChangeListener listener);

	void removeViewChangeListener(V7ViewChangeListener listener);

	/**
	 * Removes any historical navigation state
	 */
	void clearHistory();

	V7View getCurrentView();

	/**
	 * Navigate to the error view. It is assumed that the view has already been set up with error information, usually
	 * via the V7ErrorHandler
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
