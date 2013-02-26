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

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class I18NInterpreterTest {

	I18NInterpreter interpreter;

	I18NTestClass testObject;

	@Inject
	CurrentLocale currentLocale;

	@Inject
	Provider<I18NInterpreter> interpreterPro;

	@Before
	public void setup() {
		testObject = new I18NTestClass();
		// ensure switching to UK forces a change
		currentLocale.setLocale(Locale.CANADA_FRENCH);
		currentLocale.addListener(testObject);

	}

	@Test
	public void interpret() {

		// given
		// when
		currentLocale.setLocale(Locale.UK);
		// then
		assertThat(testObject.getButtonWithAnnotation().getCaption()).isEqualTo("small");
		assertThat(testObject.getButtonWithAnnotation().getDescription()).isEqualTo("use a small font");
		assertThat(testObject.getButtonWithAnnotation().getLocale()).isEqualTo(Locale.UK);

		assertThat(testObject.getLabel().getCaption()).isEqualTo("small");
		assertThat(testObject.getLabel().getDescription()).isEqualTo("use a small font");
		assertThat(testObject.getLabel().getValue()).isEqualTo("use a small font");
		assertThat(testObject.getLabel().getLocale()).isEqualTo(Locale.UK);

		assertThat(testObject.getTable().getCaption()).isEqualTo("small");
		assertThat(testObject.getTable().getDescription()).isEqualTo("use a small font");
		assertThat(testObject.getTable().getLocale()).isEqualTo(Locale.UK);

		Object[] columns = testObject.getTable().getVisibleColumns();
		assertThat(columns.length).isEqualTo(3);

		String[] headers = testObject.getTable().getColumnHeaders();
		assertThat(headers).isEqualTo(new String[] { "small", "cancel", "not i18N" });

		assertThat(testObject.getCcs().isLocaleChangeCalled()).isTrue();
		assertThat(testObject.getCcsn().isLocaleChangeCalled()).isTrue();
		assertThat(testObject.getCncn().isLocaleChangeCalled()).isTrue();
		assertThat(testObject.getCcsn().isLocaleChangeCalled()).isTrue();

	}

	@Test
	public void interpret_de() {

		String use_a_small_font = "Benutzen Sie eine kleine Schriftart";

		// given
		// when
		currentLocale.setLocale(Locale.GERMAN);
		// then
		assertThat(testObject.getButtonWithAnnotation().getCaption()).isEqualTo("klein");
		assertThat(testObject.getButtonWithAnnotation().getDescription()).isEqualTo(use_a_small_font);
		assertThat(testObject.getButtonWithAnnotation().getLocale()).isEqualTo(Locale.GERMAN);

		assertThat(testObject.getLabel().getCaption()).isEqualTo("klein");
		assertThat(testObject.getLabel().getDescription()).isEqualTo(use_a_small_font);
		assertThat(testObject.getLabel().getValue()).isEqualTo(use_a_small_font);
		assertThat(testObject.getButtonWithAnnotation().getLocale()).isEqualTo(Locale.GERMAN);

		assertThat(testObject.getTable().getCaption()).isEqualTo("klein");
		assertThat(testObject.getTable().getDescription()).isEqualTo(use_a_small_font);
		assertThat(testObject.getButtonWithAnnotation().getLocale()).isEqualTo(Locale.GERMAN);

		Object[] columns = testObject.getTable().getVisibleColumns();
		assertThat(columns.length).isEqualTo(3);

		String[] headers = testObject.getTable().getColumnHeaders();
		assertThat(headers).isEqualTo(new String[] { "klein", "stornieren", "not i18N" });

		assertThat(testObject.getCcs().isLocaleChangeCalled()).isTrue();
		assertThat(testObject.getCcsn().isLocaleChangeCalled()).isTrue();
		assertThat(testObject.getCncn().isLocaleChangeCalled()).isTrue();
		assertThat(testObject.getCcsn().isLocaleChangeCalled()).isTrue();

	}

	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(I18NInterpreter.class).to(AnnotationI18NInterpreter.class);
			}

		};
	}
}
