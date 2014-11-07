/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.base.view;

import com.vaadin.ui.Component;
import uk.q3c.krail.base.navigate.NavigationState;
import uk.q3c.krail.base.navigate.V7Navigator;


/**
 * A view is constructed by the {@link ViewFactory} from a Provider defined in the sitemap building process.  When
 * the view is selected for use, calls are made against {@link V7ViewChangeListener}s added to {@link V7Navigator}, and
 * this interface, in the following order:
 * <ol>
 * <li>{@link V7ViewChangeListener#beforeViewChange(V7ViewChangeEvent)}</li>
 * <li>{@link #init()}</li>
 * <li>{@link #beforeBuild}</li>
 * <li>{@link #buildView}</li>
 * <li>{@link #afterBuild}</li>
 * <li>{@link V7ViewChangeListener#afterViewChange(V7ViewChangeEvent)}</li>
 * </ol>
 * where build refers to the creation of UI fields and components which populate the view.  Each method, except
 * readFromEnvironment(),
 * is passed a
 * {@link V7ViewChangeEvent}, which contains the current {@link NavigationState} so that, for example, parameter
 * information can be used to determine how the View is to be built or respond in some other way to URL parameters.
 */
public interface V7View {

    /**
     * Called after the view itself has been constructed but before {@link #buildView()} is called.  Typically checks
     * whether a valid URI parameters are being passed to the view, or uses the URI parameters to set up some
     * configuration which affects the way the view is presented.
     *
     * @param event
     *         contains information about the change to this View
     */
    public void beforeBuild(V7ViewChangeEvent event);

    /**
     * Builds the UI components of the view.  MUST set the root component of the View (returned by {@link
     * #getRootComponent()}, which is used to insert into the {@link ScopedUI} view area. The view implementation may
     * need to check whether components have already been constructed, as this method may be called when the View is
     * selected again after initial construction.
     *
     * @param event
     *         contains information about the change to this View
     */
    public void buildView(V7ViewChangeEvent event);

    /**
     * To enable implementations to implement this interface without descending from Component. If the implementation
     * does descend from Component, just return 'this'.  Throws a ViewBuildException if the root component has not been
     * set
     *
     * @return
     */
    public Component getRootComponent();

    /**
     * A name for the view, typically displayed in a title bar
     *
     * @return
     */
    public String viewName();

    /**
     * Called by the {@link ViewFactory} after the construction of a view, and intended for initialisation which does
     * not depend on navigation state.
     */
    public void init();

    /**
     * Called immediately after the construction of the Views components (see {@link buildView}) to enable setting up
     * the view from URL parameters.  A typical use is to set ids for components if these are being used.
     *
     * @param navigationState
     */
    void afterBuild(V7ViewChangeEvent event);
}
