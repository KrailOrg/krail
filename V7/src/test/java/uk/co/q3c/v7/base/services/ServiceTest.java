/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.co.q3c.v7.base.services;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.jodatime.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.services.AbstractService;
import uk.co.q3c.v7.base.services.AutoStart;
import uk.co.q3c.v7.base.services.Service;
import uk.co.q3c.v7.base.services.ServiceStatus;
import uk.co.q3c.v7.base.services.ServiceUtils;
import uk.co.q3c.v7.base.services.ServicesMonitor;
import uk.co.q3c.v7.base.services.ServicesMonitorModule;
import uk.co.q3c.v7.base.services.Service.Status;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

/**
 * Combined testing for all the Service components - almost functional testing
 * 
 * @author David Sowerby
 * 
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({ ServicesMonitorModule.class })
public class ServiceTest {

	static Status a1_startStatus = Status.STARTED;
	static Status a1_stopStatus = Status.STOPPED;
	static boolean a1_exceptionOnStart = false;
	static boolean a1_exceptionOnStop = false;

	static class MockService extends AbstractService {

		int startCalls;
		int stopCalls;
		int statusChangeCount;
		List<String> dependencyChanges = new ArrayList<>();

		@Override
		public String getName() {
			return ServiceUtils.unenhancedClass(this).getSimpleName();
		}

		@Override
		public String serviceId() {
			return getName();
		}

		@Override
		public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {
			statusChangeCount++;
			String chg = this.getStatus() + ":" + service.getName() + ":" + fromStatus + ":" + toStatus;
			dependencyChanges.add(chg);
		}

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
	}

	@Singleton
	static class MockServiceA extends MockService {

	}

	static class MockServiceA1 extends MockService {

		@Override
		public Status start() {
			if (a1_exceptionOnStart) {
				throw new NullPointerException("Mock exception on start");
			} else {
				startCalls++;
				return a1_startStatus;
			}
		}

		@Override
		public Status stop() {
			if (a1_exceptionOnStop) {
				throw new NullPointerException("Mock exception on stop");
			} else {
				stopCalls++;
				return a1_stopStatus;
			}
		}

	}

	static class MockServiceB extends MockService {

		@AutoStart(auto = false)
		private final MockServiceA a;

		@Inject
		public MockServiceB(MockServiceA a) {
			super();
			this.a = a;

		}

	}

	static class MockServiceC extends MockService {

		@AutoStart
		private final MockServiceA a;
		private final MockServiceB b;

		@Inject
		public MockServiceC(MockServiceA a, MockServiceB b) {
			super();
			this.a = a;
			this.b = b;

		}
	}

	static class MockServiceD extends MockService {

		@AutoStart
		private final MockServiceA a;

		@AutoStart
		private final MockServiceA1 a1;

		@AutoStart(auto = false)
		private final MockServiceB b;
		private final MockServiceC c;

		@Inject
		public MockServiceD(MockServiceA a, MockServiceA1 a1, MockServiceB b, MockServiceC c) {
			super();
			this.a = a;
			this.b = b;
			this.c = c;
			this.a1 = a1;

		}

		@Override
		public Status start() {
			if (getStatus() == Status.DEPENDENCY_FAILED) {
				return Status.DEPENDENCY_FAILED;
			} else {
				return super.start();
			}
		}

	}

	@Inject
	MockServiceD serviced;

	@Inject
	Injector injector;

	@Before
	public void setup() {
		a1_startStatus = Status.STARTED;
		a1_stopStatus = Status.STOPPED;
		a1_exceptionOnStart = false;
		a1_exceptionOnStop = false;
	}

	@Test
	public void autoStart() {

		// given
		ServicesMonitor monitor = injector.getInstance(ServicesMonitor.class);
		// when
		serviced.start();
		// then
		assertThat(serviced.a.isStarted()).isTrue();
		assertThat(serviced.a1.isStarted()).isTrue();
		assertThat(serviced.b.isStarted()).isFalse();
		assertThat(serviced.c.isStarted()).isFalse();
		assertThat(serviced.a.startCalls).isEqualTo(1);
		assertThat(serviced.getStatus()).isEqualTo(Status.STARTED);

	}

	@Test
	public void autoStart_alreadyStarted() {

		// given
		injector.getInstance(MockServiceA.class).start();

		// when
		serviced.start();
		// then
		assertThat(serviced.a.isStarted()).isTrue();
		assertThat(serviced.a1.isStarted()).isTrue();
		assertThat(serviced.b.isStarted()).isFalse();
		assertThat(serviced.c.isStarted()).isFalse();
		assertThat(serviced.a.startCalls).isEqualTo(1); // hasn"t been called again
		assertThat(serviced.getStatus()).isEqualTo(Status.STARTED);

		// listeners

	}

	@Test
	public void autoStartFailureException() {

		// given
		a1_exceptionOnStart = true;
		// when
		serviced.start();
		// then
		assertThat(serviced.a.isStarted()).isTrue();
		assertThat(serviced.a1.isStarted()).isFalse();
		assertThat(serviced.a1.getStatus()).isEqualTo(Status.FAILED_TO_START);
		assertThat(serviced.b.isStarted()).isFalse();
		assertThat(serviced.c.isStarted()).isFalse();
		assertThat(serviced.getStatus()).isEqualTo(Status.DEPENDENCY_FAILED);
		assertThat(serviced.startCalls).isEqualTo(0);
	}

	@Test
	public void autoStartReturnsFailure() {

		// given
		a1_startStatus = Status.FAILED_TO_START;
		// when
		serviced.start();
		// then
		assertThat(serviced.a.isStarted()).isTrue();
		assertThat(serviced.a1.isStarted()).isFalse();
		assertThat(serviced.a1.getStatus()).isEqualTo(Status.FAILED_TO_START);
		assertThat(serviced.b.isStarted()).isFalse();
		assertThat(serviced.c.isStarted()).isFalse();
		assertThat(serviced.getStatus()).isEqualTo(Status.DEPENDENCY_FAILED);
		assertThat(serviced.startCalls).isEqualTo(0);
	}

	/**
	 * With this test structure there should be registered instances of a,a1,bx2,c and d
	 */
	@Test
	public void monitorHasRegisteredServices() {

		// given
		ServicesMonitor monitor = injector.getInstance(ServicesMonitor.class);
		// when
		serviced.start();
		// then
		ImmutableList<Service> registeredServices = monitor.getRegisteredServices();
		for (Service service : registeredServices) {
			System.out.println(service.getName());
		}
		assertThat(registeredServices).containsOnly(serviced, serviced.a, serviced.b, serviced.a1, serviced.c,
				serviced.c.b);

	}

	@Test
	public void monitorLogsStatusChange() {

		// given
		ServicesMonitor monitor = injector.getInstance(ServicesMonitor.class);
		// when
		serviced.start();
		// then
		ServiceStatus status = monitor.getServiceStatus(serviced);
		assertThat(status.getCurrentStatus()).isEqualTo(Status.STARTED);
		assertThat(status.getLastStartTime()).isNotNull().isBeforeOrEqualTo(DateTime.now());
		assertThat(status.getLastStopTime()).isNull();
		assertThat(status.getStatusChangeTime()).isNotNull().isEqualTo(status.getLastStartTime());
		assertThat(status.getPreviousStatus()).isEqualTo(Status.INITIAL);
		DateTime startTime = status.getLastStartTime();
		// when
		serviced.stop();
		// then
		status = monitor.getServiceStatus(serviced);
		assertThat(status.getCurrentStatus()).isEqualTo(Status.STOPPED);
		assertThat(status.getLastStartTime()).isNotNull().isEqualTo(startTime); // shouldn't have changed
		assertThat(status.getLastStopTime()).isNotNull().isBeforeOrEqualTo(DateTime.now()).isAfter(startTime);
		assertThat(status.getStatusChangeTime()).isNotNull().isEqualTo(status.getLastStopTime());
		assertThat(status.getPreviousStatus()).isEqualTo(Status.STARTED);
	}

	/**
	 * A dependency should have a listener automatically added by any Service using it
	 */
	@Test
	public void serviceMonitorsDependencyStatusChange() {

		// given
		serviced.start();
		// when
		serviced.a.stop();
		// then
		assertThat(serviced.statusChangeCount).isEqualTo(3);
		assertThat(serviced.dependencyChanges).containsOnly("INITIAL:MockServiceA1:INITIAL:STARTED",
				"INITIAL:MockServiceA:INITIAL:STARTED", "STARTED:MockServiceA:STARTED:STOPPED");
	}
}
