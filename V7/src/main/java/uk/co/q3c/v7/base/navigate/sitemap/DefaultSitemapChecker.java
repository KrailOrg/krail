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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NKey;

import com.google.inject.Inject;

/**
 * Checks the Sitemap for inconsistencies after it has been loaded. The following are considered:
 * <ol>
 * <li>Missing views
 * <li>Missing enums
 * <li>public page "inside" a private page
 * </ol>
 * 
 * @author David Sowerby
 * 
 */
public class DefaultSitemapChecker implements SitemapChecker {
	private static Logger log = LoggerFactory.getLogger(DefaultSitemapChecker.class);
	private Sitemap sitemap;
	private Class<? extends V7View> defaultView;
	private I18NKey<?> defaultKey;
	private final Set<String> missingViewClasses;
	private final Set<String> missingLabelKeys;
	private final CurrentLocale currentLocale;
	private StringBuilder report;

	@Inject
	protected DefaultSitemapChecker(Sitemap sitemap, CurrentLocale currentLocale) {
		super();
		this.sitemap = sitemap;
		this.currentLocale = currentLocale;
		missingViewClasses = new HashSet<>();
		missingLabelKeys = new HashSet<>();
	}

	public Sitemap getSitemap() {
		return sitemap;
	}

	public void setSitemap(Sitemap sitemap) {
		this.sitemap = sitemap;
	}

	/**
	 * @see uk.co.q3c.v7.base.navigate.sitemap.SitemapChecker#check()
	 */
	@Override
	public void check() {
		Locale locale = currentLocale.getLocale();
		Collator collator = Collator.getInstance(locale);
		for (SitemapNode node : sitemap.getAllNodes()) {
			String nodeUri = sitemap.uri(node);
			// Don't fail if there is a redirect in place
			if (!sitemap.getRedirects().containsKey(nodeUri)) {

				if (node.getViewClass() == null) {
					if (defaultView != null) {
						node.setViewClass(defaultView);
					} else {
						missingViewClasses.add(nodeUri);
					}
				}

				if (node.getLabelKey() == null) {
					if (defaultKey != null) {
						node.setLabelKey(defaultKey, locale, collator);
					} else {
						missingLabelKeys.add(nodeUri);
					}
				}
			}
		}
		// if there are no missing keys or views, return
		if (missingViewClasses.isEmpty() && missingLabelKeys.isEmpty()) {
			return;
		}

		report = new StringBuilder();
		report.append("\n================ Sitemap Check ===============\n\n");
		if (!missingViewClasses.isEmpty()) {
			report.append("------------ URIs with missing Views -----------\n");
			for (String view : missingViewClasses) {
				report.append(view);
				report.append("\n");
			}
		}
		if (!missingLabelKeys.isEmpty()) {
			report.append("--------- URIs with missing label keys -----------\n");
			for (String key : missingLabelKeys) {
				report.append(key);
				report.append("\n");
			}
		}

		log.info(report.toString());
		// otherwise print a report and throw an exception
		throw new SitemapException("Sitemap check failed, see log for failed items");
	}

	@Override
	public SitemapChecker replaceMissingViewWith(Class<? extends V7View> defaultView) {
		this.defaultView = defaultView;
		return this;
	}

	@Override
	public SitemapChecker replaceMissingKeyWith(I18NKey<?> defaultKey) {

		this.defaultKey = defaultKey;
		return this;
	}

	public Set<String> getMissingViewClasses() {
		return missingViewClasses;
	}

	public Set<String> getMissingLabelKeys() {
		return missingLabelKeys;
	}

	public StringBuilder getReport() {
		return report;
	}

}
