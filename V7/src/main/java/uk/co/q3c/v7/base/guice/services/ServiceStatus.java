package uk.co.q3c.v7.base.guice.services;

import org.joda.time.DateTime;

public class ServiceStatus {

	private Service.Status currentStatus;
	private Service.Status previousStatus;
	private DateTime lastStartTime;
	private DateTime lastStopTime;
	private DateTime statusChangeTime;
	private Service service;

	public Service.Status getStatus() {
		return currentStatus;
	}

	public void setStatus(Service.Status status) {
		this.currentStatus = status;
	}

	public DateTime getStartTime() {
		return lastStartTime;
	}

	public void setStartTime(DateTime startTime) {
		this.lastStartTime = startTime;
	}

	public DateTime getStopTime() {
		return lastStopTime;
	}

	public void setStopTime(DateTime stopTime) {
		this.lastStopTime = stopTime;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public Service.Status getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(Service.Status currentStatus) {
		this.currentStatus = currentStatus;
	}

	public Service.Status getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(Service.Status previousStatus) {
		this.previousStatus = previousStatus;
	}

	public DateTime getLastStartTime() {
		return lastStartTime;
	}

	public void setLastStartTime(DateTime lastStartTime) {
		this.lastStartTime = lastStartTime;
	}

	public DateTime getLastStopTime() {
		return lastStopTime;
	}

	public void setLastStopTime(DateTime lastStopTime) {
		this.lastStopTime = lastStopTime;
	}

	public DateTime getStatusChangeTime() {
		return statusChangeTime;
	}

	public void setStatusChangeTime(DateTime statusChangeTime) {
		this.statusChangeTime = statusChangeTime;
	}

}
