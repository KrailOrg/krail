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

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class I18NModule extends AbstractModule {

	private Multibinder<String> registeredAnnotations;

	@Override
	protected void configure() {
		registeredAnnotations = newSetBinder(binder(), String.class, I18N.class);
		registerAnnotation(I18N.class);
		bindProcessor();
		define();
	}

	/**
	 * Override this to define more registered annotations, by calling {@link #registerAnnotation(Class)}, or make a
	 * copy of this module and use the same structure (multiple instances of {@link #registeredAnnotations} will be
	 * merged by Guice}
	 */
	protected void define() {

	}

	protected void bindProcessor() {
		bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
	}

	private <T extends Annotation> void registerAnnotation(Class<T> i18Nclass) {
		registeredAnnotations.addBinding().toInstance(i18Nclass.getName());
	}
}
