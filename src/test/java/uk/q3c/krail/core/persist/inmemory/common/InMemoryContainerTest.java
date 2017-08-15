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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mycila.testing.junit.MycilaJunitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.i18n.VaadinCurrentLocale;
import uk.q3c.krail.core.vaadin.DataModule;
import uk.q3c.krail.core.view.component.LocaleContainer;
import uk.q3c.krail.i18n.persist.PatternCacheKey;
import uk.q3c.krail.option.RankOption;
import uk.q3c.krail.option.UserHierarchy;
import uk.q3c.krail.option.persist.OptionCacheKey;
import uk.q3c.krail.option.persist.OptionDao;
import uk.q3c.krail.option.persist.OptionSource;
import uk.q3c.krail.option.persist.dao.DefaultOptionDao;
import uk.q3c.krail.option.persist.inmemory.InMemoryOptionStore;
import uk.q3c.krail.option.persist.inmemory.OptionEntity;
import uk.q3c.krail.option.persist.inmemory.dao.InMemoryOptionDaoDelegate;
import uk.q3c.krail.option.test.TestOptionModule;
import uk.q3c.krail.persist.inmemory.InMemoryContainer;
import uk.q3c.krail.persist.inmemory.InMemoryModule;
import uk.q3c.krail.persist.inmemory.InMemoryPatternDao;
import uk.q3c.krail.persist.inmemory.InMemoryPatternStore;
import uk.q3c.krail.persist.inmemory.entity.PatternEntity;
import uk.q3c.util.UtilModule;
import uk.q3c.util.data.DataConverter;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MycilaJunitRunner.class)
public class InMemoryContainerTest {


    InMemoryOptionStore optionStore;

    InMemoryPatternStore patternStore;

    OptionDao optionDao;

    InMemoryPatternDao patternDao;

    @Mock
    UserHierarchy userHierarchy;

    @Mock
    OptionSource optionSource;

    InMemoryOptionDaoDelegate inMemoryOptionDaoDelegate;

    DataConverter optionElementConverter;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new InMemoryModule().provideOptionDao(), new TestOptionModule(), new VaadinSessionScopeModule(), new UtilModule());
        optionStore = injector.getInstance(InMemoryOptionStore.class);
        patternStore = injector.getInstance(InMemoryPatternStore.class);

        patternDao = injector.getInstance(InMemoryPatternDao.class);
        inMemoryOptionDaoDelegate = injector.getInstance(InMemoryOptionDaoDelegate.class);
        optionElementConverter = injector.getInstance(DataConverter.class);
        when(optionSource.getActiveDao()).thenReturn(inMemoryOptionDaoDelegate);
        optionDao = new DefaultOptionDao(optionElementConverter, optionSource);
        patternDao = new InMemoryPatternDao(patternStore);
        when(userHierarchy.persistenceName()).thenReturn("SimpleUserHierarchy");
        when(userHierarchy.rankName(0)).thenReturn("system");
    }

    @Test
    public void createOptionContainer() {
        //given

        //when
        InMemoryContainer<OptionEntity> container = new InMemoryContainer<>(OptionEntity.class, optionStore, patternStore);
        //then
        assertThat(container).isNotNull();
    }

    @Test
    public void createPatternContainer() {
        //given

        //when
        InMemoryContainer<PatternEntity> container = new InMemoryContainer<>(PatternEntity.class, optionStore, patternStore);
        //then
        assertThat(container).isNotNull();
        //then
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createWithInvalidEntity() {
        //given

        //when
        new InMemoryContainer<>(DataModule.class, optionStore, patternStore);
        //then
    }


    @Test
    public void loadOptionContainer() {
        //given

        OptionCacheKey<Integer> optionCacheKey1 = new OptionCacheKey<>(userHierarchy, RankOption.SPECIFIC_RANK, 0, LocaleContainer.optionKeyFlagSize);
        OptionCacheKey<Locale> optionCacheKey2 = new OptionCacheKey<>(userHierarchy, RankOption.SPECIFIC_RANK, 0, VaadinCurrentLocale.optionPreferredLocale);
        optionDao.write(optionCacheKey1, Optional.of(23));
        optionDao.write(optionCacheKey2, Optional.of(Locale.CANADA_FRENCH));
        //when
        InMemoryContainer<OptionEntity> container = new InMemoryContainer<>(OptionEntity.class, optionStore, patternStore);
        //then
        assertThat(container.getItemIds()).hasSize(2);
    }

    @Test
    public void loadPatternContainer() {
        //given
        PatternCacheKey patternCacheKey1 = new PatternCacheKey(LabelKey.Yes, Locale.CANADA_FRENCH);
        PatternCacheKey patternCacheKey2 = new PatternCacheKey(LabelKey.No, Locale.CANADA_FRENCH);
        patternDao.write(patternCacheKey1, "maybe");
        patternDao.write(patternCacheKey2, "maybe not");
        //when
        InMemoryContainer<PatternEntity> container = new InMemoryContainer<>(PatternEntity.class, optionStore, patternStore);
        //then
        assertThat(container.getItemIds()).hasSize(2);
    }
}