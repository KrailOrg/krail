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
import com.google.inject.TypeLiteral;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.persist.ActiveOptionDao;
import uk.q3c.krail.core.persist.CoreOptionDaoProvider;
import uk.q3c.krail.core.persist.DefaultCoreOptionDaoProvider;
import uk.q3c.krail.core.user.opt.cache.*;

import java.lang.annotation.Annotation;

/**
 * Configures the use of {@link Option}
 * <p>
 * Created by David Sowerby on 16/11/14.
 */
public class OptionModule extends AbstractModule {


    private Class<? extends Annotation> activeDaoAnnotation;

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bindOption();
        bindOptionCacheConfiguration();
        bindOptionCache();
        bindOptionCacheProvider();
        bindOptionPopup();
        bindCoreOptionDaoProvider();
        bindDao();
    }


    /**
     * Binds the active Dao, or if none has been defined, uses {@link InMemory}
     */
    protected void bindDao() {
        Class<? extends Annotation> annotationClass = (activeDaoAnnotation == null) ? InMemory.class : activeDaoAnnotation;
        TypeLiteral<Class<? extends Annotation>> annotationTypeLiteral = new TypeLiteral<Class<? extends Annotation>>() {
        };
        bind(annotationTypeLiteral).annotatedWith(ActiveOptionDao.class)
                                   .toInstance(annotationClass);
    }


    /**
     * Override this method to provide your own {@link CoreOptionDaoProvider} implementation
     */
    protected void bindCoreOptionDaoProvider() {
        bind(CoreOptionDaoProvider.class).to(DefaultCoreOptionDaoProvider.class);
    }


    /**
     * Override this method to provide your own {@link OptionPopup} implementation
     */
    protected void bindOptionPopup() {
        bind(OptionPopup.class).to(DefaultOptionPopup.class);
    }

    /**
     * Override this method to provide your own {@link OptionCacheProvider} implementation.
     */
    protected void bindOptionCacheProvider() {
        bind(OptionCacheProvider.class).to(DefaultOptionCacheProvider.class);
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

    protected void bindOptionCacheConfiguration() {
        bind(GuavaCacheConfiguration.class).annotatedWith(OptionCacheConfig.class)
                                           .toInstance(configureCache());
    }

    /**
     * Override this to configure the option cache
     *
     * @return a GuavaCacheConfiguration instance
     */
    protected GuavaCacheConfiguration configureCache() {
        GuavaCacheConfiguration config = new GuavaCacheConfiguration();
        config.maximumSize(5000)
              .recordStats();
        return config;
    }

    /**
     * Override this method to provide your own {@link Option} implementation.
     */
    protected void bindOption() {
        bind(Option.class).to(DefaultOption.class);
    }

    public OptionModule activeDao(Class<? extends Annotation> annotationClass) {
        activeDaoAnnotation = annotationClass;
        return this;
    }

}
