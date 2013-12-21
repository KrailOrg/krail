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
package uk.co.q3c.v7.base.navigate.sitemap;

import java.text.Collator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.reflections.Reflections;

import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.Translate;

@SuppressWarnings("rawtypes")
public class DefaultAnnotationSitemapLoader implements AnnotationSitemapLoader {

	private String reflectionRoot;
	private final Sitemap sitemap;
	private List<Class<? extends Enum>> labelKeys;
	private final Translate translate;
	private final CurrentLocale currentLocale;

	@Inject
	protected DefaultAnnotationSitemapLoader(Sitemap sitemap, Translate translate, CurrentLocale currentLocale) {
		super();
		this.sitemap = sitemap;
		this.translate = translate;
		this.currentLocale = currentLocale;

	}

	@Override
	public boolean load() {
		if (reflectionRoot == null) {
			throw new IllegalStateException("reflectionRoot must be set before loading");
		}
		if (labelKeys.isEmpty()) {
			throw new IllegalStateException("at least one labels keys class must be added before loading");
		}
		Collator collator = Collator.getInstance(currentLocale.getLocale());
		Reflections reflections = new Reflections(reflectionRoot);
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(View.class);
		for (Class<?> clazz : annotated) {
			View annotation = clazz.getAnnotation(View.class);
			I18NKey<?> key = keyFromName(annotation.labelKeyName());
			SitemapNode node = sitemap.append(annotation.uri());
			node.setViewClass(annotation.viewClass());
			node.setTranslate(translate);
			node.setLabelKey(key, currentLocale.getLocale(), collator);
			node.setPublicPage(annotation.isPublic());
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private I18NKey<?> keyFromName(String labelKeyName) {
		boolean found = false;
		I18NKey<?> result = null;
		while (!found) {
			for (Class<? extends Enum> enumClass : labelKeys) {
				try {
					Enum labelKey = Enum.valueOf(enumClass, labelKeyName);
					return (I18NKey<?>) labelKey;
				} catch (IllegalArgumentException iae) {
					// don't need to do anything, just have not found a match
				}
			}

		}
		return result;
	}

	/**
	 * An annotation cannot contain an enum parameter, so the label key is held as a String. That String (the
	 * {@code labelKeyName} parameter) must then be looked up from an implementation of {@link I18NKey}. Multiple
	 * labelKeyClass's can be added; the first one to find a match will be returned, or null if no match found.
	 * 
	 * @param labelKeyClass
	 */
	public void addLabelKeyClass(Class<? extends Enum> labelKeyClass) {
		labelKeys.add(labelKeyClass);
	}

	@Override
	public boolean overwriteExisting() {

		return false;
	}

	public String getReflectionRoot() {
		return reflectionRoot;
	}

	public void setReflectionRoot(String reflectionRoot) {
		this.reflectionRoot = reflectionRoot;
	}

}
