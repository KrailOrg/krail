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

import com.vaadin.ui.Label;
import org.junit.Before;
import org.junit.Test;
import testutil.MockTranslate;
import uk.q3c.krail.core.i18n.DescriptionKey;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.i18n.Translate;
import uk.q3c.krail.core.view.component.ViewChangeBusMessage;

import static org.assertj.core.api.Assertions.assertThat;

public class ViewBaseTest {

    private TestView view;
    private TestView2 view2;
    private Translate translate = new MockTranslate();

    @Before
    public void setup() {
        view = new TestView(translate);
        view2 = new TestView2(translate);
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
        assertThat(view2.isComponentsConstructed()).isTrue();
    }

    @Test
    public void dirtyAndComponentsConstructed() throws Exception {
        //given

        //when
        view2.setDirty(true);
        view2.rebuild();

        //then
        assertThat(view2.isDirty()).isTrue();
        assertThat(view2.isComponentsConstructed()).isFalse();
    }

    @Test
    public void nameAndDescriptionDefaultsAndSetters() throws Exception {
        assertThat(view2.getName()).isEqualTo("Unnamed");
        assertThat(view2.getNameKey()).isEqualTo(LabelKey.Unnamed);
        assertThat(view2.getDescription()).isEqualTo("No description provided");
        assertThat(view2.getDescriptionKey()).isEqualTo(DescriptionKey.No_description_provided);

        //when
        view2.setNameKey(LabelKey.Yes);
        view2.setDescriptionKey(DescriptionKey.Description_of_the_source);

        //then
        assertThat(view2.getNameKey()).isEqualTo(LabelKey.Yes);
        assertThat(view2.getDescriptionKey()).isEqualTo(DescriptionKey.Description_of_the_source);
    }


    class TestView extends ViewBase {


        protected TestView(Translate translate) {
            super(translate);
        }

        @Override
        public void doBuild(ViewChangeBusMessage event) {

        }
    }

    class TestView2 extends ViewBase {


        protected TestView2(Translate translate) {
            super(translate);
        }

        @Override
        public void doBuild(ViewChangeBusMessage event) {
            setRootComponent(new Label("blank"));
        }
    }
}