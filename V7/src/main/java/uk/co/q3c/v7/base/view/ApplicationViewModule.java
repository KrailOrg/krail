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
package uk.co.q3c.v7.base.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

/**
 * This module maps urls to {@link V7View} classes. The mapping is provided by the SiteMap.
 * 
 * @author David Sowerby 6 Apr 2013
 * 
 */
public class ApplicationViewModule extends AbstractModule {
	private static Logger log = LoggerFactory.getLogger(ApplicationViewModule.class);
	private final Sitemap sitemap;

	protected ApplicationViewModule() {
		sitemap = null;
	}

	public ApplicationViewModule(Sitemap sitemap) {
		super();
		this.sitemap = sitemap;
	}

	@Override
	protected void configure() {

		MapBinder<String, V7View> mapbinder = MapBinder.newMapBinder(binder(), String.class, V7View.class);
		bindViews(mapbinder);
	}

	/**
	 * By default V7 is set to read the site map and create bindings from it. You can disable that by setting the
	 * 'readSiteMap' property in the [options] section of V7.ini to false. You will then need to subclass and manually
	 * specify Views, to provide the Views for your application, with a set of instructions like this:
	 * <p>
	 * 
	 * mapbinder.addBinding("public/home").to(PublicHomeView.class);
	 * mapbinder.addBinding("private/home").to(PrivateHomeView.class);
	 * 
	 * @param mapbinder
	 */
	protected void bindViews(MapBinder<String, V7View> mapbinder) {
		if (sitemap == null) {
			log.error(
					"Sitemap is null, but {}.bindViews is still in use.  Either override the bindViews method, or set the 'readSiteMap' property in the [options] section of V7.ini to 'true",
					this.getClass().getName());
		} else {

			for (SitemapNode node : sitemap.getEntries()) {
				// use sitemap.url(node) to get the fully qualified url - the node itself only contains the url segment
				// intermediate nodes will not have a view if they have been redirected
				Class<? extends V7View> viewClass = node.getViewClass();
				if (viewClass != null) {
					String url = node.getUri();
					mapbinder.addBinding(url).to(viewClass);
				}
			}
		}
	}

}
