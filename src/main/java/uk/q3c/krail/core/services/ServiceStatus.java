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

import java.time.LocalDateTime;

public class ServiceStatus {

    private Service.Status currentStatus;
    private LocalDateTime lastStartTime;
    private LocalDateTime lastStopTime;
    private Service.Status previousStatus;
    private Service service;
    private LocalDateTime statusChangeTime;

    public Service.Status getStatus() {
        return currentStatus;
    }

    public void setStatus(Service.Status status) {
        this.currentStatus = status;
    }

    public LocalDateTime getStartTime() {
        return lastStartTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.lastStartTime = startTime;
    }

    public LocalDateTime getStopTime() {
        return lastStopTime;
    }

    public void setStopTime(LocalDateTime stopTime) {
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

    public LocalDateTime getLastStartTime() {
        return lastStartTime;
    }

    public void setLastStartTime(LocalDateTime lastStartTime) {
        this.lastStartTime = lastStartTime;
    }

    public LocalDateTime getLastStopTime() {
        return lastStopTime;
    }

    public void setLastStopTime(LocalDateTime lastStopTime) {
        this.lastStopTime = lastStopTime;
    }

    public LocalDateTime getStatusChangeTime() {
        return statusChangeTime;
    }

    public void setStatusChangeTime(LocalDateTime statusChangeTime) {
        this.statusChangeTime = statusChangeTime;
    }

}
