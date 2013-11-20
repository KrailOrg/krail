package uk.co.q3c.v7.base.guice.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.inject.Singleton;

@Singleton
public class ServicesRegistry {

	public enum Status {
		INITIAL, STARTED, STOPPED, FAILED
	}

	public static Method getMethodAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
		Class<?> klass = type;
		assert annotation != null;
		while (klass != Object.class) { // need to iterated thought hierarchy in
										// order to retrieve methods from above
										// the current instance
			for (final Method m : klass.getDeclaredMethods()) {
				if (m.isAnnotationPresent(annotation)) {
					return m;
				}
			}
			// move to the upper class in the hierarchy in search for more
			// methods
			klass = klass.getSuperclass();
		}
		return null;
	}

	private final Map<Object, ServiceData> services;

	public ServicesRegistry() {
		super();
		this.services = new WeakHashMap<>();
	}

	public ServiceData register(Object service) {
		Method startMethod = getMethodAnnotatedWith(service.getClass(), Start.class);
		Method stopMethod = getMethodAnnotatedWith(service.getClass(), Stop.class);
		validateService(service, startMethod, stopMethod);
		Status status = Status.INITIAL;
		ServiceData data = new ServiceData(service, status, startMethod, stopMethod);
		this.services.put(service, data);
		return data;
	}

	private void validateService(Object service, Method startMethod, Method stopMethod) {
		Class<? extends Object> serviceClass = service.getClass();
		if (!serviceClass.isAnnotationPresent(Service.class)) {
			throw new IllegalStateException("A service class must be annotated with @Service");
		}

		if (startMethod == null) {
			throw new IllegalStateException("A service must have a public method annotated with @Start");
		}

		if (stopMethod == null) {
			throw new IllegalStateException("A service must have a public method annotated with @Stop");
		}

		if (startMethod.getReturnType() != Void.TYPE || startMethod.getParameterTypes().length != 0) {
			throw new IllegalStateException("The method annotated with @Start must have no parameters and return void");
		}

		if (stopMethod.getReturnType() != Void.TYPE || stopMethod.getParameterTypes().length != 0) {
			throw new IllegalStateException("The method annotated with @Stop must have no parameters and return void");
		}
	}

	/**
	 * Returns a safe copy of the service objects
	 * 
	 * @return
	 */
	public List<ServiceData> getServices() {
		return new ArrayList<ServiceData>(services.values());
	}

	public ServiceData getServiceData(Object service) {
		return services.get(service);
	}
}