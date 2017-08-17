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

package uk.q3c.krail.core.persist.inmemory.option;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.option.inmemory.OptionEntity;
import uk.q3c.krail.option.test.TestOptionModule;
import uk.q3c.krail.persist.ContainerType;
import uk.q3c.krail.persist.VaadinContainerProvider;
import uk.q3c.krail.persist.inmemory.InMemory;
import uk.q3c.krail.persist.inmemory.InMemoryModule;
import uk.q3c.util.UtilModule;

import static org.assertj.core.api.Assertions.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestOptionModule.class, VaadinSessionScopeModule.class, UtilModule.class})
public class InMemoryContainerProviderTest_OptionOnly {

    @Inject
    @InMemory
    VaadinContainerProvider<BeanItemContainer> inMemoryContainerProvider;

    /**
     * This will fail if the INMemoryModule is not set to provide either the OptionDaoDelegate, PatternDao or both
     */
    @Test
    public void name() {
        //given

        //when
        Container container = inMemoryContainerProvider.get(OptionEntity.class, ContainerType.CACHED);
        //then
        assertThat(container).isNotNull();
    }

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new InMemoryModule().provideOptionDao();
    }
}