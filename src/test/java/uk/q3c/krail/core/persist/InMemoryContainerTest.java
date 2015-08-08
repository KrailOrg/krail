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

package uk.q3c.krail.core.persist;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.data.DataModule;
import uk.q3c.krail.core.data.OptionStringConverter;
import uk.q3c.krail.core.user.opt.DefaultInMemoryOptionStore;
import uk.q3c.krail.core.user.opt.InMemoryOptionDao;
import uk.q3c.krail.core.user.opt.InMemoryOptionStore;
import uk.q3c.krail.core.user.opt.OptionEntity;
import uk.q3c.krail.core.user.opt.cache.OptionCacheKey;
import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.core.user.profile.UserHierarchy;
import uk.q3c.krail.core.view.component.LocaleContainer;
import uk.q3c.krail.i18n.*;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({DataModule.class})
public class InMemoryContainerTest {


    InMemoryOptionStore optionStore;

    InMemoryPatternStore patternStore;

    InMemoryOptionDao optionDao;

    InMemoryPatternDao patternDao;
    @Mock
    UserHierarchy userHierarchy;
    @Inject
    private OptionStringConverter optionStringConverter;

    @Before
    public void setup() {
        optionStore = new DefaultInMemoryOptionStore(optionStringConverter);
        optionDao = new InMemoryOptionDao(optionStore);
        patternStore = new DefaultInMemoryPatternStore();
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

        OptionCacheKey optionCacheKey1 = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, 0, LocaleContainer.optionKeyFlagSize);
        OptionCacheKey optionCacheKey2 = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, 0, DefaultCurrentLocale.optionPreferredLocale);
        optionDao.write(optionCacheKey1, Optional.of("23"));
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