package uk.co.q3c.v7.base.view;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.q3c.v7.base.guice.services.Service;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;

import com.google.inject.Provider;

@Singleton
public class ApplicationViewService implements Service {

	private final Provider<Sitemap> sitemapPro;

	// the sitemap will be created right after the injector, this way many
	// errors could be seen earlier than first use
	@Inject
	public ApplicationViewService(Provider<Sitemap> sitemapPro) {
		this.sitemapPro = sitemapPro;
	}

	@Override
	public void start() {
		// the sitemap will be created right after the injector, this way many
		// errors could be seen earlier than first use
		sitemapPro.get();
	}

	@Override
	public void stop() {
		;
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "Appplication View Service";
	}

}