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
import uk.q3c.krail.core.user.opt.*;
import uk.q3c.krail.i18n.DefaultInMemoryPatternStore;
import uk.q3c.krail.i18n.InMemoryPatternDao;
import uk.q3c.krail.i18n.InMemoryPatternStore;
import uk.q3c.krail.i18n.PatternDao;

/**
 * A pseudo persistence module which actually just stores things in memory maps - useful for testing
 * <p>
 * Created by David Sowerby on 25/06/15.
 */
public class InMemoryModule extends AbstractModule implements KrailPersistenceModule<InMemoryModule> {

    private boolean provideCoreOptionDao = false;
    private boolean provideCorePatternDao;
    private boolean provideOptionDao = false;
    private boolean providePatternDao;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bindOptionDao();
        bindPatternDao();
    }


    protected void bindOptionDao() {
        if (provideCoreOptionDao) {
            bind(OptionDao.class).annotatedWith(CoreDao.class)
                                 .to(InMemoryOptionDao.class);
        }
        if (provideOptionDao) {
            bind(OptionDao.class).annotatedWith(InMemory.class)
                                 .to(InMemoryOptionDao.class);
        }
        if (provideCoreOptionDao || provideOptionDao) {
            bindOptionStore();
        }
    }

    protected void bindOptionStore() {
        bind(OptionStore.class).to(InMemoryOptionStore.class);
    }

    protected void bindPatternDao() {
        if (provideCorePatternDao) {
            bind(PatternDao.class).annotatedWith(CoreDao.class)
                                  .to(InMemoryPatternDao.class);
        }
        if (providePatternDao) {
            bind(PatternDao.class).annotatedWith(InMemory.class)
                                  .to(InMemoryPatternDao.class);
        }
        if (provideCorePatternDao || providePatternDao) {
            bindPatternStore();
        }
    }

    protected void bindPatternStore() {
        bind(InMemoryPatternStore.class).to(DefaultInMemoryPatternStore.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InMemoryModule provideCoreOptionDao() {
        provideCoreOptionDao = true;
        return this;
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
    public InMemoryModule provideCorePatternDao() {
        provideCorePatternDao = true;
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
