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

package uk.q3c.krail.core.user.opt.cache

import spock.lang.Specification
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.user.opt.OptionKey
import uk.q3c.krail.core.user.profile.RankOption
import uk.q3c.krail.core.user.profile.UserHierarchy
import uk.q3c.krail.core.view.component.LocaleContainer

/**
 * Created by David Sowerby on 22 Jan 2016
 */
class OptionCacheKeyTest extends Specification {

    UserHierarchy userHierarchy = Mock()
    OptionKey<Integer> optionKey = new OptionKey<>(33, LocaleContainer.class, LabelKey.Yes, DescriptionKey.Account_Already_In_Use, "a", "b")

    def setup() {
        userHierarchy.rankName(0) >> "top dog"
        userHierarchy.rankName(1) >> "level 1"
        userHierarchy.rankName(2) >> "level 2"
        userHierarchy.lowestRank() >> 2
        userHierarchy.persistenceName() >> "Simple"
        userHierarchy.highestRankName() >> (userHierarchy.rankName(0))
        userHierarchy.lowestRankName() >> (userHierarchy.rankName(2))


    }

    def "full construct "() {
        when:
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(userHierarchy, RankOption.HIGHEST_RANK, 2, optionKey)

        then:
        cacheKey.getHierarchy().equals(userHierarchy)
        cacheKey.getRankOption() == RankOption.HIGHEST_RANK
        cacheKey.getRequestedRankName().equals("level 2")
        cacheKey.getOptionKey() == optionKey
    }


    def "construct high without rank "() {
        when:
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(userHierarchy, RankOption.HIGHEST_RANK, optionKey)

        then:
        cacheKey.getHierarchy().equals(userHierarchy)
        cacheKey.getRankOption() == RankOption.HIGHEST_RANK
        cacheKey.getRequestedRankName().equals("top dog")
        cacheKey.getOptionKey() == optionKey
    }

    def "construct low without rank "() {
        when:
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(userHierarchy, RankOption.LOWEST_RANK, optionKey)

        then:
        cacheKey.getHierarchy().equals(userHierarchy)
        cacheKey.getRankOption() == RankOption.LOWEST_RANK
        cacheKey.getRequestedRankName().equals(userHierarchy.lowestRankName())
        cacheKey.getOptionKey() == optionKey
    }

    def "copy construct, changing RankOption only "() {
        given:
        OptionCacheKey<Integer> refKey = new OptionCacheKey<>(userHierarchy, RankOption.HIGHEST_RANK, optionKey)

        when:
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(refKey, RankOption.LOWEST_RANK)

        then:
        cacheKey.getHierarchy().equals(userHierarchy)
        cacheKey.getRankOption() == RankOption.LOWEST_RANK
        cacheKey.getRequestedRankName().equals("top dog")
        cacheKey.getOptionKey() == optionKey
    }

    def "copy construct, changing RankOption and rank "() {
        given:
        OptionCacheKey<Integer> refKey = new OptionCacheKey<>(userHierarchy, RankOption.HIGHEST_RANK, optionKey)

        when:
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(refKey, 1, RankOption.SPECIFIC_RANK)

        then:
        cacheKey.getHierarchy().equals(userHierarchy)
        cacheKey.getRankOption() == RankOption.SPECIFIC_RANK
        cacheKey.getRequestedRankName().equals(userHierarchy.rankName(1))
        cacheKey.getOptionKey() == optionKey
    }

    def "toString()"() {

        given:
        OptionCacheKey<Integer> refKey = new OptionCacheKey<>(userHierarchy, RankOption.HIGHEST_RANK, optionKey)

        expect:
        refKey.toString().equals("OptionCacheKey{hierarchy=Simple, requestedRankName='top dog', optionKey=LocaleContainer-Yes-a-b, rankOption=HIGHEST_RANK}")
    }

    def "equals & hashcode"() {
        given:
        UserHierarchy userHierarchy1 = Mock()
        userHierarchy1.rankName(0) >> 'top dog'
        userHierarchy1.rankName(1) >> "level 1"
        userHierarchy1.rankName(2) >> "level 2"
        userHierarchy1.lowestRank() >> 2
        userHierarchy1.persistenceName() >> "1"
        userHierarchy1.highestRankName() >>> ['top dog', 'other dog', 'top dog']
        userHierarchy1.lowestRankName() >> (userHierarchy1.rankName(2))

        UserHierarchy userHierarchy2 = Mock()
        userHierarchy2.rankName(0) >> "top bitch"
        userHierarchy2.rankName(1) >> "level 1"
        userHierarchy2.rankName(2) >> "level 2"
        userHierarchy2.lowestRank() >> 2
        userHierarchy2.persistenceName() >> "2"
        userHierarchy2.highestRankName() >> (userHierarchy2.rankName(0))
        userHierarchy2.lowestRankName() >> (userHierarchy2.rankName(2))


        OptionCacheKey<Integer> refKey = new OptionCacheKey<>(userHierarchy1, RankOption.HIGHEST_RANK, 1, optionKey)
        OptionCacheKey<Integer> differentHierarchy = new OptionCacheKey<>(userHierarchy2, RankOption.HIGHEST_RANK, 1, optionKey)
        OptionCacheKey<Integer> differentRankButNotSpecific = new OptionCacheKey<>(userHierarchy1, RankOption.HIGHEST_RANK, 2, optionKey)
        OptionCacheKey<Integer> differentRankOption = new OptionCacheKey<>(userHierarchy1, RankOption.SPECIFIC_RANK, 1, optionKey)
        expect:

        !refKey.equals(differentHierarchy)
        refKey.hashCode() != differentHierarchy.hashCode()

        refKey.equals(differentRankButNotSpecific)
        refKey.hashCode() == differentRankButNotSpecific.hashCode()

        !refKey.equals(differentRankOption)
        refKey.hashCode() != differentRankOption.hashCode()
    }

    def "equals & hashcode, different specific rank"() {
        given:

        OptionCacheKey<Integer> refKey = new OptionCacheKey<>(userHierarchy, RankOption.SPECIFIC_RANK, 2, optionKey)
        OptionCacheKey<Integer> differentRank = new OptionCacheKey<>(userHierarchy, RankOption.SPECIFIC_RANK, 1, optionKey)

        expect:
        !refKey.equals(differentRank)
        refKey.hashCode() != differentRank.hashCode()
    }

    def "equals & hashcode, different optionKey"() {
        given:

        OptionKey<Integer> optionKey1 = Mock()
        OptionKey<Integer> optionKey2 = Mock()
        OptionCacheKey<Integer> refKey = new OptionCacheKey<>(userHierarchy, RankOption.SPECIFIC_RANK, 2, optionKey1)
        OptionCacheKey<Integer> differentOptionKey = new OptionCacheKey<>(userHierarchy, RankOption.SPECIFIC_RANK, 2, optionKey2)

        expect:
        !refKey.equals(differentOptionKey)
        refKey.hashCode() != differentOptionKey.hashCode()
    }

    def "equals & hashcode same instance"() {
        given:
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(userHierarchy, RankOption.HIGHEST_RANK, 3, optionKey)
        OptionCacheKey<Integer> cacheKey1 = cacheKey

        expect:
        cacheKey.equals(cacheKey1)
    }

    def "equals & hashcode different class"() {
        given:
        OptionCacheKey<Integer> cacheKey = new OptionCacheKey<>(userHierarchy, RankOption.HIGHEST_RANK, 3, optionKey)

        expect:
        //noinspection GrEqualsBetweenInconvertibleTypes - the reason for testing
        !cacheKey.equals(optionKey)
    }

}
