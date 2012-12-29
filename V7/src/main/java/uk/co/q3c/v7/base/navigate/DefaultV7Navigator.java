package uk.co.q3c.v7.base.navigate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import uk.co.q3c.v7.demo.view.ErrorView;

import com.google.inject.Provider;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

public class DefaultV7Navigator implements V7Navigator {

	private V7View currentView = null;
	private final List<V7ViewChangeListener> listeners = new LinkedList<V7ViewChangeListener>();
	private final Provider<ErrorView> errorViewPro;
	private final URIFragmentHandler uriHandler;
	private final Map<String, Provider<V7View>> viewProMap;

	@Inject
	protected DefaultV7Navigator(Provider<ErrorView> errorViewPro, URIFragmentHandler uriHandler,
			Map<String, Provider<V7View>> viewProMap) {
		super();
		this.errorViewPro = errorViewPro;
		this.viewProMap = viewProMap;
		this.uriHandler = uriHandler;
	}

	@Override
	public void navigateTo(String fragment) {
		String viewName = uriHandler.setFragment(fragment).virtualPage();
		Provider<V7View> provider = viewProMap.get(viewName);
		V7View view = null;
		if (provider == null) {
			view = errorViewPro.get();
		} else {
			view = provider.get();
		}

		navigateTo(view, viewName, fragment);
		getUI().getPage().setUriFragment(fragment, false);

	}

	/**
	 * Internal method activating a view, setting its parameters and calling listeners.
	 * <p>
	 * This method also verifies that the user is allowed to perform the navigation operation.
	 * 
	 * @param view
	 *            view to activate
	 * @param viewName
	 *            (optional) name of the view or null not to change the navigation state
	 * @param parameters
	 *            parameters passed in the navigation state to the view
	 */
	protected void navigateTo(V7View view, String viewName, String fragment) {
		V7ViewChangeEvent event = new V7ViewChangeEvent(this, currentView, view, viewName, fragment);
		if (!fireBeforeViewChange(event)) {
			return;
		}

		getUI().changeView(currentView, view);
		view.enter(event);
		currentView = view;
		// ui.getPage().setUriFragment(newUriFragment, false);
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
	public List<String> geNavigationParams() {
		return uriHandler.parameterList();
	}

	public ScopedUI getUI() {
		/**
		 * This should be injected, with a UIScoped UI!
		 */
		UI ui = CurrentInstance.get(UI.class);
		ScopedUI scopedUi = (ScopedUI) ui;
		return scopedUi;
	}
}
