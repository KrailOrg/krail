package uk.co.q3c.basic.view;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import uk.co.q3c.basic.guice.navigate.GuiceView;

@Singleton
public class SiteMap {

	private static final Map<String, Class<? extends GuiceView>> viewMap = new HashMap<>();

	static {
		viewMap.put("view1", View1.class);
		viewMap.put("view2", View2.class);
		viewMap.put("", HomeView.class);
	}

	public Class<? extends GuiceView> viewClassForName(String name) {
		return viewMap.get(name);
	}

	public Class<? extends GuiceView> errorView() {
		return DemoErrorView.class;
	}

}
