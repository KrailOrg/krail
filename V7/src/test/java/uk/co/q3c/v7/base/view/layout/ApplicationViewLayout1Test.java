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

import java.util.List;

import org.fest.assertions.Fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.i18n.Translate;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
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
			add(header);
			add(loginOut).width(100);
			add(menu).height(60);
			add(nav);
			add(breadcrumb);
			add(body);
			add(subpage);
			add(messageBar);
		}

		@Override
		protected void processParams(List<String> params) {

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
		view.getRootComponent();
		// then
		AbstractComponent c = view.getLayout().orderedComponents().get(0);
		assertThat(c).isEqualTo(logo);
		HorizontalLayout row0 = (HorizontalLayout) c.getParent();
		assertThat(row0.getComponentCount()).isEqualTo(3);
		assertThat(row0.getHeight()).isEqualTo(70);
		assertThat(row0.getHeightUnits()).isEqualTo(Unit.PIXELS);
		assertThat(row0.getWidth()).isEqualTo(100);
		assertThat(row0.getWidthUnits()).isEqualTo(Unit.PERCENTAGE);

		Fail.fail("test not complete");

	}
}
