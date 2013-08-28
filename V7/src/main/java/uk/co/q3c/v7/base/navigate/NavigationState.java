package uk.co.q3c.v7.base.navigate;

import uk.co.q3c.v7.base.view.V7View;

public class NavigationState {
	private V7View view;
	private String viewName;
	private String uri;
		
	public NavigationState(V7View view, String viewName, String uri) {
		super();
		this.view = view;
		this.viewName = viewName;
		this.uri = uri;
	}
	
	public V7View getView() {
		return view;
	}
	public void setView(V7View view) {
		this.view = view;
	}
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
}
