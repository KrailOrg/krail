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

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.v7.ui.Label;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;
import uk.q3c.krail.i18n.Translate;

import static org.assertj.core.api.Assertions.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class Grid3x3ViewBaseTest {

    Grid3x3ViewBase view;

    Label topLeft;
    Label topCentre;
    Label topRight;
    Label middleLeft;
    Label centreCell;
    Label middleRight;
    Label bottomLeft;
    Label bottomCentre;
    Label bottomRight;

    @Mock
    Translate translate;

    @Mock
    private ViewChangeBusMessage busMessage;

    @Before
    public void setup() {
        view = new Grid3x3ViewBase(translate);
        topLeft = new Label();
        topCentre = new Label();
        topRight = new Label();
        middleLeft = new Label();
        centreCell = new Label();
        middleRight = new Label();
        bottomLeft = new Label();
        bottomCentre = new Label();
        bottomRight = new Label();
    }

    @Test
    public void setCells() {
        //given
        view.doBuild(busMessage);
        //when
        view.setTopLeft(topLeft);
        view.setTopCentre(topCentre);
        view.setTopRight(topRight);

        view.setMiddleLeft(middleLeft);
        view.setCentreCell(centreCell);
        view.setMiddleRight(middleRight);

        view.setBottomLeft(bottomLeft);
        view.setBottomCentre(bottomCentre);
        view.setBottomRight(bottomRight);
        //then
        assertThat(view.getGridLayout()
                       .getComponent(0, 0)).isEqualTo(topLeft);
        assertThat(view.getGridLayout()
                       .getComponent(1, 0)).isEqualTo(topCentre);
        assertThat(view.getGridLayout()
                       .getComponent(2, 0)).isEqualTo(topRight);
        assertThat(view.getGridLayout()
                       .getComponent(0, 1)).isEqualTo(middleLeft);
        assertThat(view.getGridLayout()
                       .getComponent(1, 1)).isEqualTo(centreCell);
        assertThat(view.getGridLayout()
                       .getComponent(2, 1)).isEqualTo(middleRight);
        assertThat(view.getGridLayout()
                       .getComponent(0, 2)).isEqualTo(bottomLeft);
        assertThat(view.getGridLayout()
                       .getComponent(1, 2)).isEqualTo(bottomCentre);
        assertThat(view.getGridLayout()
                       .getComponent(2, 2)).isEqualTo(bottomRight);
    }


    @Test
    public void defaultSizes() {
        //given
        view.doBuild(busMessage);
        //when

        //then
        assertThat(view.getGridLayout()
                       .getColumnExpandRatio(0)).isEqualTo(view.getGridLayout()
                                                               .getColumnExpandRatio(1))
                                                .isEqualTo(view.getGridLayout()
                                                               .getColumnExpandRatio(2));
        assertThat(view.getGridLayout()
                       .getRowExpandRatio(0)).isEqualTo(view.getGridLayout()
                                                            .getRowExpandRatio(1))
                                             .isEqualTo(view.getGridLayout()
                                                            .getRowExpandRatio(2));
    }
}