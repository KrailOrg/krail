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

package uk.q3c.krail.core.option;

import com.google.common.cache.CacheStats;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.i18n.Translate;
import uk.q3c.krail.core.persist.cache.common.GuavaCacheConfiguration;
import uk.q3c.krail.core.persist.cache.option.DefaultOptionCache;
import uk.q3c.krail.core.persist.cache.option.DefaultOptionCacheLoader;
import uk.q3c.krail.core.persist.cache.option.DefaultOptionCacheProvider;
import uk.q3c.krail.core.persist.cache.option.OptionCacheKey;
import uk.q3c.krail.core.persist.common.common.KrailPersistenceUnitHelper;
import uk.q3c.krail.core.persist.common.common.OptionDaoProviders;
import uk.q3c.krail.core.persist.common.common.PersistenceInfo;
import uk.q3c.krail.core.persist.common.option.*;
import uk.q3c.krail.core.persist.inmemory.option.DefaultInMemoryOptionStore;
import uk.q3c.krail.core.persist.inmemory.option.InMemoryOptionDaoDelegate;
import uk.q3c.krail.core.persist.inmemory.option.InMemoryOptionStore;
import uk.q3c.krail.core.shiro.SubjectIdentifier;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.core.user.profile.SimpleUserHierarchy;
import uk.q3c.krail.core.user.profile.UserHierarchy;
import uk.q3c.krail.core.view.component.LocaleContainer;
import uk.q3c.krail.testutil.TestOptionModule;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Running this test through the debugger sometimes causes random failures - running normally doesn't
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestOptionModule.class, VaadinSessionScopeModule.class})
public class Option_IntegrationTest {

    Map<Class<? extends Annotation>, PersistenceInfo<?>> optionDaoProviders = new HashMap<>();

    DefaultOptionCacheLoader cacheLoader;
    DefaultOption option;

    DefaultOptionCache optionCache;

    @Inject
    DefaultOptionCacheProvider cacheProvider;

    @Inject
    DefaultInMemoryOptionStore optionStore;

    @Inject
    OptionDao optionDao;

    @Mock
    LocaleContainer localeContainer;
    @Mock
    Subject subject1;

    @Mock
    Subject subject2;
    @Mock
    Translate translate;
    OptionKey<Integer> key1 = LocaleContainer.optionKeyFlagSize;
    OptionKey<Integer> key3 = new OptionKey<>(133, LocaleContainer.class, LabelKey.Alphabetic_Ascending);
    @Mock
    PersistenceInfo persistenceInfo;
    private UserHierarchy hierarchy;
    @Mock
    private SubjectIdentifier subjectIdentifier;
    @Mock
    private SubjectProvider subjectProvider;

    @Before
    public void setup() {

        optionStore.clear();
        when(subjectIdentifier.userId()).thenReturn("fbaton");
        when(subjectProvider.get()).thenReturn(subject1);
        when(subject1.isPermitted(any(OptionPermission.class))).thenReturn(true);
        when(subject2.isPermitted(any(OptionPermission.class))).thenReturn(true);
        hierarchy = new SimpleUserHierarchy(subjectProvider, subjectIdentifier, translate);

        cacheLoader = new DefaultOptionCacheLoader(optionDao);
        optionCache = new DefaultOptionCache(optionDao, cacheProvider);
        option = new DefaultOption(optionCache, hierarchy, subjectProvider, subjectIdentifier);
    }


    @Test
    public void putAndGet_user_authenticated() {
        //given
        when(subject1.isAuthenticated()).thenReturn(true);

        //when
        option.set(key1, 3);
        //then
        assertThat(option.get(key1)).isEqualTo(3);
    }

    @Test
    public void putAndGet_user_not_authenticated() {
        //given
        when(subject1.isAuthenticated()).thenReturn(false);

        //when
        option.set(key1, 3);
        //then
        assertThat(option.get(key1)).isEqualTo(3);
    }

    @Test
    public void put_and_get_override() {
        //given
        when(subject1.isAuthenticated()).thenReturn(true);
        //when
        option.set(key1, 3);
        option.set(key1, 1, 7);
        //then
        assertThat(option.get(key1)).isEqualTo(3);
        assertThat(option.get(key1)).isEqualTo(3);
    }


    @Test
    public void defaultValue_cache_empty_persistence_empty() {
        //given
        when(subject1.isAuthenticated()).thenReturn(true);
        //when

        //then
        assertThat(option.get(key1)).isEqualTo(32);
    }

    @Test
    public void valueIsSet() {
        //given
        when(subject1.isAuthenticated()).thenReturn(true);
        //when
        option.set(key1, 3);
        //then
        assertThat(option.get(key1)).isEqualTo(3);
    }

    @Test
    public void stats() {
        //given

        //when
        CacheStats actual = optionCache.stats();
        //then
        assertThat(actual).isNotNull();
        assertThat(actual).isInstanceOf(CacheStats.class);
    }


    @Test
    public void cacheHits() {
        //given
        when(subjectProvider.get()).thenReturn(subject1);
        when(subject1.isAuthenticated()).thenReturn(true);
        when(subjectIdentifier.userId()).thenReturn("fbaton");
        DefaultOption option2 = new DefaultOption(optionCache, hierarchy, subjectProvider, subjectIdentifier);
        //when
        option2.set(key1, 3);
        Integer actual = option2.get(key1);
        //then
        assertThat(actual).isEqualTo(3);
        option2.get(key1); //populate
        option2.get(key1); //cache hit
        option2.get(key1); //cache hit
        option2.get(key1); //cache hit
        option2.get(key1); //cache hit
        assertThat(optionCache.cacheSize()).isEqualTo(2); // creates a highest and a specific cache entry
        assertThat(optionCache.stats()
                              .hitCount()).isEqualTo(5);


    }

    /**
     * When a value is written to the cache, must invalidate the highest and lowest entry for the same OptionKey
     */
    @SuppressWarnings("unchecked")
    @Test
    public void write_to_Cache_invalidate() {
        //given
        when(subjectProvider.get()).thenReturn(subject1);
        when(subject1.isAuthenticated()).thenReturn(true);
        when(subjectIdentifier.userId()).thenReturn("fbaton");
        OptionCacheKey<Integer> highestKey = new OptionCacheKey<>(hierarchy, RankOption.HIGHEST_RANK, key3);
        OptionCacheKey<Integer> lowestKey = new OptionCacheKey<>(hierarchy, RankOption.LOWEST_RANK, key3);
        OptionCacheKey<Integer> specificKey = new OptionCacheKey<>(hierarchy, RankOption.SPECIFIC_RANK, 1, key3);
        optionDao.write(specificKey, Optional.of(236));
        //when
        optionCache.get(Optional.of(highestKey.getOptionKey()
                                              .getDefaultValue()), highestKey);
        optionCache.get(Optional.of(lowestKey.getOptionKey()
                                             .getDefaultValue()), lowestKey);
        Optional<Integer> actualHigh = (Optional<Integer>) optionCache.getIfPresent(highestKey);
        Optional<Integer> actualLow = (Optional<Integer>) optionCache.getIfPresent(lowestKey);
        //then
        assertThat(actualHigh).isNotEqualTo(Optional.empty());
        assertThat(actualLow).isNotEqualTo(Optional.empty());
        assertThat(actualHigh.get()).isEqualTo(236);
        assertThat(actualLow.get()).isEqualTo(236);

        //when write occurs cache should invalidate highest and lowest
        optionCache.write(specificKey, Optional.of(44));
        actualHigh = (Optional<Integer>) optionCache.getIfPresent(highestKey);
        //noinspection unchecked
        actualLow = (Optional<Integer>) optionCache.getIfPresent(lowestKey);
        Optional<Integer> actualSpecific = (Optional<Integer>) optionCache.getIfPresent(specificKey);
        //then highest and lowest invalidated
        assertThat(actualSpecific).isEqualTo(Optional.of(44));
        assertThat(actualHigh).isNull();
        assertThat(actualLow).isNull();

    }

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                GuavaCacheConfiguration cacheConfig = new GuavaCacheConfiguration().recordStats();

                bind(OptionDaoDelegate.class).annotatedWith(InMemory.class)
                                             .to(InMemoryOptionDaoDelegate.class);
                bind(OptionSource.class).to(DefaultOptionSource.class);

                bind(KrailPersistenceUnitHelper.annotationClassLiteral()).annotatedWith(DefaultActiveOptionSource.class)
                                                                         .toInstance(InMemory.class);
                TypeLiteral<Map<Class<? extends Annotation>, PersistenceInfo<?>>> setAnnotationTypeLiteral = new TypeLiteral<Map<Class<? extends Annotation>, PersistenceInfo<?>>>() {
                };
                optionDaoProviders.put(InMemory.class, persistenceInfo);

                bind(setAnnotationTypeLiteral).annotatedWith(OptionDaoProviders.class)
                                              .toInstance(optionDaoProviders);


                bind(OptionDaoDelegate.class).annotatedWith(InMemory.class)
                                             .to(InMemoryOptionDaoDelegate.class);
                bind(InMemoryOptionStore.class).to(DefaultInMemoryOptionStore.class);
            }

        };
    }


}