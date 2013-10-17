package uk.co.q3c.v7.base.guice.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class ServicesManager {
	
	public static Method getMethodAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
	    Class<?> klass = type;
	    assert annotation != null;
	    while (klass != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above the current instance    
	        for (final Method m : klass.getDeclaredMethods()) {
	            if (m.isAnnotationPresent(annotation)) {
	                 return m;
	            }
	        }
	        // move to the upper class in the hierarchy in search for more methods
	        klass = klass.getSuperclass();
	    }
	    return null;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ServicesManager.class);
	
	private final Injector injector;
	private final ServicesRegistry servicesRegistry;

	private Boolean started = null;

	@Inject
	public ServicesManager(Injector injector, ServicesRegistry servicesRegistry) {
		this.injector = injector;
		this.servicesRegistry = servicesRegistry;
	}

	public void start() {
		LOGGER.trace("start()");
		if (started != null) {
			if (started == false) {
				throw new IllegalStateException(
						"A recursive call has been detected while starting services");
			} else {
				throw new IllegalStateException(
						"Services has been already started, cannot start again");
			}
		}
		started = false;
		for (Class<?> serviceType : servicesRegistry.getServicesTye()) {
			Service service = serviceType.getAnnotation(Service.class);
			if (service != null && service.startAsSoonAsPossible() == true) {
				LOGGER.trace("Starting service (startAsSoonAsPossible) {}", serviceType);
				// create an instance if not already created
				injector.getInstance(serviceType);
			}
		}
		started = true;
	}

	public void stop() {
		for (Object service : servicesRegistry.getServices()) {
			try {
				stopService(service);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	void startService(Object service) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		LOGGER.trace("Starting service {} using instance {}", service.getClass().getSimpleName(), service);
		servicesRegistry.add(service);
		Method start = getMethodAnnotatedWith(service.getClass(), Start.class);
		if (start != null) {
			start.invoke(service, null);
		}
	}

	void stopService(Object service) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		LOGGER.trace("Halting service {} on instance {}", service.getClass().getSimpleName(), service);
		Method stop = getMethodAnnotatedWith(service.getClass(), Stop.class);
		if (stop != null) {
			stop.invoke(service, null);
		}
	}
}
