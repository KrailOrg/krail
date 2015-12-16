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

import net.engio.mbassy.bus.common.PubSubSupport;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LabelKey;

import static uk.q3c.krail.core.services.Service.State.*;

/**
 * Created by David Sowerby on 01/11/15.
 */
public class MockService implements Service {

    private int callsToStart = 0;
    private int callsToStop = 0;
    private I18NKey descriptionKey;
    private boolean failToStart;
    private boolean failToStop;
    private PubSubSupport<BusMessage> globalBus;
    private int instance;
    private I18NKey nameKey = LabelKey.Yes;
    private int startDelay;
    private State state;
    private int stopDelay;

    public int getCallsToStart() {
        return callsToStart;
    }

    public int getCallsToStop() {
        return callsToStop;
    }

    @Override
    public ServiceStatus start() {
        callsToStart++;
        try {
            Thread.sleep(startDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        state = failToStart ? FAILED_TO_START : STARTED;
        return new ServiceStatus(this, state);
    }


    public ServiceStatus stop() {
        return stop(STOPPED);
    }

    @Override
    public ServiceStatus stop(Service.State reasonToStop) {
        callsToStop++;
        try {
            Thread.sleep(stopDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        state = failToStop ? FAILED_TO_STOP : reasonToStop;
        return new ServiceStatus(this, state);
    }

    @Override
    public ServiceStatus fail() {
        return null;
    }

    @Override
    public String getDescription() {
        return "description";
    }

    @Override
    public State getState() {
        return state;
    }


    @Override
    public boolean isStarted() {
        return state == STARTED;
    }

    @Override
    public boolean isStopped() {
        return Service.stoppedStates.contains(state);
    }


    @Override
    public I18NKey getNameKey() {
        return nameKey;
    }



    @Override
    public I18NKey getDescriptionKey() {
        return descriptionKey;
    }

    @Override
    public void setDescriptionKey(I18NKey descriptionKey) {

        this.descriptionKey = descriptionKey;
    }

    @Override
    public int getInstance() {
        return instance;
    }

    @Override
    public void setInstance(int instance) {
        this.instance = instance;
    }

    public MockService failToStart(final boolean failToStart) {
        this.failToStart = failToStart;
        return this;
    }

    public MockService startDelay(final int startDelay) {
        this.startDelay = startDelay;
        return this;
    }

    public MockService failToStop(final boolean failToStop) {
        this.failToStop = failToStop;
        return this;
    }

    public MockService stopDelay(final int stopDelay) {
        this.stopDelay = stopDelay;
        return this;
    }

    public MockService nameKey(final I18NKey nameKey) {
        this.nameKey = nameKey;
        return this;
    }

    public MockService instance(final int instance) {
        this.instance = instance;
        return this;
    }


}
