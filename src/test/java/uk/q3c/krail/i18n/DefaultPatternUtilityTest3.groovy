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

package uk.q3c.krail.i18n

import com.google.inject.Provider
import spock.lang.Specification

/**
 * Extended Unit test, using a test implementation of {@link DatabaseBundleWriter},  for {@link DefaultPatternUtility#exportKeysToDatabase(java.util.Set, uk.q3c.krail.i18n.DatabaseBundleWriter)}
 *
 * Created by David Sowerby on 12/07/15.
 */
class DefaultPatternUtilityTest3 extends Specification {

    DefaultPatternUtility utility;

    Map<String, BundleReader> bundleReaders = Mock()

    PatternSource patternsource = Mock()

    Translate translate = Mock()

    DatabaseBundleWriter writer

    Provider<PatternDao> patternDaoProvider = Mock()

    PatternDao patternDao = Mock()


    def setup() {
        translate.from(_, _) >> "anything"
        patternDaoProvider.get() >> patternDao
        writer = new TestDatabaseBundleWriter(patternDaoProvider, translate)
        utility = new DefaultPatternUtility(bundleReaders, patternsource)
    }

    def "export to database"() {
        given:
        Set<Locale> locales = new HashSet<>()
        locales.add(Locale.UK)
        locales.add(Locale.CANADA_FRENCH)

        when:
        utility.exportKeysToDatabase(locales, writer)

        then:

        ((LabelKey.getEnumConstants().length + DescriptionKey.getEnumConstants().length + MessageKey.getEnumConstants().length) * 2) * patternDao.write(_, 'anything')

    }
}
