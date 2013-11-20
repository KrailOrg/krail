package uk.co.q3c.v7.base.guice.services;

import java.lang.reflect.Method;

import uk.co.q3c.v7.base.guice.services.ServicesRegistry.Status;

public class ServiceData {
	private final Object service;
	private Status status;
	private Method startMethod;
	private Method stopMethod;

	public ServiceData(Object service, Status status, Method startMethod, Method stopMethod) {
		super();
		assert service != null;
		this.service = service;
		this.status = status;
		this.startMethod = startMethod;
		this.stopMethod = stopMethod;
	}

	public Object getService() {
		return service;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Method getStartMethod() {
		return startMethod;
	}

	public void setStartMethod(Method startMethod) {
		this.startMethod = startMethod;
	}

	public Method getStopMethod() {
		return stopMethod;
	}

	public void setStopMethod(Method stopMethod) {
		this.stopMethod = stopMethod;
	}
}