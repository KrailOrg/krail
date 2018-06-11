/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package fixture;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import fixture.testviews2.TestLoginView;
import fixture.testviews2.TestLogoutView;
import fixture.testviews2.TestPrivateHomeView;
import fixture.testviews2.TestPublicHomeView;
import fixture.testviews2.ViewA;
import fixture.testviews2.ViewA1;
import fixture.testviews2.ViewA11;
import fixture.testviews2.ViewA111;
import fixture.testviews2.ViewB;
import fixture.testviews2.ViewB1;
import fixture.testviews2.ViewB11;
import fixture.testviews2.ViewB12;
import fixture.testviews2.ViewB121;
import fixture.testviews2.ViewB122;
import uk.q3c.krail.core.eventbus.SessionBusProvider;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.navigate.sitemap.DefaultUserSitemap;
import uk.q3c.krail.core.navigate.sitemap.EmptyView;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.view.EmptyViewConfiguration;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.krail.i18n.test.TestLabelKey;
import uk.q3c.util.guice.SerializationSupport;

import java.text.CollationKey;
import java.text.Collator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Provides a user sitemap with page layout:
 * <p>
 * -Public<br>
 * --Logout<br>
 * --ViewA<br>
 * ---ViewA1<br>
 * ----ViewA11<br>
 * -----ViewA111<br> excluded
 * --Login<br>
 * --Public Home
 * <p>
 * -Private<br>
 * --Private Home<br>
 * --ViewB<br>
 * ---ViewB1<br>
 * ----ViewB11<br> excluded
 * ----ViewB12<br>
 * -----ViewB121<br>
 * -----ViewB122<br> excluded
 * <br>
 * Insertion order ascending is set to be the same as UK alpha ascending <br>
 * <br>
 * Position index is set to be the reverse of alphabetic order
 *
 * @author dsowerby
 */
@SuppressWarnings("ALL")
public class ReferenceUserSitemap extends DefaultUserSitemap {

    public String aFragment = "public/a";
    public Class<? extends KrailView> aViewClass = ViewA.class;
    public String a1Fragment = "public/a/a1";
    public Class<? extends KrailView> a1ViewClass = ViewA1.class;
    public String a11Fragment = "public/a/a1/a11";
    public Class<? extends KrailView> a11ViewClass = ViewA11.class;
    public String a111Fragment = "public/a/a1/a11/a111";
    public Class<? extends KrailView> a111ViewClass = ViewA111.class; // excluded

    public String bFragment = "private/b";
    public Class<? extends KrailView> bViewClass = ViewB.class;
    public String b1Fragment = "private/b/b1";
    public Class<? extends KrailView> b1ViewClass = ViewB1.class;
    public String b11Fragment = "private/b/b1/b11";
    public Class<? extends KrailView> b11ViewClass = ViewB11.class; // excluded
    public String b12Fragment = "private/b/b1/b12";
    public Class<? extends KrailView> b12ViewClass = ViewB12.class;


    public String b121Fragment = "private/b/b1/b12/b121";
    public Class<? extends KrailView> b121ViewClass = ViewB121.class;
    public String b122Fragment = "private/b/b1/b12/b122";
    public Class<? extends KrailView> b122ViewClass = ViewB122.class; // excluded


    public String loginFragment = "public/login";
    public String logoutFragment = "public/logout";
    public String privateFragment = "private";
    public String publicFragment = "public";
    public String privateHomeFragment = "private/home";
    public String publicHomeFragment = "public/home";
    public Class<? extends KrailView> loginViewClass = TestLoginView.class;
    public Class<? extends KrailView> logoutViewClass = TestLogoutView.class;
    public Class<? extends KrailView> privateHomeViewClass = TestPrivateHomeView.class;
    public Class<? extends KrailView> publicHomeViewClass = TestPublicHomeView.class;
    LinkedList<String> insertionOrder;
    Map<String, Integer> positionIndexes;
    private UserSitemapNode a11Node;
    private UserSitemapNode a111Node;
    private UserSitemapNode a1Node;
    private UserSitemapNode aNode;
    private UserSitemapNode b11Node;
    private UserSitemapNode b12Node;
    private UserSitemapNode b121Node;
    private UserSitemapNode b122Node;

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
    public ReferenceUserSitemap(Translate translate, URIFragmentHandler uriHandler, SessionBusProvider sessionBusProvider, SerializationSupport serializationSupport) {
        super(translate, uriHandler, sessionBusProvider, serializationSupport);

        insertionOrder = new LinkedList<>();
        positionIndexes = new HashMap<>();


        insertionOrder.add(privateFragment);
        positionIndexes.put(privateFragment, 4);

        insertionOrder.add(publicFragment);
        positionIndexes.put(publicFragment, 2);

        insertionOrder.add(loginFragment);
        positionIndexes.put(loginFragment, 8);

        insertionOrder.add(logoutFragment);
        positionIndexes.put(logoutFragment, 7);

        insertionOrder.add(publicHomeFragment);
        positionIndexes.put(publicHomeFragment, 6);

        insertionOrder.add(bFragment);
        positionIndexes.put(bFragment, 3);


        insertionOrder.add(b1Fragment);
        positionIndexes.put(b1Fragment, 3);

        insertionOrder.add(b11Fragment);
        positionIndexes.put(b11Fragment, -1); //This one should not be in nav components

        insertionOrder.add(b12Fragment);
        positionIndexes.put(b12Fragment, 2);

        insertionOrder.add(b121Fragment);
        positionIndexes.put(b121Fragment, 1);

        insertionOrder.add(b122Fragment);
        positionIndexes.put(b122Fragment, -1);//This one should not be in nav components


        insertionOrder.add(aFragment);
        positionIndexes.put(aFragment, 5);

        insertionOrder.add(a1Fragment);
        positionIndexes.put(a1Fragment, 5);

        insertionOrder.add(a11Fragment);
        positionIndexes.put(a11Fragment, 5);

        insertionOrder.add(a111Fragment);
        positionIndexes.put(a111Fragment, -1);//This one should not be in nav components

        insertionOrder.add(privateHomeFragment);
        positionIndexes.put(privateHomeFragment, 13);


    }

    public void populate() {
        createStandardPages();
        createPages();
    }

    /**
     * Creates the nodes and pages for standard pages, including intermediate (public and private) pages.
     */
    private void createStandardPages() {
        loginNode = createNode(loginFragment, "login", loginViewClass, StandardPageKey.Log_In, PageAccessControl.PUBLIC);
        logoutNode = createNode(logoutFragment, "logout", logoutViewClass, StandardPageKey.Log_Out, PageAccessControl.PUBLIC);
        privateHomeNode = createNode(privateHomeFragment, "home", privateHomeViewClass, StandardPageKey.Private_Home, PageAccessControl.PUBLIC);
        publicHomeNode = createNode(publicHomeFragment, "home", publicHomeViewClass, StandardPageKey.Public_Home, PageAccessControl.PUBLIC);

        publicNode = createNode(publicFragment, "public", null, LabelKey.Public, PageAccessControl.PUBLIC);
        privateNode = createNode(privateFragment, "private", null, LabelKey.Private, PageAccessControl.PERMISSION);

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

        ImmutableList<String> r = ImmutableList.of();
        if (roles != null) {
            r = ImmutableList.copyOf(roles);
        }
        Integer id = insertionOrder.indexOf(fullURI);
        final Integer positionIndex = positionIndexes.get(fullURI);


        Class<? extends KrailView> vClass = (viewClass == null) ? EmptyView.class : viewClass;


        MasterSitemapNode masterNode = new MasterSitemapNode(id, uriSegment, labelKey, pageAccessControl, positionIndex, vClass, EmptyViewConfiguration.class, r);

        UserSitemapNode node = new UserSitemapNode(masterNode);
        node.setLabel(getTranslate().from(labelKey));
        CollationKey collationKey = collator.getCollationKey(node.getLabel());
        node.setCollationKey(collationKey);

        return node;
    }

    private void createPages() {
        aNode = createNode(aFragment, "a", aViewClass, TestLabelKey.ViewA, PageAccessControl.PUBLIC);
        a1Node = createNode(a1Fragment, "a1", a1ViewClass, TestLabelKey.ViewA1, PageAccessControl.PUBLIC);
        a11Node = createNode(a11Fragment, "a11", a11ViewClass, TestLabelKey.ViewA11, PageAccessControl.PUBLIC);
        a111Node = createNode(a111Fragment, "a111", a11ViewClass, TestLabelKey.ViewA111, PageAccessControl.PUBLIC);

        addChild(publicNode, aNode);
        addChild(aNode, a1Node);
        addChild(a1Node, a11Node);
        addChild(a11Node, a111Node);

        bNode = createNode(bFragment, "b", bViewClass, TestLabelKey.ViewB, PageAccessControl.PERMISSION);
        b1Node = createNode(b1Fragment, "b1", b1ViewClass, TestLabelKey.ViewB1, PageAccessControl.PERMISSION);
        b11Node = createNode(b11Fragment, "b11", b1ViewClass, TestLabelKey.ViewB11, PageAccessControl.PERMISSION);
        b12Node = createNode(b12Fragment, "b12", b12ViewClass, TestLabelKey.ViewB12, PageAccessControl.PERMISSION);
        b121Node = createNode(b121Fragment, "b121", b121ViewClass, TestLabelKey.ViewB121, PageAccessControl.PERMISSION);
        b122Node = createNode(b122Fragment, "b122", b122ViewClass, TestLabelKey.ViewB122, PageAccessControl.PERMISSION);

        addChild(privateNode, bNode);
        addChild(bNode, b1Node);
        addChild(b1Node, b11Node);
        addChild(b1Node, b12Node);
        addChild(b12Node, b121Node);
        addChild(b12Node, b122Node);
    }


    public List<UserSitemapNode> publicSortedAlphaAscending() {
        List<UserSitemapNode> list = new LinkedList<>();
        list.add(loginNode());
        list.add(publicHomeNode());
        list.add(aNode());
        return list;
    }

    public UserSitemapNode loginNode() {
        return nodeFor(loginFragment);
    }

    public UserSitemapNode publicHomeNode() {
        return nodeFor(publicHomeFragment);
    }

    public UserSitemapNode aNode() {
        return nodeFor(aFragment);
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
        return nodeFor(logoutFragment);
    }

    public UserSitemapNode privateHomeNode() {
        return nodeFor(privateHomeFragment);
    }

    public UserSitemapNode publicNode() {
        return nodeFor(publicFragment);
    }

    public UserSitemapNode privateNode() {
        return nodeFor(privateFragment);
    }

    public UserSitemapNode a1Node() {
        return nodeFor(a1Fragment);
    }

    public UserSitemapNode a11Node() {
        return nodeFor(a11Fragment);
    }

    public UserSitemapNode bNode() {
        return nodeFor(bFragment);
    }

    public UserSitemapNode b1Node() {
        return nodeFor(b1Fragment);
    }

    public UserSitemapNode b11Node() {
        return nodeFor(b11Fragment);
    }

    public UserSitemapNode b121Node() {
        return nodeFor(b121Fragment);
    }

    public UserSitemapNode b122Node() {
        return nodeFor(b122Fragment);
    }

    public UserSitemapNode a111Node() {
        return nodeFor(a111Fragment);
    }

    public void setA1Node(UserSitemapNode a1Node) {
        this.a1Node = a1Node;
    }

    public UserSitemapNode b12Node() {
        return nodeFor(b12Fragment);
    }
}
