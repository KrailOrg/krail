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

import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.BasicForest;
import uk.co.q3c.util.TreeCopier;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScoped;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.opt.UserOptionProperty;
import uk.co.q3c.v7.base.user.status.UserStatusListener;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.LocaleChangeListener;
import uk.co.q3c.v7.i18n.Translate;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

/**
 * The {@link MasterSitemap} provides the overall structure of the site, and is Singleton scoped. This class refines
 * that by presenting only those pages that the user is authorised to see, and is therefore {@link VaadinSessionScoped}.
 * It also maintains locale-aware labels and sort order, so that the navigation components are presented to the user in
 * the language and sort order of their choice.
 *
 * @author David Sowerby
 * @date 17 May 2014
 */
@VaadinSessionScoped
public class UserSitemap extends Sitemap<UserSitemapNode> implements LocaleChangeListener, UserStatusListener {
	private static Logger log = LoggerFactory.getLogger(UserSitemap.class);

	private final MasterSitemap masterSitemap;

	private final UserOption userOption;

	private boolean sorted;

	private final Translate translate;

	private final UserSitemapNodeCreator nodeCreator;

	@Inject
	public UserSitemap(MasterSitemap masterSitemap, UserOption userOption, Translate translate,
			URIFragmentHandler uriHandler, CurrentLocale currentLocale, UserSitemapNodeCreator nodeCreator) {
		super(uriHandler);
		this.masterSitemap = masterSitemap;
		this.userOption = userOption;
		this.translate = translate;
		this.nodeCreator = nodeCreator;
		currentLocale.addListener(this);
		build();
	}

	/**
	 * Iterates through contained nodes and resets the label and collation key properties to reflect a change in
	 * {@link CurrentLocale}. There is no need to reload all the nodes, no change of page authorisation is dealt with
	 * her - that is handled by {@link UserStatusListener#userStatusChanged()}
	 */
	@Override
	public synchronized void localeChanged(Locale locale) {
		log.debug("responding to locale change to {}", locale);
		BasicForest<UserSitemapNode> oldForest = forest;
		forest = new BasicForest<>();
		List<UserSitemapNode> nodeList = oldForest.getRoots();
		Collator collator = translate.collator();
		translate(oldForest, collator, nodeList);
	}

	private void translate(BasicForest<UserSitemapNode> oldForest, Collator collator, List<UserSitemapNode> nodeList) {

		for (UserSitemapNode userNode : nodeList) {
			userNode.setLabel(translate.from(userNode.getMasterNode().getLabelKey()));
			userNode.setCollationKey(collator.getCollationKey(userNode.getLabel()));
		}
		// sort the list into the required order, determined by 'sorted'
		new NodeSorter(nodeList, sorted).sort();

		// then add them to the forest
		for (UserSitemapNode userNode : nodeList) {
			forest.addNode(userNode);
			List<UserSitemapNode> subNodeList = oldForest.getChildren(userNode);
			// drill down to next level
			translate(oldForest, collator, subNodeList);
		}
	}

	private void build() {
		log.debug("building or rebuilding the map");
		forest = new BasicForest<>();
		sorted = userOption.getOptionAsBoolean(this.getClass().getSimpleName(), UserOptionProperty.SORTED, true);
		loadNodes();
		loadStandardPages();
		buildUriMap();
		loadRedirects();
	}

	private void buildUriMap() {
		uriMap.clear();
		for (UserSitemapNode node : forest.getAllNodes()) {
			uriMap.put(uri(node), node);
		}

	}

	private void loadStandardPages() {
		ImmutableMap<StandardPageKey, MasterSitemapNode> masterStdPages = masterSitemap.getStandardPages();
		for (StandardPageKey key : masterStdPages.keySet()) {
			MasterSitemapNode masterNode = masterSitemap.getStandardPages().get(key);
			UserSitemapNode userNode = nodeCreator.create(masterNode);
			if (userNode != null) {
				standardPages.put(key, userNode);
			}
		}

	}

	/**
	 * Copies the redirects from the {@link MasterSitemap},. but only adds it to this {@link UserSitemap} if the target
	 * exists in this sitemap.
	 */
	private void loadRedirects() {
		redirects.clear();
		for (Entry<String, String> entry : masterSitemap.getRedirects().entrySet()) {
			// only add the entry of the target exists
			if (hasUri(entry.getValue())) {
				redirects.put(entry.getKey(), entry.getValue());
			}
		}

	}

	private void loadNodes() {
		TreeCopier<MasterSitemapNode, UserSitemapNode> copier = new TreeCopier<>(masterSitemap.getForest(),
				this.getForest());
		copier.setNodeCreator(nodeCreator);
		copier.copy();

	}

	@Override
	public synchronized void userStatusChanged() {
		build();
	}

	/**
	 * Returns the userNode which contains {@code masterNode}. Note that this method is not very efficient for larger
	 * instances, it has to scan the {@link UserSitemap} until it finds a match. Returns null if no match found (and
	 * will have scanned the entire {@link UserSitemap}
	 *
	 * @param masterNode
	 * @return
	 */
	public UserSitemapNode userNodeFor(SitemapNode masterNode) {
		for (UserSitemapNode candidate : getAllNodes()) {
			if (candidate.getMasterNode() == masterNode) {
				return candidate;
			}
		}
		return null;
	}

	/**
	 * The {@link UserSitemap} never creates a node this way
	 */
	@Override
	protected UserSitemapNode createNode(String segment) {
		return null;
	}

	/**
	 * Does nothing in the {@link UserSitemap}
	 */
	@Override
	protected void setId(UserSitemapNode node) {

	}
}
