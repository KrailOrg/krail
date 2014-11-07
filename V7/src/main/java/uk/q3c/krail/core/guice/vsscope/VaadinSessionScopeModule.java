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
package uk.q3c.krail.core.guice.vsscope;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import uk.q3c.krail.core.shiro.DefaultVaadinSessionProvider;
import uk.q3c.krail.core.shiro.VaadinSessionProvider;

/**
 * {@link VaadinSessionScope} support module.
 *
 * @authors David Sowerby
 */
public class VaadinSessionScopeModule extends AbstractModule {
    private final VaadinSessionScope vaadinSessionScope;

    public VaadinSessionScopeModule() {
        super();
        vaadinSessionScope = VaadinSessionScope.getCurrent();
    }

    @Override
    public void configure() {
        // tell Guice about the scope
        bindScope(VaadinSessionScoped.class, vaadinSessionScope);

        // make our scope instance injectable
        bind(VaadinSessionScope.class).annotatedWith(Names.named("VaadinSessionScope"))
                                      .toInstance(vaadinSessionScope);

        bindVaadinSessionProvider();
    }

    /**
     * Override this to use a different implementation for a VaadinSessionProvider
     */
    protected void bindVaadinSessionProvider() {
        bind(VaadinSessionProvider.class).to(DefaultVaadinSessionProvider.class);
    }

    public VaadinSessionScope getUiScope() {
        return vaadinSessionScope;
    }

}