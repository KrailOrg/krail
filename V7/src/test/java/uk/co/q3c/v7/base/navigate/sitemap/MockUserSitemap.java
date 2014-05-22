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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import uk.co.q3c.v7.base.navigate.NavigationState;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.TestLabelKey;

import com.google.inject.Inject;

import fixture.testviews2.TestLoginView;
import fixture.testviews2.TestLogoutView;
import fixture.testviews2.TestPrivateHomeView;
import fixture.testviews2.TestPublicHomeView;
import fixture.testviews2.View1;
import fixture.testviews2.View2;

public class MockUserSitemap {

	public final UserSitemap userSitemap;

	public UserSitemapNode loginNode;
	public UserSitemapNode logoutNode;
	public UserSitemapNode privateHomeNode;
	public UserSitemapNode publicHomeNode;
	public UserSitemapNode publicNode;
	public UserSitemapNode privateNode;

	public String loginURI = "public/login";
	public String logoutURI = "public/logout";
	public String privateURI = "private";
	public String publicURI = "public";
	public String privateHomeURI = "private/home";
	public String publicHomeURI = "public/home";

	public UserSitemapNode public1Node;
	public String public1URI = "public/1";

	public UserSitemapNode private1Node;
	public String private1URI = "private/1";

	public UserSitemapNode public2Node;
	public String public2URI = "public/2";

	public UserSitemapNode private2Node;
	public String private2URI = "private/2";

	public Class<? extends V7View> loginViewClass = TestLoginView.class;
	public Class<? extends V7View> logoutViewClass = TestLogoutView.class;
	public Class<? extends V7View> privateHomeViewClass = TestPrivateHomeView.class;
	public Class<? extends V7View> publicHomeViewClass = TestPublicHomeView.class;

	public Class<? extends V7View> public1ViewClass = View1.class;
	public Class<? extends V7View> public2ViewClass = View2.class;
	public Class<? extends V7View> private1ViewClass = View1.class;
	public Class<? extends V7View> privateViewClass = View2.class;

	private final URIFragmentHandler uriHandler;

	@Inject
	protected MockUserSitemap(URIFragmentHandler uriHandler) {
		userSitemap = mock(UserSitemap.class);
		this.uriHandler = uriHandler;
	}

	/**
	 * Creates the nodes and pages for standard pages, including intermediate (public and private) pages.
	 */
	private void createStandardPages() {
		loginNode = createNode(loginURI, "login", loginViewClass, StandardPageKey.Login, "Login",
				PageAccessControl.PUBLIC);
		logoutNode = createNode(logoutURI, "logout", logoutViewClass, StandardPageKey.Logout, "Login",
				PageAccessControl.PUBLIC);
		privateHomeNode = createNode(privateHomeURI, "home", privateHomeViewClass, StandardPageKey.Private_Home,
				"Private Home", PageAccessControl.PUBLIC);
		publicHomeNode = createNode(publicHomeURI, "home", publicHomeViewClass, StandardPageKey.Public_Home,
				"Public Home", PageAccessControl.PUBLIC);

		publicNode = createNode(publicURI, "public", null, LabelKey.Public, "Public", PageAccessControl.PUBLIC);
		privateNode = createNode(privateURI, "private", null, LabelKey.Private, "Private", PageAccessControl.PERMISSION);
		createStandardPageLookups();
	}

	private void createStandardPageLookups() {
		when(userSitemap.standardPageURI(StandardPageKey.Login)).thenReturn(loginURI);
		when(userSitemap.standardPageURI(StandardPageKey.Logout)).thenReturn(logoutURI);
		when(userSitemap.standardPageURI(StandardPageKey.Private_Home)).thenReturn(privateHomeURI);
		when(userSitemap.standardPageURI(StandardPageKey.Public_Home)).thenReturn(publicHomeURI);
	}

	private void createPage(String URI, UserSitemapNode node) {
		when(userSitemap.nodeFor(URI)).thenReturn(node);
		when(userSitemap.getRedirectPageFor(URI)).thenReturn(URI);
		when(userSitemap.nodeFor(uriHandler.navigationState(URI))).thenReturn(node);
		when(userSitemap.uri(node)).thenReturn(URI);

	}

	public UserSitemapNode createNode(String fullURI, String uriSegment, Class<? extends V7View> viewClass,
			I18NKey<?> labelKey, String label, PageAccessControl pageAccessControl, String... roles) {

		// not used yet, but may be needed
		// Collator collator = Collator.getInstance();
		// collationKey = collator.getCollationKey(label);

		MasterSitemapNode masterNode = new MasterSitemapNode(uriSegment, viewClass, labelKey);
		UserSitemapNode node = new UserSitemapNode(masterNode);
		masterNode.setPageAccessControl(pageAccessControl);
		masterNode.setRoles(Arrays.asList(roles));

		createPage(fullURI, node);

		return node;
	}

	public void createNodeSet(int index) {

		switch (index) {
		case 1:
			public1Node = createNode(public1URI, "1", View1.class, TestLabelKey.View1, "View 1",
					PageAccessControl.PUBLIC);
			private1Node = createNode(private1URI, "1", View1.class, TestLabelKey.View1, "View 1",
					PageAccessControl.PERMISSION);
			public2Node = createNode(public2URI, "2", View2.class, TestLabelKey.View2, "View 2",
					PageAccessControl.PUBLIC);
			private2Node = createNode(private2URI, "2", View2.class, TestLabelKey.View2, "View 2",
					PageAccessControl.PERMISSION);
		}
		createStandardPages();
		addRedirect("");
	}

	/**
	 * Use this method to tell the mock which node to return for a specific URI (you'll need to do this when the URI
	 * contains parameters)
	 *
	 * @param fullFragment
	 * @param node
	 */
	public void setNodeFor(String fullFragment, UserSitemapNode node) {
		NavigationState navState = new NavigationState();
		navState.setFragment(fullFragment);
		when(userSitemap.nodeFor(navState)).thenReturn(node);

	}

	/**
	 * If a page is not fully defined, you will still need to mock the redirect check - usually just by setting 'from'
	 * and 'to' to be the same (the short-hand version for that is {@link #addRedirect(String)}
	 */
	public void addRedirect(String fromPage, String toPage) {
		when(userSitemap.getRedirectPageFor(fromPage)).thenReturn(toPage);
	}

	public void addRedirect(String fromPage) {
		addRedirect(fromPage, fromPage);
	}
}
