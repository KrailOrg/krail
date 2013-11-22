package uk.co.q3c.v7.base.guice.services;

import static org.hamcrest.Matchers.is;
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
public class ServicesManagerTest_dependencies {

	@Inject
	ServicesManager servicesManager;

	@Inject
	MockService_0 service_0;

	@Inject
	MockService_1 service_1;

	@Inject
	MockService_2 service_2;

	@Inject
	MockService_3 service_3;

	@Inject
	MockService_4 service_manual_register;

	@Inject
	StrictURIFragmentHandler defnav;

	static class MockService_0 implements Service {
		int startCalls = 0;
		int stopCalls = 0;

		@Override
		public Status start() {
			startCalls++;
			return Status.STARTED;
		}

		@Override
		public Status stop() {
			stopCalls++;
			return Status.STOPPED;
		}

		@Override
		public Set<Class<? extends Service>> getDependencies() {
			return new HashSet<Class<? extends Service>>();
		}

		@Override
		public String serviceId() {
			return "service 0";
		}

		@Override
		public String getName() {
			return "Test Service working OK";
		}

	}

	static class MockService_1 implements Service {

		@Override
		public Status start() {
			return Status.STARTED;
		}

		@Override
		public Status stop() {
			return Status.STOPPED;
		}

		@Override
		public Set<Class<? extends Service>> getDependencies() {
			return new HashSet<Class<? extends Service>>();
		}

		@Override
		public String serviceId() {
			return "service 1";
		}

		@Override
		public String getName() {
			return "Test Service fails on start";
		}

	}

	@DependsOnServices(services = { MockService_1.class, MockService_0.class })
	static class MockService_2 implements Service {

		@Override
		public Status start() {
			return Status.STARTED;
		}

		@Override
		public Status stop() {
			return Status.STOPPED;
		}

		@Override
		public Set<Class<? extends Service>> getDependencies() {
			return new HashSet<Class<? extends Service>>();
		}

		@Override
		public String serviceId() {
			return "service 2";
		}

		@Override
		public String getName() {
			return "Test Service fails on stop";
		}
	}

	@DependsOnServices(services = { MockService_1.class })
	static class MockService_3 implements Service {

		@Override
		public Status start() {
			return Status.STARTED;
		}

		@Override
		public Status stop() {
			return Status.STOPPED;
		}

		@Override
		public Set<Class<? extends Service>> getDependencies() {
			HashSet<Class<? extends Service>> deps = new HashSet<Class<? extends Service>>();
			deps.add(MockService_2.class);
			return deps;
		}

		@Override
		public String getName() {
			return "Test Service fails on start";
		}

		@Override
		public String serviceId() {
			return "service 3";
		}

	}

	static class MockService_4 implements Service {
		int startCalls = 0;
		int stopCalls = 0;

		@Override
		public Status start() {
			startCalls++;
			return Status.STARTED;
		}

		@Override
		public Status stop() {
			stopCalls++;
			return Status.STOPPED;
		}

		@Override
		public String getName() {
			return "Test Service working OK";
		}

		@Override
		public Set<Class<? extends Service>> getDependencies() {
			HashSet<Class<? extends Service>> deps = new HashSet<Class<? extends Service>>();
			deps.add(MockService_2.class);
			return deps;
		}

		@Override
		public String serviceId() {
			return "service 4";
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

	}

	@Test
	public void StopAndStartDirectly() {

		// given
		servicesManager.start();
		// when
		service_0.stop();
		service_1.stop();
		service_2.stop();
		service_3.stop();
		// then
		assertThat(servicesManager.getStatus(), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_0), is(Service.Status.STOPPED));
		assertThat(servicesManager.getStatus(service_1), is(Service.Status.STOPPED));
		assertThat(servicesManager.getStatus(service_2), is(Service.Status.FAILED_TO_STOP));
		assertThat(servicesManager.getStatus(service_3), is(Service.Status.STOPPED));
		// when
		service_0.start();
		service_1.start();
		service_2.start();
		service_3.start();

		// then
		assertThat(servicesManager.getStatus(), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_0), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_1), is(Service.Status.FAILED_TO_START));
		assertThat(servicesManager.getStatus(service_2), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_3), is(Service.Status.PARTIAL));
	}

	@Test
	public void StopAndStartIndividuallyThroughManager() {

		// given
		servicesManager.start();
		// when
		boolean rok = servicesManager.stopService(service_0);
		boolean rfos = servicesManager.stopService(service_1);
		boolean rfostop = servicesManager.stopService(service_2);
		boolean rp = servicesManager.stopService(service_3);

		// then
		assertThat(servicesManager.getStatus(), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_0), is(Service.Status.STOPPED));
		assertThat(servicesManager.getStatus(service_1), is(Service.Status.STOPPED));
		assertThat(servicesManager.getStatus(service_2), is(Service.Status.FAILED_TO_STOP));
		assertThat(servicesManager.getStatus(service_3), is(Service.Status.STOPPED));
		assertThat(rok, is(true));
		assertThat(rfos, is(true));
		assertThat(rfostop, is(false));
		assertThat(rp, is(true));

		// when
		rok = servicesManager.startService(service_0);
		rfos = servicesManager.startService(service_1);
		rfostop = servicesManager.startService(service_2);
		rp = servicesManager.startService(service_3);
		// then
		assertThat(servicesManager.getStatus(), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_0), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_1), is(Service.Status.FAILED_TO_START));
		assertThat(servicesManager.getStatus(service_2), is(Service.Status.STARTED));
		assertThat(servicesManager.getStatus(service_3), is(Service.Status.PARTIAL));

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
		assertThat(service_0.startCalls, is(1));
		// when
		service_0.stop();
		// then
		assertThat(service_0.stopCalls, is(1));
		// when
		service_0.start();
		// then
		assertThat(service_0.startCalls, is(2));
		// when (start when already started)
		service_0.start();
		// then
		assertThat(service_0.startCalls, is(2));
		// when (start when already started)
		servicesManager.startService(service_0);
		// then
		assertThat(service_0.startCalls, is(2));

		// when
		service_0.stop();
		// then
		assertThat(service_0.stopCalls, is(2));
		// when (stop when already stopped)
		service_0.stop();
		// then
		assertThat(service_0.stopCalls, is(2));
		// when (stop when already stopped)
		servicesManager.stopService(service_0);
		// then
		assertThat(service_0.stopCalls, is(2));
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
