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

import java.util.List;
import java.util.Map;

/**
 * A common interface for all implementations loading the Sitemap from whatever source - file, annotations, or direct
 * coding. The order in which they are loaded is determined by the order in which the bindings are made in the
 * {@link SitemapModule}. If there are duplicate URI entries between loaders, the handling of them will be
 * determined by the {@link MasterSitemap}
 * 
 * @author David Sowerby
 * 
 */
public interface SitemapLoader {

	public class LoaderErrorEntry {
		String msgPattern;
		Object[] msgParams;
	}

	public class LoaderWarningEntry {
		String msgPattern;
		Object[] msgParams;
	}

	public class LoaderInfoEntry {
		String msgPattern;
		Object[] msgParams;
	}

	/**
	 * Loads the Sitemap from whichever source the implementation chooses. Returns true if the load is successful
	 * 
	 * @return
	 */
	boolean load();

	Map<String, List<LoaderInfoEntry>> getInfos();

	Map<String, List<LoaderWarningEntry>> getWarnings();

	Map<String, List<LoaderErrorEntry>> getErrors();

	int getErrorCount();

	int getWarningCount();

	int getInfoCount();

}
