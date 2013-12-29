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

import com.google.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

/**
 * Dependency graph:
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
 * 
 * @author David Sowerby
 * 
 */

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ ServicesMonitorModule.class })
public class ServiceUtilsTest {
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

	static class MockService_0 extends AbstractService {
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
		public String getDescription() {
			return "service 0";
		}

		@Override
		public String getName() {
			return "Test Service working OK";
		}

		@Override
		public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {
			// TODO Auto-generated method stub

		}

	}

	static class MockService_0A extends MockService_0 {

	}

	static class MockService_1 extends AbstractService {

		@Override
		public Status start() {
			return Status.STARTED;
		}

		@Override
		public Status stop() {
			return Status.STOPPED;
		}

		@Override
		public String getDescription() {
			return "service 1";
		}

		@Override
		public String getName() {
			return "Test Service fails on start";
		}

		@Override
		public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {
			// TODO Auto-generated method stub

		}

	}

	static class MockService_2 extends AbstractService {

		@Override
		public Status start() {
			return Status.STARTED;
		}

		@Override
		public Status stop() {
			return Status.STOPPED;
		}

		@Override
		public String getDescription() {
			return "service 2";
		}

		@Override
		public String getName() {
			return "Test Service fails on stop";
		}

		@Override
		public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {
			// TODO Auto-generated method stub

		}

	}

	static class MockService_2A extends MockService_2 {
	}

	static class MockService_3 extends AbstractService {

		@Override
		public Status start() {
			return Status.STARTED;
		}

		@Override
		public Status stop() {
			return Status.STOPPED;
		}

		@Override
		public String getName() {
			return "Test Service fails on start";
		}

		@Override
		public String getDescription() {
			return "service 3";
		}

		@Override
		public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {
			// TODO Auto-generated method stub

		}

	}

	static class MockService_4 extends AbstractService {
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
		public String getDescription() {
			return "service 4";
		}

		@Override
		public void serviceStatusChange(Service service, Status fromStatus, Status toStatus) {
			// TODO Auto-generated method stub

		}

	}

	@Test
	public void unenhancedService() {

		// given

		// when
		// then
		// this just validates the need for the test, AOP enhancing changes the class
		assertThat(service_2a.getClass()).isNotEqualTo(MockService_2A.class);
		assertThat(ServiceUtils.unenhancedClass(service_2a)).isEqualTo(MockService_2A.class);
	}

}
