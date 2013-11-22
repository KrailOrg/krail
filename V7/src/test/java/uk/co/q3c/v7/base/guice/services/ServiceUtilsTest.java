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
package uk.co.q3c.v7.base.guice.services;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

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
@GuiceContext({ ServicesManagerModule.class })
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

	@Test
	public void serviceId() {

		// given

		// when
		String serviceId = ServiceUtils.serviceId(service_0);
		// then
		assertThat(serviceId, is("uk.co.q3c.v7.base.guice.services.ServiceUtilsTest$MockService_0"));
		// when
		serviceId = ServiceUtils.serviceId(service_0a);
		// then
		assertThat(serviceId, is("uk.co.q3c.v7.base.guice.services.ServiceUtilsTest$MockService_0A"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void extractAnnotationDependencies() {

		// given

		// when

		// then
		assertThat(ServiceUtils.extractAnnotationDependencies(service_0), is(nullValue()));
		assertThat(ServiceUtils.extractAnnotationDependencies(service_0a), is(nullValue()));
		assertThat(ServiceUtils.extractAnnotationDependencies(service_1), is(nullValue()));
		assertThat(ServiceUtils.extractAnnotationDependencies(service_2),
				hasItems(MockService_1.class, MockService_0.class));
		assertThat(ServiceUtils.extractAnnotationDependencies(service_2a).size(), is(1));
		assertThat(ServiceUtils.extractAnnotationDependencies(service_2a).contains(MockService_2.class), is(true));
		assertThat(ServiceUtils.extractAnnotationDependencies(service_3).size(), is(1));
		assertThat(ServiceUtils.extractAnnotationDependencies(service_3).contains(MockService_1.class), is(true));
		assertThat(ServiceUtils.extractAnnotationDependencies(service_4), is(nullValue()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void extractDependencies() {

		// given

		// when

		// then
		assertThat(ServiceUtils.extractDependencies(service_0).size(), is(0));
		assertThat(ServiceUtils.extractDependencies(service_0a).size(), is(0));
		assertThat(ServiceUtils.extractDependencies(service_1).size(), is(0));
		assertThat(ServiceUtils.extractDependencies(service_2), hasItems(MockService_1.class, MockService_0.class));
		assertThat(ServiceUtils.extractDependencies(service_2a).size(), is(1));
		assertThat(ServiceUtils.extractDependencies(service_2a).contains(MockService_2.class), is(true));
		assertThat(ServiceUtils.extractDependencies(service_3), hasItems(MockService_1.class, MockService_2.class));
		assertThat(ServiceUtils.extractDependencies(service_4).size(), is(1));
		assertThat(ServiceUtils.extractDependencies(service_4).contains(MockService_2.class), is(true));
	}

	@Test
	public void extractDependenciesServiceIds() {

		// given

		// when

		// then
		assertThat(ServiceUtils.extractDependenciesServiceIds(service_0).size(), is(0));
		assertThat(ServiceUtils.extractDependenciesServiceIds(service_0a).size(), is(0));
		assertThat(ServiceUtils.extractDependenciesServiceIds(service_1).size(), is(0));
		assertThat(ServiceUtils.extractDependenciesServiceIds(service_2),
				hasItems(MockService_1.class.getName(), MockService_0.class.getName()));
		assertThat(ServiceUtils.extractDependenciesServiceIds(service_2a).size(), is(1));
		assertThat(ServiceUtils.extractDependenciesServiceIds(service_2a).contains(MockService_2.class.getName()),
				is(true));
		assertThat(ServiceUtils.extractDependenciesServiceIds(service_3),
				hasItems(MockService_1.class.getName(), MockService_2.class.getName()));
		assertThat(ServiceUtils.extractDependenciesServiceIds(service_4).size(), is(1));
		assertThat(ServiceUtils.extractDependenciesServiceIds(service_4).contains(MockService_2.class.getName()),
				is(true));
	}
}
