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
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.I18NKey;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

/**
 * If you want to create Sitemap entries for your own code using a direct coding approach, you can either subclass this
 * module and provide the entries in the {@link #define} method, or just simply use this as an example and create your
 * own. The module then needs to be added to your subclass of {@link BaseGuiceServletInjector}. By convention, modules
 * relating to the Sitemap are added in the addSitemapModules() method.
 * <p>
 * You can add any number of modules this way, but any duplicated map keys (the URI segments) will cause the map
 * injection to fail. There is an option to change this behaviour in MapBinder#permitDuplicates()
 * <p>
 * You can use multiple subclasses of this, Guice will merge all of the bindings into a single MapBinder<String,
 * DirectSitemapEntry> for use by the {@link DirectSitemapLoader}
 * 
 * @author David Sowerby
 * 
 */
public abstract class DirectSitemapModule extends AbstractModule {

	private MapBinder<String, DirectSitemapEntry> sitemapBinder;
	private MapBinder<String, RedirectEntry> redirectBinder;
	private MapBinder<String, V7View> viewMapping;

	/**
	 * Override this method to define {@link Sitemap} entries with one or more calls to {@link #addEntry}, something
	 * like this:
	 * <p>
	 * addEntry("public/home", PublicHomeView.class, LabelKey.Home, false, "permission");
	 * <p>
	 * and redirects with {@link #addRedirect(String, String)}
	 * 
	 * @see #addEntry(String, Class, I18NKey, boolean, String)
	 */
	protected abstract void define();

	@Override
	protected void configure() {
		this.sitemapBinder = MapBinder.newMapBinder(binder(), String.class, DirectSitemapEntry.class);
		redirectBinder = MapBinder.newMapBinder(binder(), String.class, RedirectEntry.class);
		viewMapping = MapBinder.newMapBinder(binder(), String.class, V7View.class);
		define();
	}

	/**
	 * Adds an entry to be place in the {@link Sitemap} by the {@link DirectSitemapLoader}.
	 * 
	 * @param uri
	 *            the URI for this page
	 * @param viewClass
	 *            the class of the V7View for this page. This can be null if a redirection will prevent it from actually
	 *            being displayed, but it is up to the developer to ensure that the redirection is in place
	 * @param labelKey
	 *            the I18NKey for a localised label for the view
	 * @param publicPage
	 *            true if the page should be available to anyone, including unauthenticated users
	 * @param permission
	 *            the permission string for the page. May be null if no permissions are set
	 */
	protected void addEntry(String uri, Class<? extends V7View> viewClass, I18NKey<?> labelKey,
			PageAccessControl pageAccessControl, String permission) {

		DirectSitemapEntry entry = new DirectSitemapEntry(viewClass, labelKey, pageAccessControl, permission);
		sitemapBinder.addBinding(uri).toInstance(entry);
		if (viewClass != null) {
			viewMapping.addBinding(uri).to(viewClass);
		}

	}

	protected void addEntry(String uri, Class<? extends V7View> viewClass, I18NKey<?> labelKey,
			PageAccessControl pageAccessControl) {
		addEntry(uri, viewClass, labelKey, pageAccessControl, null);
	}

	protected void addRedirect(String fromURI, String toURI) {
		redirectBinder.addBinding(fromURI).toInstance(new RedirectEntry(toURI));
	}
}
