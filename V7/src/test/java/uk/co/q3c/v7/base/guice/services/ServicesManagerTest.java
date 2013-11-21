package uk.co.q3c.v7.base.guice.services;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

/**
 * Unfortunately I can't use Mocks - because the AOP will not recognise the Mock as something to enhance.
 * 
 * @author David Sowerby
 * 
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({ ServicesManagerModule.class })
public class ServicesManagerTest {

	@Inject
	ServicesManager servicesManager;

	@Inject
	MockService_working_ok service_ok;

	@Inject
	MockService_fails_on_start service_fails_on_start;

	@Inject
	MockService_fails_on_stop service_fails_on_stop;

	@Inject
	MockService_partial service_partial;

	@Inject
	MockService_manual_register service_manual_register;

	@Inject
	StrictURIFragmentHandler defnav;

	static class MockService_working_ok implements Service {
		int startCalls = 0;
		int stopCalls = 0;

		@Override
		public Status start() {
			System.out.println("starting .........................................");
			startCalls++;
			return Status.STARTED;
		}

		@Override
		public Status stop() {
			System.out.println("stopping .........................................");
			stopCalls++;
			return Status.STOPPED;
		}

		@Override
		public Set<Class<? extends Service>> getDependencies() {
			return new HashSet<Class<? extends Service>>();
		}

		@Override
		public String getName() {
			return "Test Service working OK";
		}

	}

	static class MockService_fails_on_start implements Service {

		@Override
		public Status start() {
			System.out.println("starting .........................................");
			throw new NullPointerException("fails on start");
		}

		@Override
		public Status stop() {
			System.out.println("stopping .........................................");
			return Status.STOPPED;
		}

		@Override
		public String getName() {
			return "Test Service fails on start";
		}

		@Override
		public Set<Class<? extends Service>> getDependencies() {
			return new HashSet<Class<? extends Service>>();
		}

	}

	static class MockService_fails_on_stop implements Service {

		@Override
		public Status start() {
			System.out.println("starting .........................................");
			return Status.STARTED;
		}

		@Override
		public Status stop() {
			System.out.println("stopping .........................................");
			throw new NullPointerException("fails on stop");
		}

		@Override
		public String getName() {
			return "Test Service fails on stop";
		}

		@Override
		public Set<Class<? extends Service>> getDependencies() {
			return new HashSet<Class<? extends Service>>();
		}
	}

	static class MockService_partial implements Service {

		@Override
		public Status start() {
			System.out.println("starting .........................................");
			return Status.PARTIAL;
		}

		@Override
		public Status stop() {
			System.out.println("stopping .........................................");
			return Status.STOPPED;
		}

		@Override
		public String getName() {
			return "Test Service fails on start";
		}

		@Override
		public Set<Class<? extends Service>> getDependencies() {
			return new HashSet<Class<? extends Service>>();
		}
	}

	@NoAutoRegister
	static class MockService_manual_register implements Service {
		int startCalls = 0;
		int stopCalls = 0;

		@Override
		public Status start() {
			System.out.println("starting .........................................");
			startCalls++;
			return Status.STARTED;
		}

		@Override
		public Status stop() {
			System.out.println("stopping .........................................");
			stopCalls++;
			return Status.STOPPED;
		}

		@Override
		public Set<Class<? extends Service>> getDependencies() {
			return new HashSet<Class<? extends Service>>();
		}

		@Override
		public String getName() {
			return "Test Service working OK";
		}

	}

	@Before
	public void setup() {

	}

	@After
	public void tearDown() {
		servicesManager.clear();
	}

	@Test
	public void startAndStopThroughManager() {

		// given

		// when
		servicesManager.start();
		// then
		assertThat(servicesManager.getStatus(), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_ok), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_fails_on_start), is(Service.Status.FAILED_TO_START));
		assertThat(servicesManager.getStatus(service_fails_on_stop), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_partial), is(Service.Status.PARTIAL));

		assertThat(servicesManager.getStatus(service_manual_register), is(nullValue()));
		// when
		servicesManager.stop();
		// then
		assertThat(servicesManager.getStatus(), is(Service.Status.STOPPED));
		assertThat(servicesManager.getStatus(service_ok), is(Service.Status.STOPPED));
		assertThat(servicesManager.getStatus(service_fails_on_start), is(Service.Status.STOPPED));
		assertThat(servicesManager.getStatus(service_fails_on_stop), is(Service.Status.FAILED_TO_STOP));
		assertThat(servicesManager.getStatus(service_partial), is(Service.Status.STOPPED));
	}

	@Test
	public void StopAndStartDirectly() {

		// given
		servicesManager.start();
		// when
		service_ok.stop();
		service_fails_on_start.stop();
		service_fails_on_stop.stop();
		service_partial.stop();
		// then
		assertThat(servicesManager.getStatus(), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_ok), is(Service.Status.STOPPED));
		assertThat(servicesManager.getStatus(service_fails_on_start), is(Service.Status.STOPPED));
		assertThat(servicesManager.getStatus(service_fails_on_stop), is(Service.Status.FAILED_TO_STOP));
		assertThat(servicesManager.getStatus(service_partial), is(Service.Status.STOPPED));
		// when
		service_ok.start();
		service_fails_on_start.start();
		service_fails_on_stop.start();
		service_partial.start();

		// then
		assertThat(servicesManager.getStatus(), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_ok), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_fails_on_start), is(Service.Status.FAILED_TO_START));
		assertThat(servicesManager.getStatus(service_fails_on_stop), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_partial), is(Service.Status.PARTIAL));
	}

	@Test
	public void StopAndStartIndividuallyThroughManager() {

		// given
		servicesManager.start();
		// when
		boolean rok = servicesManager.stopService(service_ok);
		boolean rfos = servicesManager.stopService(service_fails_on_start);
		boolean rfostop = servicesManager.stopService(service_fails_on_stop);
		boolean rp = servicesManager.stopService(service_partial);

		// then
		assertThat(servicesManager.getStatus(), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_ok), is(Service.Status.STOPPED));
		assertThat(servicesManager.getStatus(service_fails_on_start), is(Service.Status.STOPPED));
		assertThat(servicesManager.getStatus(service_fails_on_stop), is(Service.Status.FAILED_TO_STOP));
		assertThat(servicesManager.getStatus(service_partial), is(Service.Status.STOPPED));
		assertThat(rok, is(true));
		assertThat(rfos, is(true));
		assertThat(rfostop, is(false));
		assertThat(rp, is(true));

		// when
		rok = servicesManager.startService(service_ok);
		rfos = servicesManager.startService(service_fails_on_start);
		rfostop = servicesManager.startService(service_fails_on_stop);
		rp = servicesManager.startService(service_partial);
		// then
		assertThat(servicesManager.getStatus(), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_ok), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_fails_on_start), is(Service.Status.FAILED_TO_START));
		assertThat(servicesManager.getStatus(service_fails_on_stop), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_partial), is(Service.Status.PARTIAL));

		assertThat(rok, is(true));
		assertThat(rfos, is(false));
		assertThat(rfostop, is(true));
		assertThat(rp, is(false));

	}

	/**
	 * A call to start when already started, or stop when already stopped, should be ignored
	 */
	@Test
	public void repeatStartOrStop() {

		// given
		// when
		servicesManager.start();
		// then
		assertThat(service_ok.startCalls, is(1));
		// when
		service_ok.stop();
		// then
		assertThat(service_ok.stopCalls, is(1));
		// when
		service_ok.start();
		// then
		assertThat(service_ok.startCalls, is(2));
		// when (start when already started)
		service_ok.start();
		// then
		assertThat(service_ok.startCalls, is(2));
		// when (start when already started)
		servicesManager.startService(service_ok);
		// then
		assertThat(service_ok.startCalls, is(2));

		// when
		service_ok.stop();
		// then
		assertThat(service_ok.stopCalls, is(2));
		// when (stop when already stopped)
		service_ok.stop();
		// then
		assertThat(service_ok.stopCalls, is(2));
		// when (stop when already stopped)
		servicesManager.stopService(service_ok);
		// then
		assertThat(service_ok.stopCalls, is(2));
	}

	@Test
	public void manualRegistration_before_manager_start() {

		// given
		// when
		servicesManager.registerService(service_manual_register);
		// then (service manager has not been started)
		assertThat(servicesManager.getStatus(service_manual_register), is(Service.Status.INITIAL));
		// when
		servicesManager.start();
		// then
		assertThat(servicesManager.getStatus(service_manual_register), is(Service.Status.STARTED));

	}

	@Test
	public void manualRegistration_after_manager_start() {

		// given
		servicesManager.start();
		// when
		servicesManager.registerService(service_manual_register);
		// then
		assertThat(servicesManager.getStatus(service_manual_register), is(Service.Status.STARTED));

	}

}
