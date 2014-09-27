/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.co.q3c.v7.base.view;

import com.google.inject.Inject;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.q3c.v7.base.ui.ScopedUI;

import java.util.List;

public abstract class VerticalViewBase extends VerticalLayout implements V7View {
    private static Logger log = LoggerFactory.getLogger(VerticalViewBase.class);

    @Inject
    protected VerticalViewBase() {
        super();
    }

    @Override
    public void enter(V7ViewChangeEvent event) {
        log.debug("entered view: " + this.getClass()
                                         .getSimpleName() + " with uri: " + event.getNavigationState());
        List<String> params = event.getNavigationState()
                                   .getParameterList();
        processParams(params);
    }

    protected abstract void processParams(List<String> params);

    /**
     * typecasts and returns getUI()
     *
     * @return
     */

    @Override
    public ScopedUI getUI() {
        return (ScopedUI) super.getUI();
    }

    @Override
    public Component getRootComponent() {
        return this;
    }

    /**
     * Called by the {@link ViewFactory} after the construction of a view.
     */
    @Override
    public void init() {

    }
}
