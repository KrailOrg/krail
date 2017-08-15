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

package uk.q3c.krail.option.persist.inmemory.container;

import com.google.inject.Inject;
import com.vaadin.data.util.BeanItemContainer;
import uk.q3c.krail.option.persist.inmemory.InMemoryOptionStore;
import uk.q3c.krail.option.persist.inmemory.OptionEntity;

/**
 * An extension of {@link BeanItemContainer} to handle in memory option entities only.
 * <p>
 * Created by David Sowerby on 30/06/15.
 */
public class InMemoryOptionContainer extends BeanItemContainer<OptionEntity> {


    private final InMemoryOptionStore optionStore;

    /**
     * {@inheritDoc}
     */
    @Inject
    protected InMemoryOptionContainer(InMemoryOptionStore optionStore) {
        super(OptionEntity.class);
        this.optionStore = optionStore;
        refresh();
    }

    public final void refresh() {
        this.removeAllItems();
        this.addAll(optionStore.asEntities());
    }

}
