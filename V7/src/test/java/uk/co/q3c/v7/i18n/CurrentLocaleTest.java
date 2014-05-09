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

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class CurrentLocaleTest implements LocaleChangeListener {

	boolean listenerFired = false;

	@Inject
	CurrentLocale currentLocale;

	@Mock
	Annotation annotation;

	@Before
	public void setup() {
		listenerFired = false;
		currentLocale.addListener(this);
	}

	@Test
	public void defaultLocale() {

		// given
		currentLocale.setLocale(Locale.UK);
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

	}

	@Test
	public void setLocaleNoFire() {
		// given
		listenerFired = false;
		// when
		currentLocale.setLocale(Locale.FRANCE, false);
		// then
		assertThat(listenerFired).isFalse();
	}

	@Test
	public void setLocaleFire() {
		// given
		listenerFired = false;
		currentLocale.setLocale(Locale.ENGLISH);
		// when
		currentLocale.setLocale(Locale.FRANCE, true);
		// then
		assertThat(listenerFired).isTrue();
	}

	@Test
	public void changeButNoChange() {

		// given
		currentLocale.setLocale(Locale.ENGLISH);
		listenerFired = false;
		// when
		currentLocale.setLocale(Locale.ENGLISH);
		// then
		assertThat(listenerFired).isFalse();

	}

	@Override
	public void localeChanged(Locale toLocale) {
		listenerFired = true;
	}

	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
			}

		};
	}

}
