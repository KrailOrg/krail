package uk.co.q3c.v7.base.guice.services;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ ServicesManagerModule.class })
public class ServicesManagerTest {

	@Inject
	ServicesManager servicesManager;

	@Inject
	TestServiceObject serviceObject;

	@Inject
	StrictURIFragmentHandler defnav;

	static class TestServiceObject implements Service {
		Status status = Status.INITIAL;

		@Override
		public void start() {
			status = Status.STARTED;

		}

		@Override
		public void stop() {
			status = Status.STOPPED;
		}

		@Override
		public Status getStatus() {
			return status;
		}

		@Override
		public String getName() {
			return "Test Service";
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
		assertThat(serviceObject.getStatus(), is(Service.Status.STARTED));
		// when
		servicesManager.stop();
		// then
		assertThat(servicesManager.getStatus(), is(Service.Status.STOPPED));
		assertThat(serviceObject.getStatus(), is(Service.Status.STOPPED));

	}
}
