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

package uk.co.q3c.v7.base.view;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import uk.co.q3c.v7.base.navigate.NavigationState;

public interface V7View {
    /**
     * This view is navigated to.
     * <p/>
     * This method is always called before the view is shown on screen. {@link ViewChangeEvent#getParameters()
     * event.getParameters()} may contain extra parameters relevant to the view.
     *
     * @param event
     *         ViewChangeEvent representing the view change that is occurring. {@link ViewChangeEvent#getNewView()
     *         event.getNewView()} returns <code>this</code>.
     */
    public void enter(V7ViewChangeEvent event);

    /**
     * To enable implementations to implement this interface without descending from Component. If the implementation
     * does descend from Component, just return 'this'
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
     * Called by the {@link ViewFactory} after the construction of a view.
     */
    public void init();

    /**
     * Called immediately after construction of the view to enable setting up the view from URL parameters
     *
     * @param navigationState
     */
    void prepareView(NavigationState navigationState);
}
