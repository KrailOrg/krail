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
package uk.co.q3c.v7.base.view.layout;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import javax.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.layout.ViewBaseWithLayout.ComponentWrapper;
import uk.co.q3c.v7.i18n.TestLabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class ViewBaseWithLayoutTest {

	private Button button1;
	private Button button2;
	private Label label1;
	private Label label2;

	@Mock
	Provider<ComponentWrapper> wrapperPro;

	@Mock
	Translate translate;

	class TestViewBaseWithLayout extends ViewBaseWithLayout {

		protected TestViewBaseWithLayout(V7Navigator navigator) {
			super(navigator, new VerticalViewLayout(), translate);
		}

		@Override
		protected void buildView() {
			button1 = new Button();
			button2 = new Button();
			label1 = new Label();
			label2 = new Label();

			vbwl.add(button1);
			vbwl.add(button2);
			vbwl.add(label1);
			vbwl.add(label2);

		}

		@Override
		protected void processParams(List<String> params) {
			System.out.println(params);
		}

	}

	ViewBaseWithLayout vbwl;

	@Mock
	V7Navigator navigator;

	@Before
	public void setup() {
		vbwl = new TestViewBaseWithLayout(navigator);
	}

	@Test
	public void addComponents() {

		// given

		// when force build and layout
		vbwl.getRootComponent();
		// then
		assertThat(vbwl.orderedComponents().size()).isEqualTo(4);
		assertThat(vbwl.orderedComponents().get(0)).isEqualTo(button1);
		assertThat(vbwl.orderedComponents().get(1)).isEqualTo(button2);
		assertThat(vbwl.orderedComponents().get(3)).isEqualTo(label2);

	}

	@Test
	public void addClass() {

		// given

		// when
		vbwl.add(Button.class).caption("wiggly").height(20).width(100).style("pretty");
		vbwl.add(Label.class).caption("label").heightPer(80).widthPer(60);
		vbwl.add(Panel.class).caption("panel").height("80pt").width("60em");
		// then
		assertThat(vbwl.orderedComponents().size()).isEqualTo(3);
		assertThat(vbwl.orderedComponents().get(0)).isInstanceOf(Button.class);
		assertThat(vbwl.orderedComponents().get(0).getCaption()).isEqualTo("wiggly");
		assertThat(vbwl.orderedComponents().get(0).getHeight()).isEqualTo(20);
		assertThat(vbwl.orderedComponents().get(0).getWidth()).isEqualTo(100);
		assertThat(vbwl.orderedComponents().get(0).getStyleName()).isEqualTo("pretty");

		assertThat(vbwl.orderedComponents().get(1)).isInstanceOf(Label.class);
		assertThat(vbwl.orderedComponents().get(1).getCaption()).isEqualTo("label");
		assertThat(vbwl.orderedComponents().get(1).getHeight()).isEqualTo(80);
		assertThat(vbwl.orderedComponents().get(1).getHeightUnits()).isEqualTo(Unit.PERCENTAGE);
		assertThat(vbwl.orderedComponents().get(1).getWidth()).isEqualTo(60);
		assertThat(vbwl.orderedComponents().get(1).getWidthUnits()).isEqualTo(Unit.PERCENTAGE);

		assertThat(vbwl.orderedComponents().get(2)).isInstanceOf(Panel.class);
		assertThat(vbwl.orderedComponents().get(2).getCaption()).isEqualTo("panel");
		assertThat(vbwl.orderedComponents().get(2).getHeight()).isEqualTo(80);
		assertThat(vbwl.orderedComponents().get(2).getHeightUnits()).isEqualTo(Unit.POINTS);
		assertThat(vbwl.orderedComponents().get(2).getWidth()).isEqualTo(60);
		assertThat(vbwl.orderedComponents().get(2).getWidthUnits()).isEqualTo(Unit.EM);

	}

	@Test
	public void addObject() {

		// given

		// when
		vbwl.add(new Button()).caption("wiggly");
		// then
		assertThat(vbwl.orderedComponents().get(0).getCaption()).isEqualTo("wiggly");

	}

	@Test
	public void style() {

		// given

		// when
		vbwl.add(Button.class).style("pretty1").style("pretty2");
		vbwl.add(Label.class).style("pretty1").style("pretty2").setStyle("pretty3");
		// then
		assertThat(vbwl.orderedComponents().get(0).getStyleName()).isEqualTo("pretty1 pretty2");
		assertThat(vbwl.orderedComponents().get(1).getStyleName()).isEqualTo("pretty3");

	}

	@Test
	public void i18n() {

		// given
		when(translate.from(TestLabelKey.No)).thenReturn("No");
		// when
		vbwl.add(Button.class).caption(TestLabelKey.No);
		// then
		assertThat(vbwl.orderedComponents().get(0).getCaption()).isEqualTo("No");

	}

	@Test
	public void id() {

		// given

		// when
		vbwl.add(Button.class).id("id");
		vbwl.add(new Button()).id("id");

		// then
		assertThat(vbwl.orderedComponents().get(0).getId()).isEqualTo("id");
		assertThat(vbwl.orderedComponents().get(1).getId()).isEqualTo("id");

	}

	@Test
	public void visible() {

		// given

		// when
		vbwl.add(Button.class).visible();
		vbwl.add(new Button()).notVisible();
		// then
		assertThat(vbwl.orderedComponents().get(0).isVisible()).isTrue();
		assertThat(vbwl.orderedComponents().get(1).isVisible()).isFalse();
	}

	@Test
	public void immediate() {

		// given

		// when
		vbwl.add(Button.class).immediate();
		vbwl.add(new Button()).notImmediate();
		// then
		assertThat(vbwl.orderedComponents().get(0).isImmediate()).isTrue();
		assertThat(vbwl.orderedComponents().get(1).isImmediate()).isFalse();
	}

	@Test
	public void enabled() {

		// given

		// when
		vbwl.add(Button.class).enabled();
		vbwl.add(new Button()).disabled();
		// then
		assertThat(vbwl.orderedComponents().get(0).isEnabled()).isTrue();
		assertThat(vbwl.orderedComponents().get(1).isEnabled()).isFalse();
	}

	@Test
	public void construct() {

		// given

		// when

		// then
		assertThat(vbwl.getConfig()).isNotNull();

	}

	@Test
	public void configToLayout() {

		// given
		DefaultViewConfig config = new DefaultViewConfig();
		// when config set
		vbwl.setConfig(config);
		// then
		assertThat(vbwl.getLayout().getConfig()).isEqualTo(config);

	}

}
