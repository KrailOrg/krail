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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class SitemapLoaderBase implements SitemapLoader {
	private final Map<String, List<LoaderErrorEntry>> errors;
	private final Map<String, List<LoaderWarningEntry>> warnings;
	private final Map<String, List<LoaderInfoEntry>> infos;
	private int errorCount;
	private int warningCount;
	private int infoCount;

	protected SitemapLoaderBase() {
		errors = new TreeMap<>();
		warnings = new TreeMap<>();
		infos = new TreeMap<>();
	}

	protected void addError(String source, String msgPattern, Object... msgParams) {
		LoaderErrorEntry errorEntry = new LoaderErrorEntry();
		errorEntry.msgPattern = msgPattern;
		errorEntry.msgParams = msgParams;
		List<LoaderErrorEntry> list = errors.get(source);
		if (list == null) {
			list = new ArrayList<>();
			errors.put(source, list);
		}
		list.add(errorEntry);
		errorCount++;

	}

	protected void addWarning(String source, String msgPattern, Object... msgParams) {
		LoaderWarningEntry warningEntry = new LoaderWarningEntry();
		warningEntry.msgPattern = msgPattern;
		warningEntry.msgParams = msgParams;
		List<LoaderWarningEntry> list = warnings.get(source);
		if (list == null) {
			list = new ArrayList<>();
			warnings.put(source, list);
		}
		list.add(warningEntry);
		warningCount++;

	}

	protected void addInfo(String source, String msgPattern, Object... msgParams) {
		LoaderInfoEntry infoEntry = new LoaderInfoEntry();
		infoEntry.msgPattern = msgPattern;
		infoEntry.msgParams = msgParams;
		List<LoaderInfoEntry> list = infos.get(source);
		if (list == null) {
			list = new ArrayList<>();
			infos.put(source, list);
		}
		list.add(infoEntry);
		infoCount++;
	}

	@Override
	public Map<String, List<LoaderErrorEntry>> getErrors() {
		return errors;
	}

	@Override
	public Map<String, List<LoaderWarningEntry>> getWarnings() {
		return warnings;
	}

	@Override
	public Map<String, List<LoaderInfoEntry>> getInfos() {
		return infos;
	}

	@Override
	public int getErrorCount() {
		return errorCount;
	}

	@Override
	public int getWarningCount() {
		return warningCount;
	}

	@Override
	public int getInfoCount() {
		return infoCount;
	}

	protected void clearCounts() {
		errorCount = 0;
		warningCount = 0;
		infoCount = 0;
	}

}
