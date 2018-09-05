/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.core.view

import com.vaadin.ui.Component
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import uk.q3c.krail.core.navigate.DefaultNavigator
import uk.q3c.krail.core.ui.ScopedUI
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage
import uk.q3c.krail.core.view.component.ComponentIdGenerator
import uk.q3c.krail.i18n.NamedAndDescribed
import java.io.Serializable


/**
 * A view is constructed by the [ViewFactory] from a Provider defined in the sitemap building process.  When
 * the view is selected for use, messages are sent to the event bus, and calls made to this interface, in the following order:
 *
 *  1. A [BeforeViewChangeBusMessage] is published on the @UIBus
 *  1. [init]
 *  1. [beforeBuild]
 *  1. [buildView]
 *  1. [afterBuild]
 *  1. An [AfterViewChangeBusMessage] is published on the @UIBus
 *
 * where build refers to the creation of UI fields and components which populate the view.
 *
 *
 * The easiest way to implement this is to extend [ViewBase]
 */
interface KrailView : NamedAndDescribed, Serializable {

    /**
     * To enable implementations to implement this interface without descending from Component. If the implementation
     * does descend from Component, just return 'this'.  Throws a ViewBuildException if the root component has not been
     * set
     *
     */
    var rootComponent: Component

    /**
     * Called after the view itself has been constructed but before [.buildView]} is called.  Typically checks
     * whether a valid URI parameters are being passed to the view, or uses the URI parameters to set up some
     * configuration which affects the way the view is presented.
     *
     * @param navigationStateExt contains information about the change to this View
     */

    fun beforeBuild(navigationStateExt: NavigationStateExt)

    /**
     * Builds the UI components of the view.  MUST set the root component of the View (returned by [ ][.getRootComponent], which is used to insert into the [ScopedUI] view area. The view implementation may
     * need to check whether components have already been constructed, as this method may be called when the View is
     * selected again after initial construction.
     *
     * **Note:** Just after this method is called, the [DefaultNavigator] invokes the [ComponentIdGenerator] to provide ids to components
     *
     */

    fun buildView()

    /**
     * Called by the [ViewFactory] after the instantiation of a view, but before components are constructed by [.buildView]
     * Intended for initialisation which does not depend on navigation state.
     */
    fun init()

    /**
     * Called immediately after the construction of the View's components (see [.buildView]) to enable setting up
     * the view from URL parameters.
     *
     */

    fun afterBuild()

    /**
     * Notify the view that it needs to rebuild its components.  By default does nothing
     */
    @SuppressFBWarnings("ACEM_ABSTRACT_CLASS_EMPTY_METHODS")
    fun rebuild() {
    }
}
