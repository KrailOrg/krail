package uk.co.q3c.v7.base.guice.services;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.services.ServicesRegistry.Status;

import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ ServicesManagerModule.class })
public class ServicesRegistryTest {

	@Inject
	ServicesRegistry serviceRegistry;

	@Inject
	Provider<ServicesManager> servicesManagerProvider;

	@Inject
	ServicesManager servicesManager;

	/**
	 * correct
	 * 
	 * @author david
	 * 
	 */
	@Service
	static class TestServiceObject {

		Status status = Status.INITIAL;

		@Start
		public void start() {
			status = Status.STARTED;
		}

		@Stop
		public void stop() {
			status = Status.STARTED;
		}

	}

	/**
	 * Missing service annotation
	 * 
	 * @author david
	 * 
	 */
	static class TestServiceObject1 {

		Status status = Status.INITIAL;

		@Start
		public void start() {
			status = Status.STARTED;
		}

		@Stop
		public void stop() {
			status = Status.STARTED;
		}

	}

	/**
	 * Missing start annotation
	 * 
	 * @author david
	 * 
	 */
	static class TestServiceObject2 {

		Status status = Status.INITIAL;

		public void start() {
			status = Status.STARTED;
		}

		@Stop
		public void stop() {
			status = Status.STARTED;
		}
	}

	/**
	 * Missing stop annotation
	 * 
	 * @author david
	 * 
	 */
	static class TestServiceObject3 {

		Status status = Status.INITIAL;

		@Start
		public void start() {
			status = Status.STARTED;
		}

		public void stop() {
			status = Status.STARTED;
		}

	}

	@Before
	public void setup() {
		// serviceRegistry = new ServicesRegistry();
	}

	@Test
	public void correct() {

		// given
		Object serviceObject = new TestServiceObject();
		// when
		serviceRegistry.register(serviceObject);
		// then

		assertThat(serviceRegistry.getServices().size(), is(1));
		TestServiceObject service = (TestServiceObject) serviceRegistry.getServices().get(0).getService();
		assertThat(service, instanceOf(TestServiceObject.class));
		assertThat(service.status, is(Status.INITIAL));

		// when
		servicesManagerProvider.get().start();
		// then
		assertThat(service.status, is(Status.STARTED));

	}

	@Test(expected = IllegalStateException.class)
	public void serviceMissing() {

		// given
		Object serviceObject = new TestServiceObject1();
		// when
		serviceRegistry.register(serviceObject);
		// then
		// expected exception
	}

	@Test(expected = IllegalStateException.class)
	public void startMissing() {

		// given
		Object serviceObject = new TestServiceObject2();
		// when
		serviceRegistry.register(serviceObject);
		// then
		// expected exception
	}

	@Test(expected = IllegalStateException.class)
	public void stopMissing() {

		// given
		Object serviceObject = new TestServiceObject3();
		// when
		serviceRegistry.register(serviceObject);
		// then
		// expected exception
	}
}
