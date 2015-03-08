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

package uk.q3c.krail.core.user.opt;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.user.opt.cache.DefaultOptionCache;
import uk.q3c.krail.core.user.opt.cache.DefaultOptionCacheLoader;
import uk.q3c.krail.core.user.opt.cache.OptionCache;
import uk.q3c.krail.core.user.opt.cache.OptionCacheLoader;

/**
 * Created by David Sowerby on 16/11/14.
 */
public class OptionModule extends AbstractModule {

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bindOption();
        bindOptionStore();
        bindOptionCache();
        bindOptionCacheLoader();
        bindOptionDao();
        define();
    }

    protected void define() {

    }

    /**
     * Override this method to provide your own {@link OptionDao} implementation
     */
    protected void bindOptionDao() {
        bind(OptionDao.class).to(InMemoryOptionDao.class);
    }

    /**
     * Override this method to provide your own {@link OptionCache} implementation. The scope can be changed, but it
     * seems likely that {@link VaadinSessionScoped} will be most effective, as options are based on a user, and {@link
     * VaadinSessionScoped} is a user session.  However, for an application with a lot of options for anonymous users,
     * Singleton may work better
     */
    protected void bindOptionCache() {
        bind(OptionCache.class).to(DefaultOptionCache.class)
                               .in(VaadinSessionScoped.class);
    }

    /**
     * Override this method to provide your own {@link OptionCacheLoader} implementation
     */
    protected void bindOptionCacheLoader() {
        bind(OptionCacheLoader.class).to(DefaultOptionCacheLoader.class);
    }


    /**
     * Override this method to provide your own {@link Option} implementation. If all you want to do is change the
     * storage method, override {@link #bindOptionStore()} instead
     */
    protected void bindOption() {
        bind(Option.class).to(DefaultOption.class);
    }

    /**
     * Override this method to provide your own store implementation for user options. This is in effect a DAO
     * implementation
     */
    protected void bindOptionStore() {
        bind(OptionStore.class).to(InMemoryOptionStore.class);
    }


    protected void configureCache() {
        GuavaCacheConfiguration config = new GuavaCacheConfiguration();
    }
}
