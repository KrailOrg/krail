/*
 * Copyright (C) 2014 David Sowerby
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
package uk.q3c.krail.base.view;

import com.google.inject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.base.guice.uiscope.UIScope;

public class DefaultViewFactory implements ViewFactory {
    private static Logger log = LoggerFactory.getLogger(DefaultViewFactory.class);
    private final Injector injector;

    @Inject
    protected DefaultViewFactory(Injector injector) {
        super();
        this.injector = injector;
    }

    /* (non-Javadoc)
     * @see uk.q3c.krail.base.view.ViewFactory#get(java.lang.Class)
     */
    @Override
    public <T extends KrailView> T get(Class<T> viewClass) {
        TypeLiteral<T> typeLiteral = TypeLiteral.get(viewClass);
        Key<T> key = Key.get(typeLiteral);
        Provider<T> unscoped = injector.getProvider(key);
        UIScope.getCurrent()
               .scope(key, unscoped);
        log.debug("getting or retrieving instance of {}", viewClass);
        T view = injector.getInstance(key);
        log.debug("Calling view.readFromEnvironment()");
        view.init();
        return view;
    }
}
