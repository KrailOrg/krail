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
package uk.q3c.krail.core.navigate.sitemap;


import java.util.Set;

/**
 * Implementations off this interface take directly coded definitions of {@link MasterSitemap} entries, and load them
 * into the
 * {@link MasterSitemap} when invoked by the {@link SitemapService}.
 * <p/>
 * DirectSitemapLoader is usually used to load standard pages by including the Guice module {@link
 * StandardPagesModule}.
 * See https://sites.google.com/site/q3cjava/sitemap#TOC-Standard-Pages
 *
 * @author David Sowerby
 * @see StandardPageKey
 * @see AnnotationSitemapLoader
 * @see FileSitemapLoader
 */
public interface DirectSitemapLoader extends SitemapLoader {

    Set<String> sourceModules();
}
