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
import com.vaadin.ui.GridLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.q3c.util.ID;

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
        gridLayout.setId(ID.getId(gridLayout));
    }
}
