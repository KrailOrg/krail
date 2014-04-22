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

import java.lang.annotation.Annotation;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

public class I18NModule extends AbstractModule {
	/**
	 * Maps an I18N annotation class name to its associated reader.
	 */
	MapBinder<String, I18NAnnotationReader> registeredAnnotations;

	@Override
	protected void configure() {
		registeredAnnotations = MapBinder.newMapBinder(binder(), String.class, I18NAnnotationReader.class);
		registerAnnotation(I18N.class, I18NReader.class);
		bindTranslator();
		define();
	}

	/**
	 * Override this to define more registered annotations.
	 */
	protected void define() {

	}

	protected void bindTranslator() {
		bind(I18NTranslator.class).to(AnnotationI18NTranslator.class);
	}

	private void registerAnnotation(Class<? extends Annotation> i18Nclass, Class<I18NReader> readerClass) {
		registeredAnnotations.addBinding(i18Nclass.getName()).to(readerClass);

	}
}
