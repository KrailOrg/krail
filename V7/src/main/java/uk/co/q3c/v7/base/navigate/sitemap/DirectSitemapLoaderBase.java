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

import uk.co.q3c.v7.base.guice.BaseGuiceServletInjector;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

/**
 * If you want to create Sitemap entries directly, you can either subclass this module and provide the entries in the
 * {@link #addBindings(MapBinder)} method, or just simply use this as an example and create your own. The module then
 * needs to be added to your subclass of {@link BaseGuiceServletInjector}. By convention, modules relating to the
 * Sitemap are added in the addSitempaModules() method.
 * <p>
 * You can add any number of modules this way, but any duplicated map keys (the URI segments) will cause the map
 * injection to fail. There is an option to change this behaviour in MapBinder#permitDuplicates()
 * <p>
 * If you use multiple modules, Guice will merge all of the bindings into a single MapBinder.
 * 
 * @author David Sowerby
 * 
 */
public class DirectSitemapLoaderBase extends AbstractModule {

	/**
	 * override this method to define {@link Sitemap} entries. These will be combined with other sources of sitemap
	 * entry (if there are any) by the {@link SitemapService}. When the subclass has been defined, you will also need to
	 * ensure the binding to it is included in your subclass of the {@link SitemapFileLoaderModule}
	 * <p>
	 * Typically your subclass will have a number of lines like this:
	 * <p>
	 * addEntry("public/home", PublicHomeView.class, LabelKey.Home, false);
	 */
	protected void define() {

	}

	// protected DirectSitemapLoaderBase addEntry(String uriSegment, Class<? extends V7View> viewClass,
	// I18NKey<?> labelKey, boolean publicPage) {
	// DirectSitemapEntry entry = new DirectSitemapEntry();
	// entry.setUriSegment(uriSegment);
	// entry.setLabelKey(labelKey);
	// entry.setViewClass(viewClass);
	// entry.setPublicPage(publicPage);
	// entries.add(entry);
	// return this;
	// }

	@Override
	protected void configure() {
		MapBinder<String, DirectSitemapEntry> mapBinder = MapBinder.newMapBinder(binder(), String.class,
				DirectSitemapEntry.class);
		addBindings(mapBinder);
	}

	/**
	 * Add your bindings in this method of your subclass. An entry will look like this:
	 * <p>
	 * {@code mapBinder.addBinding("public/home").toInstance(new DirectSitemapEntry(PublicHomeView.class, LabelKey.Home,
	 * true));}
	 * <p>
	 * Note that the URI segment is the map key, and by default {@link MapBinder} does not allow duplicates
	 * 
	 * @param mapBinder
	 */
	protected void addBindings(MapBinder<String, DirectSitemapEntry> mapBinder) {

	}
}
