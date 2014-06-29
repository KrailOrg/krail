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
package uk.co.q3c.v7.base.navigate.sitemap.comparator;

import java.util.Comparator;

import uk.co.q3c.v7.base.navigate.sitemap.MasterSitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.LabelKey;

/**
 * Comparator which can be used to sort SitemapNode by insertion order, based on {@link MasterSitemapNode#getId()}
 */
public class PositionIndexDescending implements Comparator<UserSitemapNode>, UserSitemapSorter {
	/**
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(UserSitemapNode o1, UserSitemapNode o2) {
		return o2.getPositionIndex() - o1.getPositionIndex();
	}

	@Override
	public I18NKey<?> nameKey() {
		return LabelKey.Position_Index_Descending;
	}

}
