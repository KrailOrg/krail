package uk.co.q3c.v7.base.guice;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import uk.co.q3c.v7.base.guice.services.Service;
import uk.co.q3c.v7.base.guice.services.ServicesRegistry;
import uk.co.q3c.v7.base.guice.services.ServicesRegistry.Status;
import uk.co.q3c.v7.base.guice.services.Start;
import uk.co.q3c.v7.base.guice.services.Stop;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;

public class ServicesManagerModule extends AbstractModule {

	private class ServiceMatcher extends AbstractMatcher<TypeLiteral<?>> {

		public ServiceMatcher() {
			super();
		}

		@Override
		public boolean matches(TypeLiteral<?> t) {
			boolean isService = t.getRawType().isAnnotationPresent(
					Service.class);
			boolean hasStart = false;
			boolean hasStop = false;

			for (final Method method : t.getRawType().getMethods()) {
				if (method.isAnnotationPresent(Start.class)) {
					if (hasStart == true) {
						throw new IllegalStateException(
								"There must be only one public method annotated with @Start");
					}
					if (method.getParameterTypes().length != 0
							|| method.getReturnType() != Void.TYPE) {
						throw new IllegalStateException(
								"The method annotated with @Start should have no parameters and void return");
					}
					hasStart = true;
				} else if (method.isAnnotationPresent(Stop.class)) {
					if (hasStop == true) {
						throw new IllegalStateException(
								"There must be only one public method annotated with @Stop");
					}
					if (method.getParameterTypes().length != 0
							|| method.getReturnType() == null) {
						throw new IllegalStateException(
								"The method annotated with @Stop should have no parameters and void return");
					}
					hasStop = true;
				}
			}
			if (isService && hasStart == false && hasStop == false) {
				throw new IllegalStateException(
						"A Service must have at least a public method annotated with @Start or @Stop");
			}

			if (isService || hasStart || hasStop) {
				if (t.getRawType().isAnnotationPresent(Singleton.class)
						|| t.getRawType().isAnnotationPresent(
								javax.inject.Singleton.class)) {
					return true;
				} else {
					throw new IllegalStateException(
							"Only Singletons can be Services");
				}
			} else {
				return false;
			}
		}
	}

	@Override
	protected void configure() {
		final ServicesRegistry registry = new ServicesRegistry();
		bindListener(new ServiceMatcher(), new ServicesListener(
				getProvider(ServicesManager.class), registry));
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Start.class),
				new MethodInterceptor() {
					@Override
					public Object invoke(MethodInvocation invocation)
							throws Throwable {
						Object result = null;
						try {
							result = invocation.proceed();
						} catch (Throwable e) {
							registry.updateServiceStatus(invocation.getThis(),
									Status.FAILED);
							throw e;
						}
						registry.updateServiceStatus(invocation.getThis(), Status.STARTED);
						return result;
					}
				});
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Stop.class),
				new MethodInterceptor() {
					@Override
					public Object invoke(MethodInvocation invocation)
							throws Throwable {
						Object result = null;
						try {
							result = invocation.proceed();
						} catch (Throwable e) {
							registry.updateServiceStatus(invocation.getThis(),
									Status.FAILED);
							throw e;
						}
						registry.updateServiceStatus(invocation.getThis(), Status.HALTED);
						return result;
					}
				});
		bind(ServicesRegistry.class).toInstance(registry);
		bind(ServicesManager.class);
	}

}
