package uk.co.q3c.v7.base.view;

import uk.co.q3c.v7.base.navigate.NavigationState;

public class V7ViewChangeEvent {
	private final V7View newView;
	private final NavigationState navigationState;

	public V7ViewChangeEvent(V7View newView, NavigationState navigationState) {
		this.newView = newView;
		this.navigationState = navigationState;
	}

	public V7View getNewView() {
		return newView;
	}

	public NavigationState getNavigationState() {
		return navigationState;
	}

}
