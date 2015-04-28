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
import com.google.inject.multibindings.MapBinder;
import uk.q3c.krail.core.guice.DefaultBindingManager;

/**
 * If you want to create Sitemap entries for your own code from a sitemap properties file, you can either subclass this
 * module and provide the entries in the {@link #define} method, or just simply use this as an example and create your
 * own. The module then needs to be added to your subclass of {@link DefaultBindingManager}. By convention, modules
 * relating to the Sitemap are added in the addSitemapModules() method.
 * <p/>
 * You can add any number of modules this way, but any duplicated map keys (the keys you specify for
 * {@link #addEntry(String, SitemapFile)}) will cause the map injection to fail. There is an option to change this
 * behaviour in MapBinder#permitDuplicates().
 * <p/>
 * You can use multiple subclasses of this, Guice will merge all of the bindings into a single MapBinder<String,
 * SitemapFile> for use by the {@link FileSitemapLoader} - so the keys you provide must be unique across the
 * application.
 * @deprecated see <a href="https://github.com/davidsowerby/krail/issues/375">Issue 375</a>
 * @author David Sowerby
 */
@Deprecated
public abstract class FileSitemapModule extends AbstractModule {

    private MapBinder<String, SitemapFile> mapBinder;

    @Override
    protected void configure() {
        MapBinder<String, SitemapFile> mapBinder = MapBinder.newMapBinder(binder(), String.class, SitemapFile.class);
        this.mapBinder = mapBinder;

        define();
    }

    /**
     * Override this method one or more calls to {@link #addEntry} to list the sitemap files to be read, something
     * like:
     * <p/>
     * addEntry("my views","com.example.views")
     */
    protected abstract void define();

    /**
     * Maps an arbitrary String key to a file name containing a Sitemap definition
     *
     * @param key
     * @param file
     */
    protected void addEntry(String key, SitemapFile file) {
        mapBinder.addBinding(key)
                 .toInstance(file);
    }

}
