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

import java.io.Serializable

/**
 * Implementations check the Sitemap for inconsistencies, specifically:
 *
 *  1. redirect loops  (These are reported as errors)
 *
 * This used to check more things - those checks are now redundant (for example missing views and name keys) but
 * this interface name has not been changed
 *
 * @author David Sowerby
 */
interface SitemapFinisher : Serializable {

    /**
     * Throws a [SitemapException] if the check finishes with errors.
     *
     * @param sitemap the sitemap to check
     */
    fun check(sitemap: MasterSitemap)

    /**
     * Module names for the report
     *
     * @param names Module names for the report
     */
    fun setSourceModuleNames(names: Set<String>)

    /**
     * Annotation sources for the report
     *
     * @param sources Annotation sources for the report
     */
    fun setAnnotationSources(sources: Set<String>)


}
