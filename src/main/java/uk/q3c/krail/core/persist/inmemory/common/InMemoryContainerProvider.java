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

package uk.q3c.krail.core.persist.inmemory.common;

import com.google.inject.Inject;
import com.vaadin.data.Container;
import uk.q3c.krail.core.i18n.InMemoryPatternStore;
import uk.q3c.krail.core.persist.common.common.ContainerType;
import uk.q3c.krail.core.persist.common.common.VaadinContainerProvider;
import uk.q3c.krail.core.user.opt.InMemoryOptionStore;

/**
 * Created by David Sowerby on 30/06/15.
 */
public class InMemoryContainerProvider implements VaadinContainerProvider {

    private InMemoryOptionStore optionStore;
    private InMemoryPatternStore patternStore;

    @Inject
    protected InMemoryContainerProvider(InMemoryOptionStore optionStore, InMemoryPatternStore patternStore) {
        this.optionStore = optionStore;
        this.patternStore = patternStore;
    }

    @Override
    public <E> Container get(Class<E> entityClass, ContainerType containerType) {
        return new InMemoryContainer<>(entityClass, optionStore, patternStore);
    }
}
