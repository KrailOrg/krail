package uk.co.q3c.v7.base.guice.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Singleton;

@Singleton
public class ServicesRegistry {
	
	public static enum Status {
		INITIAL,
		STARTED,
		HALTED,
		FAILED
	}
	
	private final List<Class<?>> servicesType;
	private final Map<Object, Status> services;
	
	public ServicesRegistry() {
		super();
		this.servicesType = new LinkedList<>();
		this.services = new HashMap<>();
	}
	
	public boolean add(Class<?> type) {
		return this.servicesType.add(type);
	}
	
	public Iterable<Class<?>> getServicesTye() {
		return servicesType;
	}
	
	public void add(Object service) {
		this.services.put(service, Status.INITIAL);
	}
	
	public void updateServiceStatus(Object service, Status status){
		if(!services.containsKey(service)){
			throw new IllegalStateException("The service is not registered");
		}
		services.put(service, status);
	}

	public Set<Object> getServices() {
		return services.keySet();
	}
}
