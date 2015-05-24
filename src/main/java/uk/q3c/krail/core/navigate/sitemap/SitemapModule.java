/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.navigate.sitemap;

import com.google.inject.AbstractModule;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters;
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters;

public class SitemapModule extends AbstractModule {

    @Override
    protected void configure() {
        bindMasterSitemap();
        bindUserSitemap();
        bindService();
        bindLoaders();
        bindChecker();
    }

    private void bindMasterSitemap() {
        bind(MasterSitemap.class).to(DefaultMasterSitemap.class);
    }

    private void bindUserSitemap() {
        bind(UserSitemap.class).to(DefaultUserSitemap.class);
        bind(UserSitemapSorters.class).to(DefaultUserSitemapSorters.class);
    }

    protected void bindService() {
        bind(SitemapService.class).to(DefaultSitemapService.class);
    }

    protected void bindLoaders() {
        bind(FileSitemapLoader.class).to(DefaultFileSitemapLoader.class);
        bind(AnnotationSitemapLoader.class).to(DefaultAnnotationSitemapLoader.class);
        bind(DirectSitemapLoader.class).to(DefaultDirectSitemapLoader.class);
    }

    protected void bindChecker() {
        bind(SitemapFinisher.class).to(DefaultSitemapFinisher.class);

    }

}
