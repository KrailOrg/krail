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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;
import com.mycila.inject.internal.guava.base.Splitter;

@SuppressWarnings("rawtypes")
public class DefaultAnnotationSitemapLoader implements AnnotationSitemapLoader {
	private static Logger log = LoggerFactory.getLogger(DefaultAnnotationSitemapLoader.class);
	private final Sitemap sitemap;
	private final Translate translate;
	private final CurrentLocale currentLocale;
	private Map<String, AnnotationSitemapEntry> sources;

	@Inject
	protected DefaultAnnotationSitemapLoader(Sitemap sitemap, Translate translate, CurrentLocale currentLocale) {
		super();
		this.sitemap = sitemap;
		this.translate = translate;
		this.currentLocale = currentLocale;
	}

	/**
	 * Scans for {@link View} annotations, starting from {@link #reflectionRoot}. Annotations cannot hold enum
	 * parameters, so the enum name has to be converted from the labelKeyName parameter of the {@link View} annotation.
	 * In order to do that one or more enum classes must be added to {@link #labelKeyClasses}
	 * 
	 * @see uk.co.q3c.v7.base.navigate.sitemap.SitemapLoader#load()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean load() {
		Collator collator = Collator.getInstance(currentLocale.getLocale());
		if (sources != null) {
			for (Entry<String, AnnotationSitemapEntry> entry : sources.entrySet()) {
				log.debug("scanning {} for View annotations", entry.getKey());
				Reflections reflections = new Reflections(entry.getKey());
				Set<Class<?>> types = reflections.getTypesAnnotatedWith(View.class);
				log.debug("{} annotated Views found", types.size());
				for (Class<?> clazz : types) {
					Class<? extends V7View> viewClass = null;
					if (clazz.isAssignableFrom(V7View.class)) {
						viewClass = (Class<? extends V7View>) clazz;
						View annotation = viewClass.getAnnotation(View.class);
						SitemapNode node = sitemap.append(annotation.uri());
						node.setViewClass(viewClass);
						node.setTranslate(translate);
						node.setPageAccessControl(annotation.pageAccessControl());
						if (StringUtils.isNotEmpty(annotation.roles())) {
							Splitter splitter = Splitter.on(",").trimResults();
							Iterable<String> roles = splitter.split(annotation.roles());
							for (String role : roles) {
								node.addRole(role);
							}
						}
						I18NKey<?> key = keyFromName(annotation.labelKeyName(), entry.getValue().getLabelSample());
						node.setLabelKey(key, currentLocale.getLocale(), collator);

					}

				}
			}
			return true;
		} else {
			log.info("No Annotations Sitemap sources to load");
			return false;
		}
	}

	/**
	 * Returns an {@link I18NKey} enum constant from {@code labelKeyName} using {@code labelKeyClass}.
	 * 
	 * @param labelKeyName
	 * @param labelKeyClass
	 * @return an {@link I18NKey} enum constant from {@code labelKeyName} using {@code labelKeyClass}.
	 * @exception IllegalArgumentException
	 *                if <code>labelKeyClass</code> does not contain a constant of <code>labelKeyName</code>
	 */
	private I18NKey<?> keyFromName(String labelKeyName, I18NKey sampleKey) {

		Enum<?> enumSample = (Enum) sampleKey;
		Enum<?> labelKey = Enum.valueOf(enumSample.getDeclaringClass(), labelKeyName);
		return (I18NKey<?>) labelKey;

	}

	@Inject(optional = true)
	protected void setAnnotations(Map<String, AnnotationSitemapEntry> sources) {
		this.sources = sources;
	}

}
