package uk.co.q3c.v7.base.view;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.q3c.v7.base.guice.services.AbstractService;
import uk.co.q3c.v7.base.guice.services.Service;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;

import com.google.inject.Provider;

@Singleton
public class ApplicationViewService extends AbstractService {

	private final Provider<Sitemap> sitemapPro;

	// the sitemap will be created right after the injector, this way many
	// errors could be seen earlier than first use
	@Inject
	public ApplicationViewService(Provider<Sitemap> sitemapPro) {
		this.sitemapPro = sitemapPro;
	}

	@Override
	public Status start() {
		// the sitemap will be created right after the injector, this way many
		// errors could be seen earlier than first use
		sitemapPro.get();
		return Status.STARTED;
	}

	@Override
	public Status stop() {
		return Status.STOPPED;
	}

	@Override
	public String serviceId() {
		return "Application View";
	}

	@Override
	public String getName() {
		return "Appplication View Service";
	}

	@Override
	public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {
		// TODO Auto-generated method stub

	}

}