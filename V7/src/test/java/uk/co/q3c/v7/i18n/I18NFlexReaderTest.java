/*
 * Copyright (C) 2014 David Sowerby
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

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ VaadinSessionScopeModule.class, I18NModule.class })
public class I18NFlexReaderTest {

	@I18NFlex(captionKeyName = "Yes", descriptionKeyName = "Confirm_Ok")
	public String flex1;

	@I18NFlex(captionKeyClass = TestLabelKey.class, captionKeyName = "MoneyInOut", descriptionKeyClass = TestLabelKey.class, descriptionKeyName = "View1")
	public String flex2;

	@I18NFlex(captionKeyClass = TestLabelKey.class, captionKeyName = "Money", descriptionKeyClass = TestLabelKey.class, descriptionKeyName = "View1")
	public String flex3;

	@I18NFlex(captionKeyName = "Yes", descriptionKeyName = "Confirm_Ok", locale = "en-GB")
	public String flex4;

	@I18NFlex
	public String flex5;

	@Inject
	I18NFlexReader reader;

	@Inject
	CurrentLocale currentLocale;

	@Test
	public void defaults() throws NoSuchFieldException, SecurityException {
		// given
		currentLocale.setLocale(Locale.CANADA_FRENCH);
		// when
		reader.setAnnotation(getAnnotation("flex1"));
		// then
		assertThat(reader.caption()).isEqualTo("Yes");
		assertThat(reader.description()).isEqualTo("Confirm this Value is Ok");
		assertThat(reader.locale()).isEqualTo(currentLocale.getLocale());
	}

	@Test
	public void localeSet() throws NoSuchFieldException, SecurityException {
		// given
		currentLocale.setLocale(Locale.CANADA_FRENCH);
		// when
		reader.setAnnotation(getAnnotation("flex4"));
		// then
		assertThat(reader.locale()).isEqualTo(Locale.UK);
	}

	@Test
	public void noValue() throws NoSuchFieldException, SecurityException {
		// given

		// when
		reader.setAnnotation(getAnnotation("flex5"));
		// then
		assertThat(reader.caption()).isEqualTo("");
		assertThat(reader.description()).isEqualTo("");
	}

	@Test
	public void differentClasses() throws NoSuchFieldException, SecurityException {
		// given

		// when
		reader.setAnnotation(getAnnotation("flex2"));
		// then
		assertThat(reader.caption()).isEqualTo("MoneyInOut");
		assertThat(reader.description()).isEqualTo("View1");
	}

	@Test(expected = I18NException.class)
	public void invalidCombination() throws NoSuchFieldException, SecurityException {
		// given

		// when
		reader.setAnnotation(getAnnotation("flex3"));
		// then
		assertThat(reader.caption()).isEqualTo("Money");
	}

	private I18NFlex getAnnotation(String fieldName) throws NoSuchFieldException, SecurityException {
		Class<? extends I18NFlexReaderTest> clazz = this.getClass();
		Field field = clazz.getField(fieldName);
		I18NFlex annotation = field.getAnnotation(I18NFlex.class);
		return annotation;
	}

}
