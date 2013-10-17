package uk.co.q3c.v7.base.view;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class ViewsProvider {
	
	private final Injector injector;
	
	@Inject
	public ViewsProvider(Injector injector) {
		this.injector = injector;
	}
	
	public V7View get(Class<? extends V7View> type){
		return injector.getInstance(type);
	}
}
