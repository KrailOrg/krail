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

package uk.q3c.krail.core.sysadmin

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.TypeLiteral
import spock.lang.Specification
import uk.q3c.krail.core.navigate.sitemap.DirectSitemapEntry

/**
 * Created by David Sowerby on 07 Feb 2016
 */
class SystemAdminPagesTest extends Specification {


    SystemAdminPages pages

    def setup() {
        pages = new SystemAdminPages()
    }

    def "construction"() {
        expect:
        pages.rootURI.equals("system-admin")
    }

    def "configure"() {
        given:
        TypeLiteral<Map<String, DirectSitemapEntry>> mapTypeLiteral = new TypeLiteral<Map<String, DirectSitemapEntry>>() {
        };
        Key<Map<String, DirectSitemapEntry>> mapKey = Key.get(mapTypeLiteral)

        when:
        Injector injector = Guice.createInjector(pages)
        Map<String, DirectSitemapEntry> map = injector.getInstance(mapKey)

        then:
        map != null
        map.size() == 4
        map.keySet().containsAll("system-admin", "system-admin/sitemap-build-report", "system-admin/option", "system-admin/i18n")
    }


}
