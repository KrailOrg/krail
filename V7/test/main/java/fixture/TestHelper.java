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
package fixture;

import java.io.File;

import uk.co.q3c.v7.base.navigate.Sitemap;
import uk.co.q3c.v7.base.navigate.TextReaderSitemapProvider;
import uk.co.q3c.v7.base.view.ApplicationViewModule;

public class TestHelper {

	public static ApplicationViewModule applicationViewModuleUsingSitemap() {
		TextReaderSitemapProvider sitemapPro = new TextReaderSitemapProvider();
		// has to be on classpath, called as a Resource
		File dir = new File("test/main/java");
		File f = new File(dir, "sitemap.properties");
		sitemapPro.setSourceFile(f);
		Sitemap sitemap = sitemapPro.get();
		ApplicationViewModule module = new ApplicationViewModule(sitemap);
		return module;
	}

}
