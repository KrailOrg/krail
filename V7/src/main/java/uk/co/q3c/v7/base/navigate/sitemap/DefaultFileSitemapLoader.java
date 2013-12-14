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
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import uk.co.q3c.v7.base.navigate.StandardPageBuilder;
import uk.co.q3c.v7.base.navigate.StandardPageKey;
import uk.co.q3c.v7.base.navigate.URITracker;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.common.base.Strings;

public class DefaultFileSitemapLoader implements FileSitemapLoader {

	private static Logger log = LoggerFactory.getLogger(DefaultFileSitemapLoader.class);

	private enum SectionName {
		options, redirects, viewPackages, standardPageMapping, map;
	}

	private enum ValidOption {
		appendView, labelKeys, generatePublicHomePage, generateAuthenticationPages, generateRequestAccount, generateRequestAccountReset, systemAccountRoot, publicRoot, privateRoot
	}

	private final Sitemap sitemap;
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
	private final Collator collator;
	private final Translate translate;

	@Inject
	public DefaultFileSitemapLoader(StandardPageBuilder standardPageBuilder, CurrentLocale currentLocale,
			Translate translate, Sitemap sitemap) {
		super();
		this.standardPageBuilder = standardPageBuilder;
		this.collator = Collator.getInstance(currentLocale.getLocale());
		this.translate = translate;
		this.sitemap = sitemap;

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

		standardPageBuilder.setSitemap(sitemap);
		sections = new HashMap<>();
		labelClassNotI18N = false;
		labelClassNonExistent = false;
		labelClassMissing = true;
		parsed = false;
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

			log.info("Sitemap loaded successfully");
			log.debug(sitemap.toString());

		} else {
			log.warn("The site map source is missing these sections: {}", missingSections());
			log.error("Site map failed to process, see previous log warnings for details");
			sitemap.setErrors(errorSum());
		}

		endTime = DateTime.now();
		parsed = true;
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
				viewlessURIs.add("uri: \"" + sitemap.uri(node) + "\"");
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
		Collection<String> uris = sitemap.uris();
		for (String target : getRedirects().values()) {
			if (getRedirects().keySet().contains(target)) {
				redirectErrors.add("'" + target + "' cannot be both a redirect source and redirect target");
			}
			if (!uris.contains(target)) {
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

	@Override
	public void parse(File file, boolean firstLoad) {

		source = file.getAbsolutePath();
		sourceFile = file;
		log.info("Loading sitemap from {}", source);
		try {
			List<String> lines = FileUtils.readLines(file);
			processLines(lines);

		} catch (Exception e) {
			log.error("Unable to load site map", e);
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
				sitemap.setPrivateRoot(value);
				break;

			case publicRoot:
				sitemap.setPublicRoot(value);
				break;
			}

		} catch (Exception e) {
			log.warn("unrecognised option '{}' in site map", key);
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
				log.warn(labelKeys + " does not implement I18NKeys");
			}
		} catch (ClassNotFoundException e) {
			valid = false;
			labelClassNonExistent = true;
			log.warn(labelKeys + " does not exist on the classpath");
		}
		if (!valid) {
			log.warn(labelKeys + " is not a valid enum class for I18N labels");
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
			SitemapNode node = sitemap.append(uriTracker.uri());
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

	public void labelKeyForName(String labelKeyName, SitemapNode node) {
		// gets name from segment if necessary
		String keyName = keyName(labelKeyName, node);
		// could be null if invalid label keys given
		if (lkfn != null) {
			node.setLabelKey(lkfn.keyForName(keyName, missingEnums), translate, collator);
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

	//
	// private int lastIndent(String line) {
	// int index = 0;
	// while (line.charAt(index) == '-') {
	// index++;
	// }
	// return index;
	// }

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

	@Override
	public StringBuilder buildReport(StringBuilder report) {
		if (!parsed) {
			throw new SitemapException("Sitemap file must be parsed before report is run");
		}
		String df = "dd MMM YYYY HH:mm:SS";

		report.append("==================== Sitemap reader report ==================== \n\n");
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
		reportChunk(report, missingSections(), "missing sections",
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

		reportChunk(report, missingPages, "missing pages", "these MUST be defined");
		reportChunk(report, propertyErrors, "property errors", "should be key=value, spaces are ignored");
		reportChunk(report, missingEnums, "missing enum declarations",
				"you could just paste these into your enum declaration");
		reportChunk(report, invalidViewClasses, "invalid view classes", "invalid because they do not implement V7View");
		reportChunk(report, undeclaredViewClasses, "undeclared view classes",
				"these could not be found in the view packages declared in the [viewPackages] section");

		reportChunk(report, syntaxErrors, "syntax errors",
				"these have been ignored, and the system may work, but you may not get the intended result", true);
		reportChunk(report, redirectErrors, "redirect errors",
				"Redirect(s) causing an inconsistency and must be fixed", true);
		reportChunk(report, viewlessURIs, "viewless URIs", "these URIs have no view associated with them", true);
		reportChunk(report, standardPageErrors, "standard page errors",
				"incomplete or incorrect defintion of standard pages in [standardPageMapping]", true);

		if (warningSum() > 0) {
			report.append(" --------------- warnings ---------");
			report.append("\n\n");
			reportChunk(
					report,
					indentationErrors,
					"indentation errors",
					"line indentation should be <= 1 greater than the preceding line.  Parsing will still work but you may not get the intended result");
			reportChunk(report, unrecognisedOptions, "unrecognised options",
					"these have just been ignored, will do no harm");
		}

		report.append("================================================================= ");
		return report;
	}

	private void reportChunk(StringBuilder report, Set<String> source, String name, String explain) {
		reportChunk(report, source, name, explain, false);
	}

	private void reportChunk(StringBuilder report, Set<String> source, String name, String explain, boolean multiline) {
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
		return sitemap.getNodeCount();
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

	public String standardPageUri(StandardPageKey key) {
		return standardPages().get(key);
	}

	public Map<StandardPageKey, String> standardPages() {
		return sitemap.getStandardPages();
	}

	public Set<String> getMissingPages() {
		return missingPages;
	}

	public Set<String> getPropertyErrors() {
		return propertyErrors;
	}

	public Map<String, String> getRedirects() {
		return sitemap.getRedirects();
	}

	public Set<String> getViewlessURIs() {
		return viewlessURIs;
	}

	public Set<String> getDuplicateURIs() {
		return duplicateURIs;
	}

	public Set<String> redirectEntries() {
		Set<String> ss = new HashSet<>();
		for (Map.Entry<String, String> entry : getRedirects().entrySet()) {
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

	public Sitemap getSitemap() {

		return sitemap;
	}

	@Override
	public boolean load() {
		
		return false;
	}

	@Override
	public boolean overwriteExisting() {
		
		return false;
	}

}
