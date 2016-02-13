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

package uk.q3c.krail.core.i18n

import com.google.common.collect.ImmutableSet
import spock.lang.Specification
import uk.q3c.krail.UnitTestFor
import uk.q3c.krail.core.i18n.i18nModule.TestPatternSource
import uk.q3c.krail.core.option.Option
import uk.q3c.krail.core.persist.cache.i18n.DefaultPatternCacheLoader
import uk.q3c.krail.core.persist.cache.i18n.PatternCacheKey
import uk.q3c.krail.core.persist.clazz.i18n.ClassPatternDao
import uk.q3c.krail.core.persist.clazz.i18n.ClassPatternSource

/**
 * This test had to be changed to use a real DefaultPatternCacheLoader rather than a mock, although oddly the Mock did work originally
 * There are some issues with CGLib https://groups.google.com/forum/#!topic/spockframework/59WIHGgcSNE
 *
 * Cannot mock the interface because DefaultPatternCacheLoader has to extend {@link com.google.common.cache.CacheLoader}, but that has no interface
 *
 * Created by David Sowerby on 29/07/15.
 */
@SuppressWarnings("GroovyAssignabilityCheck")
@UnitTestFor(DefaultPatternSource.class)
class DefaultPatternSourceTest extends Specification {


    DefaultPatternSource patternSource

    def patternCacheLoader
    def option = Mock(Option)
    def sourceProvider = Mock(PatternSourceProvider)
    def classPatternDao = Mock(ClassPatternDao)

    def setup() {
        patternCacheLoader = new DefaultPatternCacheLoader(sourceProvider, option)
        patternSource = new DefaultPatternSource(patternCacheLoader)
    }


    def "cache is created on construction"() {
        expect:
        patternSource.getCache() != null
    }

    def "retrieve pattern calls the cacheLoader and places entry in cache "() {

        given:
        PatternCacheKey key = new PatternCacheKey(LabelKey.Active_Source, Locale.UK)
        sourceProvider.orderedSources(LabelKey.Active_Source) >> ImmutableSet.of(ClassPatternSource)
        sourceProvider.sourceFor(ClassPatternSource.class) >> Optional.of(classPatternDao)
        classPatternDao.getValue(key) >> Optional.of("a value")
        option.get(DefaultPatternCacheLoader.optionKeyAutoStub.qualifiedWith(ClassPatternSource.class.simpleName)) >> false

        when:

        patternSource.retrievePattern(LabelKey.Active_Source, Locale.UK)


        then:
        patternSource.getCache().size() == 1
    }


    def "clearCache() empties cache"() {
        given:
        PatternCacheKey key1 = new PatternCacheKey(LabelKey.Active_Source, Locale.UK)
        PatternCacheKey key2 = new PatternCacheKey(DescriptionKey.Account_Already_In_Use, Locale.UK)
        sourceProvider.orderedSources(LabelKey.Active_Source) >> ImmutableSet.of(ClassPatternSource)
        sourceProvider.orderedSources(DescriptionKey.Account_Already_In_Use) >> ImmutableSet.of(ClassPatternSource)
        sourceProvider.sourceFor(ClassPatternSource.class) >> Optional.of(classPatternDao)
        classPatternDao.getValue(key1) >> Optional.of("a value")
        classPatternDao.getValue(key2) >> Optional.of("a value")
        option.get(DefaultPatternCacheLoader.optionKeyAutoStub.qualifiedWith(ClassPatternSource.class.simpleName)) >> false


        when:

        patternSource.retrievePattern(LabelKey.Active_Source, Locale.UK)
        patternSource.retrievePattern(DescriptionKey.Account_Already_In_Use, Locale.UK)
        patternSource.clearCache()


        then:

        patternSource.getCache().size() == 0
    }


    def "clearCache for just one source"() {

        given:
        PatternCacheKey key1 = new PatternCacheKey(LabelKey.Active_Source, Locale.UK)
        PatternCacheKey key2 = new PatternCacheKey(DescriptionKey.Account_Already_In_Use, Locale.UK)
        key1.setSource(ClassPatternSource)
        key2.setSource(TestPatternSource)
        patternSource.getCache().put(key1, "Active Source")
        patternSource.getCache().put(key2, "Account Already In Use")

        when:

        patternSource.clearCache(ClassPatternSource)


        then:
        patternSource.getCache().size() == 1
        patternSource.getCache().get(key2).equals("Account Already In Use")
    }
//    /**
//     * PatternSource is not required to check for a supportedLocale
//     */
//    @Test
//    public void retrievePattern() {
//        //given
//
//        //when
//        String value = source.retrievePattern(TestLabelKey.No, Locale.UK);
//        //then
//        assertThat(value).isEqualTo("No");
//
//        //when supported locale
//        value = source.retrievePattern(TestLabelKey.No, Locale.GERMANY);
//        //then
//        assertThat(value).isEqualTo("Nein");
//
//        //when not a supported locale, it defaults to standard Java behaviour and uses default translation
//        value = source.retrievePattern(TestLabelKey.No, Locale.CHINA);
//        //then
//        assertThat(value).isEqualTo("No");
//
//        //when not a supported locale, but there is not even a default translation
//        value = source.retrievePattern(TestLabelKey.ViewA, Locale.CHINA);
//        //then
//        assertThat(value).isEqualTo("ViewA");
//
//        //when supported locale but no value for key
//        value = source.retrievePattern(TestLabelKey.ViewA, Locale.UK);
//        //then
//        assertThat(value).isEqualTo("ViewA");
//    }
//
//    @Test
//    public void clearCache() {
//        //given
//        String value = source.retrievePattern(TestLabelKey.No, Locale.UK);
//        //when
//
//        //then
//        assertThat(source.getCache()
//                .size()).isEqualTo(1);
//
//        //when
//        source.clearCache();
//
//        //then
//        assertThat(source.getCache()
//                .size()).isEqualTo(0);
//    }
//
//    @Test
//    public void clearCache_Source() {
//        //given
//        String value = source.retrievePattern(TestLabelKey.No, Locale.UK);
//        value = source.retrievePattern(TestLabelKey.No, Locale.GERMANY);
//        value = source.retrievePattern(TestLabelKey.Yes, Locale.ITALIAN);
//        value = source.retrievePattern(TestLabelKey.Blank, new Locale(""));
//        //when
//        //Map map=source.getCache().asMap();
//        //then
//        assertThat(source.getCache()
//                .size()).isEqualTo(4);
//
//        //when
//        source.clearCache(ClassPatternSource.class);
//
//        //then
//        assertThat(source.getCache()
//                .size()).isEqualTo(0);
//    }

}
