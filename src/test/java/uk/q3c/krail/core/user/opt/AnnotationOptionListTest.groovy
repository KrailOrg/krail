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

import com.google.common.collect.ImmutableList
import spock.lang.Specification
import uk.q3c.krail.UnitTestFor
import uk.q3c.krail.i18n.ClassPatternSource

/**
 *
 * Created by David Sowerby on 08/08/15.
 */
@UnitTestFor(AnnotationOptionList)
class AnnotationOptionListTest extends Specification {

    AnnotationOptionList list

    def "create empty list"() {
        when:
        list = new AnnotationOptionList()

        then:

        list.isEmpty()
    }

    def "create with elements"() {
        when:
        list = new AnnotationOptionList(ClassPatternSource)

        then:
        list.size() == 1
        list.getList().contains(ClassPatternSource)
    }

    def "create with list"() {
        when:
        list = new AnnotationOptionList(ImmutableList.of(ClassPatternSource))

        then:
        list.size() == 1
        list.getList().contains(ClassPatternSource)
    }

    def "create with empty list"() {
        when:
        list = new AnnotationOptionList(ImmutableList.of())

        then:

        list.isEmpty()
    }


}
