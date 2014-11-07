/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package fixture;

import com.google.inject.Inject;
import fixture.testviews2.*;
import uk.q3c.krail.base.navigate.URIFragmentHandler;
import uk.q3c.krail.base.navigate.sitemap.DefaultUserSitemap;
import uk.q3c.krail.base.navigate.sitemap.MasterSitemapNode;
import uk.q3c.krail.base.navigate.sitemap.StandardPageKey;
import uk.q3c.krail.base.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.base.shiro.PageAccessControl;
import uk.q3c.krail.base.view.V7View;
import uk.q3c.krail.i18n.*;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides a user sitemap with page layout:
 * <p/>
 * -Public --Logout --ViewA ---ViewA1 ----ViewA11 --Login --Public Home
 * <p/>
 * -Private --Private Home --ViewB ---ViewB1 ----ViewB11 <br>
 * <br>
 * Insertion order ascending is set to be the same as UK alpha ascending <br>
 * <br>
 * Position index is set to be the reverse of alphabetic order
 *
 * @author dsowerby
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

    /**
     * Creates the nodes and pages for standard pages, including intermediate (public and private) pages.
     */
    private void createStandardPages() {
        loginNode = createNode(loginURI, "login", loginViewClass, StandardPageKey.Log_In, PageAccessControl.PUBLIC);
        logoutNode = createNode(logoutURI, "logout", logoutViewClass, StandardPageKey.Log_Out,
                PageAccessControl.PUBLIC);
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

        addStandardPage(StandardPageKey.Log_In, loginNode);
        addStandardPage(StandardPageKey.Log_Out, logoutNode);
        addStandardPage(StandardPageKey.Public_Home, publicHomeNode);
        addStandardPage(StandardPageKey.Private_Home, privateHomeNode);
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
