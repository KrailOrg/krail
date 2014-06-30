package fixture;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import uk.co.q3c.v7.base.navigate.URIFragmentHandler;
import uk.co.q3c.v7.base.navigate.sitemap.DefaultUserSitemap;
import uk.co.q3c.v7.base.navigate.sitemap.MasterSitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.StandardPageKey;
import uk.co.q3c.v7.base.navigate.sitemap.UserSitemapNode;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NKey;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.TestLabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Inject;

import fixture.testviews2.TestLoginView;
import fixture.testviews2.TestLogoutView;
import fixture.testviews2.TestPrivateHomeView;
import fixture.testviews2.TestPublicHomeView;
import fixture.testviews2.ViewA;
import fixture.testviews2.ViewA1;
import fixture.testviews2.ViewA11;
import fixture.testviews2.ViewB;
import fixture.testviews2.ViewB1;
import fixture.testviews2.ViewB11;

/**
 * Provides a user sitemap with page layout:
 * 
 * -Public --Logout --ViewA ---ViewA1 ----ViewA11 --Login --Public Home
 * 
 * -Private --Private Home --ViewB ---ViewB1 ----ViewB11 <br>
 * <br>
 * Insertion order ascending is set to be the same as UK alpha ascending <br>
 * <br>
 * Position index is set to be the reverse of alphabetic order
 * 
 *
 * @author dsowerby
 *
 */
public class ReferenceUserSitemap extends DefaultUserSitemap {

	public UserSitemapNode aNode;
	public String aURI = "public/a";
	public Class<? extends V7View> aViewClass = ViewA.class;
	public UserSitemapNode a1Node;
	public String a1URI = "public/a/a1";
	public Class<? extends V7View> a1ViewClass = ViewA1.class;
	public UserSitemapNode a11Node;
	public String a11URI = "public/a/a1/a11";
	public Class<? extends V7View> a11ViewClass = ViewA11.class;

	public UserSitemapNode bNode;
	public String bURI = "private/b";
	public Class<? extends V7View> bViewClass = ViewB.class;
	public UserSitemapNode b1Node;
	public String b1URI = "private/b/b1";
	public Class<? extends V7View> b1ViewClass = ViewB1.class;
	public UserSitemapNode b11Node;
	public String b11URI = "private/b/b1/b11";
	public Class<? extends V7View> b11ViewClass = ViewB11.class;

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

	public Class<? extends V7View> loginViewClass = TestLoginView.class;
	public Class<? extends V7View> logoutViewClass = TestLogoutView.class;
	public Class<? extends V7View> privateHomeViewClass = TestPrivateHomeView.class;
	public Class<? extends V7View> publicHomeViewClass = TestPublicHomeView.class;

	@Inject
	public ReferenceUserSitemap(Translate translate, URIFragmentHandler uriHandler, CurrentLocale currentLocale) {
		super(translate, uriHandler, currentLocale);

	}

	public void populate() {
		createStandardPages();
		createPages();
		setupMasterNodes();
	}

	/**
	 * Insertion order ascending is set to be the same as UK alpha ascending <br>
	 * <br>
	 * Position index is set to be the reverse of alphabetic order
	 */
	private void setupMasterNodes() {
		masterNode(privateNode, 1, 4);

		masterNode(bNode, 3, 3);
		masterNode(b1Node, 4, 3);
		masterNode(b11Node, 5, 3);

		masterNode(publicNode, 2, 2);
		masterNode(loginNode, 6, 8);
		masterNode(logoutNode, 7, 7);
		masterNode(publicHomeNode, 8, 6);
		masterNode(aNode, 9, 5);

		masterNode(a1Node, 10, 5);

		masterNode(a11Node, 11, 5);

	}

	private MasterSitemapNode masterNode(UserSitemapNode userNode, int id, int positionIndex) {
		MasterSitemapNode mnode = userNode.getMasterNode();
		mnode.setId(id);
		mnode.setPositionIndex(positionIndex);
		return mnode;
	}

	public UserSitemapNode createNode(String fullURI, String uriSegment, Class<? extends V7View> viewClass,
			I18NKey<?> labelKey, PageAccessControl pageAccessControl, String... roles) {

		Collator collator = Collator.getInstance();

		MasterSitemapNode masterNode = new MasterSitemapNode(uriSegment, viewClass, labelKey);
		UserSitemapNode node = new UserSitemapNode(masterNode);
		masterNode.setPageAccessControl(pageAccessControl);
		masterNode.setRoles(Arrays.asList(roles));

		node.setLabel(getTranslate().from(labelKey));
		CollationKey collationKey = collator.getCollationKey(node.getLabel());
		node.setCollationKey(collationKey);

		return node;
	}

	/**
	 * Creates the nodes and pages for standard pages, including intermediate (public and private) pages.
	 */
	private void createStandardPages() {
		loginNode = createNode(loginURI, "login", loginViewClass, StandardPageKey.Login, PageAccessControl.PUBLIC);
		logoutNode = createNode(logoutURI, "logout", logoutViewClass, StandardPageKey.Logout, PageAccessControl.PUBLIC);
		privateHomeNode = createNode(privateHomeURI, "home", privateHomeViewClass, StandardPageKey.Private_Home,
				PageAccessControl.PUBLIC);
		publicHomeNode = createNode(publicHomeURI, "home", publicHomeViewClass, StandardPageKey.Public_Home,
				PageAccessControl.PUBLIC);

		publicNode = createNode(publicURI, "public", null, LabelKey.Public, PageAccessControl.PUBLIC);
		privateNode = createNode(privateURI, "private", null, LabelKey.Private, PageAccessControl.PERMISSION);

		addChild(publicNode, publicHomeNode);
		addChild(publicNode, loginNode);
		addChild(publicNode, logoutNode);
		addChild(privateNode, privateHomeNode);

		addStandardPage(StandardPageKey.Login, loginNode);
		addStandardPage(StandardPageKey.Logout, logoutNode);
		addStandardPage(StandardPageKey.Public_Home, publicHomeNode);
		addStandardPage(StandardPageKey.Private_Home, privateHomeNode);
	}

	private void createPages() {
		aNode = createNode(aURI, "a", aViewClass, TestLabelKey.ViewA, PageAccessControl.PUBLIC);
		a1Node = createNode(a1URI, "a1", a1ViewClass, TestLabelKey.ViewA1, PageAccessControl.PUBLIC);
		a11Node = createNode(a11URI, "a11", a11ViewClass, TestLabelKey.ViewA11, PageAccessControl.PUBLIC);

		addChild(publicNode, aNode);
		addChild(aNode, a1Node);
		addChild(a1Node, a11Node);

		bNode = createNode(bURI, "b", bViewClass, TestLabelKey.ViewB, PageAccessControl.PERMISSION);
		b1Node = createNode(b1URI, "b1", b1ViewClass, TestLabelKey.ViewB1, PageAccessControl.PERMISSION);
		b11Node = createNode(b11URI, "b11", b1ViewClass, TestLabelKey.ViewB11, PageAccessControl.PERMISSION);

		addChild(privateNode, bNode);
		addChild(bNode, b1Node);
		addChild(b1Node, b11Node);
	}

	public List<UserSitemapNode> publicSortedAlphaAscending() {
		List<UserSitemapNode> list = new LinkedList<>();
		list.add(loginNode);
		list.add(publicHomeNode);
		list.add(aNode);
		return list;
	}

	public List<UserSitemapNode> publicSortedAlphaDescending() {
		List<UserSitemapNode> list = new LinkedList<>();
		list.add(aNode);
		list.add(publicHomeNode);
		list.add(loginNode);
		return list;
	}

	public List<UserSitemapNode> publicSortedInsertionAscending() {
		List<UserSitemapNode> list = new LinkedList<>();
		list.add(loginNode);
		list.add(publicHomeNode);
		list.add(aNode);
		return list;
	}

	public List<UserSitemapNode> publicSortedInsertionDescending() {
		List<UserSitemapNode> list = new LinkedList<>();
		list.add(aNode);
		list.add(publicHomeNode);
		list.add(loginNode);
		return list;
	}

	public List<UserSitemapNode> publicSortedPositionAscending() {
		List<UserSitemapNode> list = new LinkedList<>();
		list.add(aNode);
		list.add(publicHomeNode);
		list.add(loginNode);
		return list;
	}

	public List<UserSitemapNode> publicSortedPositionDescending() {
		List<UserSitemapNode> list = new LinkedList<>();
		list.add(loginNode);
		list.add(publicHomeNode);
		list.add(aNode);
		return list;
	}

}
