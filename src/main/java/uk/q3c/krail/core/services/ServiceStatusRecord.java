/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.services;

import java.time.LocalDateTime;

public class ServiceStatusRecord {

    private Service.State currentState;
    private LocalDateTime lastStartTime;
    private LocalDateTime lastStopTime;
    private Service.State previousState;
    private Service service;
    private LocalDateTime statusChangeTime;


    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Service.State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(Service.State currentState) {
        this.currentState = currentState;
    }

    public Service.State getPreviousState() {
        return previousState;
    }

    public void setPreviousState(Service.State previousState) {
        this.previousState = previousState;
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
