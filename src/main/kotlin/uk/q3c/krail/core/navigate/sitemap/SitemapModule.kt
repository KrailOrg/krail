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

import com.google.inject.AbstractModule
import com.google.inject.Singleton
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters

open class SitemapModule : AbstractModule() {

    override fun configure() {
        bindMasterSitemap()
        bindUserSitemap()
        bindUserSitemapNodeSorter()
        bindService()
        bindLoaders()
        bindChecker()
        bindEmptyView()
    }

    protected fun bindEmptyView() {
        bind(EmptyView::class.java).to(DefaultEmptyView::class.java)
    }


    open protected fun bindMasterSitemap() {
        bind(MasterSitemap::class.java).to(DefaultMasterSitemap::class.java).`in`(Singleton::class.java)
    }

    protected fun bindUserSitemapNodeSorter() {
        bind(UserSitemapNodeSorter::class.java).to(DefaultUserSitemapNodeSorter::class.java)
    }

    protected open fun bindUserSitemap() {
        bind(UserSitemap::class.java).to(DefaultUserSitemap::class.java)
        bind(UserSitemapSorters::class.java).to(DefaultUserSitemapSorters::class.java)
    }

    protected fun bindService() {
        bind(SitemapService::class.java).to(DefaultSitemapService::class.java)
    }

    protected fun bindLoaders() {
        bind(AnnotationSitemapLoader::class.java).to(DefaultAnnotationSitemapLoader::class.java)
        bind(DirectSitemapLoader::class.java).to(DefaultDirectSitemapLoader::class.java)
    }

    protected fun bindChecker() {
        bind(SitemapFinisher::class.java).to(DefaultSitemapFinisher::class.java)

    }

}
