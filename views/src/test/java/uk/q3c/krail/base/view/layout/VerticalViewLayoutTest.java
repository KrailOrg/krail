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
package uk.q3c.krail.base.view.layout;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore("https://github.com/davidsowerby/krail/issues/185")
public class VerticalViewLayoutTest {

    VerticalViewLayout vvl;

    Button button;
    Image image;
    Label label;
    Panel panel;

    @Before
    public void setup() {
        vvl = new VerticalViewLayout();
        // config is normally set by View implementation
        vvl.setConfig(vvl.defaultConfig());
        button = new Button();
        image = new Image();
        label = new Label();
        panel = new Panel();

    }

    /**
     * If the buildPslitters tests fails the some or all of the other tests will
     */
    @Test
    public void buildSplitters_1() {

        // given

        // when
        LinkedList<AbstractSplitPanel> q = vvl.buildSplitterQueue(1);
        // then
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp0");

        // when
    }

    @Test
    public void build_splitters_2() {

        // when
        LinkedList<AbstractSplitPanel> q = vvl.buildSplitterQueue(2);
        // then
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp1");
        q.pop();
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp0");
    }

    @Test
    public void build_splitters_3() {

        // given

        // when
        LinkedList<AbstractSplitPanel> q = vvl.buildSplitterQueue(3);
        // then
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp1");
        q.pop();
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp2");
    }

    @Test
    public void build_splitters_4() {

        // given

        // when
        LinkedList<AbstractSplitPanel> q = vvl.buildSplitterQueue(4);
        // then
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp3");
        q.pop();
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp1");
        q.pop();
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp2");
    }

    @Test
    public void build_splitters_5() {
        // when
        LinkedList<AbstractSplitPanel> q = vvl.buildSplitterQueue(5);
        // then
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp3");
        q.pop();
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp4");
        q.pop();
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp2");
    }

    @Test
    public void build_splitters_6() { // when
        LinkedList<AbstractSplitPanel> q = vvl.buildSplitterQueue(6);
        // then
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp3");
        q.pop();
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp4");
        q.pop();
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp5");
        q.pop();
        assertThat(q.getFirst()
                    .getId()).isEqualTo("vsp2");

    }

    @Test
    public void buildPop_1() {

        // given
        vvl.addComponent(button);
        vvl.addComponent(image);
        vvl.addComponent(label);
        vvl.addComponent(panel);
        ViewConfig config = vvl.getConfig();
        config.addSplit(0, 1);
        vvl.setConfig(config);
        vvl.validateSplits();
        // when
        LinkedList<Integer> result = vvl.buildPopulations(config);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(1);
        assertThat(result.get(1)).isEqualTo(3);

    }

    @Test
    public void buildPop_2() {

        // given
        vvl.addComponent(button);
        vvl.addComponent(image);
        vvl.addComponent(label);
        vvl.addComponent(panel);
        ViewConfig config = vvl.getConfig();
        config.addSplit(1, 2);
        vvl.setConfig(config);
        vvl.validateSplits();
        // when
        LinkedList<Integer> result = vvl.buildPopulations(config);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(2);
        assertThat(result.get(1)).isEqualTo(2);

    }

    @Test
    public void buildPop_3() {

        // given
        vvl.addComponent(button);
        vvl.addComponent(image);
        vvl.addComponent(label);
        vvl.addComponent(panel);
        ViewConfig config = vvl.getConfig();
        config.addSplit(2, 3);
        vvl.setConfig(config);
        vvl.validateSplits();
        // when
        LinkedList<Integer> result = vvl.buildPopulations(config);

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(3);
        assertThat(result.get(1)).isEqualTo(1);
    }

    @Test
    public void buildPop_1_2() {

        // given
        vvl.addComponent(button);
        vvl.addComponent(image);
        vvl.addComponent(label);
        vvl.addComponent(panel);
        ViewConfig config = vvl.getConfig();
        config.addSplit(0, 1);
        config.addSplit(1, 2);
        vvl.setConfig(config);
        vvl.validateSplits();

        // when
        LinkedList<Integer> result = vvl.buildPopulations(config);

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0)).isEqualTo(1);
        assertThat(result.get(1)).isEqualTo(1);
        assertThat(result.get(2)).isEqualTo(2);
    }

    @Test
    public void buildPop_1_2_3() {

        // given
        vvl.addComponent(button);
        vvl.addComponent(image);
        vvl.addComponent(label);
        vvl.addComponent(panel);
        ViewConfig config = vvl.getConfig();
        config.addSplit(0, 1);
        config.addSplit(1, 2);
        config.addSplit(2, 3);
        vvl.setConfig(config);
        vvl.validateSplits();
        // when
        LinkedList<Integer> result = vvl.buildPopulations(config);

        // then
        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0)).isEqualTo(1);
        assertThat(result.get(1)).isEqualTo(1);
        assertThat(result.get(2)).isEqualTo(1);
        assertThat(result.get(3)).isEqualTo(1);
    }

    @Test
    public void assemble_no_splits() {

        // given

        vvl.addComponent(button);
        vvl.addComponent(image);
        vvl.addComponent(label);
        vvl.addComponent(panel);
        // when
        vvl.assemble();
        // then
        assertThat(vvl.layoutRoot).isInstanceOf(VerticalLayout.class);
        VerticalLayout vl = (VerticalLayout) vvl.layoutRoot;
        assertThat(vl.getComponentCount()).isEqualTo(4);
        assertThat(vl.getComponentIndex(button)).isEqualTo(0);
        assertThat(vl.getComponentIndex(image)).isEqualTo(1);
        assertThat(vl.getComponentIndex(label)).isEqualTo(2);
        assertThat(vl.getComponentIndex(panel)).isEqualTo(3);

    }

    @Test
    public void assemble_split_0() {

        // given
        vvl.addComponent(button);
        vvl.addComponent(image);
        vvl.addComponent(label);
        vvl.addComponent(panel);
        ViewConfig config = vvl.getConfig();
        config.addSplit(0, 1);
        // when
        vvl.assemble();
        // then
        assertThat(vvl.layoutRoot).isInstanceOf(VerticalSplitPanel.class);
        VerticalSplitPanel vsp = (VerticalSplitPanel) vvl.layoutRoot;
        assertThat(vsp.getFirstComponent()).isEqualTo(button);
        assertThat(vsp.getSecondComponent()).isInstanceOf(VerticalLayout.class);
        VerticalLayout vl = (VerticalLayout) vsp.getSecondComponent();
        assertThat(vl.getComponentCount()).isEqualTo(3);
        assertThat(vl.getComponentIndex(image)).isEqualTo(0);
        assertThat(vl.getComponentIndex(label)).isEqualTo(1);
        assertThat(vl.getComponentIndex(panel)).isEqualTo(2);

    }

    @Test
    public void assemble_split_1() {

        // given
        vvl.addComponent(button);
        vvl.addComponent(image);
        vvl.addComponent(label);
        vvl.addComponent(panel);
        ViewConfig config = vvl.getConfig();
        config.addSplit(1, 2);

        // when
        vvl.assemble();
        // then
        assertThat(vvl.layoutRoot).isInstanceOf(VerticalSplitPanel.class);
        VerticalSplitPanel vsp = (VerticalSplitPanel) vvl.layoutRoot;
        assertThat(vsp.getFirstComponent()).isInstanceOf(VerticalLayout.class);
        VerticalLayout vl = (VerticalLayout) vsp.getFirstComponent();
        assertThat(vl.getComponentCount()).isEqualTo(2);
        assertThat(vl.getComponent(0)).isEqualTo(button);
        assertThat(vl.getComponent(1)).isEqualTo(image);

        VerticalLayout vl1 = (VerticalLayout) vsp.getSecondComponent();
        assertThat(vl1.getComponentCount()).isEqualTo(2);
        assertThat(vl1.getComponent(0)).isEqualTo(label);
        assertThat(vl1.getComponent(1)).isEqualTo(panel);

    }

    @Test
    public void assemble_split_2() {

        // given
        vvl.addComponent(button);
        vvl.addComponent(image);
        vvl.addComponent(label);
        vvl.addComponent(panel);
        ViewConfig config = vvl.getConfig();
        config.addSplit(2, 3);

        // when
        vvl.assemble();
        // then
        assertThat(vvl.layoutRoot).isInstanceOf(VerticalSplitPanel.class);
        VerticalSplitPanel vsp = (VerticalSplitPanel) vvl.layoutRoot;
        assertThat(vsp.getFirstComponent()).isInstanceOf(VerticalLayout.class);
        VerticalLayout vl = (VerticalLayout) vsp.getFirstComponent();
        assertThat(vl.getComponentCount()).isEqualTo(3);
        assertThat(vl.getComponent(0)).isEqualTo(button);
        assertThat(vl.getComponent(1)).isEqualTo(image);
        assertThat(vl.getComponent(2)).isEqualTo(label);

        assertThat(vsp.getSecondComponent()).isEqualTo(panel);
    }

    @Test
    public void assemble_split_0_and_1() {

        // given
        vvl.addComponent(button);
        vvl.addComponent(image);
        vvl.addComponent(label);
        vvl.addComponent(panel);
        ViewConfig config = vvl.getConfig();
        config.addSplit(0, 1);
        config.addSplit(1, 2);

        // when
        vvl.assemble();
        // then
        assertThat(vvl.layoutRoot).isInstanceOf(VerticalSplitPanel.class);
        VerticalSplitPanel vsp0 = (VerticalSplitPanel) vvl.layoutRoot;

        assertThat(vsp0.getFirstComponent()).isInstanceOf(VerticalSplitPanel.class);
        assertThat(vsp0.getSecondComponent()).isInstanceOf(VerticalLayout.class);

        VerticalSplitPanel vsp1 = (VerticalSplitPanel) vsp0.getFirstComponent();
        VerticalLayout vl1 = (VerticalLayout) vsp0.getSecondComponent();

        assertThat(vsp1.getFirstComponent()).isEqualTo(button);
        assertThat(vsp1.getSecondComponent()).isEqualTo(image);

        assertThat(vl1.getComponent(0)).isEqualTo(label);
        assertThat(vl1.getComponent(1)).isEqualTo(panel);

    }

    @Test
    public void tolerateInvalidSplit() {

        // given
        vvl.addComponent(button);
        vvl.addComponent(image);
        vvl.addComponent(label);
        vvl.addComponent(panel);
        ViewConfig config = vvl.getConfig();
        config.addSplit(0, 1);
        config.addSplit(1, 3);
        config.addSplit(1, 2);

        // when
        vvl.assemble();
        // then
        assertThat(vvl.layoutRoot).isInstanceOf(VerticalSplitPanel.class);
        VerticalSplitPanel vsp0 = (VerticalSplitPanel) vvl.layoutRoot;

        assertThat(vsp0.getFirstComponent()).isInstanceOf(VerticalSplitPanel.class);
        assertThat(vsp0.getSecondComponent()).isInstanceOf(VerticalLayout.class);

        VerticalSplitPanel vsp1 = (VerticalSplitPanel) vsp0.getFirstComponent();
        VerticalLayout vl1 = (VerticalLayout) vsp0.getSecondComponent();

        assertThat(vsp1.getFirstComponent()).isEqualTo(button);
        assertThat(vsp1.getSecondComponent()).isEqualTo(image);

        assertThat(vl1.getComponent(0)).isEqualTo(label);
        assertThat(vl1.getComponent(1)).isEqualTo(panel);

    }

    @Test
    public void defaultConfig() {

        // given
        ViewConfig config = vvl.defaultConfig();
        // when

        // then
        assertThat(config.isWidthEnabled()).isTrue();
        assertThat(config.getWidthUnit()).isEqualTo(Unit.PERCENTAGE);
        assertThat(config.getWidth()).isEqualTo(100);
        assertThat(config.isHeightEnabled()).isFalse();
        assertThat(config.splitCount()).isEqualTo(0);

    }
}
