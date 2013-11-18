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

		if (this.startMethod == null && this.stopMethod == null) {
			throw new IllegalStateException(
					"A service must have at least a public method annotated with @Start or @Stop");
		}
		if (this.startMethod != null
				&& (this.startMethod.getReturnType() != Void.TYPE || this.startMethod.getParameterTypes().length != 0)) {
			throw new IllegalStateException("The method annotated with @Start must have no parameters and return void");
		}
		if (this.stopMethod != null
				&& (this.stopMethod.getReturnType() != Void.TYPE || this.stopMethod.getParameterTypes().length != 0)) {
			throw new IllegalStateException("The method annotated with @Stop must have no parameters and return void");
		}
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