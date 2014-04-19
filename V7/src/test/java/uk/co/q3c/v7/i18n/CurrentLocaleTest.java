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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;
import java.util.Locale;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class CurrentLocaleTest implements I18NListener {

	boolean listenerFired = false;
	I18NTranslator translator = null;

	@Inject
	CurrentLocale currentLocale;

	@Mock
	Provider<I18NReader> readerPro;

	@Mock
	I18NReader reader;

	@Mock
	Annotation annotation;

	@Before
	public void setup() {
		listenerFired = false;
		translator = null;
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
		assertThat(translator).isNotNull();
		assertThat(translator.getLocale()).isEqualTo(Locale.ENGLISH);

	}

	@Test
	public void changeButNoChange() {

		// given
		currentLocale.setLocale(Locale.ENGLISH);
		listenerFired = false;
		translator = null;
		// when
		currentLocale.setLocale(Locale.ENGLISH);
		// then
		assertThat(listenerFired).isFalse();

	}

	@Test
	public void registerAnnotation() {

		// given
		when(readerPro.get()).thenReturn(reader);
		// when
		currentLocale.registerAnnotation(annotation.getClass(), readerPro);
		// then
		assertThat(currentLocale.readerForAnnotation(annotation.getClass())).isEqualTo(readerPro);
		assertThat(currentLocale.readerForAnnotation(Inject.class)).isNull();
		Provider<? extends I18NAnnotationReader> r = currentLocale.readerForAnnotation(I18N.class);
		assertThat(r).isNotNull();
		assertThat(r.get()).isInstanceOf(I18NReader.class);
		assertThat(currentLocale.getI18NReaders().size()).isEqualTo(2);
		assertThat(currentLocale.registeredAnnotations()).contains(I18N.class, annotation.getClass());

	}

	@Override
	public void localeChange(I18NTranslator translator) {
		listenerFired = true;
		this.translator = translator;
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
