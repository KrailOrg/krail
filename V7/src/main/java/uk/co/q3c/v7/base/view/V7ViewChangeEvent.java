package uk.co.q3c.v7.base.view;

import uk.co.q3c.v7.base.navigate.NavigationState;

public class V7ViewChangeEvent {
	private final NavigationState navigationState;

	public V7ViewChangeEvent(NavigationState navigationState) {
		this.navigationState = navigationState;
	}

	public NavigationState getNavigationState() {
		return navigationState;
	}

}
