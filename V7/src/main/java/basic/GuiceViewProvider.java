package basic;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

/**
 * Provides a map of uri to View class - almost a site map. The way in which the URI is interpreted to represent a
 * virtual page (a View) and its parameters is determined by the {@link URIDecoder} which is injected
 */
public class GuiceViewProvider implements ViewProvider {
	private static final long serialVersionUID = -2197223852036965786L;
	private static Logger log = LoggerFactory.getLogger(GuiceViewProvider.class);

	static final Map<String, Class<? extends View>> viewMap = new HashMap<>();

	static {
		viewMap.put("view1", View1.class);
		viewMap.put("view2", View2.class);
		viewMap.put("", HomeView.class);
	}

	private final Injector injector;
	private final URIDecoder uriDecoder;

	@Inject
	protected GuiceViewProvider(Injector injector, URIDecoder uriDecoder) {
		super();
		this.injector = injector;
		this.uriDecoder = uriDecoder;
	}

	/**
	 * This uses a more strict interpretation of the URI than Vaadin does by default. Returns just the view name after
	 * stripping out the parameters.
	 * 
	 * @see com.vaadin.navigator.ViewProvider#getViewName(java.lang.String)
	 */
	@Override
	public String getViewName(String viewAndParameters) {
		return uriDecoder.setNavigationState(viewAndParameters).virtualPage();

	}

	@Override
	public View getView(String viewName) {
		log.debug("instantiating " + viewName + " with Guice");
		Class<? extends View> clazz = viewMap.get(viewName);
		assert (BasicFilter.getInjector().equals(injector));
		View instance = injector.getInstance(clazz);
		return instance;
	}

}
