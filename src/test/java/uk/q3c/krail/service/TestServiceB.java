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

package uk.q3c.krail.service;

import com.google.inject.Inject;
import uk.q3c.krail.eventbus.MessageBus;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.i18n.test.TestLabelKey;
import uk.q3c.util.guice.SerializationSupport;

public class TestServiceB extends AbstractService {

    private TestServiceC serviceC;

    @Inject
    protected TestServiceB(Translate translate, MessageBus globalBusProvider, SerializationSupport serializationSupport) {
        super(translate, globalBusProvider, serializationSupport);
    }

    @Override
    public I18NKey getNameKey() {
        return TestLabelKey.ServiceB;
    }

    @Override
    protected void doStop() {

    }

    @Override
    protected void doStart() {

    }
}
