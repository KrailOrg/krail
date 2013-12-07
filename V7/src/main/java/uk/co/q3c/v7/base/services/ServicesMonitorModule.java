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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.ReflectionUtils;
import uk.co.q3c.v7.base.services.Service.Status;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Acknowledgement: developed from code contributed by https://github.com/lelmarir
 * 
 * @author David Sowerby
 * 
 */
public class ServicesMonitorModule extends AbstractModule {

	private static final Logger log = LoggerFactory.getLogger(ServicesMonitorModule.class);

	/**
	 * This listener is constructed using the {@link Service} interface to identify service implementation instances..
	 * All instances of {@link Service} implementations are registered with the {@link ServicesMonitor}
	 * 
	 * @author David Sowerby
	 * 
	 */
	public class ServicesListener implements TypeListener {
		private final ServicesMonitor servicesManager;

		public ServicesListener(ServicesMonitor servicesManager) {
			this.servicesManager = servicesManager;
		}

		@Override
		public <I> void hear(final TypeLiteral<I> type, TypeEncounter<I> encounter) {
			InjectionListener<Object> listener = new InjectionListener<Object>() {
				@Override
				public void afterInjection(Object injectee) {

					// cast is safe - if not, the matcher is wrong
					Service service = (Service) injectee;
					servicesManager.registerService(service);

				}
			};
			encounter.register(listener);
		}

	}

	private class ServiceMethodStartInterceptor implements MethodInterceptor {

		private final ServicesMonitor servicesMonitor;

		public ServiceMethodStartInterceptor(ServicesMonitor servicesMonitor) {
			this.servicesMonitor = servicesMonitor;
		}

		/**
		 * The AOP code for all {@link Service#start()} methods. Looks for any fields in the 'this' which are annotated
		 * with {@link AutoStart}, and starts them first before invoking its own start method.
		 */
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Service service = (Service) invocation.getThis();
			log.info("Start request received by {} ...", service.getName());
			if (service.isStarted()) {
				log.info("{} has already been started, no further action required", service.getName());
				return Service.Status.STARTED;
			}

			// identify any predecessor Services which are annotated with @AutoStart
			// get the 'real' (unenhanced) class
			Class<?> clazz = ServiceUtils.unenhancedClass(service);

			// start the @AutoStart dependencies
			List<Status> dependencyStatuses = new ArrayList<>();
			Field[] declaredFields = clazz.getDeclaredFields();

			for (Field field : declaredFields) {
				Class<?> fieldClass = field.getType();
				// if it is a service field, add a listener to it
				if (Service.class.isAssignableFrom(fieldClass)) {
					field.setAccessible(true);
					Service dependency = (Service) field.get(service);
					dependency.addListener(service);
					// if annotated with @AutoStart(true), start the dependency
					AutoStart autoStart = field.getAnnotation(AutoStart.class);
					if (autoStart != null) {
						if (autoStart.auto()) {
							Method startMethod = dependency.getClass().getMethod("start");
							dependencyStatuses.add((Status) startMethod.invoke(dependency));
						}
					}

				}
			}

			// If any dependency has failed to start, overall status is DEPENDENCY_FAILED
			boolean dependencyFailed = false;
			for (Status depStatus : dependencyStatuses) {
				if (depStatus != Status.STARTED) {
					dependencyFailed = true;
					break;
				}
			}

			// If no dependency failures call the Service implementation start method code for 'this'
			Status result = null;
			try {
				if (dependencyFailed) {
					service.setStatus(Status.DEPENDENCY_FAILED);
				}
				result = (Status) invocation.proceed();
			} catch (Throwable e) {
				result = Status.FAILED_TO_START;
				log.error("Service {} failed to start, with exception: {}", service.getName(), e.getMessage());

			}

			service.setStatus(result);
			log.info("starting {} service concluded with a status of {}", service.getName(), service.getStatus());
			return result;
		}
	}

	private class ServiceMethodStopInterceptor implements MethodInterceptor {

		private final ServicesMonitor servicesManager;

		public ServiceMethodStopInterceptor(ServicesMonitor servicesManager) {
			this.servicesManager = servicesManager;
		}

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Service service = (Service) invocation.getThis();
			log.debug("stopping service '{}'", service.getName());
			Status result = null;
			if (service.getStatus() != Status.STOPPED) {
				try {
					result = (Status) invocation.proceed();
				} catch (Exception e) {
					result = Status.FAILED_TO_STOP;
					log.warn("service '{}' failed to stop correctly.  The exception reported was:", service.getName(),
							e);
				}
			} else {
				result = Status.STOPPED;
				log.debug("The service '{}' is already stopped, stop request ignored", service.getName());
			}
			service.setStatus(result);
			return result;
		}
	}

	private class FinalizeMethodMatcher extends AbstractMatcher<Method> {
		@Override
		public boolean matches(Method method) {
			return method.getName().equals("finalize");
		}
	}

	/**
	 * Calls {@link Service#stop} before passing on the finalize() call
	 * 
	 */
	private class FinalizeMethodInterceptor implements MethodInterceptor {

		public FinalizeMethodInterceptor() {
		}

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Service service = (Service) invocation.getThis();
			service.stop();
			return invocation.proceed();
		}
	}

	/**
	 * Matches classes implementing {@link Service}
	 * 
	 */
	private class ServiceInterfaceMatcher extends AbstractMatcher<TypeLiteral<?>> {
		@Override
		public boolean matches(TypeLiteral<?> t) {
			Class<?> rawType = t.getRawType();
			Set<Class<?>> interfaces = ReflectionUtils.allInterfaces(rawType);

			for (Class<?> intf : interfaces) {
				if (intf.equals(Service.class)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Matches the {@link Service#start()} method
	 * 
	 */
	private class InterfaceStartMethodMatcher extends AbstractMatcher<Method> {
		@Override
		public boolean matches(Method method) {
			return method.getName().equals("start");
		}
	}

	/**
	 * Matches the {@link Service#stop()} method
	 * 
	 */
	private class InterfaceStopMethodMatcher extends AbstractMatcher<Method> {
		@Override
		public boolean matches(Method method) {
			return method.getName().equals("stop");
		}
	}

	/**
	 * Needs to be created this way because it is inside the module, but note that the @Provides method at
	 * getServicesManager() ensures that injection scope remains consistent
	 */
	private final ServicesMonitor servicesManager = new ServicesMonitor();

	@Override
	protected void configure() {

		bindListener(new ServiceInterfaceMatcher(), new ServicesListener(servicesManager));

		bindInterceptor(Matchers.subclassesOf(Service.class), new InterfaceStartMethodMatcher(),
				new ServiceMethodStartInterceptor(servicesManager));

		bindInterceptor(Matchers.subclassesOf(Service.class), new InterfaceStopMethodMatcher(),
				new ServiceMethodStopInterceptor(servicesManager));

		bindInterceptor(Matchers.subclassesOf(Service.class), new FinalizeMethodMatcher(),
				new FinalizeMethodInterceptor());

	}

	@Provides
	public ServicesMonitor getServicesManager() {
		return servicesManager;
	}

}