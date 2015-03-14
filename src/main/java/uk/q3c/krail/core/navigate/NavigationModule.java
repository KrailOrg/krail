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

package uk.q3c.krail.core.navigate;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;

/**
 * Created by David Sowerby on 08/02/15.
 */
public class NavigationModule extends AbstractModule {

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bindNavigator();
        bindURIHandler();
        bindNavigationRules();
        bindViewChangeRule();
    }

    protected void bindViewChangeRule() {
        bind(ViewChangeRule.class).to(DefaultViewChangeRule.class);
    }

    protected void bindNavigationRules() {
        bind(LoginNavigationRule.class).to(DefaultLoginNavigationRule.class);
        bind(LogoutNavigationRule.class).to(DefaultLogoutNavigationRule.class);
    }


    /**
     * Override to bind your choice of URI handler
     */
    protected void bindURIHandler() {
        bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
    }


    protected void bindNavigator() {
        bind(Navigator.class).to(DefaultNavigator.class);
    }
}
