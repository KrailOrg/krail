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

package uk.q3c.krail.core.view;

import com.vaadin.ui.Component;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import uk.q3c.krail.core.navigate.DefaultNavigator;
import uk.q3c.krail.core.ui.ScopedUI;
import uk.q3c.krail.core.view.component.AfterViewChangeBusMessage;
import uk.q3c.krail.core.view.component.ComponentIdGenerator;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.NamedAndDescribed;


/**
 * A view is constructed by the {@link ViewFactory} from a Provider defined in the sitemap building process.  When
 * the view is selected for use, messages are sent to the event bus, and calls made to this interface, in the following order:
 * <ol>
 * <li>A {@link BeforeViewChangeBusMessage} is published on the @UIBus</li>
 * <li>{@link #init()}</li>
 * <li>{@link #beforeBuild(ViewChangeBusMessage)}</li>
 * <li>{@link #buildView(ViewChangeBusMessage)}</li>
 * <li>{@link #afterBuild(AfterViewChangeBusMessage)}</li>
 * <li>An {@link AfterViewChangeBusMessage} is published on the @UIBus</li>
 * </ol>
 * where build refers to the creation of UI fields and components which populate the view.
 * <p>
 * The easiest way to implement this is to extend {@link ViewBase}
 */
public interface KrailView extends NamedAndDescribed {

    /**
     * Called after the view itself has been constructed but before {@link #buildView(ViewChangeBusMessage)}} is called.  Typically checks
     * whether a valid URI parameters are being passed to the view, or uses the URI parameters to set up some
     * configuration which affects the way the view is presented.
     *
     * @param busMessage contains information about the change to this View
     */
    void beforeBuild(ViewChangeBusMessage busMessage);

    /**
     * Builds the UI components of the view.  MUST set the root component of the View (returned by {@link
     * #getRootComponent()}, which is used to insert into the {@link ScopedUI} view area. The view implementation may
     * need to check whether components have already been constructed, as this method may be called when the View is
     * selected again after initial construction.
     *
     * <b>Note:</b> Just after this method is called, the {@link DefaultNavigator} invokes the {@link ComponentIdGenerator} to provide ids to components
     *
     * @param busMessage contains information about the change to this View
     */
    void buildView(ViewChangeBusMessage busMessage);

    /**
     * To enable implementations to implement this interface without descending from Component. If the implementation
     * does descend from Component, just return 'this'.  Throws a ViewBuildException if the root component has not been
     * set
     *
     * @return
     */
    Component getRootComponent();

    /**
     * Called by the {@link ViewFactory} after the instantiation of a view, but before components are constructed by {@Link #buildView}
     * Intended for initialisation which does not depend on navigation state.
     */
    void init();

    /**
     * Called immediately after the construction of the View's components (see {@link #buildView(ViewChangeBusMessage)}) to enable setting up
     * the view from URL parameters.
     *
     * @param busMessage
     */
    void afterBuild(AfterViewChangeBusMessage busMessage);

    /**
     * Notify the view that it needs to rebuild its components.  By default does nothing
     */
    @SuppressFBWarnings("ACEM_ABSTRACT_CLASS_EMPTY_METHODS")
    default void rebuild() {
    }
}
