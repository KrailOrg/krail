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

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class ViewTemplateVerticalStackTest {

	VerticalStackViewTemplate template;
	Label label1;
	Label label2;
	Label label3;

	@Before
	public void setup() {
		template = new VerticalStackViewTemplate();
		label1 = new Label("1");
		label2 = new Label("2");
		label3 = new Label("3");
	}

	@Test
	public void set() {
		// given

		// when

		template.set(1, label2);
		// then
		assertThat(template.getComponentCount()).isEqualTo(2);
		assertThat(template.getComponent(1)).isEqualTo(label2);
		assertThat(template.getComponent(0)).isInstanceOf(Panel.class);

		// when setting a replacement
		template.set(0, label3);
		// then
		assertThat(template.getComponentCount()).isEqualTo(2);
		assertThat(template.getComponent(0)).isEqualTo(label3);
		assertThat(template.getComponent(1)).isEqualTo(label2);

	}

}
