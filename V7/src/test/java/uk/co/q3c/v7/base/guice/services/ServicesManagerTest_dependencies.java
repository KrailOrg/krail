package uk.co.q3c.v7.base.guice.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

/**
 * Unfortunately I can't use Mocks - because the AOP will not recognise the Mock as something to enhance. Dependency
 * graph:
 * <p>
 * x - depends on<br>
 * 2 - 0,1<br>
 * 3 - 2,1<br>
 * 4 - 2<br>
 * 2A- 2<br>
 * <br>
 * 0, 0A, and 1 are roots<br>
 * <br>
 * 
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
	MockService_0A service_0a;

	@Inject
	MockService_1 service_1;

	@Inject
	MockService_2 service_2;

	@Inject
	MockService_2A service_2a;

	@Inject
	MockService_3 service_3;

	@Inject
	MockService_4 service_4;

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

	static class MockService_0A extends MockService_0 {

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

	@DependsOnServices(services = { MockService_2.class })
	static class MockService_2A extends MockService_2 {
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
	public void startOrder() {

		// given

		// when
		List<String> start = servicesManager.start();
		// then
	}

}
