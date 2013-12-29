package uk.co.q3c.v7.base.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.services.AbstractService;
import uk.co.q3c.v7.base.services.Service;

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
	public String getName() {
		return "Appplication View Service";
	}

	@Override
	public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}