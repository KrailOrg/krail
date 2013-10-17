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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.shiro.io.ResourceUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.navigate.LabelKeyForName;
import uk.co.q3c.v7.base.navigate.MapLineReader;
import uk.co.q3c.v7.base.navigate.MapLineRecord;
import uk.co.q3c.v7.base.navigate.StandardPageBuilder;
import uk.co.q3c.v7.base.navigate.StandardPageKey;
import uk.co.q3c.v7.base.navigate.URITracker;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.ViewsProvider;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NKey;

import com.google.common.base.Strings;

public class TextReaderSitemapProvider implements SitemapProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(TextReaderSitemapProvider.class);

	private enum SectionName {
		options,
		redirects,
		viewPackages,
		standardPageMapping,
		map;
	}

	private enum ValidOption {
		appendView,
		labelKeys,
		generatePublicHomePage,
		generateAuthenticationPages,
		generateRequestAccount,
		generateRequestAccountReset,
		systemAccountRoot,
		publicRoot,
		privateRoot
	}

	private Sitemap sitemap;
	private int commentLines;
	private int blankLines;
	private Map<SectionName, List<String>> sections;

	private SectionName currentSection;

	private Class<? extends Enum<?>> labelKeysClass;
	// options
	private boolean appendView;
	private String labelKeys;

	private Set<String> missingEnums;
	private Set<String> invalidViewClasses;
	private Set<String> undeclaredViewClasses;
	private Set<String> indentationErrors;
	private Set<String> missingPages;
	private Set<String> propertyErrors;
	private Set<String> duplicateURIs;
	private Set<String> viewlessURIs;
	private Set<String> unrecognisedOptions;
	private Set<String> redirectErrors;
	private Set<String> syntaxErrors;
	private Set<String> standardPageErrors;

	// messages to go in the report, for info only
	private Set<String> infoMessages;

	private StringBuilder report;
	private DateTime startTime;
	private DateTime endTime;
	private String source;
	private boolean parsed = false;
	private boolean labelClassNotI18N;
	private boolean labelClassNonExistent;
	private boolean labelClassMissing = true;
	private String labelClassName;

	private File sourceFile;
	private final StandardPageBuilder standardPageBuilder;
	private LabelKeyForName lkfn;
	private final CurrentLocale currentLocale;
	private final ViewsProvider viewsProvider;

	@Inject
	public TextReaderSitemapProvider(StandardPageBuilder standardPageBuilder, CurrentLocale currentLocale, ViewsProvider viewsProvider) {
		super();
		this.standardPageBuilder = standardPageBuilder;
		this.currentLocale = currentLocale;
		this.viewsProvider = viewsProvider;
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
		viewlessURIs = new HashSet<>();
		duplicateURIs = new HashSet<>();
		unrecognisedOptions = new HashSet<>();
		redirectErrors = new HashSet<>();
		infoMessages = new HashSet<>();
		syntaxErrors = new HashSet<>();
		standardPageErrors = new HashSet<>();

		sitemap = new Sitemap(viewsProvider);
		standardPageBuilder.setSitemap(sitemap);
		sections = new HashMap<>();
		labelClassNotI18N = false;
		labelClassNonExistent = false;
		labelClassMissing = true;
		parsed = false;
	}

	@Override
	public void parse(String resourcePath) {
		source = resourcePath;
		LOGGER.info("Loading sitemap from {}", source);
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
			LOGGER.error("Unable to load site map ", e);
			String report = (parsed) ? getReport().toString() : "failed to parse input, unable to generate report";
			LOGGER.debug(report);
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
			processOptions();
			processRedirects();
			// processStandardPages();
			generateStandardPages();
			processMap();
			validateRedirects();
			checkLabelKeys();
			checkViews();
			sitemap.setErrors(errorSum());

			LOGGER.info("Sitemap loaded successfully");
			LOGGER.debug(sitemap.toString());

		} else {
			LOGGER.warn("The site map source is missing these sections: {}", missingSections());
			LOGGER.error("Site map failed to process, see previous log warnings for details");
			sitemap.setErrors(errorSum());
		}

		endTime = DateTime.now();
		parsed = true;
		sitemap.setReport(getReport().toString());
	}

	/**
	 * indentation errors and + unrecognisedOptions are given as warnings rather than errors
	 * 
	 * @return
	 */
	private int errorSum() {
		int c = missingSections().size() + missingEnums.size() + invalidViewClasses.size();
		c += undeclaredViewClasses.size() + missingPages.size() + propertyErrors.size();
		c += viewlessURIs.size() + duplicateURIs.size() + redirectErrors.size() + syntaxErrors.size()
				+ standardPageErrors.size();

		if (getViewPackages() == null || getViewPackages().isEmpty()) {
			c++;
		}
		if (labelClassNotI18N) {
			c++;
		}
		if (labelClassNonExistent) {
			c++;
		}
		if (labelClassMissing) {
			c++;
		}
		return c;
	}

	private int warningSum() {
		int c = unrecognisedOptions.size() + indentationErrors.size();
		return c;
	}

	/**
	 * Looks for any URIs without views and captures them in {@link #viewlessURIs} for reporting
	 */
	private void checkViews() {
		for (SitemapNode node : sitemap.getAllNodes()) {
			if (node.getViewClass() == null) {
				viewlessURIs.add("uri: \"" + node.getUri() + "\"");
			}
		}

	}

	private void checkLabelKeys() {
		for (SitemapNode node : sitemap.getAllNodes()) {
			if (node.getLabelKey() == null) {
				labelKeyForName(null, node);
			}
		}
	}

	/**
	 * Ensure that redirection targets exist, and that no loops can be created
	 */
	private void validateRedirects() {
		for (String target : sitemap.getRedirects().values()) {
			if (sitemap.getRedirects().keySet().contains(target)) {
				redirectErrors.add("'" + target + "' cannot be both a redirect source and redirect target");
			}
			if (!sitemap.hasUri(target)) {
				redirectErrors.add("'" + target + "' cannot be a redirect target, it has not been defined as a page");

			}
		}

	}

	/**
	 * Generates the standard pages according to the settings of options. See
	 * https://sites.google.com/site/q3cjava/sitemap#TOC-options-
	 */
	private void generateStandardPages() {
		standardPageBuilder.setLabelKeysClass(labelKeysClass);
		standardPageBuilder.setMissingEnums(missingEnums);
		standardPageBuilder.setStandardPageErrors(standardPageErrors);
		standardPageBuilder.generateStandardPages();
	}

	// /**
	// * Standard pages are added after the page map has been built. This may cause a duplication of urls - if so, that
	// is
	// * captured in {@link #duplicateURLs}. As the map is built from the standard page URLs, it may also cause
	// * intermediate nodes to have no View assigned. This is checked and captured in {@link #viewlessURLs}
	// */
	// private void processStandardPages() {
	//
	// List<String> lines = sections.get(SectionName.standardPageMapping);
	// int i = 1;
	//
	// for (String line : lines) {
	//
	// StandardPageKey pageKey = null;
	// String toUrl = null;
	// String viewName = null;
	//
	// if (!line.contains("=")) {
	// propertyErrors.add("Property must contain an '=' sign at line "
	// + linenum(SectionName.standardPageMapping, i));
	// } else {
	// String[] pair = StringUtils.split(line, "=");
	// String pageKeyName = pair[0].trim();
	//
	// try {
	// pageKey = StandardPageKey.valueOf(pageKeyName);
	// if (pair.length > 1) {
	// if (pair[1].contains(":")) {
	// String[] urlView = StringUtils.split(pair[1], ":");
	// if (pair[1].startsWith(":")) {
	// toUrl = "";
	// viewName = pair[1].replace(":", "");
	// } else {
	// toUrl = urlView[0].trim();
	// if (urlView.length > 1) {
	// viewName = urlView[1].trim();
	// }
	// }
	//
	// } else {
	// toUrl = pair[1].trim();
	// }
	//
	// standardPages().put(pageKey, toUrl);
	// } else {
	// standardPages().put(pageKey, "");
	// }
	// } catch (Exception e) {
	// propertyErrors.add(pageKeyName + " is not a valid " + StandardPageKey.class.getSimpleName()
	// + linenum(SectionName.standardPageMapping, i));
	//
	// }
	//
	// }
	//
	// // we now have defined a node, add it to the map
	// // but only if url is there
	// if (toUrl != null) {
	// SitemapNode node = sitemap.append(toUrl);
	// node.setLabelKey(pageKey);
	// // and set the view
	// findView(node, node.getUrlSegment(), viewName);
	// }
	// i++;
	// }
	//
	// // check for missing standard pages
	// for (StandardPageKey spk : StandardPageKey.values()) {
	// if (!standardPages().containsKey(spk)) {
	// missingPages.add(spk.name());
	// }
	// }
	//
	// }

	// private String linenum(SectionName sectionName, int i) {
	// return "at line " + i + " in the " + sectionName + " section";
	// }

	@Override
	public void parse(File file) {

		source = file.getAbsolutePath();
		sourceFile = file;
		LOGGER.info("Loading sitemap from {}", source);
		try {
			List<String> lines = FileUtils.readLines(file);
			processLines(lines);

		} catch (Exception e) {
			LOGGER.error("Unable to load site map", e);
			String report = (parsed) ? getReport().toString() : "failed to parse input, unable to generate report";
			LOGGER.debug(report);
		}
	}

	private void processRedirects() {
		List<String> sectionLines = sections.get(SectionName.redirects);
		for (String line : sectionLines) {
			// if starts with ':' then f==""
			// split the line on ':'

			if (line.startsWith(":")) {
				sitemap.addRedirect("", line.replace(":", "").trim());
			} else {
				String[] pair = null;
				pair = StringUtils.split(line, ":");
				String f = pair[0].trim();
				String t = (pair.length > 1) ? pair[1].trim() : "";
				sitemap.addRedirect(f, t);
			}
		}
	}

	private void processOptions() {
		List<String> sectionLines = sections.get(SectionName.options);
		String sectionName = SectionName.options.name();
		int i = 1;
		for (String line : sectionLines) {
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
					setOption(key, value);
				}
			}
			// increment line count
			i++;
		}
	}

	private void setOption(String key, String value) {

		try {
			ValidOption k = ValidOption.valueOf(key);
			switch (k) {
			case appendView:
				appendView = "true".equals(value);
				break;
			case generateAuthenticationPages:
				standardPageBuilder.setGenerateAuthenticationPages("true".equals(value));
				break;
			case generatePublicHomePage:
				standardPageBuilder.setGeneratePublicHomePage("true".equals(value));
				break;
			case generateRequestAccount:
				standardPageBuilder.setGenerateRequestAccount("true".equals(value));
				break;
			case generateRequestAccountReset:
				standardPageBuilder.setGenerateRequestAccountReset("true".equals(value));
				break;
			case labelKeys:
				labelKeys = value;
				if (!Strings.isNullOrEmpty(value)) {
					labelClassMissing = false;
					labelClassName = value;
					validateLabelKeys();
				}
				break;
			case systemAccountRoot:
				setSystemAccountRoot(value);
				break;
			case privateRoot:
				StandardPageKey.Private_Home.setUri(value);
				break;

			case publicRoot:
				StandardPageKey.Public_Home.setUri(value);
				break;
			}

		} catch (Exception e) {
			LOGGER.warn("unrecognised option '{}' in site map", key);
			unrecognisedOptions.add(key);
		}

	}

	private void setSystemAccountRoot(String systemAccountRoot) {
		standardPageBuilder.setSystemAccountRoot(systemAccountRoot);

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
			Class<I18NKey> i18nClass = I18NKey.class;
			if (!i18nClass.isAssignableFrom(requestedLabelKeysClass)) {
				valid = false;
				labelClassNotI18N = true;
				LOGGER.warn(labelKeys + " does not implement I18NKeys");
			}
		} catch (ClassNotFoundException e) {
			valid = false;
			labelClassNonExistent = true;
			LOGGER.warn(labelKeys + " does not exist on the classpath");
		}
		if (!valid) {
			LOGGER.warn(labelKeys + " is not a valid enum class for I18N labels");
			this.labelClassNotI18N = true;
		} else {
			labelKeysClass = (Class<? extends Enum<?>>) requestedLabelKeysClass;
			lkfn = new LabelKeyForName(labelKeysClass);
		}
	}

	public List<String> getViewPackages() {
		return sections.get(SectionName.viewPackages);
	}

	private void processMap() {
		URITracker uriTracker = new URITracker();
		MapLineReader reader = new MapLineReader();
		List<String> sectionLines = sections.get(SectionName.map);
		int lineIndex = 1;
		int currentIndent = 0;
		for (String line : sectionLines) {
			MapLineRecord lineRecord = reader.processLine(lineIndex, line, syntaxErrors, indentationErrors,
					currentIndent);
			uriTracker.track(lineRecord.getIndentLevel(), lineRecord.getSegment());
			SitemapNode node = sitemap.addNode(uriTracker.uri());
			// if node is a standard page do not overwrite it
			if (node.getLabelKey() instanceof StandardPageKey) {
				// warning
			} else {
				node.setUriSegment(lineRecord.getSegment());
				findView(node, lineRecord.getSegment(), lineRecord.getViewName());
				labelKeyForName(lineRecord.getKeyName(), node);
			}
			currentIndent = lineRecord.getIndentLevel();
			lineIndex++;
		}
	}

	// private void processMap() {
	// List<String> sectionLines = sections.get(SectionName.map);
	// int i = 0;
	// SitemapNode currentNode = null;
	// int currentLevel = 0;
	// for (String line : sectionLines) {
	// if (line.startsWith("-")) {
	// int treeLevel = lastIndent(line);
	// int viewStart = line.indexOf(":");
	// int labelStart = line.indexOf("~");
	// String segment = null;
	// String view = null;
	// String labelKeyName = null;
	// if ((labelStart > 0) && (viewStart > 0)) {
	// if (viewStart < labelStart) {
	// segment = line.substring(treeLevel, viewStart);
	// view = line.substring(viewStart + 1, labelStart);
	// labelKeyName = line.substring(labelStart + 1);
	// } else {
	// segment = line.substring(treeLevel, labelStart);
	// labelKeyName = line.substring(labelStart + 1, viewStart);
	// view = line.substring(viewStart + 1);
	// }
	// } else {
	// // only label
	// if (labelStart > 0) {
	// segment = line.substring(treeLevel, labelStart);
	// labelKeyName = line.substring(labelStart + 1);
	// }// only view
	// else if (viewStart > 0) {
	// segment = line.substring(treeLevel, viewStart);
	// view = line.substring(viewStart + 1);
	// }
	// // only segment
	// else {
	// segment = line.substring(treeLevel);
	// }
	// }
	//
	// // segment has been set, view & label may be null
	// SitemapNode node = new SitemapNode();
	// node.setUriSegment(segment);
	//
	// // do structure before labels
	// // labels are not needed for redirected pages
	// // but we cannot get full URI until structure done
	//
	// // add the node
	// if (treeLevel == 1) {
	// // at level 1 each becomes a 'root' (technically the site
	// // tree is a forest)
	// sitemap.addNode(node);
	// currentNode = node;
	// currentLevel = treeLevel;
	// } else {
	// // if indent going back up tree, walk up from current node
	// // to the parent level needed
	// if (treeLevel < currentLevel) {
	// int retraceLevels = currentLevel - treeLevel;
	// for (int k = 1; k <= retraceLevels; k++) {
	// currentNode = sitemap.getParent(currentNode);
	// currentLevel--;
	// }
	// sitemap.addChild(currentNode, node);
	// currentNode = node;
	// currentLevel++;
	// } else if (treeLevel == currentLevel) {
	// SitemapNode parentNode = sitemap.getParent(currentNode);
	// sitemap.addChild(parentNode, node);
	// } else if (treeLevel > currentLevel) {
	// if (treeLevel - currentLevel > 1) {
	// log.warn(
	// "indentation for {} line is too great.  It should be a maximum of 1 greater than its predecessor",
	// node.getUriSegment());
	// indentationErrors.add(node.getUriSegment());
	// }
	// sitemap.addChild(currentNode, node);
	// currentNode = node;
	// currentLevel++;
	// }
	//
	// }
	//
	// String uri = sitemap.uri(node);
	// // do the view
	// if (!getRedirects().containsKey(uri)) {
	// findView(node, segment, view);
	// }
	//
	// // do the label
	// labelKeyForName(labelKeyName, node);
	//
	// } else {
	// String msg = "line in map must start with a'-', line " + i;
	// log.warn(msg);
	// syntaxErrors.add(msg);
	// }
	// }
	//
	// }

	public void labelKeyForName(String labelKeyName, SitemapNode node) {
		// gets name from segment if necessary
		String keyName = keyName(labelKeyName, node);
		// could be null if invalid label keys given
		if (lkfn != null) {
			node.setLabelKey(lkfn.keyForName(keyName, missingEnums), currentLocale.getLocale());
		} else {
			missingEnums.add(keyName);
		}
	}

	public String keyName(String labelKeyName, SitemapNode node) {
		String keyName = labelKeyName;
		if (keyName == null) {
			keyName = node.getUriSegment().replace("-", " ");
			keyName = keyName.replace("_", " ");
			keyName = WordUtils.capitalize(keyName);
			// hyphen not valid in enum, but may be used in segment
			keyName = keyName.replace(" ", "_");
			return keyName;
		} else {
			return keyName;
		}
	}

	/**
	 * Updates the node with the required view. If {@link #appendView} is true the 'View' is appended to the
	 * {@code viewName} before attempting to find its class declaration. If no class can be found, {@code viewName} is
	 * added to {@link #undeclaredViewClasses}
	 * 
	 * @param node
	 * @param segment
	 * @param viewName
	 */
	@SuppressWarnings("unchecked")
	private void findView(SitemapNode node, String segment, String viewName) {
		// if view is null use the segment
		if (viewName == null) {
			viewName = StringUtils.capitalize(segment);
		}

		// user option whether to append 'View' or not
		if (appendView) {
			viewName = viewName + "View";
		}
		Class<?> viewClass = null;
		// try and find the view in the specified packages
		for (String pkg : getViewPackages()) {
			String fullViewName = pkg + "." + viewName;
			try {
				viewClass = Class.forName(fullViewName);
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
		if (viewClass == null) {
			undeclaredViewClasses.add(viewName);
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
				LOGGER.warn("section requires closing ']' at line " + linenum);
			} else {
				String sectionName = strippedLine.substring(1, strippedLine.length() - 1);

				List<String> section = new ArrayList<>();
				try {
					SectionName key = SectionName.valueOf(sectionName);
					currentSection = key;
					sections.put(key, section);
				} catch (IllegalArgumentException iae) {
					LOGGER.warn(
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
	public Sitemap getSitemap() {
		return sitemap;
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
		report.append("parsing source from:\t\t");
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

		report.append("pages defined:\t\t\t");
		report.append(getPagesDefined());
		report.append("\n\n");

		report.append("redirects:\t\t\t");
		for (String entry : redirectEntries()) {
			report.append("\n -- ");
			report.append(entry);
		}
		report.append("\n\n");

		if (getViewPackages() != null) {
			report.append("view packages declared:\t\t");
			report.append(getViewPackages().toString());
			report.append("\n\n");
		}

		if (!(labelClassMissing || labelClassNonExistent || labelClassNotI18N)) {
			report.append("I18N Label class:\t\t");
			report.append(labelClassName);
			report.append("\n\n");
		}

		report.append("parsing status:  ");
		if (sitemap.hasErrors()) {
			report.append("FAILED");
		} else {
			report.append("PASSED");
		}

		report.append("\n\n");

		if (sitemap.hasErrors()) {
			report.append(" -------- errors --------\n\n");
		}
		reportChunk(missingSections(), "missing sections",
				"if any section is missing, parsing will fail, and results will be indeterminate - correct this first");

		if (getViewPackages() == null) {
			report.append("No view packages declared - site map will not build without them\n\n");
		}

		if (labelClassMissing || labelClassNonExistent || labelClassNotI18N) {
			report.append("I18N Label class:\t\t");
			if (labelClassMissing) {
				report.append(" has not been declared, you need to define it using the 'labelKeys=' property in [options]");
				report.append("\n\n");
			} else {
				if (labelClassNonExistent) {
					report.append(labelClassName);
					report.append(" has been declared but does not exist on the classpath");
					report.append("\n\n");
				} else {
					if (labelClassNotI18N) {
						report.append(labelClassName);
						report.append(" has been declared, is on the classpath, but does not implement I18NKeys, as it should");
						report.append("\n\n");
					}
				}
			}
		}

		reportChunk(missingPages, "missing pages", "these MUST be defined");
		reportChunk(propertyErrors, "property errors", "should be key=value, spaces are ignored");
		reportChunk(missingEnums, "missing enum declarations", "you could just paste these into your enum declaration");
		reportChunk(invalidViewClasses, "invalid view classes", "invalid because they do not implement V7View");
		reportChunk(undeclaredViewClasses, "undeclared view classes",
				"these could not be found in the view packages declared in the [viewPackages] section");

		reportChunk(syntaxErrors, "syntax errors",
				"these have been ignored, and the system may work, but you may not get the intended result", true);
		reportChunk(redirectErrors, "redirect errors", "Redirect(s) causing an inconsistency and must be fixed", true);
		reportChunk(viewlessURIs, "viewless URIs", "these URIs have no view associated with them", true);
		reportChunk(standardPageErrors, "standard page errors",
				"incomplete or incorrect defintion of standard pages in [standardPageMapping]", true);

		if (warningSum() > 0) {
			report.append(" --------------- warnings ---------");
			report.append("\n\n");
			reportChunk(
					indentationErrors,
					"indentation errors",
					"line indentation should be <= 1 greater than the preceding line.  Parsing will still work but you may not get the intended result");
			reportChunk(unrecognisedOptions, "unrecognised options", "these have just been ignored, will do no harm");
		}

		report.append("================================================================= ");

	}

	private void reportChunk(Set<String> source, String name, String explain) {
		reportChunk(source, name, explain, false);
	}

	private void reportChunk(Set<String> source, String name, String explain, boolean multiline) {
		if (source.size() > 0) {
			report.append(name);
			report.append("\t\t");
			report.append(source.size());
			report.append("  (");
			report.append(explain);
			report.append(")\n");
			if (source.size() > 0) {
				if (multiline) {
					for (String s : source) {
						report.append("\t");
						report.append(s);
						report.append("\n");
					}
				} else {
					report.append("  -- ");
					report.append(source);
					report.append("\n");
				}
			}
			report.append("\n");
		}
	}

	public int getPagesDefined() {
		return getSitemap().getNodeCount();
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

	public boolean isLabelClassNotI18N() {
		return labelClassNotI18N;
	}

	public boolean isLabelClassNonExistent() {
		return labelClassNonExistent;
	}

	public Set<String> getIndentationErrors() {
		return indentationErrors;
	}

	/**
	 * If sitemap has already been parsed, returns it. If not, loads input and parses it. The source depends on what has
	 * been set in {@link #source} and {@link #sourceFile}. The first available source is taken from the following
	 * order:
	 * <ol>
	 * <li>source
	 * <li>sourceFile
	 * <li>default (which is "classpath:sitemap.properties")
	 * 
	 * @see uk.co.q3c.v7.base.navigate.sitemap.SitemapProvider#get()
	 */

	@Override
	public Sitemap get() {
		if (parsed) {
			return getSitemap();
		}
		if (source != null) {
			parse(source);
		} else {
			if (sourceFile != null) {
				parse(sourceFile);
			} else {
				parse("classpath:sitemap.properties");
			}
		}

		return getSitemap();
	}

	public Set<String> getMissingPages() {
		return missingPages;
	}

	public Set<String> getPropertyErrors() {
		return propertyErrors;
	}

	public Set<String> getViewlessURIs() {
		return viewlessURIs;
	}

	public Set<String> getDuplicateURIs() {
		return duplicateURIs;
	}

	public Set<String> redirectEntries() {
		Set<String> ss = new HashSet<>();
		for (Map.Entry<String, String> entry : sitemap.getRedirects().entrySet()) {
			ss.add(entry.getKey() + ":" + entry.getValue());
		}
		return ss;
	}

	public boolean isLabelClassMissing() {
		return labelClassMissing;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	/**
	 * 
	 * Sets the source of the sitemap input. See also {@link #setSource(String)} , and {@link #get()} for loading order.
	 * 
	 * @param sourceFile
	 */
	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getSource() {
		return source;
	}

	/**
	 * Sets the source of the sitemap input. Must be in the format of
	 * {@link ResourceUtils#getInputStreamForPath(String)}. See also {@link #setSourceFile(File)}, and {@link #get()}
	 * for loading order.
	 * 
	 * @param source
	 */
	public void setSource(String source) {
		this.source = source;
	}

	public Set<String> getRedirectErrors() {
		return redirectErrors;
	}

	public Set<String> getSyntaxErrors() {
		return syntaxErrors;
	}

	public Set<String> getInfoMessages() {
		return infoMessages;
	}

	public boolean isGeneratePublicHomePage() {
		return standardPageBuilder.isGeneratePublicHomePage();
	}

	public boolean isGenerateAuthenticationPages() {
		return standardPageBuilder.isGenerateAuthenticationPages();
	}

	public boolean isGenerateRequestAccount() {
		return standardPageBuilder.isGenerateRequestAccount();
	}

	public boolean isGenerateRequestAccountReset() {
		return standardPageBuilder.isGenerateRequestAccountReset();
	}

	public String getSystemAccountUri() {
		return standardPageBuilder.getSystemAccountRoot();
	}

}
