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
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.Translate;

import static com.google.common.base.Preconditions.*;

public class Grid3x3ViewBase extends ViewBase {
    private static final float[] defaultColumnWidths = new float[]{1f, 1f, 1f};
    private static final float[] defaultRowHeights = new float[]{1f, 1f, 1f};
    private GridLayout grid;

    @Inject
    protected Grid3x3ViewBase(Translate translate) {
        super(translate);
    }

    @Override
    protected void doBuild(ViewChangeBusMessage busMessage) {

        grid = new GridLayout(3, 3);
        grid.setSizeFull();
        setColumnWidths(defaultColumnWidths);
        setRowHeights(defaultRowHeights);


        setRootComponent(grid);

    }

    /**
     * Sets the relative widths of the grid columns
     *
     * @param relativeWidths
     *         3 column widths to use
     *
     * @throws IllegalArgumentException
     *         if {@code relativeWidths}  does not have exactly 3 columns
     */
    protected void setColumnWidths(float... relativeWidths) {
        checkArgument(relativeWidths.length == 3);
        grid.setColumnExpandRatio(0, relativeWidths[0]);
        grid.setColumnExpandRatio(1, relativeWidths[1]);
        grid.setColumnExpandRatio(2, relativeWidths[2]);
    }

    /**
     * Sets the relative width of the grid rows
     *
     * @param relativeHeights
     *         3 row heightsuse
     *
     * @throws IllegalArgumentException
     *         if {@code relativeHeights}  does not have exactly 3 rows
     */
    protected void setRowHeights(float... relativeHeights) {
        checkArgument(relativeHeights.length == 3);
        grid.setRowExpandRatio(0, relativeHeights[0]);
        grid.setRowExpandRatio(1, relativeHeights[1]);
        grid.setRowExpandRatio(2, relativeHeights[2]);
    }

    public void setTopLeft(Component component) {
        grid.addComponent(component, 0, 0);
    }

    public void setTopCentre(Component component) {
        grid.addComponent(component, 1, 0);
    }

    public void setTopRight(Component component) {
        grid.addComponent(component, 2, 0);
    }

    public void setMiddleLeft(Component component) {
        grid.addComponent(component, 0, 1);
    }

    public void setMiddleCentre(Component component) {
        grid.addComponent(component, 1, 1);
    }

    public void setMiddleRight(Component component) {
        grid.addComponent(component, 2, 1);
    }

    public void setCentreCell(Component component) {
        grid.addComponent(component, 1, 1);
    }

    public void setBottomLeft(Component component) {
        grid.addComponent(component, 0, 2);
    }

    public void setBottomCentre(Component component) {
        grid.addComponent(component, 1, 2);
    }

    public void setBottomRight(Component component) {
        grid.addComponent(component, 2, 2);
    }

    protected GridLayout getGridLayout() {
        return grid;
    }

}
