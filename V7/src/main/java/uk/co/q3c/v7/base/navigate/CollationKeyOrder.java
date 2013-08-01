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

import java.util.Comparator;

/**
 * Comparator which can be used to sort SitemapNode by collation key order, based on
 * {@link SitemapNode#getCollationKey()()}. This enables sorting by Locale sensitive labels, as the collation key is set
 * to reflect the current locale
 */
public class CollationKeyOrder implements Comparator<SitemapNode> {

	@Override
	public int compare(SitemapNode o1, SitemapNode o2) {
		return o1.getCollationKey().compareTo(o2.getCollationKey());
	}

}
