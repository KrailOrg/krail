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
package uk.co.q3c.v7.base.navigate;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.io.ResourceUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.config.V7Ini.StandardPageKey;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.I18NKeys;

import com.google.common.base.Strings;

public class TextReaderSiteMapProvider implements SiteMapProvider {

	private static Logger log = LoggerFactory.getLogger(TextReaderSiteMapProvider.class);

	private enum SectionName {
		options,
		redirects,
		viewPackages,
		standardPages,
		map;
	}

	private SiteMap siteMap;
	private int commentLines;
	private int blankLines;
	private Map<SectionName, List<String>> sections;
	private Map<StandardPageKey, String> standardPages;
	private SectionName currentSection;
	private String labelKeys;
	@SuppressWarnings("rawtypes")
	private Class<? extends Enum> labelKeysClass;

	private boolean appendView;

	private Set<String> missingEnums;
	private Set<String> invalidViewClasses;
	private Set<String> undeclaredViewClasses;
	private Set<String> indentationErrors;
	private Set<String> missingPages;
	private Set<String> propertyErrors;
	private Set<String> duplicateURLs;
	private Set<String> viewlessURLs;

	private StringBuilder report;
	private DateTime startTime;
	private DateTime endTime;
	private String source;
	private boolean parsed = false;
	private boolean enumNotI18N;
	private boolean enumNotExtant;
	private Map<String, String> redirects;

	@Inject
	public TextReaderSiteMapProvider() {
		super();
	}

	private void init() {
		startTime = DateTime.now();
		endTime = null;
		missingEnums = new HashSet<>();
		invalidViewClasses = new HashSet<>();
		undeclaredViewClasses = new HashSet<>();
		indentationErrors = new HashSet<>();
		missingPages = new HashSet<>();
		propertyErrors = new HashSet<>();
		viewlessURLs = new HashSet<>();
		duplicateURLs = new HashSet<>();
		redirects = new TreeMap<>();

		standardPages = new TreeMap<>();
		siteMap = new SiteMap();
		sections = new HashMap<>();
		enumNotI18N = false;
		enumNotExtant = false;
		parsed = false;
	}

	@Override
	public void parse(String resourcePath) {
		// File file = new File(ResourceUtils.applicationBaseDirectory(), fileName);
		source = resourcePath;
		InputStream is;
		try {
			is = ResourceUtils.getInputStreamForPath(resourcePath);
			InputStreamReader isr = new InputStreamReader(is);
			Scanner scanner = new Scanner(isr);
			List<String> lines = new ArrayList<>();
			try {
				while (scanner.hasNextLine()) {
					lines.add(scanner.nextLine());
				}
			} finally {
				scanner.close();
			}
			processLines(lines);

		} catch (Exception e) {
			log.error("Unable to load site map file", e);
			return;
		}

	}

	private void processLines(List<String> lines) {
		init();
		int i = 0;
		for (String line : lines) {
			divideIntoSections(line, i);
			i++;
		}

		// can only process if ALL required sections are present
		if (missingSections().size() == 0) {
			for (SectionName sectionName : SectionName.values()) {
				processSection(sectionName, sections.get(sectionName));
			}
			processStandardPages();
		} else {
			log.warn("The site map source is missing these sections: {}", missingSections());
			log.error("Site map failed to process, see previous log warnings for details");
		}

		endTime = DateTime.now();
		parsed = true;
	}

	/**
	 * Standard pages are added after the page map has been built. This may cause a duplication of urls - if so, that is
	 * captured in {@link #duplicateURLs}. As the map is built from the standard page URLs, it may also cause
	 * intermediate nodes to have no View assigned. This is checked and captured in {@link #viewlessURLs}
	 */
	private void processStandardPages() {

		for (StandardPageKey spk : StandardPageKey.values()) {
			String pageUrl = standardPages.get(spk);
			// siteMap.append(pageUrl);
		}
	}

	@Override
	public void parse(File file) {

		source = file.getAbsolutePath();

		try {
			@SuppressWarnings("unchecked")
			List<String> lines = FileUtils.readLines(file);
			processLines(lines);

		} catch (Exception e) {
			log.error("Sitemap could not be loaded", e);
		}
	}

	private void processSection(SectionName key, List<String> sectionLines) {
		switch (key) {
		case map:
			processMap(sectionLines);
			break;

		case viewPackages:
			processViewPackages(sectionLines);
			break;

		case options:
			processProperties(key, sectionLines);
			break;

		case redirects:
			processRedirects(key, sectionLines);
			break;

		case standardPages:
			processProperties(key, sectionLines);
			for (StandardPageKey spk : StandardPageKey.values()) {
				if (!standardPages.containsKey(spk)) {
					missingPages.add(spk.name());
				}
			}
			break;

		default:
			// do nothing, just ignore it
		}
	}

	private void processRedirects(SectionName key, List<String> sectionLines) {
		for (String line : sectionLines) {
			// if starts with ':' then f==""
			// split the line on ':'

			if (line.startsWith(":")) {
				redirects.put("", line.replace(":", "").trim());
			} else {
				String[] pair = null;
				pair = StringUtils.split(line, ":");
				String f = pair[0].trim();
				String t = (pair.length > 1) ? pair[1].trim() : null;
				redirects.put(f, t);
			}
		}
	}

	private void processProperties(SectionName sectionName, List<String> lines) {
		int i = 1;
		for (String line : lines) {
			if (!line.contains("=")) {
				propertyErrors.add("Property must contain an '=' sign at line " + i + " in the " + sectionName
						+ " section");
			} else {
				// split the line on '='
				String[] pair = StringUtils.split(line, "=");
				String key = pair[0].trim();

				// malformed property may not have anything after the '='
				String value = (pair.length > 1) ? pair[1].trim() : null;

				// check for empty key or value
				boolean valid = true;

				if (Strings.isNullOrEmpty(key)) {
					propertyErrors.add("Property must have a key at line " + i + " in the " + sectionName + " section");
					valid = false;
				} else {
					if (Strings.isNullOrEmpty(value)) {
						propertyErrors.add("Property " + key + " cannot have an empty value");
						valid = false;
					}
				}

				// process valid properties only
				if (valid) {
					if (sectionName.equals(SectionName.options)) {
						setOption(key, value);
					} else {
						setStandardPage(key, value);
					}
				}
			}
			// increment line count
			i++;
		}
	}

	private void setStandardPage(String key, String value) {
		try {
			StandardPageKey pageKey = StandardPageKey.valueOf(key);
			standardPages.put(pageKey, value);
		} catch (Exception e) {
			throw new SiteMapFormatException(key + " is not a valid standard page key");
		}

	}

	private void setOption(String key, String value) {
		switch (key) {
		case "labelKeys":
			labelKeys = value;
			validateLabelKeys();
			break;
		case "appendView":
			appendView = "true".equals(value);
			break;
		default:
			log.warn("unrecognised option '{}' in site map", key);
		}
	}

	@SuppressWarnings("unchecked")
	private void validateLabelKeys() {
		boolean valid = true;
		Class<?> requestedLabelKeysClass = null;
		try {

			requestedLabelKeysClass = Class.forName(labelKeys);
			// enum
			if (!requestedLabelKeysClass.isEnum()) {
				valid = false;
			}

			// instance of I18NKeys
			@SuppressWarnings("rawtypes")
			Class<I18NKeys> i18nClass = I18NKeys.class;
			if (!i18nClass.isAssignableFrom(requestedLabelKeysClass)) {
				valid = false;
				enumNotI18N = true;
			}
		} catch (ClassNotFoundException e) {
			valid = false;
			enumNotExtant = true;
		}
		if (!valid) {
			log.warn(labelKeys + " is not a valid enum class for I18N labels");
		} else {
			labelKeysClass = (Class<? extends Enum<?>>) requestedLabelKeysClass;
		}
	}

	private void processViewPackages(List<String> sectionLines) {
		// seems like nothing needs to be done!
	}

	public List<String> getViewPackages() {
		return sections.get(SectionName.viewPackages);
	}

	@SuppressWarnings("unchecked")
	private void processMap(List<String> sectionLines) {
		int i = 0;
		SiteMapNode currentNode = null;
		int currentLevel = 0;
		for (String line : sectionLines) {
			if (line.startsWith("-")) {
				int treeLevel = lastIndent(line);
				int viewStart = line.indexOf(":");
				int labelStart = line.indexOf("~");
				String segment = null;
				String view = null;
				String labelKeyName = null;
				if ((labelStart > 0) && (viewStart > 0)) {
					if (viewStart < labelStart) {
						segment = line.substring(treeLevel, viewStart);
						view = line.substring(viewStart + 1, labelStart);
						labelKeyName = line.substring(labelStart + 1);
					} else {
						segment = line.substring(treeLevel, labelStart);
						labelKeyName = line.substring(labelStart + 1, viewStart);
						view = line.substring(viewStart + 1);
					}
				} else {
					// only label
					if (labelStart > 0) {
						segment = line.substring(treeLevel, labelStart);
						labelKeyName = line.substring(labelStart + 1);
					}// only view
					else if (viewStart > 0) {
						segment = line.substring(treeLevel, viewStart);
						view = line.substring(viewStart + 1);
					}
					// only segment
					else {
						segment = line.substring(treeLevel);
					}
				}

				// segment has been set, view & label may be null

				// do the label, and try and get enum for it
				// if the enum is missing, keep a note of it
				// handy for identifying missing ones, just cut and paste from the console
				// to the enum definition
				SiteMapNode node = new SiteMapNode();
				node.setUrlSegment(segment);
				try {
					if (labelKeyName == null) {
						labelKeyName = segment;
					}
					// hyphen not valid in enum, translate to underscore
					labelKeyName = labelKeyName.replace("-", "_");
					@SuppressWarnings("rawtypes")
					Enum labelKey = Enum.valueOf(labelKeysClass, labelKeyName);

					node.setLabelKey(labelKey);
				} catch (Exception e) {
					missingEnums.add(labelKeyName);
				}

				// do the view
				// if view is null use the segment
				if (view == null) {
					view = StringUtils.capitalize(segment);
				}

				// user option whether to append 'View' or not
				if (appendView) {
					view = view + "View";
				}

				// try and find the view in the specified packages
				for (String pkg : getViewPackages()) {
					String fullViewName = pkg + "." + view;
					try {
						Class<?> viewClass = Class.forName(fullViewName);
						if (V7View.class.isAssignableFrom(viewClass)) {
							node.setViewClass((Class<V7View>) viewClass);
							break;
						} else {
							invalidViewClasses.add(fullViewName);
						}
					} catch (ClassNotFoundException e) {
						// don't need to do anything
					}

				}
				if (node.getViewClass() == null) {
					undeclaredViewClasses.add(view);
				}

				// now add the node
				if (treeLevel == 1) {
					// at level 1 each becomes a 'root' (technically the site tree is a forest)
					siteMap.addNode(node);
					currentNode = node;
					currentLevel = treeLevel;
				} else {
					// if indent going back up tree, walk up from current node to the parent level needed
					if (treeLevel < currentLevel) {
						int retraceLevels = currentLevel - treeLevel;
						for (int k = 1; k <= retraceLevels; k++) {
							currentNode = siteMap.getParent(currentNode);
							currentLevel--;
						}
						siteMap.addChild(currentNode, node);
						currentNode = node;
						currentLevel++;
					} else if (treeLevel == currentLevel) {
						SiteMapNode parentNode = siteMap.getParent(currentNode);
						siteMap.addChild(parentNode, node);
					} else if (treeLevel > currentLevel) {
						if (treeLevel - currentLevel > 1) {
							log.warn(
									"indentation for {} line is too great.  It should be a maximum of 1 greater than its predecessor",
									node.getUrlSegment());
							indentationErrors.add(node.getUrlSegment());
						}
						siteMap.addChild(currentNode, node);
						currentNode = node;
						currentLevel++;
					}

				}

			} else {
				throw new SiteMapFormatException("line in map must start with a'-', line " + i);
			}
		}

	}

	private int lastIndent(String line) {
		int index = 0;
		while (line.charAt(index) == '-') {
			index++;
		}
		return index;
	}

	/**
	 * process a line of text from the file into the appropriate section
	 * 
	 * @param line
	 */
	private void divideIntoSections(String line, int linenum) {
		String strippedLine = StringUtils.deleteWhitespace(line);
		if (strippedLine.startsWith("#")) {
			commentLines++;
			return;
		}
		if (Strings.isNullOrEmpty(strippedLine)) {
			blankLines++;
			return;
		}
		if (strippedLine.startsWith("[")) {
			if ((!strippedLine.endsWith("]"))) {
				log.warn("section requires closing ']' at line " + linenum);
			} else {
				String sectionName = strippedLine.substring(1, strippedLine.length() - 1);

				List<String> section = new ArrayList<>();
				try {
					SectionName key = SectionName.valueOf(sectionName);
					currentSection = key;
					sections.put(key, section);
				} catch (IllegalArgumentException iae) {
					log.warn(
							"Invalid section '{}' in site map file, this section has been ignored. Only sections {} are allowed.",
							sectionName, getSections().toString());
				}

			}
			return;
		}

		List<String> section = sections.get(currentSection);
		if (section != null) {
			section.add(strippedLine);
		}
	}

	@Override
	public SiteMap getSiteMap() {
		return siteMap;
	}

	public int getCommentLines() {
		return commentLines;
	}

	public int getBlankLines() {
		return blankLines;
	}

	public Set<String> getSections() {
		Set<String> sections = new TreeSet<>();
		for (SectionName sectionName : SectionName.values()) {
			sections.add(sectionName.name());
		}
		return sections;
	}

	public String getLabelKeys() {
		return labelKeys;
	}

	public boolean isAppendView() {
		return appendView;
	}

	@SuppressWarnings("rawtypes")
	public Class<? extends Enum> getLabelKeysClass() {
		return labelKeysClass;
	}

	public Set<String> getMissingEnums() {
		return missingEnums;
	}

	private void buildReport() {
		report = new StringBuilder();
		String df = "dd MMM YYYY HH:mm:SS";

		report.append("==================== SiteMap builder report ==================== \n\n");
		report.append("parsing source from:\t\t\t");
		report.append(source);
		report.append("\n\n");

		report.append("start at:\t\t\t");
		report.append(startTime.toString(df));
		report.append("\n");

		report.append("end at:\t\t\t\t");
		report.append(endTime.toString(df));
		report.append("\n");

		report.append("run time:\t\t\t");
		report.append(runtime().toString());
		report.append(" ms\n\n");

		report.append("missing sections:\t\t");
		report.append(missingSections().size());
		report.append("  (if any section is missing, parsing will fail, and results will be indeterminate - correct this first)");
		report.append("\n");
		if (missingSections().size() > 0) {
			report.append("  -- ");
			report.append(missingSections());
			report.append("\n");
		}
		report.append("\n");

		report.append("view packages declared:\t\t");
		if (getViewPackages() == null) {
			report.append("none declared - site map will not build without them");
		} else {
			report.append(getViewPackages().toString());
		}
		report.append("\n\n");

		report.append("pages defined:\t\t\t");
		report.append(getPagesDefined());
		report.append("\n\n");

		report.append("missing standard pages:\t\t");
		report.append(missingPages.size());
		report.append("  (these MUST be defined)");
		report.append("\n");
		if (missingPages.size() > 0) {
			report.append("  -- ");
			report.append(missingPages);
			report.append("\n");
		}
		report.append("\n");

		report.append("property format errors:\t\t");
		report.append(propertyErrors.size());
		report.append("  (should be key=value, spaces are ignored)");
		report.append("\n");
		if (missingPages.size() > 0) {
			for (String p : propertyErrors) {
				report.append("  -- ");
				report.append(p);
				report.append("\n");
			}
		}
		report.append("\n");

		report.append("missing enum declarations:\t");
		report.append(missingEnums.size());
		report.append("  (you could just paste these into your enum declaration)");
		report.append("\n");
		if (missingEnums.size() > 0) {
			report.append("  -- ");
			report.append(missingEnums);
			report.append("\n");
		}
		report.append("\n");

		report.append("invalid view classes:\t\t");
		report.append(invalidViewClasses.size());
		report.append("  (invalid because they do not implement V7View)\n");
		if (invalidViewClasses.size() > 0) {
			report.append("  -- ");
			report.append(invalidViewClasses);
			report.append("\n");
		}
		report.append("\n");

		report.append("undeclared view classes:\t");
		report.append(undeclaredViewClasses.size());
		report.append("  (these could not be found in the view packages declared in the [viewPackages] section)\n");
		if (undeclaredViewClasses.size() > 0) {
			report.append("  -- ");
			report.append(undeclaredViewClasses);
			report.append("\n");
		}
		report.append("\n");

		report.append("indentation errors:\t\t");
		report.append(indentationErrors.size());
		report.append("  (line indentation should be <= 1 greater than the preceding line.  Parsing will still work but you may not get the intended result\n");
		if (indentationErrors.size() > 0) {
			report.append("  -- ");
			report.append(indentationErrors);
			report.append("\n");
		}
		report.append("\n\n");

		report.append("================================================================= ");

	}

	public int getPagesDefined() {
		return getSiteMap().getNodeCount();
	}

	public Long runtime() {
		Long r = endTime.getMillis() - startTime.getMillis();
		return r;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	@Override
	public StringBuilder getReport() {
		if (!parsed) {
			throw new SiteMapException("File must be parsed before report is requested");
		}
		buildReport();
		return report;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public Set<String> missingSections() {
		Set<String> missing = new HashSet<>();
		for (SectionName section : SectionName.values()) {
			if (!sections.containsKey(section)) {
				missing.add(section.name());
			}
		}
		return missing;
	}

	public Set<String> getInvalidViewClasses() {
		return invalidViewClasses;
	}

	public Set<String> getUndeclaredViewClasses() {
		return undeclaredViewClasses;
	}

	public boolean isEnumNotI18N() {
		return enumNotI18N;
	}

	public boolean isEnumNotExtant() {
		return enumNotExtant;
	}

	public Set<String> getIndentationErrors() {
		return indentationErrors;
	}

	@Override
	public SiteMap get() {
		parse("classpath:sitemap.properties");
		return getSiteMap();
	}

	public String standardPageUrl(StandardPageKey property) {
		return standardPages.get(property);
	}

	public Set<String> getMissingPages() {
		return missingPages;
	}

	public Set<String> getPropertyErrors() {
		return propertyErrors;
	}

	public Set<String> getRedirects() {
		Set<String> ss = new HashSet<>();
		for (Map.Entry<String, String> entry : redirects.entrySet()) {
			ss.add(entry.getKey() + ":" + entry.getValue());
		}
		return ss;
	}

	public Set<String> getViewlessURLs() {
		return viewlessURLs;
	}

	public Set<String> getDuplicateURLs() {
		return duplicateURLs;
	}

}
