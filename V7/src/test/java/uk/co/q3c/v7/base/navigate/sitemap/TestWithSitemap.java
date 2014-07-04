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

import static org.mockito.Mockito.when;

import java.text.Collator;
import java.util.Locale;

import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.shiro.PageAccessController;
import uk.co.q3c.v7.base.shiro.SubjectProvider;
import uk.co.q3c.v7.base.user.opt.DefaultUserOption;
import uk.co.q3c.v7.base.user.opt.UserOption;
import uk.co.q3c.v7.base.user.status.UserStatus;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.DefaultI18NProcessor;
import uk.co.q3c.v7.i18n.I18NProcessor;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.TestLabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public abstract class TestWithSitemap {

	@Inject
	protected CurrentLocale currentLocale;

	@Inject
	protected Translate translate;

	@Inject
	protected URIFragmentHandler uriHandler;

	@Inject
	protected MasterSitemap masterSitemap;

	@Mock
	protected SubjectProvider subjectProvider;

	@Mock
	protected Subject subject;

	@Mock
	protected PageAccessController pageAccessController;

	@Mock
	UserStatus userStatus;

	protected UserSitemapBuilder userSitemapBuilder;

	protected MasterSitemapNode masterNode1;
	protected MasterSitemapNode masterNode2;
	protected MasterSitemapNode masterNode3;
	protected MasterSitemapNode masterNode4;
	protected MasterSitemapNode masterNode5;
	protected MasterSitemapNode masterNode6;

	protected MasterSitemapNode privateHomeNode;
	protected MasterSitemapNode publicHomeNode;
	protected MasterSitemapNode loginNode;
	protected MasterSitemapNode logoutNode;

	protected UserSitemap userSitemap;
	protected UserSitemapNode userNode1;
	protected UserSitemapNode userNode2;
	protected UserSitemapNode userNode3;
	protected UserSitemapNode userNode4;
	protected UserSitemapNode userNode5;
	protected UserSitemapNode userNode6;

	Locale locale = Locale.UK;
	Collator collator;

	@Before
	public void setup() {
		// userOption = new DefaultUserOption(new DefaultUserOptionStore());
		currentLocale.setLocale(Locale.UK);
		currentLocale.removeAllListeners();
		collator = Collator.getInstance(locale);
		when(subjectProvider.get()).thenReturn(subject);

	}

	/**
	 * If using the userSitemap, the order of calling should be: buildMasterSitemap, set up mocks for user sitemap,
	 * createUserSitemap()
	 *
	 * @param i
	 */
	protected void buildMasterSitemap(int i) {

		switch (i) {
		case 0:
			break; // empty sitemap

		case 2:
			masterNode4 = newNode("b");
			masterNode5 = newNode("b1");
			masterNode6 = newNode("b11");
			masterSitemap.addChild(masterNode4, masterNode5);
			masterSitemap.addChild(masterNode5, masterNode6);

		case 1:
			masterNode1 = newNode("a");
			masterNode2 = newNode("a1");
			masterNode3 = newNode("a11");
			masterSitemap.addChild(masterNode1, masterNode2);
			masterSitemap.addChild(masterNode2, masterNode3);
			break;

		case 3:
			masterNode1 = newNode("public");
			masterNode1.setLabelKey(StandardPageKey.Public_Home);
			masterNode2 = newNode("logout");
			masterNode2.setLabelKey(StandardPageKey.Log_Out);
			masterSitemap.addChild(masterNode1, masterNode2);
			break;
		case 4:
			masterNode1 = newNode("public");
			masterNode1.setLabelKey(StandardPageKey.Public_Home);
			masterNode2 = newNode("logout");
			masterNode2.setLabelKey(StandardPageKey.Log_Out);
			masterNode3 = newNode("private");
			masterNode4 = newNode("wiggly");
			masterSitemap.addChild(masterNode1, masterNode2);
			masterSitemap.addChild(masterNode3, masterNode4);
			break;

		case 5: // one node has missing key
			masterNode4 = newNode("b");
			masterNode5 = newNode("b1");
			masterNode6 = newNode("b11");
			masterSitemap.addChild(masterNode4, masterNode5);
			masterSitemap.addChild(masterNode5, masterNode6);
			masterNode1 = new MasterSitemapNode();
			masterNode1.setUriSegment("a");
			masterNode1.setPageAccessControl(PageAccessControl.PUBLIC);
			masterNode2 = newNode("a1");
			masterNode3 = newNode("a11");
			masterSitemap.addChild(masterNode1, masterNode2);
			masterSitemap.addChild(masterNode2, masterNode3);
			break;

		case 6: // one node is private
			masterNode4 = newNode("b");
			masterNode5 = newNode("b1");
			masterNode6 = newNode("b11");
			masterNode6.setPageAccessControl(PageAccessControl.PERMISSION);
			masterSitemap.addChild(masterNode4, masterNode5);
			masterSitemap.addChild(masterNode5, masterNode6);
			masterNode1 = new MasterSitemapNode();
			masterNode1.setUriSegment("a");
			masterNode1.setLabelKey(TestLabelKey.Yes);
			masterNode1.setPageAccessControl(PageAccessControl.PUBLIC);
			masterNode2 = newNode("a1");
			masterNode2.setLabelKey(TestLabelKey.Home);
			masterNode3 = newNode("a11");
			masterNode3.setLabelKey(TestLabelKey.Yes);
			masterSitemap.addChild(masterNode1, masterNode2);
			masterSitemap.addChild(masterNode2, masterNode3);
			break;

		case 7: // redirect has no page access control
			masterNode4 = newNode("b");
			masterNode4.setPageAccessControl(null);
			masterNode5 = newNode("b1");
			masterNode6 = newNode("b11");

			String fromPage = masterSitemap.navigationState(masterNode4).getVirtualPage();
			String toPage = masterSitemap.navigationState(masterNode5).getVirtualPage();
			masterSitemap.addRedirect(fromPage, toPage);
			masterSitemap.addChild(masterNode4, masterNode5);
			masterSitemap.addChild(masterNode5, masterNode6);
			break;

		case 8:
			masterNode1 = newNode("1");
			masterNode1.setLabelKey(LabelKey.Yes);
			masterNode2 = newNode("2");
			masterNode2.setLabelKey(LabelKey.No);
			masterNode3 = newNode("3");
			masterNode3.setLabelKey(LabelKey.Enable_Account);
			masterSitemap.addChild(null, masterNode1);
			masterSitemap.addChild(null, masterNode2);
			masterSitemap.addChild(masterNode1, masterNode3);

			masterSitemap.addRedirect("a", "1");
			masterSitemap.addRedirect("b", "9");

			buildStandardPages();

			break;

		}

	}

	protected MasterSitemapNode newNode(String urlSegment) {
		MasterSitemapNode node0 = new MasterSitemapNode();
		node0.setLabelKey(TestLabelKey.Home);
		node0.setUriSegment(urlSegment);
		node0.setViewClass(PublicHomeView.class);
		node0.setPageAccessControl(PageAccessControl.PUBLIC);
		return node0;
	}

	/**
	 * user sitemap cannot be created until the master sitemap has been created. You will also need to set up any mocks
	 * needed before calling this method
	 */
	protected void createUserSitemap() {
		userSitemap = new DefaultUserSitemap(translate, uriHandler, currentLocale);
		UserSitemapNodeModifier nodeModifier = new UserSitemapNodeModifier(subjectProvider, currentLocale,
				masterSitemap, pageAccessController, translate);
		UserSitemapCopyExtension copyExtension = new UserSitemapCopyExtension(masterSitemap, userSitemap);
		userSitemapBuilder = new UserSitemapBuilder(masterSitemap, userSitemap, nodeModifier, copyExtension, userStatus);
		userSitemapBuilder.build();

		userNode1 = userSitemap.userNodeFor(masterNode1);
		userNode2 = userSitemap.userNodeFor(masterNode2);
		userNode3 = userSitemap.userNodeFor(masterNode3);
		userNode4 = userSitemap.userNodeFor(masterNode4);
		userNode5 = userSitemap.userNodeFor(masterNode5);
		userNode6 = userSitemap.userNodeFor(masterNode6);
	}

	protected void buildStandardPages() {

		loginNode = newNode("login");
		loginNode.setLabelKey(StandardPageKey.Log_In);
		logoutNode = newNode("logout");
		loginNode.setLabelKey(StandardPageKey.Log_Out);
		publicHomeNode = newNode("public/home");
		publicHomeNode.setLabelKey(StandardPageKey.Public_Home);
		privateHomeNode = newNode("private/home");
		privateHomeNode.setLabelKey(StandardPageKey.Private_Home);
		masterSitemap.addStandardPage(StandardPageKey.Log_In, loginNode);
		masterSitemap.addStandardPage(StandardPageKey.Log_Out, logoutNode);
		masterSitemap.addStandardPage(StandardPageKey.Public_Home, publicHomeNode);
		masterSitemap.addStandardPage(StandardPageKey.Private_Home, privateHomeNode);
		masterSitemap.addChild(masterSitemap.nodeFor("public"), publicHomeNode);
		masterSitemap.addChild(masterSitemap.nodeFor("private"), privateHomeNode);
		masterSitemap.addChild(null, loginNode);
		masterSitemap.addChild(null, logoutNode);
	}

	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
				bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
				bind(UserOption.class).to(DefaultUserOption.class);
			}

		};
	}
}
