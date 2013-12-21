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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.co.q3c.util.ReflectionUtils;
import uk.co.q3c.v7.base.view.V7View;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Captures {@link View} annotations and holds them in preparation for building the {@link Sitemap} in the
 * {@link SitemapService}
 * 
 * @author David Sowerby
 * 
 */
public class SitemapAnnotationsModule extends AbstractModule {

	/**
	 * Needs to be created this way because it is inside the module, but note that the @Provides method at
	 * {@link #getSitemapEntries()} ensures that injection scope remains consistent
	 */
	private final List<AnnotationSitemapEntry> sitemapEntries = new ArrayList<>();

	public class V7ViewListener implements TypeListener {

		public V7ViewListener() {
		}

		@Override
		public <I> void hear(final TypeLiteral<I> type, TypeEncounter<I> encounter) {
			InjectionListener<Object> listener = new InjectionListener<Object>() {
				@Override
				public void afterInjection(Object injectee) {

					// cast is safe - if not, the matcher is wrong
					Class<? extends Object> clazz = injectee.getClass();
					if (clazz.isAnnotationPresent(View.class)) {
						View annotation = clazz.getAnnotation(View.class);
						AnnotationSitemapEntry entry = new AnnotationSitemapEntry();
						entry.setPublicPage(annotation.isPublic());
						entry.setLabelKeyName(annotation.labelKeyName());
						entry.setViewClass(annotation.viewClass());
						entry.setUriSegment(annotation.uri());
						sitemapEntries.add(entry);
					}

				}
			};
			encounter.register(listener);
		}

	}

	/**
	 * Matches classes implementing {@link V7View}
	 * 
	 */
	private class V7ViewInterfaceMatcher extends AbstractMatcher<TypeLiteral<?>> {
		@Override
		public boolean matches(TypeLiteral<?> t) {
			Class<?> rawType = t.getRawType();
			Set<Class<?>> interfaces = ReflectionUtils.allInterfaces(rawType);

			for (Class<?> intf : interfaces) {
				if (intf.equals(V7View.class)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	protected void configure() {
		bindListener(new V7ViewInterfaceMatcher(), new V7ViewListener());
	}

	@Provides
	public ImmutableList<AnnotationSitemapEntry> getSitemapEntries() {
		return ImmutableList.copyOf(sitemapEntries);
	}

}
