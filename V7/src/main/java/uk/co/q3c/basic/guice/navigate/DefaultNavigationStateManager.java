package uk.co.q3c.basic.guice.navigate;

import javax.inject.Inject;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;

/**
 * A {@link NavigationStateManager} using hashbang fragments in the Page location URI to track views and enable
 * listening to view changes.
 * <p>
 * A hashbang URI is one where the optional fragment or "hash" part - the part following a # sign - is used to encode
 * navigation state in a web application. The advantage of this is that the fragment can be dynamically manipulated by
 * javascript without causing page reloads.
 * <p>
 * This class is mostly for internal use by Navigator, and is only public and static to enable testing.
 */
public class DefaultNavigationStateManager implements GuiceNavigationStateManager, UriFragmentChangedListener {

	private final Page page;
	private final GuiceNavigator navigator;

	/**
	 * Creates a new URIFragmentManager and attach it to listen to URI fragment changes of a {@link Page}.
	 * 
	 * @param page
	 *            page whose URI fragment to get and modify
	 */
	@Inject
	public DefaultNavigationStateManager(GuiceNavigator navigator) {
		this.page = Page.getCurrent();
		page.addUriFragmentChangedListener(this);
		this.navigator = navigator;
	}

	@Override
	public String getState() {
		String fragment = getFragment();
		if (fragment == null || !fragment.startsWith("!")) {
			return "";
		} else {
			return fragment.substring(1);
		}
	}

	@Override
	public void uriFragmentChanged(UriFragmentChangedEvent event) {
		navigator.navigateTo(getState());
	}

	/**
	 * Returns the current URI fragment tracked by this UriFragentManager.
	 * 
	 * @return The URI fragment.
	 */
	protected String getFragment() {
		return page.getUriFragment();
	}

	/**
	 * Sets the URI fragment to the given string.
	 * 
	 * @param fragment
	 *            The new URI fragment.
	 */
	protected void setFragment(String fragment) {
		page.setUriFragment(fragment, false);
	}
}
