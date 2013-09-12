package uk.co.q3c.v7.base.view;

import uk.co.q3c.v7.base.navigate.NavigationState;
import uk.co.q3c.v7.base.navigate.V7Navigator;

/* the event could implement a suspend & resume mechanism to allow beforeViewChange to 
 * handle the navigation without have to save the parameters to re-initiate the navigation */

public class V7ViewChangeEvent {
	private final V7Navigator navigator;
	private final NavigationState oldNavigationState;
	private final NavigationState newNavigationState;	

	public V7ViewChangeEvent(V7Navigator navigator, NavigationState oldNavigationState, NavigationState newNavigationState) {
		super();
		this.navigator = navigator;
		this.oldNavigationState = oldNavigationState;
		this.newNavigationState = newNavigationState;
	}

	public V7Navigator getNavigator() {
		return navigator;
	}

	public NavigationState getOldNavigationState() {
		return oldNavigationState;
	}

	public NavigationState getNewNavigationState() {
		return newNavigationState;
	}

}
