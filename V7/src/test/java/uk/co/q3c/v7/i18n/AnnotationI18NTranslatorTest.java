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
public class AnnotationI18NTranslatorTest {

	I18NTestClass testObject;

	@Inject
	CurrentLocale currentLocale;

	@Inject
	Provider<TestI18Nreader> testI18NreaderPro;

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
		assertThat(testObject.getButtonWithAnnotation().getCaption()).isEqualTo("Ok");
		assertThat(testObject.getButtonWithAnnotation().getDescription()).isEqualTo("Confirm this Value is Ok");
		assertThat(testObject.getButtonWithAnnotation().getLocale()).isEqualTo(Locale.UK);

		assertThat(testObject.getLabel().getCaption()).isEqualTo("Ok");
		assertThat(testObject.getLabel().getDescription()).isEqualTo("Confirm this Value is Ok");
		assertThat(testObject.getLabel().getValue()).isEqualTo("Confirm this Value is Ok");
		assertThat(testObject.getLabel().getLocale()).isEqualTo(Locale.UK);

		assertThat(testObject.getTable().getCaption()).isEqualTo("Ok");
		assertThat(testObject.getTable().getDescription()).isEqualTo("Confirm this Value is Ok");
		assertThat(testObject.getTable().getLocale()).isEqualTo(Locale.UK);

		Object[] columns = testObject.getTable().getVisibleColumns();
		assertThat(columns.length).isEqualTo(3);

		String[] headers = testObject.getTable().getColumnHeaders();
		assertThat(headers).isEqualTo(new String[] { "Small", "Cancel", "not i18N" });

		assertThat(testObject.getCcs().isLocaleChangeCalled()).isTrue();
		assertThat(testObject.getCcsn().isLocaleChangeCalled()).isTrue();
		assertThat(testObject.getCncn().isLocaleChangeCalled()).isTrue();
		assertThat(testObject.getCcsn().isLocaleChangeCalled()).isTrue();

	}

	@Test
	public void interpret_de() {

		String confirmValueOk = "Bestätigen, dass dieser Wert in Ordnung ist";

		// given
		// when
		currentLocale.setLocale(Locale.GERMAN);
		// then
		assertThat(testObject.getButtonWithAnnotation().getCaption()).isEqualTo("Ok");
		assertThat(testObject.getButtonWithAnnotation().getDescription()).isEqualTo(confirmValueOk);
		assertThat(testObject.getButtonWithAnnotation().getLocale()).isEqualTo(Locale.GERMAN);

		assertThat(testObject.getLabel().getCaption()).isEqualTo("Ok");
		assertThat(testObject.getLabel().getDescription()).isEqualTo(confirmValueOk);
		assertThat(testObject.getLabel().getValue()).isEqualTo(confirmValueOk);
		assertThat(testObject.getButtonWithAnnotation().getLocale()).isEqualTo(Locale.GERMAN);

		assertThat(testObject.getTable().getCaption()).isEqualTo("Ok");
		assertThat(testObject.getTable().getDescription()).isEqualTo(confirmValueOk);
		assertThat(testObject.getButtonWithAnnotation().getLocale()).isEqualTo(Locale.GERMAN);

		Object[] columns = testObject.getTable().getVisibleColumns();
		assertThat(columns.length).isEqualTo(3);

		String[] headers = testObject.getTable().getColumnHeaders();
		assertThat(headers).isEqualTo(new String[] { "Klein", "Stornieren", "not i18N" });

		assertThat(testObject.getCcs().isLocaleChangeCalled()).isTrue();
		assertThat(testObject.getCcsn().isLocaleChangeCalled()).isTrue();
		assertThat(testObject.getCncn().isLocaleChangeCalled()).isTrue();
		assertThat(testObject.getCcsn().isLocaleChangeCalled()).isTrue();

	}

	@Test
	public void multiAnnotations() {

		// given
		currentLocale.registerAnnotation(TestI18N.class, testI18NreaderPro);
		System.out.println(currentLocale.registeredAnnotations());
		// when
		currentLocale.setLocale(Locale.GERMAN);
		// then
		assertThat(testObject.getDemoLabel().getCaption()).isEqualTo("Ja");
		assertThat(testObject.getDemoLabel().getDescription()).isEqualTo("Ja");
		assertThat(testObject.getDemoLabel().getValue()).isEqualTo("Nein");
	}

	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(I18NTranslator.class).to(AnnotationI18NTranslator.class);

			}

		};
	}
}
