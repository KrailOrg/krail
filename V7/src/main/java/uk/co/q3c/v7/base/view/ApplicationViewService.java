package uk.co.q3c.v7.base.view;

import javax.inject.Provider;

import uk.co.q3c.v7.base.guice.services.Service;
import uk.co.q3c.v7.base.guice.services.Start;
import uk.co.q3c.v7.base.guice.services.Stop;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@Service
// (startAsSoonAsPossible = true)
public class ApplicationViewService {

	private final Provider<Sitemap> sitemapPro;

	// the sitemap will be created right after the injector, this way many
	// errors could be seen earlier than first use
	@Inject
	public ApplicationViewService(Provider<Sitemap> sitemapPro) {
		this.sitemapPro = sitemapPro;
	}

	@Start
	public void start() throws Exception {
		// the sitemap will be created right after the injector, this way many
		// errors could be seen earlier than first use
		sitemapPro.get();
	}

	@Stop
	public void stop() {
		;
	}

}