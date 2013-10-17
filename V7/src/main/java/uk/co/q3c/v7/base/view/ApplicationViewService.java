package uk.co.q3c.v7.base.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import uk.co.q3c.v7.base.guice.services.Service;
import uk.co.q3c.v7.base.guice.services.Start;
import uk.co.q3c.v7.base.guice.services.Stop;
import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;

@Singleton
@Service(startAsSoonAsPossible=true)
public class ApplicationViewService {

	// the sitemap will be created right after the injector, this way many
	// errors could be seen earlier than first use
	@Inject
	public ApplicationViewService(Sitemap sitemap) {
	}

	@Start
	public void start() throws Exception {
		;
	}

	@Stop
	public void stop() {
		;
	}

}
