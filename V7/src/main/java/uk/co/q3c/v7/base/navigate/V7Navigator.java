package uk.co.q3c.v7.base.navigate;

import java.util.List;

import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;

import com.vaadin.server.Page.UriFragmentChangedListener;

/**
 * Looks up the view for the supplied URI, and calls on {@link ScopedUI} to present that view. Listeners are notified
 * before and after a change of view occurs. The {@link #loginSuccessful()} method is called after a successful user
 * login - this allows the navigator to change views appropriate (according to the implementation). Typically this would
 * be to either return to the view where the user was before they went to the login page, or perhaps to a specified
 * landing page (Page here refers really to a V7View - a "virtual page").
 * 
 * @author David Sowerby 20 Jan 2013
 * 
 */
public interface V7Navigator extends UriFragmentChangedListener {

	public void navigateTo(String navigationState);

	/**
	 * A convenience method to look up the URI fragment for the {@link PageKey} and navigate to it
	 * 
	 * @param pageKey
	 */
	public void navigateTo(PageKey pageKey);

	public String getCurrentUri();

	public void addViewChangeListener(V7ViewChangeListener listener);

	public void removeViewChangeListener(V7ViewChangeListener listener);

	/**
	 * A signal to the navigator that a login has been successful. The implementation defines which view should be
	 * switched to, but typically the view is changed from the {@link LoginView} to the one the user was at before
	 * requesting a log in, or to a "landing page" view.
	 */
	public void loginSuccessful();

	/**
	 * Removes any historical navigation state
	 */
	public void clearHistory();

	@Deprecated
	public void navigateTo(SitemapNode node);

	/**
	 * Navigate to the error view. It is assumed that the view has already been set up with error information, usually
	 * via the V7ErrorHandler
	 */
	public void error();

}
