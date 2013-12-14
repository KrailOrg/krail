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

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a line from the [map] section during the processing of sitemap.properties, and returns a {@link MapLineRecord}
 * of the constituent parts. Records syntax errors and other failures for the {@link FileSitemapLoader} to provide a
 * report
 * 
 * @author David Sowerby 5 Jul 2013
 * 
 */

public class MapLineReader {
	private static Logger log = LoggerFactory.getLogger(MapLineReader.class);
	public static final String NO_HYPHEN = "Line must start with hyphen(s) to indicate indent level, line ";
	public static final String VIEW_FIRST = "View name must precede label key name at line ";
	private int index = 0;
	private String line;
	private MapLineRecord lineRecord;

	public MapLineRecord processLine(int lineIndex, String line, Set<String> syntaxErrors,
			Set<String> indentationErrors, int currentIndent) {
		index = 0;
		this.line = line;
		lineRecord = new MapLineRecord();
		StringBuilder buf = new StringBuilder();

		int keyIndex = line.indexOf('~');
		int viewIndex = line.indexOf(':');

		if ((keyIndex > 0) && (keyIndex < viewIndex)) {
			syntaxErrors.add(VIEW_FIRST + lineIndex);
			return lineRecord;
		}

		// leading spaces
		spaces();

		int indent = 0;
		// hyphen indent
		while (line.charAt(index) == '-') {
			index++;
			indent++;
		}

		lineRecord.setIndentLevel(indent);

		// no hyphens at start, cannot establish indent level
		if (indent == 0) {
			syntaxErrors.add(NO_HYPHEN + lineIndex);
			return lineRecord;
		}

		spaces();
		// processing segment
		while ((index < line.length() && (line.charAt(index) != ' ') && (line.charAt(index) != ':') && (line
				.charAt(index) != '~'))) {
			buf.append(line.charAt(index));
			index++;
		}
		lineRecord.setSegment(buf.toString());

		// has to be done here, because we don't know what the segment is until now
		if (indent - currentIndent > 1) {
			indentationErrors.add(lineRecord.getSegment());
		}
		spaces();

		// may be segment only
		if (index < line.length()) {
			char c = line.charAt(index);
			// processing view
			if (c == ':') {
				index++;
				spaces();
				buf = new StringBuilder();
				while ((index < line.length()) && (line.charAt(index) != ' ') && (line.charAt(index) != '~')) {
					buf.append(line.charAt(index));
					index++;
				}
				lineRecord.setViewName(buf.toString());
				spaces();

				// may be no label key
				if (index < line.length()) {
					c = line.charAt(index);
					if (c == '~') {
						index++;
						spaces();
						label();
					}
				}

			} else {
				// processing label without view
				if (c == '~') {
					index++;
					spaces();
					label();
				}
			}
		}
		return lineRecord;
	}

	private void spaces() {
		// spaces
		while ((index < line.length()) && (line.charAt(index) == ' ')) {
			index++;
		}
	};

	private void label() {
		StringBuilder buf = new StringBuilder();
		while ((index < line.length()) && (line.charAt(index) != ' ')) {
			buf.append(line.charAt(index));
			index++;
		}
		lineRecord.setKeyName(buf.toString());
	}

}
