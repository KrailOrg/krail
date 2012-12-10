package uk.co.q3c.basic.guice.navigate;


public class GuiceViewChangeEvent {
	private final GuiceView oldView;
	private final GuiceView newView;
	private final String viewName;
	private final String parameters;
	private final GuiceNavigator navigator;

	protected GuiceViewChangeEvent(GuiceNavigator navigator, GuiceView oldView, GuiceView newView, String viewName,
			String parameters) {
		super();
		this.oldView = oldView;
		this.newView = newView;
		this.viewName = viewName;
		this.parameters = parameters;
		this.navigator = navigator;
	}

	public GuiceView getOldView() {
		return oldView;
	}

	public GuiceView getNewView() {
		return newView;
	}

	public String getViewName() {
		return viewName;
	}

	public String getParameters() {
		return parameters;
	}

	public GuiceNavigator getNavigator() {
		return navigator;
	}

}
