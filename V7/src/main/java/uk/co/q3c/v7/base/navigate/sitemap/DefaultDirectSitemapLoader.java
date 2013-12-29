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

import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;

/**
 * If a Map<String, DirectSitemapEntry> binding has been created (using Guice modules sub-classed from
 * {@link DirectSitemapModule}), then {@link #map} will be non-null. If so, its contents are transferred to the
 * {@link Sitemap}
 * 
 * @author David Sowerby
 * 
 */
public class DefaultDirectSitemapLoader implements DirectSitemapLoader {

	private Map<String, DirectSitemapEntry> map;
	private final Sitemap sitemap;
	private final CurrentLocale currentLocale;
	private final Translate translate;

	@Inject
	protected DefaultDirectSitemapLoader(Sitemap sitemap, Translate translate, CurrentLocale currentLocale) {
		this.sitemap = sitemap;
		this.currentLocale = currentLocale;
		this.translate = translate;
	}

	@Override
	public boolean load() {
		if (map != null) {
			Collator collator = Collator.getInstance(currentLocale.getLocale());
			for (Entry<String, DirectSitemapEntry> entry : map.entrySet()) {
				SitemapNode node = sitemap.append(entry.getKey());
				DirectSitemapEntry value = entry.getValue();
				node.setLabelKey(value.getLabelKey(), translate, collator);
				node.setPublicPage(value.isPublicPage());
				node.addPermission(value.getPermission());
				node.setTranslate(translate);
				node.setViewClass(value.getViewClass());
			}
			return true;
		}
		return false;
	}

	@Inject(optional = true)
	protected void setMap(Map<String, DirectSitemapEntry> map) {
		this.map = map;
	}

}
