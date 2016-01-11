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

package uk.q3c.krail.core.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.q3c.krail.core.eventbus.GlobalBusProvider;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.TestLabelKey;
import uk.q3c.krail.i18n.Translate;

@Singleton
public class TestServiceA extends AbstractService {

    @Dependency
    private TestServiceB serviceB;

    @Inject
    protected TestServiceA(Translate translate,
                           GlobalBusProvider globalBusProvider, RelatedServicesExecutor servicesExecutor) {
        super(translate, globalBusProvider, servicesExecutor);
    }

    @Override
    public I18NKey getNameKey() {
        return TestLabelKey.ServiceA;
    }

    @Override
    protected void doStop() throws Exception {

    }

    @Override
    protected void doStart() throws Exception {

    }
}
