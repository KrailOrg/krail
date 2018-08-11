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
package uk.q3c.krail.core.view.component

import com.google.inject.AbstractModule
import uk.q3c.krail.core.env.BindingsCollator

class DefaultComponentModule : AbstractModule() {

    override fun configure() {
        bindUserNavigationTree()
        bindBreadcrumb()
        bindLoginStatusPanel()
        bindNavigationMenu()
        bindSubpagePanel()
        bindMessageStatusPanel()
        bindApplicationLogo()
        bindApplicationHeader()
        bindLocaleSelector()
        bindUserStatusPanel()
        bindSubPageButtonBuilder()
        bindPageNavigationPanel()
    }

    /**
     * Override this method to provide your own implementation of [PageNavigationPanel] in a sub-class of this module.
     * Your module will then need to replace this module in [BindingsCollator]
     */
    protected fun bindPageNavigationPanel() {
        bind(PageNavigationPanel::class.java).to(DefaultPageNavigationPanel::class.java)
    }


    /**
     * Override this method to provide your own implementation of [LocaleSelector] in a sub-class of this module.
     * Your module will then need to replace this module in [BindingsCollator]
     */
    protected fun bindLocaleSelector() {
        bind(LocaleSelector::class.java).to(DefaultLocaleSelector::class.java)
    }

    /**
     * Override this method to provide your own implementation of [MessageBar] in a sub-class of this module.
     * Your
     * module will then need to replace this module in [BindingsCollator]
     */
    protected fun bindMessageStatusPanel() {
        bind(MessageBar::class.java).to(DefaultMessageBar::class.java)
    }

    /**
     * Override this method to provide your own implementation of [ApplicationHeader] in a sub-class of this
     * module. Your module will then need to replace this module in [BindingsCollator]
     */
    protected fun bindApplicationHeader() {
        bind(ApplicationHeader::class.java).to(DefaultApplicationHeader::class.java)
    }

    /**
     * Override this method to provide your own implementation of [ApplicationLogo] in a sub-class of this
     * module.
     * Your module will then need to replace this module in [BindingsCollator]
     */
    protected fun bindApplicationLogo() {
        bind(ApplicationLogo::class.java).to(DefaultApplicationLogo::class.java)
    }

    /**
     * Override this method to provide your own implementation of [SubPagePanel] in a sub-class of this module.
     * Your module will then need to replace this module in [BindingsCollator]
     */
    protected fun bindSubpagePanel() {
        bind(SubPagePanel::class.java).to(DefaultSubPagePanel::class.java)
    }

    /**
     * Override this method to provide your own implementation of [UserStatusPanel] in a sub-class of this
     * module.
     * Your module will then need to replace this module in [BindingsCollator]
     */
    protected fun bindLoginStatusPanel() {
        bind(UserStatusPanel::class.java).to(DefaultUserStatusPanel::class.java)
    }

    /**
     * Override this method to provide your own implementation of [UserNavigationMenu] in a sub-class of this
     * module. Your module will then need to replace this module in [BindingsCollator]
     */
    protected fun bindNavigationMenu() {
        bind(UserNavigationMenu::class.java).to(DefaultUserNavigationMenu::class.java)
        bind(UserNavigationMenuBuilder::class.java).to(DefaultUserNavigationMenuBuilder::class.java)
    }

    /**
     * Override this method to provide your own implementation of [Breadcrumb] in a sub-class of this module.
     * Your
     * module will then need to replace this module in [BindingsCollator]
     */
    protected fun bindBreadcrumb() {
        bind(Breadcrumb::class.java).to(DefaultBreadcrumb::class.java)
    }

    /**
     * Override this method to provide your own implementation of [UserNavigationTree], and its associated
     * builder, in a sub-class of this module. Your module will then need to replace this module in
     * [BindingsCollator]
     */
    protected fun bindUserNavigationTree() {
        bind(UserNavigationTree::class.java).to(DefaultUserNavigationTree::class.java)
        bind(UserNavigationTreeBuilder::class.java).to(DefaultUserNavigationTreeBuilder::class.java)
    }

    /**
     * Override this method to provide your own implementation of [UserStatusPanel], and its associated
     * builder, in a sub-class of this module. Your module will then need to replace this module in
     * [BindingsCollator]
     */
    protected fun bindUserStatusPanel() {
        bind(UserStatusPanel::class.java).to(DefaultUserStatusPanel::class.java)
    }

    /**
     * Override this method to provide your own implementation of [PageNavigationButtonBuilder], and its associated
     * builder, in a sub-class of this module. Your module will then need to replace this module in
     * [BindingsCollator]
     */
    protected fun bindSubPageButtonBuilder() {
        bind(PageNavigationButtonBuilder::class.java).to(DefaultPageNavigationButtonBuilder::class.java)
    }
}
