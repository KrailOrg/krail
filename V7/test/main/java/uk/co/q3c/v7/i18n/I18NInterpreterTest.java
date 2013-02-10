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
package uk.co.q3c.v7.i18n;

import static org.fest.assertions.Assertions.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class I18NInterpreterTest {

	I18NInterpreter interpreter;

	I18NTestClass testObject;

	@Before
	public void setup() {
		testObject = new I18NTestClass();

	}

	@Test
	public void interpret() {

		// given
		interpreter = new I18NInterpreter();
		interpreter.setLocale(Locale.UK);
		// when
		testObject.localeChange(interpreter);
		// then
		assertThat(testObject.getButtonWithAnnotation().getCaption()).isEqualTo("ok");
		assertThat(testObject.getButtonWithAnnotation().getDescription()).isEqualTo("confirm this value is ok");

	}
}
