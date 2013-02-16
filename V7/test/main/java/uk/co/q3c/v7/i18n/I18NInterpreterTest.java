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

import javax.inject.Inject;
import javax.inject.Provider;

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

	@Inject
	CurrentLocale currentLocale;

	@Inject
	Provider<AnnotationI18NInterpreter> interpreterPro;

	@Before
	public void setup() {
		currentLocale = new CurrentLocale(interpreterPro);
		currentLocale.setLocale(Locale.UK);
		testObject = new I18NTestClass();

	}

	@Test
	public void interpret() {

		// given
		interpreter = interpreterPro.get();
		// when
		testObject.localeChange(interpreter);
		// then
		assertThat(testObject.getButtonWithAnnotation().getCaption()).isEqualTo("ok");
		assertThat(testObject.getButtonWithAnnotation().getDescription()).isEqualTo("confirm this value is ok");
		assertThat(testObject.getButtonWithAnnotation().getLocale()).isEqualTo(Locale.UK);

		assertThat(testObject.getLabel().getCaption()).isEqualTo("ok");
		assertThat(testObject.getLabel().getDescription()).isEqualTo("confirm this value is ok");
		assertThat(testObject.getLabel().getValue()).isEqualTo("confirm this value is ok");
		assertThat(testObject.getLabel().getLocale()).isEqualTo(Locale.UK);

		assertThat(testObject.getTable().getCaption()).isEqualTo("ok");
		assertThat(testObject.getTable().getDescription()).isEqualTo("confirm this value is ok");
		assertThat(testObject.getTable().getLocale()).isEqualTo(Locale.UK);

		// table should have caption and description (no value)
		// but how to handle columns?

	}
}
