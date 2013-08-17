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
package uk.co.q3c.v7.base.view.template;

import static org.fest.assertions.Assertions.*;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class VerticalViewLayoutTest {

	VerticalViewLayout vvl;

	Button button;
	Image image;
	Label label;
	Panel panel;

	@Before
	public void setup() {
		vvl = new VerticalViewLayout();
		button = new Button();
		image = new Image();
		label = new Label();
		panel = new Panel();

	}

	@Test
	public void assemble_no_splits() {

		// given

		vvl.addComponent(button);
		vvl.addComponent(image);
		vvl.addComponent(label);
		vvl.addComponent(panel);
		// when
		vvl.assemble(vvl.defaultConfig());
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
		ViewConfig config = vvl.defaultConfig();
		config.addSplit(0, 1);

		// when
		vvl.assemble(config);
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
		ViewConfig config = vvl.defaultConfig();
		config.addSplit(1, 2);

		// when
		vvl.assemble(config);
		// then
		assertThat(vvl.layoutRoot).isInstanceOf(VerticalLayout.class);
		VerticalLayout vl0 = (VerticalLayout) vvl.layoutRoot;
		assertThat(vl0.getComponentIndex(button)).isEqualTo(0);
		assertThat(vl0.getComponent(1)).isInstanceOf(VerticalSplitPanel.class);
		VerticalSplitPanel vsp = (VerticalSplitPanel) vl0.getComponent(1);
		assertThat(vsp.getFirstComponent()).isInstanceOf(Image.class);
		assertThat(vsp.getSecondComponent()).isInstanceOf(VerticalLayout.class);
		VerticalLayout vl1 = (VerticalLayout) vsp.getSecondComponent();
		assertThat(vl1.getComponentCount()).isEqualTo(2);
		assertThat(vl1.getComponentIndex(label)).isEqualTo(0);
		assertThat(vl1.getComponentIndex(panel)).isEqualTo(1);

	}

	@Test
	public void assemble_split_2() {

		// given
		vvl.addComponent(button);
		vvl.addComponent(image);
		vvl.addComponent(label);
		vvl.addComponent(panel);
		ViewConfig config = vvl.defaultConfig();
		config.addSplit(2, 3);

		// when
		vvl.assemble(config);
		// then
		assertThat(vvl.layoutRoot).isInstanceOf(VerticalLayout.class);
		VerticalLayout vl0 = (VerticalLayout) vvl.layoutRoot;
		assertThat(vl0.getComponent(0)).isEqualTo(button);
		assertThat(vl0.getComponent(1)).isEqualTo(image);
		assertThat(vl0.getComponent(2)).isInstanceOf(VerticalSplitPanel.class);

		VerticalSplitPanel vsp = (VerticalSplitPanel) vl0.getComponent(2);
		assertThat(vsp.getFirstComponent()).isInstanceOf(Label.class);
		assertThat(vsp.getSecondComponent()).isInstanceOf(Panel.class);

	}

	@Test
	public void assemble_split_0_and_1() {

		// given
		vvl.addComponent(button);
		vvl.addComponent(image);
		vvl.addComponent(label);
		vvl.addComponent(panel);
		ViewConfig config = vvl.defaultConfig();
		config.addSplit(0, 1);
		config.addSplit(1, 2);

		// when
		vvl.assemble(config);
		// then
		assertThat(vvl.layoutRoot).isInstanceOf(VerticalSplitPanel.class);
		VerticalSplitPanel vsp1 = (VerticalSplitPanel) vvl.layoutRoot;
		assertThat(vsp1.getFirstComponent()).isEqualTo(button);
		assertThat(vsp1.getSecondComponent()).isInstanceOf(VerticalSplitPanel.class);
		VerticalSplitPanel vsp2 = (VerticalSplitPanel) vsp1.getSecondComponent();
		assertThat(vsp2.getFirstComponent()).isEqualTo(image);
		assertThat(vsp2.getSecondComponent()).isInstanceOf(VerticalLayout.class);
		VerticalLayout vl = ((VerticalLayout) vsp2.getSecondComponent());
		assertThat(vl.getComponent(0)).isEqualTo(label);
		assertThat(vl.getComponent(1)).isEqualTo(panel);
	}
}
