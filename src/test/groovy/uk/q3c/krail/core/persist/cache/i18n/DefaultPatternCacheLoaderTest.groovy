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

package uk.q3c.krail.core.persist.cache.i18n

import com.google.common.collect.ImmutableSet
import spock.lang.Specification
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.i18nModule.TestPatternSource
import uk.q3c.krail.core.option.Option
import uk.q3c.krail.i18n.ClassPatternDao
import uk.q3c.krail.i18n.ClassPatternSource
import uk.q3c.krail.i18n.PatternCacheKey
import uk.q3c.krail.i18n.PatternSourceProvider
import uk.q3c.krail.i18n.cache.DefaultPatternCacheLoader
import uk.q3c.util.testutil.LogMonitor

/**
 *
 * Created by David Sowerby on 30/07/15.
 */
//
class DefaultPatternCacheLoaderTest extends Specification {

    DefaultPatternCacheLoader loader
    def option = Mock(Option)
    def sourceProvider = Mock(PatternSourceProvider)

    LogMonitor logMonitor

    def setup() {
        logMonitor = new LogMonitor()
        logMonitor.addClassFilter(this.getClass())
        loader = new DefaultPatternCacheLoader(sourceProvider, option)
    }

    def cleanup() {
        logMonitor.close()
    }

    def "auto stub is true but stub targets are empty, logs a warning"() {

    }


    @SuppressWarnings("GroovyAssignabilityCheck")
    def "auto stub and use key name is true, value not found therefore write stub is called for each target with key name for each Locale candidate"() {
        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK)
        def sourcePatternDao = Mock(ClassPatternDao)
        def targetPatternDao1 = Mock(ClassPatternDao)
        def targetPatternDao2 = Mock(ClassPatternDao)
        Optional<ClassPatternDao> optionalTargetPatternDao1 = Optional.of(targetPatternDao1)
        Optional<ClassPatternDao> optionalTargetPatternDao2 = Optional.of(targetPatternDao2)
        sourceProvider.orderedSources(LabelKey.Yes) >> ImmutableSet.copyOf(ClassPatternSource.class)
        sourceProvider.sourceFor(ClassPatternSource) >> Optional.of(sourcePatternDao)
        sourcePatternDao.getValue(cacheKey) >> Optional.empty()

        sourceProvider.selectedTargets() >> new LinkedHashSet<>(Arrays.asList(TestPatternSource.class, ClassPatternSource.class))
        sourceProvider.targetFor(ClassPatternSource) >> optionalTargetPatternDao1
        sourceProvider.targetFor(TestPatternSource) >> optionalTargetPatternDao2

        option.get(DefaultPatternCacheLoader.optionKeyAutoStub.qualifiedWith(ClassPatternSource.class.simpleName)) >> true
        option.get(DefaultPatternCacheLoader.optionKeyStubWithKeyName.qualifiedWith(ClassPatternSource.class.simpleName)) >> true
        option.get(DefaultPatternCacheLoader.optionKeyStubValue.qualifiedWith(ClassPatternSource.class.simpleName)) >> "stubby value"

        when:
        loader.load(cacheKey)

        then:
        1 * targetPatternDao1.write({ PatternCacheKey k -> k.actualLocale == Locale.UK }, { String v -> v == "Yes" })
        1 * targetPatternDao1.write({ PatternCacheKey k -> k.actualLocale == Locale.ENGLISH }, { String v -> v == "Yes" })
        1 * targetPatternDao1.write({ PatternCacheKey k -> k.actualLocale == new Locale("") }, { String v -> v == "Yes" })
        1 * targetPatternDao2.write({ PatternCacheKey k -> k.actualLocale == Locale.UK }, { String v -> v == "Yes" })
        1 * targetPatternDao2.write({ PatternCacheKey k -> k.actualLocale == Locale.ENGLISH }, { String v -> v == "Yes" })
        1 * targetPatternDao2.write({ PatternCacheKey k -> k.actualLocale == new Locale("") }, { String v -> v == "Yes" })
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "auto stub is true, use key name is false, value not found therefore write stub is called with optionKeyStubValue value"() {
        given:

        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK)
        def sourcePatternDao = Mock(ClassPatternDao)
        def targetPatternDao1 = Mock(ClassPatternDao)
        def targetPatternDao2 = Mock(ClassPatternDao)
        Optional<ClassPatternDao> optionalTargetPatternDao1 = Optional.of(targetPatternDao1)
        Optional<ClassPatternDao> optionalTargetPatternDao2 = Optional.of(targetPatternDao2)
        sourceProvider.orderedSources(LabelKey.Yes) >> ImmutableSet.copyOf(ClassPatternSource.class)
        sourceProvider.sourceFor(ClassPatternSource) >> Optional.of(sourcePatternDao)
        sourcePatternDao.getValue(cacheKey) >> Optional.empty()

        sourceProvider.selectedTargets() >> new LinkedHashSet<>(Arrays.asList(TestPatternSource.class, ClassPatternSource.class))
        sourceProvider.targetFor(ClassPatternSource) >> optionalTargetPatternDao1
        sourceProvider.targetFor(TestPatternSource) >> optionalTargetPatternDao2

        option.get(DefaultPatternCacheLoader.optionKeyAutoStub.qualifiedWith(ClassPatternSource.class.simpleName)) >> true
        option.get(DefaultPatternCacheLoader.optionKeyStubWithKeyName.qualifiedWith(ClassPatternSource.class.simpleName)) >> false
        option.get(DefaultPatternCacheLoader.optionKeyStubValue.qualifiedWith(ClassPatternSource.class.simpleName)) >> "stubby value"

        when:
        loader.load(cacheKey)

        then:
        1 * targetPatternDao1.write({ PatternCacheKey k -> k.actualLocale == Locale.UK }, { String v -> v == "stubby value" })
        1 * targetPatternDao1.write({ PatternCacheKey k -> k.actualLocale == Locale.ENGLISH }, { String v -> v == "stubby value" })
        1 * targetPatternDao1.write({ PatternCacheKey k -> k.actualLocale == new Locale("") }, { String v -> v == "stubby value" })
        1 * targetPatternDao2.write({ PatternCacheKey k -> k.actualLocale == Locale.UK }, { String v -> v == "stubby value" })
        1 * targetPatternDao2.write({ PatternCacheKey k -> k.actualLocale == Locale.ENGLISH }, { String v -> v == "stubby value" })
        1 * targetPatternDao2.write({ PatternCacheKey k -> k.actualLocale == new Locale("") }, { String v -> v == "stubby value" })
    }

    def "value not found, but auto stub is false therefore write stub is not called"() {
        given:
        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK)
        def sourcePatternDao = Mock(ClassPatternDao)
        def targetPatternDao1 = Mock(ClassPatternDao)
        def targetPatternDao2 = Mock(ClassPatternDao)
        Optional<ClassPatternDao> optionalTargetPatternDao1 = Optional.of(targetPatternDao1)
        Optional<ClassPatternDao> optionalTargetPatternDao2 = Optional.of(targetPatternDao2)
        sourceProvider.orderedSources(LabelKey.Yes) >> ImmutableSet.copyOf(ClassPatternSource.class)
        sourceProvider.sourceFor(ClassPatternSource) >> Optional.of(sourcePatternDao)
        sourcePatternDao.getValue(cacheKey) >> Optional.empty()

        sourceProvider.selectedTargets() >> new LinkedHashSet<>(Arrays.asList(TestPatternSource.class, ClassPatternSource.class))
        sourceProvider.targetFor(ClassPatternSource) >> optionalTargetPatternDao1
        sourceProvider.targetFor(TestPatternSource) >> optionalTargetPatternDao2

        option.get(DefaultPatternCacheLoader.optionKeyAutoStub.qualifiedWith(ClassPatternSource.class.simpleName)) >> false

        when:
        loader.load(cacheKey)

        then:
        0 * targetPatternDao1.write(cacheKey, "Yes")
        0 * targetPatternDao2.write(cacheKey, "Yes")

    }

    def "option is to use the name as stub"() {
        given:

        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK)
        option.get(DefaultPatternCacheLoader.optionKeyStubWithKeyName.qualifiedWith(ClassPatternSource.class.simpleName)) >> true
        option.get(DefaultPatternCacheLoader.optionKeyStubValue.qualifiedWith(ClassPatternSource.class.simpleName)) >> "stubby value"

        expect:
        loader.stubValue(ClassPatternSource.class, cacheKey).equals("Yes")
    }


    def "option is to use the supply stub value"() {
        given:

        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK)
        option.get(DefaultPatternCacheLoader.optionKeyStubWithKeyName.qualifiedWith(ClassPatternSource.class.simpleName)) >> false
        option.get(DefaultPatternCacheLoader.optionKeyStubValue.qualifiedWith(ClassPatternSource.class.simpleName)) >> "stubby value"

        expect:
        loader.stubValue(ClassPatternSource.class, cacheKey).equals("stubby value")
    }

    def "getOption"() {
        expect:
        loader.getOption() == option
    }

    def "optionValueChanged does nothing, call for coverage"() {
        expect:
        loader.optionValueChanged(null)
    }


    @SuppressWarnings("GroovyAssignabilityCheck")
    def "value found in requestedLocale, then PatternCacheKey.requestedLocale and actualLocale as requested"() {
        given:
        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK)
        def classPatternDao = Mock(ClassPatternDao)
        Optional<ClassPatternDao> optionalClassPatternDao = Optional.of(classPatternDao)
        Optional<String> daoGetValueResult = Optional.of("value")

        when:
        String result = loader.load(cacheKey)

        then:
        1 * sourceProvider.orderedSources(LabelKey.Yes) >> ImmutableSet.of(ClassPatternSource)
        1 * sourceProvider.sourceFor(ClassPatternSource) >> optionalClassPatternDao
        1 * classPatternDao.getValue(cacheKey) >> daoGetValueResult
        0 * option.get(DefaultPatternCacheLoader.optionKeyAutoStub.qualifiedWith(ClassPatternSource.class.simpleName)) >> true
        0 * sourceProvider.selectedTargets() >> new LinkedHashSet<>()
        result == "value"
        cacheKey.getRequestedLocale() == Locale.UK
        cacheKey.getActualLocale() == Locale.UK
        cacheKey.getSource() == ClassPatternSource
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "value found in Locale different to requested, PatternCacheKey.requestedLocale as requested, actualLocale as found, source is set "() {
        given:
        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Yes, Locale.UK)
        def classPatternDao = Mock(ClassPatternDao)
        Optional<ClassPatternDao> optionalClassPatternDao = Optional.of(classPatternDao)
        Optional<String> daoGetValueResult = Optional.of("value")

        when:
        String result = loader.load(cacheKey)

        then:
        2 * sourceProvider.orderedSources(LabelKey.Yes) >> ImmutableSet.of(ClassPatternSource)
        2 * sourceProvider.sourceFor(ClassPatternSource) >> optionalClassPatternDao
        1 * classPatternDao.getValue(cacheKey) >> Optional.empty()
        1 * classPatternDao.getValue(cacheKey) >> daoGetValueResult
        1 * option.get(DefaultPatternCacheLoader.optionKeyAutoStub.qualifiedWith(ClassPatternSource.class.simpleName)) >> false
        0 * sourceProvider.selectedTargets() >> new LinkedHashSet<>()
        result == "value"
        cacheKey.getRequestedLocale() == Locale.UK
        cacheKey.getActualLocale() == Locale.ENGLISH
        cacheKey.getSource() == ClassPatternSource
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "value not found, PatternCacheKey.requestedLocale as requested, actualLocale==requestedLocale, source==null and key name returned with underscore removed "() {
        given:
        PatternCacheKey cacheKey = new PatternCacheKey(LabelKey.Active_Source, Locale.UK)
        def classPatternDao = Mock(ClassPatternDao)
        Optional<ClassPatternDao> optionalClassPatternDao = Optional.of(classPatternDao)

        when:
        String result = loader.load(cacheKey)

        then:
        3 * sourceProvider.orderedSources(LabelKey.Active_Source) >> ImmutableSet.of(ClassPatternSource)
        3 * sourceProvider.sourceFor(ClassPatternSource) >> optionalClassPatternDao
        3 * classPatternDao.getValue(cacheKey) >> Optional.empty()
        3 * option.get(DefaultPatternCacheLoader.optionKeyAutoStub.qualifiedWith(ClassPatternSource.class.simpleName)) >> false
        0 * sourceProvider.selectedTargets() >> new LinkedHashSet<>()
        result == "Active Source"
        cacheKey.getRequestedLocale() == Locale.UK
        cacheKey.getActualLocale() == new Locale("")
        cacheKey.getSource() == null
    }


}
