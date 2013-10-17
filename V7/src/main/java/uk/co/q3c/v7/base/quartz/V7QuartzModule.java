package uk.co.q3c.v7.base.quartz;

import org.nnsoft.guice.guartz.QuartzModule;

import uk.co.q3c.v7.base.guice.services.ServicesManager;

public class V7QuartzModule extends QuartzModule {

	/**
	 * Override this method to provide your jobj: <b>IMPORTANT: call
	 * super.schedule()</b>
	 * 
	 * scheduleJob(PuliziaNuovaSettimana.class);
	 */
	@Override
	protected void schedule() {
		configureScheduler().withManualStart();
		requireBinding(ServicesManager.class);
		bindQuartzService();
	}

	protected void bindQuartzService() {
		bind(V7QuartzService.class);
	}
}
