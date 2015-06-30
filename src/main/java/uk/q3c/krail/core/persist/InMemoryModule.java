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

package uk.q3c.krail.core.persist;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import uk.q3c.krail.core.user.opt.*;
import uk.q3c.krail.i18n.DefaultInMemoryPatternStore;
import uk.q3c.krail.i18n.InMemoryPatternDao;
import uk.q3c.krail.i18n.InMemoryPatternStore;
import uk.q3c.krail.i18n.PatternDao;

import java.lang.annotation.Annotation;

/**
 * A pseudo persistence module which actually just stores things in memory maps - useful for testing
 * <p>
 * Created by David Sowerby on 25/06/15.
 */
public class InMemoryModule extends AbstractModule implements KrailPersistenceUnit<InMemoryModule> {

    private Multibinder<Class<? extends Annotation>> optionDaoProviders;
    private Multibinder<Class<? extends Annotation>> patternDaoProviders;
    private boolean provideOptionDao = false;
    private boolean providePatternDao;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {

        TypeLiteral<Class<? extends Annotation>> annotationClassLiteral = new TypeLiteral<Class<? extends Annotation>>() {
        };

        patternDaoProviders = Multibinder.newSetBinder(binder(), annotationClassLiteral, PatternDaoProviders.class);
        optionDaoProviders = Multibinder.newSetBinder(binder(), annotationClassLiteral, OptionDaoProviders.class);


        bindOptionDao();
        bindPatternDao();
    }


    protected void bindOptionDao() {

        if (provideOptionDao) {
            bindOptionStore();
            bind(OptionDao.class).annotatedWith(InMemory.class)
                                 .to(InMemoryOptionDao.class);
            optionDaoProviders.addBinding()
                              .toInstance(InMemory.class);

        }
    }

    protected void bindOptionStore() {
        bind(InMemoryOptionStore.class).to(DefaultInMemoryOptionStore.class);
    }

    protected void bindPatternDao() {

        if (providePatternDao) {
            bindPatternStore();
            bind(PatternDao.class).annotatedWith(InMemory.class)
                                  .to(InMemoryPatternDao.class);
            patternDaoProviders.addBinding()
                               .toInstance(InMemory.class);

        }
    }

    protected void bindPatternStore() {
        bind(InMemoryPatternStore.class).to(DefaultInMemoryPatternStore.class);
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


}
