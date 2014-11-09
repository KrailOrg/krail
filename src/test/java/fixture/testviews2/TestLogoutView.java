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
package fixture.testviews2;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import uk.q3c.krail.core.view.KrailViewChangeEvent;
import uk.q3c.krail.core.view.LogoutView;

public class TestLogoutView implements LogoutView {


    /**
     * Called after the view itself has been constructed but before {@link #buildView()} is called.  Typically checks
     * whether a valid URI parameters are being passed to the view, or uses the URI parameters to set up some
     * configuration which affects the way the view is presented.
     *
     * @param event
     *         contains information about the change to this View
     */
    @Override
    public void beforeBuild(KrailViewChangeEvent event) {

    }

    /**
     * Builds the UI components of the view.  The view implementation may need to check whether components have already
     * been constructed, as this method may be called when the View is selected again after initial construction.
     *
     * @param event
     *
     * @return the root component of the View, which is used to insert into the {@link ScopedUI} view area.
     */
    @Override
    public void buildView(KrailViewChangeEvent event) {
    }

    @Override
    public Component getRootComponent() {
        return new Label("not used");
    }

    @Override
    public String viewName() {

        return getClass().getSimpleName();
    }

    @Override
    public void init() {
    }

    /**
     * Called immediately after the construction of the Views components (see {@link buildView}) to enable setting up
     * the view from URL parameters
     *
     * @param event
     */
    @Override
    public void afterBuild(KrailViewChangeEvent event) {

    }


}
