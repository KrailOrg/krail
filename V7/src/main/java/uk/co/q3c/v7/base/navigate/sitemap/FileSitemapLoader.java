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

import java.io.File;

/**
 * Implementations of this interface take definitions of {@link Sitemap} entries from a file, and load them into the
 * {@link Sitemap} when invoked by the {@link SitemapService}. See
 * https://sites.google.com/site/q3cjava/sitemap?pli=1#TOC-The-File-Loader for a description of the file format
 */
public interface FileSitemapLoader extends SitemapLoader {

	/**
	 * Loads {@code file}, parses it and applies the entries to the {@link Sitemap}
	 * 
	 * @param file
	 *            the file to load.
	 */
	public abstract void parse(File file);

	public abstract StringBuilder buildReport(StringBuilder report);

}
