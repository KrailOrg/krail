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

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.i18n.Translate;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.Sizeable;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
@Ignore("https://github.com/davidsowerby/v7/issues/185")
public class ApplicationViewLayout1Test {
	@Mock
	V7Navigator navigator;
	@Mock
	Translate translate;
	Panel logo;
	Panel loginOut;
	Panel header;
	Panel breadcrumb;
	Panel menu;
	Panel nav;
	Panel messageBar;
	Panel body;
	Panel subpage;
	ApplicationViewLayout1 viewLayout;
	TestView view;

	class TestView extends ViewBaseWithLayout {

		protected TestView(V7Navigator navigator, ViewLayout viewLayout, Translate translate) {
			super(navigator, viewLayout, translate);
		}

		@Override
		protected void buildView() {
			add(logo).width(50).height(70);
			add(header).widthUndefined().heightPercent(100);
			add(loginOut).width(100).heightPercent(100);
			add(menu).height(60);
			add(nav);
			add(breadcrumb).height(45);
			add(body).heightPercent(100);
			add(subpage).height(55);
			add(messageBar).height(80);
		}

		@Override
		protected void processParams(List<String> params) {

		}

		@Override
		public String viewName() {

			return "TestView";
		}

	}

	@Before
	public void setup() {
		logo = new Panel("logo");
		header = new Panel("header");
		breadcrumb = new Panel("breadcrumb");
		menu = new Panel("menu");
		nav = new Panel("nav");
		messageBar = new Panel("messageBar");
		body = new Panel("body");
		subpage = new Panel("subpage");
		viewLayout = new ApplicationViewLayout1();
		view = new TestView(navigator, viewLayout, translate);
		loginOut = new Panel("loginOut");
	}

	@Test
	public void positions() {

		// given

		// when
		Component r = view.getRootComponent();
		// then
		assertThat(r).isInstanceOf(VerticalLayout.class);
		VerticalLayout root = (VerticalLayout) r;

		Component c = view.getLayout().orderedComponents().get(0);
		HorizontalLayout row0 = (HorizontalLayout) c.getParent();
		assertThat(row0.getComponentCount()).isEqualTo(3);
		assertThat(row0.getWidth()).isEqualTo(100f);
		assertThat(row0.getWidthUnits()).isEqualTo(Unit.PERCENTAGE);
		assertThat(row0.getHeight()).isEqualTo(Sizeable.SIZE_UNDEFINED);
		assertThat(row0.getHeightUnits()).isEqualTo(Unit.PIXELS);

		assertThat(c).isEqualTo(logo);
		assertThat(c.getCaption()).isEqualTo("logo");
		assertThat(c.getWidth()).isEqualTo(50f);
		assertThat(c.getWidthUnits()).isEqualTo(Unit.PIXELS);
		assertThat(c.getHeight()).isEqualTo(70f);
		assertThat(c.getHeightUnits()).isEqualTo(Unit.PIXELS);

		c = row0.getComponent(1);
		assertThat(c).isEqualTo(header);
		assertThat(header.getWidth()).isEqualTo(Sizeable.SIZE_UNDEFINED);
		assertThat(header.getWidthUnits()).isEqualTo(Unit.PIXELS);
		assertThat(header.getHeight()).isEqualTo(100f);
		assertThat(header.getHeightUnits()).isEqualTo(Unit.PERCENTAGE);

		c = row0.getComponent(2);
		assertThat(c).isEqualTo(loginOut);
		assertThat(loginOut.getWidth()).isEqualTo(100f);
		assertThat(loginOut.getWidthUnits()).isEqualTo(Unit.PIXELS);
		assertThat(loginOut.getHeight()).isEqualTo(100f);
		assertThat(loginOut.getHeightUnits()).isEqualTo(Unit.PERCENTAGE);

		c = root.getComponent(1);
		assertThat(c).isEqualTo(menu);
		assertThat(menu.getWidth()).isEqualTo(100f);
		assertThat(menu.getWidthUnits()).isEqualTo(Unit.PERCENTAGE);
		assertThat(menu.getHeight()).isEqualTo(60f);
		assertThat(menu.getHeightUnits()).isEqualTo(Unit.PIXELS);

		c = root.getComponent(2);
		assertThat(c).isInstanceOf(HorizontalSplitPanel.class);
		HorizontalSplitPanel hsp = (HorizontalSplitPanel) c;

		c = hsp.getFirstComponent();
		assertThat(c).isEqualTo(nav);
		assertThat(nav.getWidth()).isEqualTo(100f);
		assertThat(nav.getWidthUnits()).isEqualTo(Unit.PERCENTAGE);
		assertThat(nav.getHeight()).isEqualTo(100f);
		assertThat(nav.getHeightUnits()).isEqualTo(Unit.PERCENTAGE);

		c = hsp.getSecondComponent();
		assertThat(c).isInstanceOf(VerticalLayout.class);

		VerticalLayout vl = (VerticalLayout) c;
		c = vl.getComponent(0);
		assertThat(c).isEqualTo(breadcrumb);
		assertThat(breadcrumb.getWidth()).isEqualTo(100f);
		assertThat(breadcrumb.getWidthUnits()).isEqualTo(Unit.PERCENTAGE);
		assertThat(breadcrumb.getHeight()).isEqualTo(45f);
		assertThat(breadcrumb.getHeightUnits()).isEqualTo(Unit.PIXELS);

		c = vl.getComponent(1);
		assertThat(c).isEqualTo(body);
		assertThat(body.getWidth()).isEqualTo(100f);
		assertThat(body.getWidthUnits()).isEqualTo(Unit.PERCENTAGE);
		assertThat(body.getHeight()).isEqualTo(100f);
		assertThat(body.getHeightUnits()).isEqualTo(Unit.PERCENTAGE);

		c = vl.getComponent(2);
		assertThat(c).isEqualTo(subpage);
		assertThat(subpage.getWidth()).isEqualTo(100f);
		assertThat(subpage.getWidthUnits()).isEqualTo(Unit.PERCENTAGE);
		assertThat(subpage.getHeight()).isEqualTo(55f);
		assertThat(subpage.getHeightUnits()).isEqualTo(Unit.PIXELS);

		c = root.getComponent(3);
		assertThat(c).isEqualTo(messageBar);

		assertThat(messageBar.getWidth()).isEqualTo(100f);
		assertThat(messageBar.getWidthUnits()).isEqualTo(Unit.PERCENTAGE);
		assertThat(messageBar.getHeight()).isEqualTo(80f);
		assertThat(messageBar.getHeightUnits()).isEqualTo(Unit.PIXELS);

		// ordered the same way as diagram
		Object[] contents = new Object[] { logo, header, loginOut, menu, nav, breadcrumb, body, subpage, messageBar };
		for (int i = 0; i < contents.length; i++) {
			assertThat(view.getLayout().orderedComponents().get(i)).isEqualTo(contents[i]);
		}

	}
}
