/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package fixture;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fixture.testviews2.*;
import net.engio.mbassy.bus.MBassador;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.eventbus.SessionBus;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.navigate.sitemap.DefaultUserSitemap;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.TestLabelKey;
import uk.q3c.krail.i18n.Translate;

import java.text.CollationKey;
import java.text.Collator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Provides a user sitemap with page layout:
 * <p>
 * -Public --Logout --ViewA ---ViewA1 ----ViewA11 --Login --Public Home
 * <p>
 * -Private --Private Home --ViewB ---ViewB1 ----ViewB11 <br>
 * <br>
 * Insertion order ascending is set to be the same as UK alpha ascending <br>
 * <br>
 * Position index is set to be the reverse of alphabetic order
 *
 * @author dsowerby
 */
public class ReferenceUserSitemap extends DefaultUserSitemap {

    public String aURI = "public/a";
    public Class<? extends KrailView> aViewClass = ViewA.class;
    public String a1URI = "public/a/a1";
    public Class<? extends KrailView> a1ViewClass = ViewA1.class;
    public String a11URI = "public/a/a1/a11";
    public Class<? extends KrailView> a11ViewClass = ViewA11.class;
    public String bURI = "private/b";
    public Class<? extends KrailView> bViewClass = ViewB.class;
    public String b1URI = "private/b/b1";
    public Class<? extends KrailView> b1ViewClass = ViewB1.class;
    public String b11URI = "private/b/b1/b11";
    public Class<? extends KrailView> b11ViewClass = ViewB11.class;
    public String loginURI = "public/login";
    public String logoutURI = "public/logout";
    public String privateURI = "private";
    public String publicURI = "public";
    public String privateHomeURI = "private/home";
    public String publicHomeURI = "public/home";
    public Class<? extends KrailView> loginViewClass = TestLoginView.class;
    public Class<? extends KrailView> logoutViewClass = TestLogoutView.class;
    public Class<? extends KrailView> privateHomeViewClass = TestPrivateHomeView.class;
    public Class<? extends KrailView> publicHomeViewClass = TestPublicHomeView.class;
    Map<String, Integer> insertionOrder;
    Map<String, Integer> positionIndexes;
    private UserSitemapNode a11Node;
    private UserSitemapNode a1Node;
    private UserSitemapNode aNode;
    private UserSitemapNode b11Node;
    private UserSitemapNode b1Node;
    private UserSitemapNode bNode;
    private UserSitemapNode loginNode;
    private UserSitemapNode logoutNode;
    private UserSitemapNode privateHomeNode;
    private UserSitemapNode privateNode;
    private UserSitemapNode publicHomeNode;
    private UserSitemapNode publicNode;


    /**
     * Insertion order ascending is set to be the same as UK alpha ascending <br>
     * <br>
     * Position index is set to be the reverse of alphabetic order
     */

    @Inject
    public ReferenceUserSitemap(Translate translate, URIFragmentHandler uriHandler, @SessionBus MBassador<BusMessage> eventBus) {
        super(translate, uriHandler, eventBus);

        insertionOrder = new HashMap<>();
        positionIndexes = new HashMap<>();


        insertionOrder.put(privateURI, 1);
        positionIndexes.put(privateURI, 4);

        insertionOrder.put(publicURI, 2);
        positionIndexes.put(publicURI, 2);

        insertionOrder.put(loginURI, 6);
        positionIndexes.put(loginURI, 8);

        insertionOrder.put(logoutURI, 7);
        positionIndexes.put(logoutURI, 7);

        insertionOrder.put(publicHomeURI, 8);
        positionIndexes.put(publicHomeURI, 6);

        insertionOrder.put(bURI, 3);
        positionIndexes.put(bURI, 3);


        insertionOrder.put(b1URI, 4);
        positionIndexes.put(b1URI, 3);

        insertionOrder.put(b11URI, 5);
        positionIndexes.put(b11URI, 3);

        insertionOrder.put(aURI, 9);
        positionIndexes.put(aURI, 5);

        insertionOrder.put(a1URI, 10);
        positionIndexes.put(a1URI, 5);

        insertionOrder.put(a11URI, 11);
        positionIndexes.put(a11URI, 5);

        insertionOrder.put(privateHomeURI, 12);
        positionIndexes.put(privateHomeURI, 13);


    }

    public void populate() {
        createStandardPages();
        createPages();
    }

    /**
     * Creates the nodes and pages for standard pages, including intermediate (public and private) pages.
     */
    private void createStandardPages() {
        loginNode = createNode(loginURI, "login", loginViewClass, StandardPageKey.Log_In, PageAccessControl.PUBLIC);
        logoutNode = createNode(logoutURI, "logout", logoutViewClass, StandardPageKey.Log_Out, PageAccessControl.PUBLIC);
        privateHomeNode = createNode(privateHomeURI, "home", privateHomeViewClass, StandardPageKey.Private_Home, PageAccessControl.PUBLIC);
        publicHomeNode = createNode(publicHomeURI, "home", publicHomeViewClass, StandardPageKey.Public_Home, PageAccessControl.PUBLIC);

        publicNode = createNode(publicURI, "public", null, LabelKey.Public, PageAccessControl.PUBLIC);
        privateNode = createNode(privateURI, "private", null, LabelKey.Private, PageAccessControl.PERMISSION);

        addChild(publicNode, publicHomeNode);
        addChild(publicNode, loginNode);
        addChild(publicNode, logoutNode);
        addChild(privateNode, privateHomeNode);

        //        addStandardPage(StandardPageKey.Log_In, loginNode);
        //        addStandardPage(StandardPageKey.Log_Out, logoutNode);
        //        addStandardPage(StandardPageKey.Public_Home, publicHomeNode);
        //        addStandardPage(StandardPageKey.Private_Home, privateHomeNode);
    }

    public UserSitemapNode createNode(String fullURI, String uriSegment, Class<? extends KrailView> viewClass, I18NKey labelKey, PageAccessControl
            pageAccessControl, String... roles) {

        Collator collator = Collator.getInstance();

        List<String> r = null;
        if (roles != null) {
            r = Lists.newArrayList(roles);
        }
        Integer id = insertionOrder.get(fullURI);
        final Integer positionIndex = positionIndexes.get(fullURI);
        MasterSitemapNode masterNode = new MasterSitemapNode(id, uriSegment, viewClass, labelKey, positionIndex, pageAccessControl, r);

        UserSitemapNode node = new UserSitemapNode(masterNode);
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

    private MasterSitemapNode masterNode(UserSitemapNode userNode, int id, int positionIndex) {
        MasterSitemapNode masterNode = new MasterSitemapNode(id, userNode.getUriSegment(), userNode.getViewClass(), userNode.getLabelKey(), positionIndex,
                userNode.getPageAccessControl(), userNode.getRoles());
        return masterNode;
    }

    public List<UserSitemapNode> publicSortedAlphaAscending() {
        List<UserSitemapNode> list = new LinkedList<>();
        list.add(loginNode());
        list.add(publicHomeNode());
        list.add(aNode());
        return list;
    }

    public UserSitemapNode loginNode() {
        return nodeFor(loginURI);
    }

    public UserSitemapNode publicHomeNode() {
        return nodeFor(publicHomeURI);
    }

    public UserSitemapNode aNode() {
        return nodeFor(aURI);
    }

    public List<UserSitemapNode> publicSortedAlphaDescending() {
        List<UserSitemapNode> list = new LinkedList<>();
        list.add(aNode());
        list.add(publicHomeNode());
        list.add(loginNode());
        return list;
    }

    public List<UserSitemapNode> publicSortedInsertionAscending() {
        List<UserSitemapNode> list = new LinkedList<>();
        list.add(loginNode());
        list.add(publicHomeNode());
        list.add(aNode());
        return list;
    }

    public List<UserSitemapNode> publicSortedInsertionDescending() {
        List<UserSitemapNode> list = new LinkedList<>();
        list.add(aNode());
        list.add(publicHomeNode());
        list.add(loginNode());
        return list;
    }

    public List<UserSitemapNode> publicSortedPositionAscending() {
        List<UserSitemapNode> list = new LinkedList<>();
        list.add(aNode());
        list.add(publicHomeNode());
        list.add(loginNode());
        return list;
    }

    public List<UserSitemapNode> publicSortedPositionDescending() {
        List<UserSitemapNode> list = new LinkedList<>();
        list.add(loginNode());
        list.add(publicHomeNode());
        list.add(aNode());
        return list;
    }

    public UserSitemapNode logoutNode() {
        return nodeFor(logoutURI);
    }

    public UserSitemapNode privateHomeNode() {
        return nodeFor(privateHomeURI);
    }

    public UserSitemapNode publicNode() {
        return nodeFor(publicURI);
    }

    public UserSitemapNode privateNode() {
        return nodeFor(privateURI);
    }

    public UserSitemapNode a1Node() {
        return nodeFor(a1URI);
    }

    public UserSitemapNode a11Node() {
        return nodeFor(a11URI);
    }

    public UserSitemapNode bNode() {
        return nodeFor(bURI);
    }

    public UserSitemapNode b1Node() {
        return nodeFor(b1URI);
    }

    public UserSitemapNode b11Node() {
        return nodeFor(b11URI);
    }

}
