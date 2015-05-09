/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.view;

import com.vaadin.ui.Label;
import org.junit.Before;
import org.junit.Test;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

import static org.assertj.core.api.Assertions.assertThat;

public class ViewBaseTest {

    private TestView view;
    private TestView2 view2;

    @Before
    public void setup() {
        view = new TestView();
        view2 = new TestView2();
    }

    @Test(expected = ViewBuildException.class)
    public void getRootComponent() {
        //given

        //when
        view.getRootComponent();
        //then
    }

    @Test
    public void setIds() {

        //given

        //when
        view2.buildView(null);
        view2.afterBuild(null);
        //then
        assertThat(view2.getRootComponent()
                        .getId()).isNotNull();
    }

    @Test
    public void name() {
        //given

        //when

        //then
        assertThat(view.viewName()).isEqualTo("TestView");
    }

    class TestView extends ViewBase {




        @Override
        public void doBuild(ViewChangeBusMessage event) {

        }
    }

    class TestView2 extends ViewBase {



        @Override
        public void doBuild(ViewChangeBusMessage event) {
            setRootComponent(new Label("blank"));
        }
    }
}