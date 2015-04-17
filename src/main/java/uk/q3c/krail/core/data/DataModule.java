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
import com.google.inject.Binder;
import com.vaadin.data.util.converter.ConverterFactory;
import uk.q3c.krail.i18n.PatternDao;

/**
 * Created by David Sowerby on 18/03/15.
 */
public class DataModule extends AbstractModule {
    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bindConverterFactory();
        bindPatternDao();
        //        bindConfigurations();
        //        bindConnectionProvider();
        //        configureDataSources();
    }

    /**
     * Override to provide a {@link PatternDao} binding.  An implementation of PatternDao will be required if I18N patterns are sourced from a database (or
     * potentially REST service).  Binding will probably
     * require the Key for the implementation, as it is likely to have been bound with an annotation
     */
    protected void bindPatternDao() {
        //bind(PatternDao.class).to(Key.get(Key.get(JpaPatternDao.class, Jpa1.class)));
    }

    //
    //    /**
    //     * A {@link DataSourceConfiguration} contains one or more {@link DataSourceInstanceConfiguration} which contains one or more {@link
    //     * DataSourceConnectionMethod}.  The last is to allow for data bases / sources which have multiple models - for example, OrientDb provides a Graph,
    //     * Document and Object API.<p>We generally recommend the use of binding annotations to distinguish between multiple implementations of the
    //     * same interface, but you can also use a @Named annotation (the latter is more prone to typos and is less refactor friendly).
    //     */
    //    protected void bindConfigurations() {
    //
    //    }

    //    /**
    //     * Different data sources (for example JPA, OrientDb & REST) are supported, and each may have multiple instances (DEV, TEST PROD etc).  These bindings
    //     * bring the overall configuration together ... the {@link DataSourceService} uses this configuration to start up / check the presence of required
    // data
    //     * sources, and the {@link DataSourceConnectionProvider} to provide a (usually pooled) connection.<p>
    //     */
    //    protected void configureDataSources() {
    //
    //    }

    /**
     * Provides a factory for converting data types for display by Vaadin.  Override this method to provide your own implementation
     */
    protected void bindConverterFactory() {
        bind(ConverterFactory.class).to(DefaultConverterFactory.class);
    }
}
