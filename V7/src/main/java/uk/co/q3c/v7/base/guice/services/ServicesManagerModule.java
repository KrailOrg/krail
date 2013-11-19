package uk.co.q3c.v7.base.guice.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.guice.services.ServicesRegistry.Status;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class ServicesManagerModule extends AbstractModule {

	private static final Logger log = LoggerFactory.getLogger(ServicesManagerModule.class);

	public class ServicesListener implements TypeListener {
		private final ServicesManager servicesManager;

		public ServicesListener(ServicesManager servicesManager) {
			this.servicesManager = servicesManager;
		}

		@Override
		public <I> void hear(final TypeLiteral<I> type, TypeEncounter<I> encounter) {
			encounter.register(new InjectionListener<Object>() {
				@Override
				public void afterInjection(Object injectee) {
					servicesManager.registerService(injectee);
				}
			});
		}

	}

	private class ServiceMatcher extends AbstractMatcher<TypeLiteral<?>> {
		@Override
		public boolean matches(TypeLiteral<?> t) {
			return t.getRawType().isAnnotationPresent(Service.class);
		}
	}

	private class MethodAnnotatedWith extends AbstractMatcher<Method> {
		private final Class<? extends Annotation> annotation;

		public MethodAnnotatedWith(Class<? extends Annotation> annotation) {
			this.annotation = annotation;
		}

		@Override
		public boolean matches(Method method) {
			if (method.isAnnotationPresent(annotation)) {
				return true;
			} else {
				Class<?> clazz = method.getDeclaringClass();
				while (clazz != Object.class) {
					clazz = clazz.getSuperclass();
					try {
						if (clazz.getMethod(method.getName(), method.getParameterTypes()).isAnnotationPresent(
								annotation)) {
							return true;
						}
					} catch (NoSuchMethodException | SecurityException e) {
						return false;
					}
				}
				return false;
			}
		}
	}

	private class ServiceMethodStartInterceptor implements MethodInterceptor {
		private final ServicesManager servicesManager;

		public ServiceMethodStartInterceptor(ServicesManager servicesManager) {
			this.servicesManager = servicesManager;
		}

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Status status = servicesManager.getServiceData(invocation.getThis()).getStatus();
			switch (status) {
			case INITIAL:
			case STOPPED:
				Object result = null;
				try {
					result = invocation.proceed();
					servicesManager.markAs(invocation.getThis(), Status.STARTED);
					return result;
				} catch (Throwable e) {
					servicesManager.markAs(invocation.getThis(), Status.FAILED);
					throw e;
				}
			default:
				log.trace("The service {} is already started, start method will not be invoked any more",
						invocation.getThis());
				return null;
			}
		}
	}

	private class ServiceMethodStopInterceptor implements MethodInterceptor {
		private final ServicesManager servicesManager;

		public ServiceMethodStopInterceptor(ServicesManager servicesManager) {
			this.servicesManager = servicesManager;
		}

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Status status = servicesManager.getServiceData(invocation.getThis()).getStatus();
			switch (status) {
			case STARTED:
				Object result = null;
				try {
					result = invocation.proceed();
					servicesManager.markAs(invocation.getThis(), Status.STOPPED);
					return result;
				} catch (Throwable e) {
					servicesManager.markAs(invocation.getThis(), Status.FAILED);
					throw e;
				}
			default:
				log.trace("The service {} is already halted, stop method will not be invoked any more",
						invocation.getThis());
				return null;
			}
		}
	}

	private class FinalizeMethodMatcher extends AbstractMatcher<Method> {
		@Override
		public boolean matches(Method method) {
			return method.getName().equals("finalize");
		}
	}

	private class FinalizeMethodInterceptor implements MethodInterceptor {
		private final ServicesManager servicesManager;

		public FinalizeMethodInterceptor(ServicesManager servicesManager) {
			this.servicesManager = servicesManager;
		}

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			servicesManager.finalize(invocation.getThis());
			return invocation.proceed();
		}
	}

	private final ServicesManager servicesManager = new ServicesManager();

	@Override
	protected void configure() {
		bindListener(new ServiceMatcher(), new ServicesListener(servicesManager));
		bindInterceptor(Matchers.annotatedWith(Service.class), new MethodAnnotatedWith(Start.class),
				new ServiceMethodStartInterceptor(servicesManager));
		bindInterceptor(Matchers.annotatedWith(Service.class), new MethodAnnotatedWith(Stop.class),
				new ServiceMethodStopInterceptor(servicesManager));
		bindInterceptor(Matchers.annotatedWith(Service.class), new FinalizeMethodMatcher(),
				new FinalizeMethodInterceptor(servicesManager));
	}

	@Provides
	public ServicesManager getServicesManager() {
		return servicesManager;
	}
}