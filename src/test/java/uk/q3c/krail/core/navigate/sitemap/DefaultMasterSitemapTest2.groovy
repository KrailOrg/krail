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

package uk.q3c.krail.core.navigate.sitemap

import spock.lang.Specification
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler
import uk.q3c.krail.core.navigate.URIFragmentHandler

/**
 * Created by David Sowerby on 07 Jan 2016
 */
class DefaultMasterSitemapTest2 extends Specification {


    DefaultMasterSitemap sitemap

    URIFragmentHandler fragmentHandler = new StrictURIFragmentHandler()

    String uri = '/home/david'

    def setup() {
        sitemap = new DefaultMasterSitemap(fragmentHandler)
        sitemap.append(new NodeRecord(uri))
        sitemap.lock()
    }

    def "getChildCount() with node not in graph throws SitemapException"() {
        when:
        sitemap.getChildCount(new MasterSitemapNode(5, uri))

        then:
        thrown(SitemapException)

    }
}
