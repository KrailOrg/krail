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
package uk.co.q3c.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;

public class IDTest {

	Button button = new Button();
	Panel Panel = new Panel();

	@Test
	public void getIdComponentArray() {

		// given

		// when

		// then
		assertThat(ID.getId(Panel, button), is("Panel-Button"));

	}

	@Test
	public void getIdComponent() {

		// given

		// when

		// then
		assertThat(ID.getId(Panel), is("Panel"));

	}

	@Test
	public void getIdStringComponentArray() {

		// given

		// when

		// then
		assertThat(ID.getId("user", Panel, button), is("Panel-Button-user"));

	}
}
