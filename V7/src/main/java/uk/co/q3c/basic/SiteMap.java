package uk.co.q3c.basic;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import com.vaadin.navigator.View;

@Singleton
public class SiteMap {

	private static final Map<String, Class<? extends View>> viewMap = new HashMap<>();

	static {
		viewMap.put("view1", View1.class);
		viewMap.put("view2", View2.class);
		viewMap.put("", HomeView.class);
	}

	public Class<? extends View> viewClassForName(String name) {
		return viewMap.get(name);
	}

}
