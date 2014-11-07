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

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.vaadin.ui.GridLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.util.ID;

public abstract class GridViewBase extends ViewBase implements V7View {
    private static Logger log = LoggerFactory.getLogger(GridViewBase.class);
    private GridLayout gridLayout = new GridLayout();


    @Inject
    protected GridViewBase() {
        super();
    }

    public GridLayout getGridLayout() {
        return gridLayout;
    }

    /**
     * You only need to override / implement this method if you are using TestBench, or another testing tool which
     * looks
     * for debug
     * ids. If you do override it to add your own subclass ids, make sure you call super
     */
    @Override
    protected void setIds() {
        super.setIds();
        gridLayout.setId(ID.getId(Optional.absent(), gridLayout));
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
    public void buildView(V7ViewChangeEvent event) {
        setRootComponent(gridLayout);
    }
}
