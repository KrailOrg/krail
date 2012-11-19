package basic;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.google.inject.Injector;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

public class GuiceViewProvider implements ViewProvider {
	static final Map<String, Class<? extends View>> viewMap = new HashMap<>();

	static {
		viewMap.put("view1", View1.class);
		viewMap.put("view2", View2.class);
	}

	private final Injector injector;

	@Inject
	protected GuiceViewProvider(Injector injector) {
		super();
		this.injector = injector;
	}

	@Override
	public String getViewName(String viewAndParameters) {

		if (viewAndParameters.startsWith("view1")) {
			return "view1";
		}

		if (viewAndParameters.startsWith("view2")) {
			return "view2";
		}
		return null;

	}

	@Override
	public View getView(String viewName) {
		System.out.println("instantiating " + viewName + " with Guice");
		Class<? extends View> clazz = viewMap.get(viewName);
		assert (BasicFilter.getInjector().equals(injector));
		View instance = injector.getInstance(clazz);
		return instance;
	}

}
