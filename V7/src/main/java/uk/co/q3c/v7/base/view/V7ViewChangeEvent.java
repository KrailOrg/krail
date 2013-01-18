package uk.co.q3c.v7.base.view;

import uk.co.q3c.v7.base.navigate.V7Navigator;

public class V7ViewChangeEvent {
	private final V7View oldView;
	private final V7View newView;
	private final String viewName;
	private final String parameters;
	private final V7Navigator navigator;

	public V7ViewChangeEvent(V7Navigator navigator, V7View oldView, V7View newView, String viewName, String parameters) {
		super();
		this.oldView = oldView;
		this.newView = newView;
		this.viewName = viewName;
		this.parameters = parameters;
		this.navigator = navigator;
	}

	public V7View getOldView() {
		return oldView;
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
