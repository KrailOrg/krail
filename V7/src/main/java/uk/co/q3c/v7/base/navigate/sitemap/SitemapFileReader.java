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
 * Implementations must read a sitemap definition from a file, and constructs the Sitemap object from it. Depending on
 * the way the application has been configured, this may be only one of the ways in which the Sitemap is populated, as
 * there are similar implementations to populate the Sitemap from annotations and definitions in a Guice module - these
 * may be combined if the developer so wishes.
 */
public interface SitemapFileReader {

	/**
	 * Parses {@code file}. If {@code firstLoad} is true, then no checks are made to see whether the {@link Sitemap}
	 * already contains a URI already - in other words, this is the first sitemap source to be loaded. If
	 * {@code firstLoad} is false, then before any URI to {@link SitemapNode} mapping is added, the implementation must
	 * check to see whether the URI exists already - if it does exist, then this source must be ignored for that URI.
	 * 
	 * @param file
	 * @param firstLoad
	 */
	public abstract void parse(File file, boolean firstLoad);

	public abstract StringBuilder buildReport(StringBuilder report);

}
