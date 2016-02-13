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

package uk.q3c.krail.core.persist.common.option

import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import com.google.inject.Inject
import com.google.inject.Singleton
import org.apache.commons.collections15.ListUtils
import spock.lang.Specification
import uk.q3c.krail.core.data.OptionElementConverter
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters
import uk.q3c.krail.core.option.*
import uk.q3c.krail.core.persist.cache.option.OptionCacheKey
import uk.q3c.krail.core.persist.clazz.i18n.ClassPatternSource
import uk.q3c.krail.core.user.profile.UserHierarchy
import uk.q3c.krail.core.view.component.LocaleContainer

import static uk.q3c.krail.core.user.profile.RankOption.*
/**
 * Created by David Sowerby on 27 Jan 2016
 */
abstract class OptionDaoTestBase extends Specification {

    @Inject
    OptionElementConverter optionElementConverter;

    OptionDao dao
    String expectedConnectionUrl
    OptionKey<Integer> optionKey1 = Mock();
    OptionKey<Integer> optionKey2 = Mock();
    OptionCacheKey cacheKeySpecific11 = Mock()
    OptionCacheKey cacheKeySpecific10 = Mock()
    OptionCacheKey cacheKeySpecific12 = Mock()
    OptionCacheKey cacheKeySpecific2 = Mock()
    OptionCacheKey cacheKeyHigh1 = Mock()
    OptionCacheKey cacheKeyLow1 = Mock()
    UserHierarchy hierarchy1 = Mock()

    OptionSource optionSource = Mock()

    OptionKey<String> optionKeyString = new OptionKey<>("a", LocaleContainer.class, LabelKey.Yes, "a");
    OptionKey<Integer> optionKeyInteger = new OptionKey<>(21, LocaleContainer.class, LabelKey.Yes, "b");
    OptionKey<Boolean> optionKeyBoolean = new OptionKey<>(true, LocaleContainer.class, LabelKey.Yes, "c");
    OptionKey<Long> optionKeyLong = new OptionKey<>(121L, LocaleContainer.class, LabelKey.Yes, "d");
    OptionKey<BigDecimal> optionKeyBigDecimal = new OptionKey<>(new BigDecimal(23.3), LocaleContainer.class, LabelKey.Yes, "e");
    OptionKey<DefaultUserSitemapSorters.SortType> optionKeyEnum = new OptionKey<>(DefaultUserSitemapSorters.SortType.INSERTION, LocaleContainer.class, LabelKey.Yes, "f");
    OptionKey<Locale> optionKeyLocale = new OptionKey<>(Locale.CHINA, LocaleContainer.class, LabelKey.Yes, "g");

    OptionKey<OptionList<String>> optionKeyStringImmutableSet = new OptionKey<>(new OptionList<String>(String.class), LocaleContainer.class, LabelKey.Yes, "h");
    OptionKey<AnnotationOptionList> optionKeyAnnotationList = new OptionKey<>(new AnnotationOptionList(Inject.class, Singleton.class), LocaleContainer.class, LabelKey.Yes, "i")


    def setup() {


        optionKey1.getContext() >> LocaleContainer.class
        optionKey1.getDefaultValue() >> 99
        optionKey1.getKey() >> LabelKey.Yes
        optionKey1.compositeKey() >> 'composite key 1'
        optionKey2.getContext() >> LocaleContainer.class
        optionKey2.getDefaultValue() >> 109
        optionKey2.getKey() >> LabelKey.No
        optionKey2.compositeKey() >> 'composite key 2'


        cacheKeySpecific10.getRankOption() >> SPECIFIC_RANK
        cacheKeySpecific10.getOptionKey() >> optionKey1
        cacheKeySpecific10.getHierarchy() >> hierarchy1
        cacheKeySpecific10.getRequestedRankName() >> 'level 0'

        cacheKeySpecific11.getRankOption() >> SPECIFIC_RANK
        cacheKeySpecific11.getOptionKey() >> optionKey1
        cacheKeySpecific11.getHierarchy() >> hierarchy1
        cacheKeySpecific11.getRequestedRankName() >> 'level 1'

        cacheKeySpecific12.getRankOption() >> SPECIFIC_RANK
        cacheKeySpecific12.getOptionKey() >> optionKey1
        cacheKeySpecific12.getHierarchy() >> hierarchy1
        cacheKeySpecific12.getRequestedRankName() >> 'level 2'


        cacheKeySpecific2.getRankOption() >> SPECIFIC_RANK
        cacheKeySpecific2.getOptionKey() >> optionKey2
        cacheKeySpecific2.getHierarchy() >> hierarchy1
        cacheKeySpecific2.getRequestedRankName() >> 'level 0'

        cacheKeyHigh1.getRankOption() >> HIGHEST_RANK
        cacheKeyHigh1.getOptionKey() >> optionKey1
        cacheKeyHigh1.getHierarchy() >> hierarchy1
        cacheKeyHigh1.getRequestedRankName() >> 'level 1'

        cacheKeyLow1.getRankOption() >> LOWEST_RANK
        cacheKeyLow1.getOptionKey() >> optionKey1
        cacheKeyLow1.getHierarchy() >> hierarchy1
        cacheKeyLow1.getRequestedRankName() >> 'level 1'

        hierarchy1.persistenceName() >> 'hierarchy1'
        hierarchy1.rankName(0) >> 'level 0'
        hierarchy1.rankName(1) >> 'level 1'
        hierarchy1.rankName(2) >> 'level 2'
        hierarchy1.lowestRank() >> 2
        hierarchy1.lowestRankName() >> 'level 2'
        hierarchy1.highestRankName() >> 'level 0'
        hierarchy1.ranksForCurrentUser() >> ImmutableList.of('level 0', 'level 1', 'level 2')

    }

    def "connectionUrl"() {
        expect:
        dao.connectionUrl().equals(expectedConnectionUrl)
    }

    def "write with empty optional throws exception"() {
        when:
        dao.write(cacheKeySpecific11, Optional.empty())

        then:
        thrown(IllegalArgumentException)
    }

    def "write with rankOption HIGHEST throws exception"() {
        when:
        dao.write(cacheKeyHigh1, Optional.of(33))

        then:
        thrown(OptionKeyException)
    }


    def "write with rankOption LOWEST throws exception"() {
        when:
        dao.write(cacheKeyLow1, Optional.of(33))

        then:
        thrown(OptionKeyException)
    }

    def "write and get specific"() {
        when:
        dao.write(cacheKeySpecific11, Optional.of(23))

        then:
        dao.getValue(cacheKeySpecific11).equals(Optional.of(23))
    }

    def "write, 2 different values for same key, latest is returned"() {

        when:
        dao.write(cacheKeySpecific11, Optional.of(33))
        dao.write(cacheKeySpecific11, Optional.of(232))

        then:
        dao.getValue(cacheKeySpecific11).equals(Optional.of(232))
    }

    def "count"() {

        when:
        dao.write(cacheKeySpecific11, Optional.of(33))
        dao.write(cacheKeySpecific11, Optional.of(232))
        dao.write(cacheKeySpecific2, Optional.of(232))

        then:
        dao.count() == 2 // 1 duplicated
    }

    def "clear"() {
        given:
        dao.write(cacheKeySpecific11, Optional.of(33))
        dao.write(cacheKeySpecific11, Optional.of(232))
        dao.write(cacheKeySpecific2, Optional.of(232))

        when:
        dao.clear()

        then:
        dao.count() == 0
    }

    def "delete value, key not specific, throws exception"() {
        when:
        dao.deleteValue(cacheKeyHigh1)

        then:
        thrown(OptionKeyException)
    }

    def "delete value, did not exist, returns Optional.empty()"() {
        expect:
        dao.deleteValue(cacheKeySpecific11).equals(Optional.empty())
    }

    def "delete value, did exist, returns Optional.of(value)"() {
        given:
        dao.write(cacheKeySpecific11, Optional.of(33))

        when:
        Optional<String> result = dao.deleteValue(cacheKeySpecific11)

        then:
        result.equals(Optional.of('33'))
    }


    def "getHighest with 0 entries returns Optional.empty"() {
        expect:
        dao.getValue(cacheKeyHigh1).equals(Optional.empty())
    }

    def "getLowest with 0 entries returns Optional.empty"() {
        expect:
        dao.getValue(cacheKeyLow1).equals(Optional.empty())
    }

    def "getSpecific with 0 entries returns Optional.empty"() {
        expect:
        dao.getValue(cacheKeySpecific11).equals(Optional.empty())
    }

    def "getHighest with 1 entry returns correct value"() {
        when:
        dao.write(cacheKeySpecific11, Optional.of(33))

        then:
        dao.getValue(cacheKeyHigh1).equals(Optional.of(33))
    }

    def "getLowest with 1 entry returns correct value"() {
        when:
        dao.write(cacheKeySpecific11, Optional.of(33))

        then:
        dao.getValue(cacheKeyLow1).equals(Optional.of(33))
    }

    def "getSpecific with 1 entry returns correct value"() {
        when:
        dao.write(cacheKeySpecific11, Optional.of(33))

        then:
        dao.getValue(cacheKeySpecific11).equals(Optional.of(33))
    }

    def "getHighest with 3 entries returns correct value"() {
        when:
        dao.write(cacheKeySpecific10, Optional.of(4893))
        dao.write(cacheKeySpecific12, Optional.of(353))
        dao.write(cacheKeySpecific11, Optional.of(33))

        then:
        dao.getValue(cacheKeyHigh1).equals(Optional.of(4893))
    }

    def "getLowest with 3 entries returns correct value"() {
        when:
        dao.write(cacheKeySpecific10, Optional.of(4893))
        dao.write(cacheKeySpecific12, Optional.of(353))
        dao.write(cacheKeySpecific11, Optional.of(33))

        then:
        dao.getValue(cacheKeyLow1).equals(Optional.of(353))
    }

    def "getSpecific with 3 entries returns correct value"() {
        when:
        dao.write(cacheKeySpecific10, Optional.of(4893))
        dao.write(cacheKeySpecific12, Optional.of(353))
        dao.write(cacheKeySpecific11, Optional.of(33))

        then:
        dao.getValue(cacheKeySpecific12).equals(Optional.of(353))
    }

    def "OptionList conversion round trip"() {
        given:
        OptionKey<OptionList<Integer>> listKey = new OptionKey(new OptionList<Integer>(Integer.class), LocaleContainer.class, LabelKey.Yes)
        OptionCacheKey<OptionList<Integer>> cacheKey = new OptionCacheKey<>(hierarchy1, SPECIFIC_RANK, 0, listKey)
        OptionList<Integer> list = new OptionList<>(Integer.class, 3, 5, 7)

        when:
        dao.write(cacheKey, Optional.of(list))
        Optional<OptionList<Integer>> result = dao.getValue(cacheKey)

        then:
        ListUtils.isEqualList(list.getList(), result.get().getList())
    }

    def "AnnotationList conversion round trip"() {
        given:
        OptionKey<AnnotationOptionList> listKey = new OptionKey(new AnnotationOptionList(), LocaleContainer.class, LabelKey.Yes)
        OptionCacheKey<AnnotationOptionList> cacheKey = new OptionCacheKey<>(hierarchy1, SPECIFIC_RANK, 0, listKey)
        AnnotationOptionList list = new AnnotationOptionList(InMemory, Inject)

        when:
        dao.write(cacheKey, Optional.of(list))
        Optional<AnnotationOptionList> result = dao.getValue(cacheKey)

        then:
        ListUtils.isEqualList(list.getList(), result.get().getList())
    }

    def "round trip, single value data types"() {
        given:
        OptionCacheKey cacheKeyString = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, optionKeyString);
        OptionCacheKey cacheKeyInteger = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, optionKeyInteger);
        OptionCacheKey cacheKeyBoolean = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, optionKeyBoolean);
        OptionCacheKey cacheKeyLong = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, optionKeyLong);
        OptionCacheKey cacheKeyBigDecimal = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, optionKeyBigDecimal);
        OptionCacheKey cacheKeyEnum = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, optionKeyEnum);
        OptionCacheKey cacheKeyLocale = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, optionKeyLocale);
        when:
        dao.write(cacheKeyString, Optional.of("4"));
        dao.write(cacheKeyInteger, Optional.of(41));
        dao.write(cacheKeyBoolean, Optional.of(false));
        dao.write(cacheKeyLong, Optional.of(200L));
        dao.write(cacheKeyBigDecimal, Optional.of(new BigDecimal(341.44)));
        dao.write(cacheKeyEnum, Optional.of(DefaultUserSitemapSorters.SortType.POSITION));
        dao.write(cacheKeyLocale, Optional.of(Locale.CANADA_FRENCH));


        then:
        dao.getValue(cacheKeyString).get().equals("4")
        dao.getValue(cacheKeyInteger).get().equals(41)
        dao.getValue(cacheKeyBoolean).get().equals(false)
        dao.getValue(cacheKeyLong).get().equals(200L)
        dao.getValue(cacheKeyBigDecimal).get().equals(new BigDecimal(341.44))
        dao.getValue(cacheKeyEnum).get().equals(DefaultUserSitemapSorters.SortType.POSITION)
        dao.getValue(cacheKeyLocale).get().equals(Locale.CANADA_FRENCH)
    }

    def "round trip OptionList"() {

        given:
        OptionList<String> optionList = new OptionList<>(Lists.newArrayList("a", "b"), String.class)
        OptionCacheKey cacheKey = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, optionKeyStringImmutableSet);

        when:
        dao.write(cacheKey, Optional.of(optionList));
        Optional<OptionList<String>> result = dao.getValue(cacheKey);
        then:
        ListUtils.isEqualList(result.get().getList(), optionList.getList());

    }


    def "round trip OptionList with comma in entry"() {

        given:
        OptionList<String> optionList = new OptionList<>(Lists.newArrayList("a,c", "b"), String.class)
        OptionCacheKey cacheKey = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, optionKeyStringImmutableSet);

        when:
        dao.write(cacheKey, Optional.of(optionList));
        Optional<OptionList<String>> result = dao.getValue(cacheKey);
        then:
        ListUtils.isEqualList(result.get().getList(), optionList.getList());

    }

    def "round trip AnnotationOptionList"() {
        given:
        AnnotationOptionList optionList = new AnnotationOptionList(ClassPatternSource, Inject)
        OptionCacheKey cacheKey = new OptionCacheKey(hierarchy1, SPECIFIC_RANK, optionKeyAnnotationList);

        when:
        dao.write(cacheKey, Optional.of(optionList));
        Optional<AnnotationOptionList> result = dao.getValue(cacheKey);
        then:
        ListUtils.isEqualList(result.get().getList(), optionList.getList());
    }
}
