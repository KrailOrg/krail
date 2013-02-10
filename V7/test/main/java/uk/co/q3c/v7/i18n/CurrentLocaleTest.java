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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class CurrentLocaleTest implements I18NListener {

	boolean listenerFired = false;
	I18NInterpreter interpreter = null;

	@Inject
	CurrentLocale currentLocale;

	@Before
	public void setup() {
		listenerFired = false;
		interpreter = null;
		currentLocale.addListener(this);
	}

	@Test
	public void defaultLocale() {

		// given

		// when
		Locale locale = currentLocale.getLocale();
		// then
		assertThat(locale).isNotNull();
		assertThat(locale).isEqualTo(Locale.UK);

	}

	@Test
	public void changeLocaleValid() {

		// given

		// when
		currentLocale.setLocale(Locale.ENGLISH);
		// then
		assertThat(listenerFired).isTrue();
		assertThat(interpreter).isNotNull();
		assertThat(interpreter.getLocale()).isEqualTo(Locale.ENGLISH);

	}

	@Test
	public void changeButNoChange() {

		// given
		currentLocale.setLocale(Locale.ENGLISH);
		listenerFired = false;
		interpreter = null;
		// when
		currentLocale.setLocale(Locale.ENGLISH);
		// then
		assertThat(listenerFired).isFalse();

	}

	@Override
	public void localeChange(I18NInterpreter interpreter) {
		listenerFired = true;
		this.interpreter = interpreter;
	}

}
