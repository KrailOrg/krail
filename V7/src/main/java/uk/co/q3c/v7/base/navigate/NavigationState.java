package uk.co.q3c.v7.base.navigate;

import uk.co.q3c.v7.base.view.V7View;

public class NavigationState {
	private URIFragment fragment;
	private V7View view;
	
	public NavigationState(URIFragment uriFragment, V7View view) {
		this.fragment = uriFragment;
		this.view = view;
	}

	public V7View getView() {
		return view;
	}
	public void setView(V7View view) {
		this.view = view;
	}
	public URIFragment getFragment() {
		return fragment;
	}
	public void setFragment(URIFragment fragment) {
		this.fragment = fragment;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName()+"@"+fragment.getUri();
	}
}
