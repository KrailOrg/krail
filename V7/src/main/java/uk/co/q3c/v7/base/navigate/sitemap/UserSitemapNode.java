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

import java.text.CollationKey;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.Translate;

public class UserSitemapNode implements SitemapNode {

	private final MasterSitemapNode masterNode;
	private String label;
	private CollationKey collationKey;

	public UserSitemapNode(MasterSitemapNode masterNode) {
		super();
		this.masterNode = masterNode;
	}

	/**
	 * Updates the {@link #label} and {@link #collationKey} for the {@code locale}
	 */
	public void translate(Translate translate, Locale locale, Collator collator) {
		label = translate.from(masterNode.getLabelKey());
		collationKey = collator.getCollationKey(label);
	}

	public MasterSitemapNode getMasterNode() {
		return masterNode;
	}

	public String getLabel() {
		return label;
	}

	public CollationKey getCollationKey() {
		return collationKey;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setCollationKey(CollationKey collationKey) {
		this.collationKey = collationKey;
	}

	public int getId() {
		return masterNode.getId();
	}

	@Override
	public I18NKey<?> getLabelKey() {
		return masterNode.getLabelKey();
	}

	@Override
	public String getUriSegment() {
		return masterNode.getUriSegment();
	}

	@Override
	public Class<? extends V7View> getViewClass() {
		return masterNode.getViewClass();
	}

	@Override
	public PageAccessControl getPageAccessControl() {
		return masterNode.getPageAccessControl();
	}

	@Override
	public List<String> getRoles() {
		return masterNode.getRoles();
	}

	@Override
	public String toString() {
		return label;
	}

}
