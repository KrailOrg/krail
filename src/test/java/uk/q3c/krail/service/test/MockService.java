/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.service.test;

import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.eventbus.MessageBus;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.service.AbstractService;
import uk.q3c.krail.service.RelatedServiceExecutor;

/**
 * Created by David Sowerby on 01/11/15.
 */
public class MockService extends AbstractService {

    private int callsToStart = 0;
    private int callsToStop = 0;
    private boolean failToStart;
    private boolean failToStop;
    private int startDelay;
    private int stopDelay;

    protected MockService(Translate translate, MessageBus globalBusProvider, RelatedServiceExecutor servicesExecutor) {
        super(translate, globalBusProvider, servicesExecutor);
        setNameKey(LabelKey.Yes);
    }


    public int getCallsToStart() {
        return callsToStart;
    }

    public int getCallsToStop() {
        return callsToStop;
    }


    @Override
    protected void doStart() throws Exception {
        System.out.println("starting " + getNameKey());
        callsToStart++;
        try {
            Thread.sleep(startDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (failToStart) {
            throw new RuntimeException();
        }
    }



    @Override
    protected void doStop() throws Exception {
        System.out.println("stopping " + getNameKey());
        callsToStop++;
        if (failToStop) {
            throw new RuntimeException();
        }
        try {
            Thread.sleep(stopDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public MockService instance(final int instance) {
        setInstanceNumber(instance);
        return this;
    }


}
