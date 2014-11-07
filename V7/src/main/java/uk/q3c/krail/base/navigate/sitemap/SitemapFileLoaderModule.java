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
package uk.q3c.krail.base.navigate.sitemap;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

/**
 * Adds a sitemap file loader at priority 50
 *
 * @author David Sowerby
 */
public class SitemapFileLoaderModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder<Integer, SitemapLoader> mapbinder = MapBinder.newMapBinder(binder(), Integer.class,
                SitemapLoader.class);
        mapbinder.addBinding(new Integer(50))
                 .to(FileSitemapLoader.class);

    }

}
