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

package uk.q3c.krail.core.option

import spock.lang.Specification
import uk.q3c.krail.core.view.component.LocaleContainer
import uk.q3c.krail.option.OptionContext
import uk.q3c.krail.option.OptionKey
import uk.q3c.krail.option.RankOption
import uk.q3c.krail.option.UserHierarchy
import uk.q3c.krail.option.persist.OptionCacheKey
import uk.q3c.krail.option.persist.inmemory.OptionEntity

/**
 * Created by David Sowerby on 21 Jan 2016
 */
class OptionEntityTest extends Specification {


    OptionCacheKey cacheKey = Mock()
    UserHierarchy hierarchy = Mock()
    OptionKey optionKey = Mock()
    Class<? extends OptionContext> context = LocaleContainer.class

    def setup() {
        cacheKey.getHierarchy() >> hierarchy
        cacheKey.getOptionKey() >> optionKey
        cacheKey.getOptionKey().getContext() >> context
        cacheKey.getRankOption() >> RankOption.SPECIFIC_RANK
        cacheKey.getRequestedRankName() >> 'a rank'
        hierarchy.persistenceName() >> "user hierarchy"
        optionKey.compositeKey() >> 'composite Key'
    }

    def "construct from OptionCacheKey"() {
        given:
        OptionEntity entity = new OptionEntity(cacheKey, "Kiss me quick")

        expect:
        entity.getValue().equals("Kiss me quick")
        entity.getOptionKey().equals(optionKey.compositeKey())
        entity.getContext().equals(LocaleContainer.class.getName())
        entity.getRankName() == 'a rank'
        entity.getUserHierarchyName().equals("user hierarchy")
    }

    def "equals and hashcode"() {
        given:
        OptionEntity refEntity = new OptionEntity(cacheKey, "Kiss me quick")
        OptionEntity differentValue = new OptionEntity(cacheKey, "Kiss me slow")

        expect:
        !refEntity.equals(differentValue)
        refEntity.hashCode() != differentValue.hashCode()
    }

}
