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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.fest.util.Strings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.I18NKeys;

public class TextReaderSiteMapBuilder implements SiteMapBuilder {

	private static Logger log = LoggerFactory.getLogger(TextReaderSiteMapBuilder.class);

	private enum SectionName {
		options,
		viewPackages,
		map;
	}

	private SiteMap siteMap;
	private int commentLines;
	private int blankLines;
	private Map<SectionName, List<String>> sections;
	private SectionName currentSection;
	private String labelKeys;
	@SuppressWarnings("rawtypes")
	private Class<? extends Enum> labelKeysClass;

	private boolean appendView;

	private Set<String> missingEnums;
	private Set<String> invalidViewClasses;
	private Set<String> undeclaredViewClasses;
	private Set<String> indentationErrors;

	private StringBuilder report;
	private DateTime startTime;
	private DateTime endTime;
	private File sourceFile;
	private boolean parsed = false;
	private boolean enumNotI18N;
	private boolean enumNotExtant;

	@Inject
	protected TextReaderSiteMapBuilder() {
		super();
	}

	private void init() {
		startTime = DateTime.now();
		endTime = null;
		missingEnums = new HashSet<>();
		invalidViewClasses = new HashSet<>();
		undeclaredViewClasses = new HashSet<>();
		indentationErrors = new HashSet<>();
		enumNotI18N = false;
		enumNotExtant = false;
		parsed = false;
	}

	public void parse(File file) throws IOException {
		init();
		sourceFile = file;
		@SuppressWarnings("unchecked")
		List<String> lines = FileUtils.readLines(file);
		siteMap = new SiteMap();
		sections = new HashMap<>();
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
		} else {
			log.warn("The site map source is missing these sections: {}", missingSections());
			log.error("Site map failed to process, see previous log warnings for details");
		}

		endTime = DateTime.now();
		parsed = true;
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
			processOptions(sectionLines);
			break;
		default:
			throw new SiteMapFormatException("Unrecognised section " + key + " in site map file");
		}
	}

	private void processOptions(List<String> lines) {
		int i = 0;
		for (String line : lines) {
			if (!line.contains("=")) {
				throw new SiteMapFormatException("Option must contain an '=' sign at line " + i
						+ " in the options section");
			}
			String[] pair = StringUtils.split(line, "=");
			String key = pair[0].trim();
			String value = pair[1].trim();
			i++;
			setOption(key, value);
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
							node.setViewClass((Class<? extends V7View>) viewClass);
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
		if (Strings.isEmpty(strippedLine)) {
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
		report.append("parsing file:\t\t\t");
		report.append(sourceFile.getAbsolutePath());
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

		report.append("view packages declared:\t\t");
		report.append(getViewPackages().toString());
		report.append("\n\n");

		report.append("pages defined:\t\t\t");
		report.append(getSiteMap().getNodeCount());
		report.append("\n\n");

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
		if (undeclaredViewClasses.size() > 0) {
			report.append("  -- ");
			report.append(indentationErrors);
			report.append("\n");
		}
		report.append("\n\n");

		report.append("================================================================= ");

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

}
