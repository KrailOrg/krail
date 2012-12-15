package uk.co.q3c.basic.guice.navigate;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.basic.URIHandler;
import uk.co.q3c.basic.view.SiteMap;

import com.google.inject.Injector;

/**
 * Provides a map of uri to View class - almost a site map. The way in which the URI is interpreted to represent a
 * virtual page (a View) and its parameters is determined by the {@link URIHandler} which is injected
 */
public class DefaultGuiceViewProvider implements GuiceViewProvider {
	private static Logger log = LoggerFactory.getLogger(DefaultGuiceViewProvider.class);

	private final Injector injector;
	private final URIHandler uriDecoder;
	private final SiteMap sitemap;

	@Inject
	protected DefaultGuiceViewProvider(SiteMap sitemap, Injector injector, URIHandler uriDecoder) {
		super();
		this.injector = injector;
		this.uriDecoder = uriDecoder;
		this.sitemap = sitemap;
	}

	/**
	 * Uses uriDecoder to provide interpretation of the URI. Returns just the view name after stripping out the
	 * parameters.
	 * 
	 * @see com.vaadin.navigator.ViewProvider#getViewName(java.lang.String)
	 */
	@Override
	public String getViewName(String viewAndParameters) {
		return uriDecoder.setNavigationState(viewAndParameters).virtualPage();

	}

	@Override
	public GuiceView getView(String viewName) {
		log.debug("instantiating " + viewName + " with Guice");
		Class<? extends GuiceView> clazz = sitemap.viewClassForName(viewName);
		if (clazz == null) {
			return injector.getInstance(sitemap.errorView());
		} else {
			GuiceView instance = injector.getInstance(clazz);
			return instance;
		}

	}

}
