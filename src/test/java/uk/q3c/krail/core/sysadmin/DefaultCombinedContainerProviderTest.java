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

package uk.q3c.krail.core.sysadmin;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.data.Container;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.data.DataModule;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.persist.ContainerType;
import uk.q3c.krail.core.persist.PersistenceInfo;
import uk.q3c.krail.core.persist.VaadinContainerProvider;
import uk.q3c.krail.core.user.opt.InMemory;
import uk.q3c.krail.core.user.opt.OptionEntity;
import uk.q3c.krail.core.user.opt.OptionException;
import uk.q3c.krail.i18n.I18NException;
import uk.q3c.krail.i18n.PatternEntity;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultCombinedContainerProviderTest {

    DefaultCombinedContainerProvider provider;


    @Mock
    Injector injector;

    @Mock
    VaadinContainerProvider containerProvider;

    Map<Class<? extends Annotation>, PersistenceInfo<?>> optionDaoProviders;
    Map<Class<? extends Annotation>, PersistenceInfo<?>> patternDaoProviders;

    @Mock
    PersistenceInfo persistenceInfo;

    @Mock
    Container container;


    @Before
    public void setup() {
        optionDaoProviders = new HashMap<>();
        patternDaoProviders = new HashMap<>();
        provider = new DefaultCombinedContainerProvider(injector, optionDaoProviders, patternDaoProviders);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void invalid_entity_class() {
        //given

        //when
        provider.getContainer(InMemory.class, DataModule.class);
        //then
        assertThat(true).isFalse();
    }

    @Test
    public void option_good() {
        //given
        Key<VaadinContainerProvider> expectedKey = Key.get(VaadinContainerProvider.class, InMemory.class);
        when(containerProvider.get(OptionEntity.class, ContainerType.CACHED)).thenReturn(container);
        when(injector.getInstance(expectedKey)).thenReturn(containerProvider);
        optionDaoProviders.put(InMemory.class, persistenceInfo);
        //when
        Container actual = provider.getContainer(InMemory.class, OptionEntity.class);
        //then
        verify(injector).getInstance(expectedKey);
        assertThat(actual).isEqualTo(container);
    }

    @Test(expected = OptionException.class)
    public void option_wrong_annotation() {
        //given
        Key<VaadinContainerProvider> expectedKey = Key.get(VaadinContainerProvider.class, InMemory.class);
        when(containerProvider.get(OptionEntity.class, ContainerType.CACHED)).thenReturn(container);
        when(injector.getInstance(expectedKey)).thenReturn(containerProvider);
        optionDaoProviders.put(InMemory.class, persistenceInfo);
        //when
        provider.getContainer(SessionBus.class, OptionEntity.class);
        //then
    }

    @Test
    public void pattern_good() {
        //given
        Key<VaadinContainerProvider> expectedKey = Key.get(VaadinContainerProvider.class, InMemory.class);
        when(containerProvider.get(PatternEntity.class, ContainerType.CACHED)).thenReturn(container);
        when(injector.getInstance(expectedKey)).thenReturn(containerProvider);
        optionDaoProviders.put(InMemory.class, persistenceInfo);
        //when
        Container actual = provider.getContainer(InMemory.class, PatternEntity.class);
        //then
        verify(injector).getInstance(expectedKey);
        assertThat(actual).isEqualTo(container);
    }

    @Test(expected = I18NException.class)
    public void pattern_wrong_annotation() {
        //given
        Key<VaadinContainerProvider> expectedKey = Key.get(VaadinContainerProvider.class, InMemory.class);
        when(containerProvider.get(PatternEntity.class, ContainerType.CACHED)).thenReturn(container);
        when(injector.getInstance(expectedKey)).thenReturn(containerProvider);
        optionDaoProviders.put(InMemory.class, persistenceInfo);
        //when
        provider.getContainer(SessionBus.class, PatternEntity.class);
        //then
    }
}