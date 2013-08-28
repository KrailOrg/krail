package uk.co.q3c.v7.base.view;

import uk.co.q3c.v7.base.navigate.NavigationState;
import uk.co.q3c.v7.base.navigate.V7Navigator;

/* the event could implement a suspend & resume mechanism to allow beforeViewChange to 
 * handle the navigation without have to save the parameters to re-initiate the navigation */

public class V7ViewChangeEvent {
	private final NavigationState oldNavigationState;
	private final V7View newView;
	private final String viewName;
	private final String parameters;
	private final V7Navigator navigator;

	public V7ViewChangeEvent(V7Navigator navigator, NavigationState oldNavigationState, V7View newView, String viewName, String parameters) {
		super();
		this.oldNavigationState = oldNavigationState;
		this.newView = newView;
		this.viewName = viewName;
		this.parameters = parameters;
		this.navigator = navigator;
	}

	public V7View getOldView() {
		return oldNavigationState.getView();
	}

	public V7View getNewView() {
		return newView;
	}

	public String getViewName() {
		return viewName;
	}

	public String getParameters() {
		return parameters;
	}

	public V7Navigator getNavigator() {
		return navigator;
	}

}
