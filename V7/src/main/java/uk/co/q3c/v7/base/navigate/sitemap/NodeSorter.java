/*
 * Copyright (C) 2014 David Sowerby
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

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.navigate.CollationKeyOrder;
import uk.co.q3c.v7.base.navigate.InsertionOrder;

public class NodeSorter {
	private static Logger log = LoggerFactory.getLogger(NodeSorter.class);
	private final List<SitemapNode> nodeList;
	private final boolean sorted;

	public NodeSorter(List<SitemapNode> nodeList, boolean sorted) {
		this.nodeList = nodeList;
		this.sorted = sorted;
	}

	public void sort() {
		if (sorted) {
			log.debug("'sorted' is true, sorting by collation key");
			Collections.sort(nodeList, new CollationKeyOrder());
		} else {
			log.debug("'sorted' is false, using insertion order");
			Collections.sort(nodeList, new InsertionOrder());
		}

	}

}
