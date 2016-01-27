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
import uk.q3c.krail.core.validation.ValidationKey

//((ValidationKey.getEnumConstants().length + LabelKey.getEnumConstants().length + DescriptionKey.getEnumConstants().length + MessageKey.getEnumConstants().length) * 2) * patternDao.write(_, 'anything')
/**
 *
 *
 * Created by David Sowerby on 29/07/15.
 */
@UnitTestFor(DefaultPatternUtility.class)
class DefaultPatternUtilityTest extends Specification {

    DefaultPatternUtility utility

    def patternSource = Mock(PatternSource)
    def patternSourceProvider = Mock(PatternSourceProvider)
    def targetPatternDao = Mock(ClassPatternDao)
    def sourcePatternDao = Mock(ClassPatternDao)
    Set<Locale> supportedLocales

    def setup() {
        supportedLocales = new HashSet<>()
        utility = new DefaultPatternUtility(patternSource, supportedLocales)
    }

    def "export core keys (which uses export with PatternSource) should be a count of all core keys"() {
        given:
        supportedLocales.addAll(Locale.UK, Locale.GERMANY)
        patternSourceProvider.orderedSources(_) >> ClassPatternSource
        patternSourceProvider.sourceFor(ClassPatternSource) >> sourcePatternDao
        long expectedCount = (ValidationKey.getEnumConstants().length + LabelKey.getEnumConstants().length + DescriptionKey.getEnumConstants().length + MessageKey.getEnumConstants().length) * supportedLocales.size()

        when:

        long count = utility.exportCoreKeys(targetPatternDao)

        then:

        count == expectedCount
        expectedCount * targetPatternDao.write(_, _)

    }


    def "export with auto stub true and useKeyName = true"() {
        given:
        supportedLocales.addAll(Locale.UK, Locale.GERMANY)
        patternSourceProvider.orderedSources(_) >> ClassPatternSource
        patternSourceProvider.sourceFor(ClassPatternSource) >> sourcePatternDao
        ImmutableSet<Class<? extends I18NKey>> bundles = ImmutableSet.of(LabelKey.class, DescriptionKey.class, MessageKey.class, ValidationKey.class);
        ImmutableSet<Locale> locales = ImmutableSet.of(Locale.UK, Locale.GERMANY)
        long expectedCount = (ValidationKey.getEnumConstants().length + LabelKey.getEnumConstants().length + DescriptionKey.getEnumConstants().length + MessageKey.getEnumConstants().length) * supportedLocales.size()

        when:

        long count = utility.export(sourcePatternDao, targetPatternDao, bundles, locales, true, true, "empty")

        then:

        count == expectedCount
        expectedCount * sourcePatternDao.getValue(_) >> Optional.of("anything")
        expectedCount * targetPatternDao.write(_, "anything")

    }

    def "export with auto stub true and useKeyName = false"() {
        given:
        supportedLocales.addAll(Locale.UK, Locale.GERMANY)
        patternSourceProvider.orderedSources(_) >> ClassPatternSource
        patternSourceProvider.sourceFor(ClassPatternSource) >> sourcePatternDao
        ImmutableSet<Class<? extends I18NKey>> bundles = ImmutableSet.of(LabelKey.class, DescriptionKey.class, MessageKey.class, ValidationKey.class);
        ImmutableSet<Locale> locales = ImmutableSet.of(Locale.UK, Locale.GERMANY)
        long expectedCount = (ValidationKey.getEnumConstants().length + LabelKey.getEnumConstants().length + DescriptionKey.getEnumConstants().length + MessageKey.getEnumConstants().length) * supportedLocales.size()

        when:

        long count = utility.export(sourcePatternDao, targetPatternDao, bundles, locales, true, false, "empty")

        then:

        count == expectedCount
        1 * sourcePatternDao.getValue(_) >> Optional.empty()
        (expectedCount - 1) * sourcePatternDao.getValue(_) >> Optional.of("anything")
        1 * targetPatternDao.write(_, "empty")
        (expectedCount - 1) * targetPatternDao.write(_, "anything")

    }


    def "stubValue with useKeyName true"() {
        expect:
        utility.stubValue(LabelKey.Active_Source, true, "rubbish").equals("Active Source")


    }


    def "stubValue with useKeyName false"() {

        expect:
        utility.stubValue(LabelKey.Active_Source, false, "rubbish").equals("rubbish")

    }
}
