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

package uk.q3c.krail.core.user.opt

import com.google.common.collect.Lists
import spock.lang.Specification

/**
 * Created by David Sowerby on 05/08/15.
 */
class OptionListTest extends Specification {


    def "genericType"() {
        given:
        List<String> sourceList = Lists.newArrayList("a", "b")

        when:

        OptionList<String> optionList = new OptionList<>(sourceList, String.class)

        then:

        optionList.getElementClass() == String.class
        optionList.getList() == sourceList
        optionList.isEmpty() == false
    }
}
