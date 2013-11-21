package uk.co.q3c.v7.base.guice.services;

import org.joda.time.DateTime;

import uk.co.q3c.v7.base.guice.services.Service.Status;

public class ServiceStatus {

	private Service.Status status = Status.INITIAL;
	private DateTime startTime;
	private DateTime stopTime;

	public Service.Status getStatus() {
		return status;
	}

	public void setStatus(Service.Status status) {
		this.status = status;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getStopTime() {
		return stopTime;
	}

	public void setStopTime(DateTime stopTime) {
		this.stopTime = stopTime;
	}

}
