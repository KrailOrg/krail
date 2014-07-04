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
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.util.NodeModifier;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScoped;
import uk.co.q3c.v7.base.shiro.PageAccessController;
import uk.co.q3c.v7.base.shiro.SubjectProvider;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;

public class UserSitemapNodeModifier implements NodeModifier<MasterSitemapNode, UserSitemapNode> {
	private static Logger log = LoggerFactory.getLogger(UserSitemapNodeModifier.class);

	private final SubjectProvider subjectProvider;
	private final MasterSitemap masterSitemap;
	private final PageAccessController pageAccessController;
	private final Collator collator;
	private final Translate translate;

	@Inject
	public UserSitemapNodeModifier(SubjectProvider subjectProvider, CurrentLocale currentLocale,
			MasterSitemap masterSitemap, PageAccessController pageAccessController, Translate translate) {
		super();
		this.subjectProvider = subjectProvider;
		this.masterSitemap = masterSitemap;
		this.pageAccessController = pageAccessController;
		this.collator = Collator.getInstance(currentLocale.getLocale());
		this.translate = translate;
	}

	/**
	 * * Checks each node to ensure that the Subject has permission to view, and if so, adds it to this tree. Note that
	 * if a node is redirected, its pageAccessControl attribute will have been modified to be the same as the redirect
	 * target by the SitemapChecker.
	 * <p>
	 * Nodes which have a null label key are ignored, as they cannot be displayed. The logout page is never loaded. The
	 * login page is only shown if the user is not authenticated.<br>
	 * <br>
	 * Nodes are sorted according to the required order, as specified by (the {@link #sorted}<br>
	 * <br>
	 * The label and collation key for the node are created using {@link CurrentLocale}, which may be different for
	 * different users; so both this class and CurrentLocale are {@link VaadinSessionScoped}
	 *
	 * @param subject
	 * @param collator
	 * @param masterNode
	 * @return
	 */
	@Override
	public UserSitemapNode create(UserSitemapNode parentUserNode, MasterSitemapNode masterNode) {

		log.debug("creating a node for master node {}", masterNode);
		// if there is no labelKey (usually when page is redirected), cannot be shown
		if (masterNode.getLabelKey() == null) {
			return null;
		}

		// if the subject is already authenticated, don't show the login page
		if (subjectProvider.get().isAuthenticated()) {
			if (masterNode.equals(masterSitemap.standardPageNode(StandardPageKey.Log_In))) {
				log.debug("User has already authenticated, do not show the login node");
				return null;
			}
		}
		if (pageAccessController.isAuthorised(subjectProvider.get(), masterNode)) {
			log.debug("User is authorised for page {}, creating a node for it");
			UserSitemapNode userNode = new UserSitemapNode(masterNode);
			userNode.setLabel(translate.from(masterNode.getLabelKey()));
			userNode.setCollationKey(collator.getCollationKey(userNode.getLabel()));
			return userNode;
		} else {
			log.debug("User is NOT authorised for page {}, returning null", masterSitemap.uri(masterNode));
			return null;
		}
	}

	@Override
	public MasterSitemapNode sourceNodeFor(UserSitemapNode target) {
		return target.getMasterNode();
	}

	/**
	 * Not used in this implementation
	 */
	@Override
	public void setLeaf(UserSitemapNode targetNode, boolean isLeaf) {

	}

	/**
	 * Not used in this implementation
	 */

	@Override
	public void setCaption(UserSitemapNode targetNode, String caption) {

	}

	@Override
	public boolean attachOnCreate() {
		return false;
	}

	/**
	 * Not used in this implementation
	 */
	@Override
	public void sortChildren(UserSitemapNode parentNode, Comparator<UserSitemapNode> comparator) {

	}

}
