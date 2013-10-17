package uk.co.q3c.v7.base.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import uk.co.q3c.v7.base.guice.services.Service;
import uk.co.q3c.v7.base.guice.services.Start;
import uk.co.q3c.v7.base.guice.services.Stop;

@Singleton
@Service(startAsSoonAsPossible=true)
public class V7QuartzService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(V7QuartzService.class);
	
	private Provider<Scheduler> schedulerProvider;
	
	@Inject
	public V7QuartzService(Provider<Scheduler> schedulerProvider) {
		this.schedulerProvider = schedulerProvider;
	}
	
	@Start
	public void start() throws Exception {
		try {
			schedulerProvider.get().start();
		} catch (SchedulerException | NullPointerException e) {
			LOGGER.trace("Impossibile avviare lo scheduler di Quarz", e);
		}
	}

	@Stop
	public void stop() {
		// arresto Quartz
		try {
			schedulerProvider.get().shutdown();
		} catch (SchedulerException | NullPointerException e) {
			LOGGER.trace("Impossibile terminare lo scheduler di Quarz", e);
		}
	}

}
