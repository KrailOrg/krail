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

import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters;
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters;
import uk.q3c.krail.core.services.AbstractServiceModule;
import uk.q3c.krail.core.services.Dependency;
import uk.q3c.krail.i18n.LabelKey;

public class SitemapModule extends AbstractServiceModule {

    @Override
    protected void configure() {
        super.configure();
        bindMasterSitemap();
        bindUserSitemap();
        bindService();
        bindLoaders();
        bindChecker();
    }

    @Override
    protected void registerServices() {
        registerService(LabelKey.Sitemap_Service, SitemapService.class);
    }

    @Override
    protected void defineDependencies() {
        addDependency(LabelKey.Sitemap_Service, LabelKey.Application_Configuration_Service, Dependency.Type.REQUIRED_ONLY_AT_START);
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
        bind(AnnotationSitemapLoader.class).to(DefaultAnnotationSitemapLoader.class);
        bind(DirectSitemapLoader.class).to(DefaultDirectSitemapLoader.class);
    }

    protected void bindChecker() {
        bind(SitemapFinisher.class).to(DefaultSitemapFinisher.class);

    }

}
