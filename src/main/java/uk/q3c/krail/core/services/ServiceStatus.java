/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.services;

import org.joda.time.DateTime;

public class ServiceStatus {

    private Service.Status currentStatus;
    private DateTime lastStartTime;
    private DateTime lastStopTime;
    private Service.Status previousStatus;
    private Service service;
    private DateTime statusChangeTime;

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
