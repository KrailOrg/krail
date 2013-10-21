package uk.co.q3c.v7.base.guice.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.google.inject.Singleton;

@Singleton
public class ServicesRegistry {

	public static enum Status {
		INITIAL, STARTED, HALTED, FAILED
	}

	public static Method getMethodAnnotatedWith(final Class<?> type,
			final Class<? extends Annotation> annotation) {
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

	private final List<Class<?>> servicesTypes;
	private final Map<Object, ServiceData> services;

	public ServicesRegistry() {
		super();
		this.servicesTypes = new LinkedList<>();
		this.services = new WeakHashMap<>();
	}

	public void registerType(Class<?> type) {
		this.servicesTypes.add(type);
	}
	
	public ServiceData register(Object service) {
		ServiceData data = new ServiceData(service, Status.INITIAL,
				getMethodAnnotatedWith(service.getClass(), Start.class),
				getMethodAnnotatedWith(service.getClass(), Stop.class));
		this.services.put(service, data);
		return data;
	}

	public Collection<ServiceData> getServices() {
		return services.values();
	}
	
	public ServiceData getServiceData(Object service) {
		return services.get(service);
	}
}
