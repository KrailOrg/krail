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

package uk.q3c.krail.persist.inmemory;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.vaadin.data.util.BeanItemContainer;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.option.inmemory.InMemoryOptionContainerProvider;
import uk.q3c.krail.core.option.inmemory.container.DefaultInMemoryOptionContainerProvider;
import uk.q3c.krail.core.persist.inmemory.InMemoryContainerProvider;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.persist.I18NPersistenceEnabler;
import uk.q3c.krail.i18n.persist.I18NPersistenceHelper;
import uk.q3c.krail.i18n.persist.PatternDao;
import uk.q3c.krail.i18n.persist.PatternDaoProviders;
import uk.q3c.krail.option.inmemory.InMemoryOptionStore;
import uk.q3c.krail.option.inmemory.dao.InMemoryOptionDaoDelegate;
import uk.q3c.krail.option.inmemory.store.DefaultInMemoryOptionStore;
import uk.q3c.krail.option.persist.OptionContainerProvider;
import uk.q3c.krail.option.persist.OptionDaoDelegate;
import uk.q3c.krail.option.persist.OptionDaoProviders;
import uk.q3c.krail.option.persist.OptionPersistenceEnabler;
import uk.q3c.krail.persist.DefaultPersistenceInfo;
import uk.q3c.krail.persist.PersistenceInfo;
import uk.q3c.krail.persist.VaadinContainerProvider;
import uk.q3c.krail.persist.inmemory.entity.DefaultInMemoryPatternStore;

import java.lang.annotation.Annotation;

/**
 * A pseudo persistence module which actually just stores things in memory maps - useful for testing
 * <p>
 * Created by David Sowerby on 25/06/15.
 */
public class InMemoryModule extends AbstractModule implements I18NPersistenceEnabler<InMemoryModule>, OptionPersistenceEnabler<InMemoryModule> {

    private String connectionUrl = "in memory";
    private I18NKey description = DescriptionKey.Data_is_held_in_memory;
    private I18NKey name = LabelKey.In_Memory;
    private MapBinder<Class<? extends Annotation>, PersistenceInfo<?>> optionDaoProviders;
    private MapBinder<Class<? extends Annotation>, PersistenceInfo<?>> patternDaoProviders;
    private boolean provideOptionDao = false;
    private boolean providePatternDao;
    private boolean volatilePersistence = true;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {

        TypeLiteral<Class<? extends Annotation>> annotationClassLiteral = new TypeLiteral<Class<? extends Annotation>>() {
        };
        TypeLiteral<PersistenceInfo<?>> persistenceInfoClassLiteral = new TypeLiteral<PersistenceInfo<?>>() {
        };

        patternDaoProviders = I18NPersistenceHelper.patternDaoProviders(binder());

        patternDaoProviders = MapBinder.newMapBinder(binder(), annotationClassLiteral, persistenceInfoClassLiteral, PatternDaoProviders.class);
        optionDaoProviders = MapBinder.newMapBinder(binder(), annotationClassLiteral, persistenceInfoClassLiteral, OptionDaoProviders.class);


        bindStores();
        bindOptionDao();
        bindOptionContainerProvider();
        bindPatternDao();

    }

    protected void bindOptionContainerProvider() {
        if (provideOptionDao || providePatternDao) {
            bind(InMemoryOptionContainerProvider.class).to(DefaultInMemoryOptionContainerProvider.class);
            bind(OptionContainerProvider.class).annotatedWith(InMemory.class)
                    .to(InMemoryOptionContainerProvider.class);
        }
    }

    // TODO once pattern and option container providers are separated this shold not be necessary - tidy up
    private void bindStores() {
        if (provideOptionDao || providePatternDao) {
            bindOptionStore();
            bindPatternStore();
            bindContainerProvider();
        }
    }

    protected void bindContainerProvider() {
        TypeLiteral<VaadinContainerProvider<BeanItemContainer>> containerProviderLiteral = new TypeLiteral<VaadinContainerProvider<BeanItemContainer>>() {
        };
        bind(containerProviderLiteral).annotatedWith(InMemory.class)
                .to(InMemoryContainerProvider.class);
    }

    protected void bindOptionStore() {
        bind(InMemoryOptionStore.class).to(DefaultInMemoryOptionStore.class);
    }

    protected void bindPatternStore() {
        bind(InMemoryPatternStore.class).to(DefaultInMemoryPatternStore.class);
    }

    /**
     * binds {@link OptionDaoDelegate} annotated with {@link InMemory} but only if {@link #provideOptionDao} has been set by a previous call to {@link
     * #provideOptionDao()}
     */
    protected void bindOptionDao() {

        if (provideOptionDao) {
            bind(OptionDaoDelegate.class).annotatedWith(InMemory.class)
                    .to(InMemoryOptionDaoDelegate.class);
            optionDaoProviders.addBinding(InMemory.class)
                    .toInstance(new DefaultPersistenceInfo(this));


        }
    }

    /**
     * binds {@link PatternDao} annotated with {@link InMemory} but only if {@link #providePatternDao} has been set by a previous call to {@link #providePatternDao()}
     */
    protected void bindPatternDao() {

        if (providePatternDao) {
            bind(PatternDao.class).annotatedWith(InMemory.class)
                    .to(InMemoryPatternDao.class);
            patternDaoProviders.addBinding(InMemory.class)
                    .toInstance(new DefaultPersistenceInfo(this));

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InMemoryModule provideOptionDao() {
        provideOptionDao = true;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InMemoryModule providePatternDao() {
        providePatternDao = true;
        return this;
    }

    @Override
    public I18NKey getName() {
        return name;
    }

    @Override
    public String getConnectionUrl() {
        return connectionUrl;
    }

    @Override
    public I18NKey getDescription() {
        return description;
    }


    @Override
    public boolean isVolatilePersistence() {
        return volatilePersistence;
    }

    @Override
    public InMemoryModule name(final I18NKey name) {
        this.name = name;
        return this;
    }

    @Override
    public InMemoryModule description(final I18NKey description) {
        this.description = description;
        return this;
    }

    @Override
    public InMemoryModule connectionUrl(final String connectionUrl) {
        this.connectionUrl = connectionUrl;
        return this;
    }

    @Override
    public InMemoryModule volatilePersistence(final boolean volatilePersistence) {
        this.volatilePersistence = volatilePersistence;
        return this;
    }
}
