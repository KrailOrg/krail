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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.common.collect.Maps;

/**
 * Provides a singleton reference to the currently selected Locale. {@link I18NListener}s can be added to listen for
 * locale changes. This class also support the use of annotations to specify the {@link I18NKey} to be used for
 * translation. Annotations cannot be sub-classed, and in order to support the use of multiple annotations, they must be
 * registered with {@link CurrentLocale} so that the {@link I18NTranslator} implementation can check for their
 * existence. The {@link I18N} annotation is registered by default.
 * 
 * @see https://sites.google.com/site/q3cjava/internationalisation-i18n
 * @author David Sowerby 3 Mar 2013
 * 
 */
@Singleton
public class CurrentLocale {

	private Locale locale = Locale.UK;
	private final List<I18NListener> listeners = new ArrayList<>();
	private final Provider<I18NTranslator> translatorPro;
	private final Map<Class<? extends Annotation>, Provider<? extends I18NAnnotationReader>> readers = new HashMap<>();

	@Inject
	protected CurrentLocale(Provider<I18NTranslator> translatorPro, Provider<I18NReader> readerPro) {
		super();
		this.translatorPro = translatorPro;
		registerAnnotation(I18N.class, readerPro);
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		if (locale != this.locale) {
			this.locale = locale;
			fireListeners(locale);
		}
	}

	public void addListener(I18NListener listener) {
		listeners.add(listener);
	}

	public void removeListener(I18NListener listener) {
		listeners.remove(listener);
	}

	private void fireListeners(Locale locale) {
		for (I18NListener listener : listeners) {
			I18NTranslator translator = translatorPro.get();
			listener.localeChange(translator);
		}
	}

	public Map<Class<? extends Annotation>, Provider<? extends I18NAnnotationReader>> getI18NReaders() {
		return Maps.newHashMap(readers);
	}

	/**
	 * Returns the Annotations which the translator will look for. Register an Annotation using
	 * {@link #registerAnnotation(Annotation)}
	 * 
	 * @return
	 */
	public HashSet<Class<? extends Annotation>> registeredAnnotations() {
		Set<Class<? extends Annotation>> list = readers.keySet();
		return new HashSet<Class<? extends Annotation>>(list);
	}

	/**
	 * Register an Annotation which you want the translator to use, together with a Provider for its associated reader
	 * 
	 * @param annotation
	 */

	public void registerAnnotation(Class<? extends Annotation> annotationClass,
			Provider<? extends I18NAnnotationReader> readerProvider) {
		readers.put(annotationClass, readerProvider);
	}

	public Provider<? extends I18NAnnotationReader> readerForAnnotation(Class<? extends Annotation> annotationClass) {
		return readers.get(annotationClass);
	}

}
