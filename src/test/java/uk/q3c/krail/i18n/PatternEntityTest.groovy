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

package uk.q3c.krail.i18n

import spock.lang.Specification

/**
 * Created by David Sowerby on 16 Jan 2016
 */
class PatternEntityTest extends Specification {

    PatternEntity entity;

    def "create decodes the I18NKey"() {
        given:

        PatternCacheKey cacheKey1 = new PatternCacheKey(LabelKey.Yes, Locale.UK)

        when:

        entity = new PatternEntity(cacheKey1, "x")


        then:

        entity.getI18nkey().equals("uk.q3c.krail.i18n.LabelKey.Yes")
    }
}


