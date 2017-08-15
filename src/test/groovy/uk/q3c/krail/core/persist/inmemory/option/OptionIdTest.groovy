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

package uk.q3c.krail.core.persist.inmemory.option

import spock.lang.Specification
import uk.q3c.krail.core.i18n.DescriptionKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.sysadmin.option.SourcePanel
import uk.q3c.krail.core.view.component.LocaleContainer
import uk.q3c.krail.option.OptionKey
import uk.q3c.krail.option.RankOption
import uk.q3c.krail.option.UserHierarchy
import uk.q3c.krail.option.persist.OptionCacheKey
import uk.q3c.krail.option.persist.OptionId

/**
 * Created by David Sowerby on 21 Jan 2016
 */
class OptionIdTest extends Specification {

    UserHierarchy userHierarchy = Mock()
    UserHierarchy userHierarchy1 = Mock()
    OptionKey<Integer> optionKey = new OptionKey<>(33, LocaleContainer.class, LabelKey.Yes, DescriptionKey.Account_Already_In_Use, "a", "b")
    OptionKey<Integer> optionKey1 = new OptionKey<>(35, SourcePanel.class, LabelKey.Yes, DescriptionKey.Account_Already_In_Use, "a", "b")
    OptionCacheKey cacheKeySpecific
    OptionCacheKey cacheKeyHigh


    def setup() {
        userHierarchy.persistenceName() >> "Simple"
        userHierarchy1.persistenceName() >> "Not so simple"
        userHierarchy.highestRankName() >> "top dog"
        userHierarchy.lowestRankName() >> "top department"
        userHierarchy.rankName(0) >> "top dog"
        userHierarchy.rankName(1) >> "top team"
        userHierarchy.rankName(2) >> "top department"
        cacheKeySpecific = new OptionCacheKey(userHierarchy, RankOption.SPECIFIC_RANK, 1, optionKey)
        cacheKeyHigh = new OptionCacheKey(userHierarchy, RankOption.HIGHEST_RANK, optionKey)
    }

    def "construct from CacheKey must be SPECIFIC"() {
        when:
        new OptionId(cacheKeyHigh)

        then:
        thrown(IllegalArgumentException)
    }


    def "construct from CacheKey"() {
        when:
        OptionId id = new OptionId(cacheKeySpecific)
        then:
        id.getContext().equals(LocaleContainer.class.getName())
        id.getUserHierarchyName().equals("Simple")
        id.getOptionKey().equals(optionKey.compositeKey())
        id.getRankName().equals("top team")
    }


    def "construct from component parts using rank index"() {
        when:
        OptionId id = new OptionId(userHierarchy, 1, optionKey)

        then:
        id.getContext().equals(LocaleContainer.class.getName())
        id.getUserHierarchyName().equals("Simple")
        id.getOptionKey().equals(optionKey.compositeKey())
        id.getRankName().equals("top team")
    }

    def "construct from component parts using rank name"() {
        when:
        OptionId id = new OptionId(userHierarchy, "top team", optionKey)

        then:
        id.getContext().equals(LocaleContainer.class.getName())
        id.getUserHierarchyName().equals("Simple")
        id.getOptionKey().equals(optionKey.compositeKey())
        id.getRankName().equals("top team")
    }

    def "construct from component parts, non-zero rank"() {
        when:
        OptionId id = new OptionId(userHierarchy, 2, optionKey)

        then:
        id.getContext().equals(LocaleContainer.class.getName())
        id.getUserHierarchyName().equals("Simple")
        id.getOptionKey().equals(optionKey.compositeKey())
        id.getRankName().equals("top department")
    }

    def "equals and hashcode"() {
        given:
        userHierarchy1.rankName(0) >> "top dog"
        userHierarchy1.rankName(1) >> "top team"
        userHierarchy1.rankName(2) >> "top department"
        OptionId id1 = new OptionId(userHierarchy, 2, optionKey)
        OptionId id2 = new OptionId(userHierarchy, 2, optionKey1)
        OptionId id3 = new OptionId(userHierarchy1, 2, optionKey)
        OptionId id4 = new OptionId(userHierarchy, 1, optionKey)
        OptionId id5 = new OptionId(userHierarchy, 2, optionKey1)
        OptionId id6 = new OptionId(userHierarchy, 2, optionKey)

        expect:
        id1 == id6
        id1 != id2
        id1 != id3
        id1 != id4
        id1 != id5
        id1.hashCode() == id6.hashCode()
        id1.hashCode() != id2.hashCode()
        id1.hashCode() != id3.hashCode()
        id1.hashCode() != id4.hashCode()
        id1.hashCode() != id5.hashCode()
    }

    def "empty constructor, no defaults to check, but needed for persistence"() {
        expect:
        new OptionId()
    }
}
