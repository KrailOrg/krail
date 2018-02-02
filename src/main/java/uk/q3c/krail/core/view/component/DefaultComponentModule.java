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
package uk.q3c.krail.core.view.component;

import com.google.inject.AbstractModule;
import uk.q3c.krail.core.guice.DefaultBindingManager;

public class DefaultComponentModule extends AbstractModule {

    @Override
    protected void configure() {
        bindUserNavigationTree();
        bindBreadcrumb();
        bindLoginStatusPanel();
        bindNavigationMenu();
        bindSubpagePanel();
        bindMessageStatusPanel();
        bindApplicationLogo();
        bindApplicationHeader();
        bindLocaleSelector();
        bindUserStatusPanel();
    }


    /**
     * Override this method to provide your own implementation of {@link LocaleSelector} in a sub-class of this module.
     * Your module will then need to replace this module in {@link DefaultBindingManager}
     */
    protected void bindLocaleSelector() {
        bind(LocaleSelector.class).to(DefaultLocaleSelector.class);
    }

    /**
     * Override this method to provide your own implementation of {@link MessageBar} in a sub-class of this module.
     * Your
     * module will then need to replace this module in {@link DefaultBindingManager}
     */
    protected void bindMessageStatusPanel() {
        bind(MessageBar.class).to(DefaultMessageBar.class);
    }

    /**
     * Override this method to provide your own implementation of {@link ApplicationHeader} in a sub-class of this
     * module. Your module will then need to replace this module in {@link DefaultBindingManager}
     */
    protected void bindApplicationHeader() {
        bind(ApplicationHeader.class).to(DefaultApplicationHeader.class);
    }

    /**
     * Override this method to provide your own implementation of {@link ApplicationLogo} in a sub-class of this
     * module.
     * Your module will then need to replace this module in {@link DefaultBindingManager}
     */
    protected void bindApplicationLogo() {
        bind(ApplicationLogo.class).to(DefaultApplicationLogo.class);
    }

    /**
     * Override this method to provide your own implementation of {@link SubPagePanel} in a sub-class of this module.
     * Your module will then need to replace this module in {@link DefaultBindingManager}
     */
    protected void bindSubpagePanel() {
        bind(SubPagePanel.class).to(DefaultSubPagePanel.class);
    }

    /**
     * Override this method to provide your own implementation of {@link UserStatusPanel} in a sub-class of this
     * module.
     * Your module will then need to replace this module in {@link DefaultBindingManager}
     */
    protected void bindLoginStatusPanel() {
        bind(UserStatusPanel.class).to(DefaultUserStatusPanel.class);
    }

    /**
     * Override this method to provide your own implementation of {@link UserNavigationMenu} in a sub-class of this
     * module. Your module will then need to replace this module in {@link DefaultBindingManager}
     */
    protected void bindNavigationMenu() {
        bind(UserNavigationMenu.class).to(DefaultUserNavigationMenu.class);
        bind(UserNavigationMenuBuilder.class).to(DefaultUserNavigationMenuBuilder.class);
    }

    /**
     * Override this method to provide your own implementation of {@link Breadcrumb} in a sub-class of this module.
     * Your
     * module will then need to replace this module in {@link DefaultBindingManager}
     */
    protected void bindBreadcrumb() {
        bind(Breadcrumb.class).to(DefaultBreadcrumb.class);
    }

    /**
     * Override this method to provide your own implementation of {@link UserNavigationTree}, and its associated
     * builder, in a sub-class of this module. Your module will then need to replace this module in
     * {@link DefaultBindingManager}
     */
    protected void bindUserNavigationTree() {
        bind(UserNavigationTree.class).to(DefaultUserNavigationTree.class);
        bind(UserNavigationTreeBuilder.class).to(DefaultUserNavigationTreeBuilder.class);
    }

    /**
     * Override to bind your implementation of  UserStatusPanel
     */
    protected void bindUserStatusPanel() {
        bind(UserStatusPanel.class).to(DefaultUserStatusPanel.class);
    }


}
