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

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a line from the [map] section during the processing of sitemap.properties, and returns a {@link MapLineRecord}
 * of the constituent parts. Records syntax errors and other failures for the {@link SitemapProvider} to provide a
 * report
 * 
 * @author David Sowerby 5 Jul 2013
 * 
 */

public class MapLineReader {
	private static Logger log = LoggerFactory.getLogger(MapLineReader.class);

	public MapLineRecord processLine(int lineIndex, String line, Set<String> syntaxErrors) {
		if (!line.startsWith("-")) {
			String msg = "line in map must start with a'-', line " + lineIndex;
			log.warn(msg);
			syntaxErrors.add(msg);
			return null;
		}
		MapLineRecord linerecord = new MapLineRecord();
		linerecord.setIndentLevel(lastIndent(line));
		int viewStart = line.indexOf(":");
		int labelStart = line.indexOf("~");

		boolean hasView = viewStart > -1;
		boolean hasLabel = labelStart > -1;

		int segmentEnd = 0;
		if (hasView) {
			segmentEnd = viewStart - 1;
		} else if (hasLabel) {
			segmentEnd = labelStart - 1;
		} else {
			segmentEnd = line.length() - 1;
		}

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
		SitemapNode node = new SitemapNode();
		node.setUriSegment(segment);

		// do structure before labels
		// labels are not needed for redirected pages
		// but we cannot get full URI until structure done

		// add the node
		if (treeLevel == 1) {
			// at level 1 each becomes a 'root' (technically the site
			// tree is a forest)
			sitemap.addNode(node);
			currentNode = node;
			currentLevel = treeLevel;
		} else {
			// if indent going back up tree, walk up from current node
			// to the parent level needed
			if (treeLevel < currentLevel) {
				int retraceLevels = currentLevel - treeLevel;
				for (int k = 1; k <= retraceLevels; k++) {
					currentNode = sitemap.getParent(currentNode);
					currentLevel--;
				}
				sitemap.addChild(currentNode, node);
				currentNode = node;
				currentLevel++;
			} else if (treeLevel == currentLevel) {
				SitemapNode parentNode = sitemap.getParent(currentNode);
				sitemap.addChild(parentNode, node);
			} else if (treeLevel > currentLevel) {
				if (treeLevel - currentLevel > 1) {
					log.warn(
							"indentation for {} line is too great.  It should be a maximum of 1 greater than its predecessor",
							node.getUriSegment());
					indentationErrors.add(node.getUriSegment());
				}
				sitemap.addChild(currentNode, node);
				currentNode = node;
				currentLevel++;
			}

		}

		String uri = sitemap.uri(node);
		// do the view
		if (!getRedirects().containsKey(uri)) {
			findView(node, segment, view);
		}

		// do the label
		labelKeyForName(labelKeyName, node);

	}

	private int lastIndent(String line) {
		int index = 0;
		while (line.charAt(index) == '-') {
			index++;
		}
		return index;
	}

}
