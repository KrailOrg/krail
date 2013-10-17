package uk.co.q3c.v7.base.view;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import uk.co.q3c.v7.base.guice.services.Service;
import uk.co.q3c.v7.base.guice.services.Start;
import uk.co.q3c.v7.base.guice.services.Stop;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;

@Singleton
@Service
public class ApplicationViewService {

	private final Provider<Sitemap> sitemapProvider;
	
	@Inject
	public ApplicationViewService(Provider<Sitemap> sitemapProvider) {
		this.sitemapProvider = sitemapProvider;
	}

	@Start
	public void start() throws Exception {
		// the sitemap will be created right after the injector, this way many
		// errors could be seen earlier than first use
		sitemapProvider.get();
	}

	@Stop
	public void stop() {
		;
	}

}
