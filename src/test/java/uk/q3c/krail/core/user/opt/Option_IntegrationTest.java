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

package uk.q3c.krail.core.user.opt;

import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
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
import uk.q3c.krail.core.data.DataModule;
import uk.q3c.krail.core.persist.ActiveOptionDao;
import uk.q3c.krail.core.persist.CoreOptionDaoProvider;
import uk.q3c.krail.core.persist.DefaultCoreOptionDaoProvider;
import uk.q3c.krail.core.persist.OptionDaoProviders;
import uk.q3c.krail.core.shiro.SubjectIdentifier;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.krail.core.user.opt.cache.*;
import uk.q3c.krail.core.user.profile.RankOption;
import uk.q3c.krail.core.user.profile.SimpleUserHierarchy;
import uk.q3c.krail.core.user.profile.UserHierarchy;
import uk.q3c.krail.core.view.component.LocaleContainer;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.Translate;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Running this test through the debugger sometimes causes random failures - running normally doesn't
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({DataModule.class})
public class Option_IntegrationTest {

    Set<Class<? extends Annotation>> optionDaoProviders = new HashSet<>();


    DefaultOptionCacheLoader cacheLoader;
    DefaultOption option;

    DefaultOptionCache optionCache;

    @Inject
    DefaultOptionCacheProvider cacheProvider;

    @Inject
    DefaultInMemoryOptionStore optionStore;

    @Inject
    CoreOptionDaoProvider daoProvider;

    @Inject
    InMemoryOptionDao dao;

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

        cacheLoader = new DefaultOptionCacheLoader(daoProvider);
        optionCache = new DefaultOptionCache(daoProvider, cacheProvider);
        option = new DefaultOption(optionCache, hierarchy, subjectProvider, subjectIdentifier);
    }


    @Test
    public void putAndGet_user_authenticated() {
        //given
        when(subject1.isAuthenticated()).thenReturn(true);

        //when
        option.set(3, key1);
        //then
        assertThat(option.get(key1)).isEqualTo(3);
    }

    @Test
    public void putAndGet_user_not_authenticated() {
        //given
        when(subject1.isAuthenticated()).thenReturn(false);

        //when
        option.set(3, key1);
        //then
        assertThat(option.get(key1)).isEqualTo(3);
    }

    @Test
    public void put_and_get_override() {
        //given
        when(subject1.isAuthenticated()).thenReturn(true);
        //when
        option.set(3, key1);
        option.set(7, 1, key1);
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
        option.set(3, key1);
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
    public void multiUser() {
        //given
        when(subjectProvider.get()).thenReturn(subject1);
        when(subject1.isAuthenticated()).thenReturn(true);
        when(subjectIdentifier.userId()).thenReturn("fbaton");
        DefaultOption option2 = new DefaultOption(optionCache, hierarchy, subjectProvider, subjectIdentifier);
        //when
        option2.set(3, key1);
        when(subjectProvider.get()).thenReturn(subject2);
        when(subject2.isAuthenticated()).thenReturn(true);
        when(subjectIdentifier.userId()).thenReturn("equick");
        hierarchy = new SimpleUserHierarchy(subjectProvider, subjectIdentifier, translate);
        option2.set(9, key1);
        //then
        when(subjectProvider.get()).thenReturn(subject1);
        when(subject1.isAuthenticated()).thenReturn(true);
        when(subjectIdentifier.userId()).thenReturn("fbaton");

        Integer actual = option2.get(key1);
        assertThat(actual).isEqualTo(3);
        option2.get(key1);
        option2.get(key1);
        option2.get(key1);
        option2.get(key1);
        assertThat(optionCache.cacheSize()).isEqualTo(3);
        assertThat(optionCache.stats()
                              .hitCount()).isEqualTo(4);

        when(subjectProvider.get()).thenReturn(subject2);
        when(subject2.isAuthenticated()).thenReturn(true);
        when(subjectIdentifier.userId()).thenReturn("equick");

        optionCache.cleanup();
        actual = option2.get(key1);
        assertThat(actual).isEqualTo(9);
        option2.get(key1);
        option2.get(key1);
        option2.get(key1);
        option2.get(key1);


        final LoadingCache<OptionCacheKey, Optional<?>> cache = optionCache.getCache();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        cache.asMap()
             .forEach((k, v) -> System.out.println(">>>>   " + k.toString() + "   :   " + v.toString()));


        assertThat(optionCache.cacheSize()).isEqualTo(4);// TODO
        assertThat(optionCache.stats()
                              .hitCount()).isEqualTo(8);
        option2.getLowestRanked(key1);
        assertThat(optionCache.cacheSize()).isEqualTo(5);// TODO
        assertThat(optionCache.stats()
                              .hitCount()).isEqualTo(8);
        option2.getLowestRanked(key3);
        assertThat(optionCache.cacheSize()).isEqualTo(6);// TODO
        assertThat(optionCache.stats()
                              .hitCount()).isEqualTo(8);
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
        OptionCacheKey highestKey = new OptionCacheKey(hierarchy, RankOption.HIGHEST_RANK, key3);
        OptionCacheKey lowestKey = new OptionCacheKey(hierarchy, RankOption.LOWEST_RANK, key3);
        OptionCacheKey specificKey = new OptionCacheKey(hierarchy, RankOption.SPECIFIC_RANK, 1, key3);
        dao.write(specificKey, Optional.of(236));
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

                bind(OptionDao.class).annotatedWith(CoreDao.class)
                                     .to(InMemoryOptionDao.class);
                bind(GuavaCacheConfiguration.class).annotatedWith(OptionCacheConfig.class)
                                                   .toInstance(cacheConfig);
                bind(CoreOptionDaoProvider.class).to(DefaultCoreOptionDaoProvider.class);
                Class<? extends Annotation> annotationClass = InMemory.class;
                TypeLiteral<Class<? extends Annotation>> annotationTypeLiteral = new TypeLiteral<Class<? extends Annotation>>() {
                };
                bind(annotationTypeLiteral).annotatedWith(ActiveOptionDao.class)
                                           .toInstance(annotationClass);
                TypeLiteral<Set<Class<? extends Annotation>>> setAnnotationTypeLiteral = new TypeLiteral<Set<Class<? extends Annotation>>>() {
                };
                optionDaoProviders.add(InMemory.class);
                bind(setAnnotationTypeLiteral).annotatedWith(OptionDaoProviders.class)
                                              .toInstance(optionDaoProviders);
                bind(OptionDao.class).annotatedWith(InMemory.class)
                                     .to(InMemoryOptionDao.class);
                bind(InMemoryOptionStore.class).to(DefaultInMemoryOptionStore.class);
            }

        };
    }


}