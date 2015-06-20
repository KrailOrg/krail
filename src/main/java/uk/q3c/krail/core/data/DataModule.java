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

package uk.q3c.krail.core.data;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.vaadin.data.util.converter.ConverterFactory;
import uk.q3c.krail.i18n.InMemoryPatternDao;
import uk.q3c.krail.i18n.PatternDao;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides data related configuration
 *
 * Created by David Sowerby on 18/03/15.
 */
public class DataModule extends AbstractModule {

    private Class<? extends Annotation> prepPatternDaoAnnotatedWith;
    private Class<? extends PatternDao> prepPatternDaoImplementation;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        define();
        bindConverterFactory();
        bindPatternDao();

    }

    /**
     * Override this method to directly define configuration required, or {@link #patternDao} from your Binding Manager
     */
    protected void define() {

    }

    /**
     * Override to provide a {@link PatternDao} binding.  An implementation of PatternDao will be required if I18N patterns are sourced from a database (or
     * potentially REST service).  Binding will probably require the Key for the implementation, as it is likely to have been bound with an annotation
     */
    protected void bindPatternDao() {
        if (prepPatternDaoImplementation == null) {
            prepPatternDaoImplementation = InMemoryPatternDao.class;
        }
        if (prepPatternDaoAnnotatedWith == null) {
            bind(PatternDao.class).to(prepPatternDaoImplementation);
        } else {
            final Key<? extends PatternDao> key = Key.get(prepPatternDaoImplementation, prepPatternDaoAnnotatedWith);
            bind(PatternDao.class).to(key);
        }

    }

    /**
     * Provides a factory for converting data types for display by Vaadin.  Override this method to provide your own implementation
     */
    protected void bindConverterFactory() {
        bind(ConverterFactory.class).to(DefaultConverterFactory.class);
    }

    public DataModule patternDao(@Nonnull Class<? extends PatternDao> patternDaoImplementation) {
        checkNotNull(patternDaoImplementation);
        prepPatternDaoImplementation = patternDaoImplementation;
        return this;
    }

    public DataModule patternDao(@Nonnull Class<? extends PatternDao> patternDaoImplementation, @Nonnull Class<? extends Annotation> annotatedWith) {
        checkNotNull(patternDaoImplementation);
        prepPatternDaoImplementation = patternDaoImplementation;
        prepPatternDaoAnnotatedWith = annotatedWith;
        return this;
    }
}
