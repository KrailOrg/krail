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

import com.google.inject.Inject;
import com.vaadin.ui.GridLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.ui.ScopedUI;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.Translate;

public abstract class GridViewBase extends ViewBase implements KrailView {
    private static Logger log = LoggerFactory.getLogger(GridViewBase.class);
    private GridLayout gridLayout = new GridLayout();


    @Inject
    protected GridViewBase(Translate translate) {
        super(translate);
    }

    public GridLayout getGridLayout() {
        return gridLayout;
    }


    /**
     * Builds the UI components of the view.  MUST set the root component of the View (returned by {@link
     * #getRootComponent()}, which is used to insert into the {@link ScopedUI} view area. The view implementation may
     * need to check whether components have already been constructed, as this method may be called when the View is
     * selected again after initial construction.
     *
     * @param event
     *         contains information about the change to this View
     */
    @Override
    public void buildView(ViewChangeBusMessage event) {
        super.buildView(event);
        setRootComponent(gridLayout);
        setIds();
    }
}