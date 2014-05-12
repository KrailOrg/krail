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

import static com.google.inject.multibindings.Multibinder.newSetBinder;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class I18NModule extends AbstractModule {

	private Multibinder<String> registeredAnnotations;
	private Multibinder<String> registeredValueAnnotations;
	private Multibinder<Locale> supportedLocales;

	@Override
	protected void configure() {
		registeredAnnotations = newSetBinder(binder(), String.class, I18N.class);
		registeredValueAnnotations = newSetBinder(binder(), String.class, I18NValue.class);
		supportedLocales = newSetBinder(binder(), Locale.class, SupportedLocales.class);
		registerAnnotation(I18N.class);
		registerAnnotation(I18NFlex.class);
		registerValueAnnotation(I18NValue.class);
		registerValueAnnotation(I18NValueFlex.class);

		bindProcessor();
		bindCurrentLocale();

		define();
	}

	/**
	 * Override this method to provide your own implementation of {@link CurrentLocale}
	 */
	protected void bindCurrentLocale() {
		bind(CurrentLocale.class).to(DefaultCurrentLocale.class);
	}

	/**
	 * Override this to define more registered annotations, or registered value annotations, by calling
	 * {@link #registerAnnotation(Class)} or {@link #registerValueAnnotation(Class)}, or make a copy of this module and
	 * use the same structure (multiple instances of the {@link #registeredAnnotations} and
	 * {@link #registerValueAnnotation(Class)} will be merged by Guice}.
	 * <p>
	 * Here you should also defines the locales are supporting
	 */
	protected void define() {
		addSupportedLocale(Locale.UK);
	}

	/**
	 * Override this method to provide your own implementation of {@link I18NProcessor}
	 */
	protected void bindProcessor() {
		bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
	}

	private <T extends Annotation> void registerAnnotation(Class<T> i18Nclass) {
		registeredAnnotations.addBinding().toInstance(i18Nclass.getName());
	}

	private <T extends Annotation> void registerValueAnnotation(Class<T> i18Nclass) {
		registeredValueAnnotations.addBinding().toInstance(i18Nclass.getName());
	}

	protected void addSupportedLocale(String locale) {
		addSupportedLocale(Locale.forLanguageTag(locale));
	}

	protected void addSupportedLocale(Locale locale) {
		supportedLocales.addBinding().toInstance(locale);
	}

	protected void addSupportedLocale(List<String> locales) {
		for (String locale : locales) {
			addSupportedLocale(locale);
		}
	}

}
