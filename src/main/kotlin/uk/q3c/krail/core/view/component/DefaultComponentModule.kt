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

open class DefaultComponentModule : AbstractModule() {

    override fun configure() {
        bindUserNavigationTree()
        bindLoginStatusPanel()
        bindNavigationMenu()
        bindMessageStatusPanel()
        bindApplicationLogo()
        bindApplicationHeader()
        bindLocaleSelector()
        bindUserStatusPanel()
        bindUserStatusComponents()
        bindSubPageButtonBuilder()
        bindPageNavigationPanel()
        bindIconFactory()
        bindTranslatableComponents()
    }


    /**
     * Override this method to provide your own implementation of [TranslatableComponents] in a sub-class of this module.
     * Your module will then need to replace this module in [BindingsCollator]
     */
    protected open fun bindTranslatableComponents() {
        bind(TranslatableComponents::class.java).to(DefaultTranslatableComponents::class.java)
    }


    /**
     * Override this method to provide your own implementation of [PageNavigationPanel] in a sub-class of this module.
     * Your module will then need to replace this module in [BindingsCollator]
     */
    protected open fun bindPageNavigationPanel() {
        bind(PageNavigationPanel::class.java).to(DefaultPageNavigationPanel::class.java)
    }

    /**
     * Override this method to provide your own implementation of [IconFactory] in a sub-class of this module.
     * Your module will then need to replace this module in [BindingsCollator]
     */
    protected open fun bindIconFactory() {
        bind(IconFactory::class.java).to(DefaultIconFactory::class.java)
    }


    /**
     * Override this method to provide your own implementation of [LocaleSelector] in a sub-class of this module.
     * Your module will then need to replace this module in [BindingsCollator]
     */
    protected open fun bindLocaleSelector() {
        bind(LocaleSelector::class.java).to(DefaultLocaleSelector::class.java)
    }

    /**
     * Override this method to provide your own implementation of [MessageBar] in a sub-class of this module.
     * Your
     * module will then need to replace this module in [BindingsCollator]
     */
    protected open fun bindMessageStatusPanel() {
        bind(MessageBar::class.java).to(DefaultMessageBar::class.java)
    }

    /**
     * Override this method to provide your own implementation of [ApplicationHeader] in a sub-class of this
     * module. Your module will then need to replace this module in [BindingsCollator]
     */
    protected open fun bindApplicationHeader() {
        bind(ApplicationHeader::class.java).to(DefaultApplicationHeader::class.java)
    }

    /**
     * Override this method to provide your own implementation of [ApplicationLogo] in a sub-class of this
     * module.
     * Your module will then need to replace this module in [BindingsCollator]
     */
    protected open fun bindApplicationLogo() {
        bind(ApplicationLogo::class.java).to(DefaultApplicationLogo::class.java)
    }

    /**
     * Override this method to provide your own implementation of [UserStatusPanel] in a sub-class of this
     * module.
     * Your module will then need to replace this module in [BindingsCollator]
     */
    protected open fun bindLoginStatusPanel() {
        bind(UserStatusPanel::class.java).to(DefaultUserStatusPanel::class.java)
    }

    /**
     * Override this method to provide your own implementation of [UserNavigationMenu] in a sub-class of this
     * module. Your module will then need to replace this module in [BindingsCollator]
     */
    protected open fun bindNavigationMenu() {
        bind(UserNavigationMenu::class.java).to(DefaultUserNavigationMenu::class.java)
        bind(UserNavigationMenuBuilder::class.java).to(DefaultUserNavigationMenuBuilder::class.java)
    }

    /**
     * Override this method to provide your own implementation of [UserNavigationTree], and its associated
     * builder, in a sub-class of this module. Your module will then need to replace this module in
     * [BindingsCollator]
     */
    protected open fun bindUserNavigationTree() {
        bind(UserNavigationTree::class.java).to(DefaultUserNavigationTree::class.java)
        bind(UserNavigationTreeBuilder::class.java).to(DefaultUserNavigationTreeBuilder::class.java)
    }

    /**
     * Override this method to provide your own implementation of [UserStatusPanel], and its associated
     * builder, in a sub-class of this module. Your module will then need to replace this module in
     * [BindingsCollator]
     */
    protected open fun bindUserStatusPanel() {
        bind(UserStatusPanel::class.java).to(DefaultUserStatusPanel::class.java)
    }

    /**
     * Override this method to provide your own implementation of [UserStatusComponents], and its associated
     * builder, in a sub-class of this module. Your module will then need to replace this module in
     * [BindingsCollator]
     */
    protected open fun bindUserStatusComponents() {
        bind(UserStatusComponents::class.java).to(DefaultUserStatusComponents::class.java)
    }

    /**
     * Override this method to provide your own implementation of [PageNavigationButtonBuilder], and its associated
     * builder, in a sub-class of this module. Your module will then need to replace this module in
     * [BindingsCollator]
     */
    protected open fun bindSubPageButtonBuilder() {
        bind(PageNavigationButtonBuilder::class.java).to(DefaultPageNavigationButtonBuilder::class.java)
    }
}
