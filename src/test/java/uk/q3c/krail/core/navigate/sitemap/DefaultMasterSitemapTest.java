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
package uk.q3c.krail.core.navigate.sitemap;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.eventbus.VaadinEventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.DefaultI18NProcessor;
import uk.q3c.krail.core.i18n.I18NProcessor;
import uk.q3c.krail.core.i18n.TestKrailI18NModule;
import uk.q3c.krail.core.navigate.NavigationState;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.view.LoginView;
import uk.q3c.krail.core.view.PublicHomeView;
import uk.q3c.krail.eventbus.mbassador.EventBusModule;
import uk.q3c.krail.i18n.test.TestLabelKey;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.persist.inmemory.InMemoryModule;
import uk.q3c.krail.persist.inmemory.InMemoryOptionStore;
import uk.q3c.krail.persist.inmemory.store.DefaultInMemoryOptionStore;
import uk.q3c.krail.util.UtilsModule;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.q3c.krail.core.navigate.sitemap.StandardPageKey.Public_Home;
import static uk.q3c.krail.core.shiro.PageAccessControl.AUTHENTICATION;
import static uk.q3c.krail.core.shiro.PageAccessControl.PUBLIC;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestKrailI18NModule.class, UtilsModule.class, EventBusModule.class, VaadinSessionScopeModule.class, VaadinEventBusModule.class, InMemoryModule.class, UIScopeModule.class,})
public class DefaultMasterSitemapTest {

    @Mock
    Option option;

    @Inject
    URIFragmentHandler uriHandler;

    @Mock
    NavigationState navState;

    DefaultMasterSitemap sitemap;

    @Before
    public void setup() {
        sitemap = new DefaultMasterSitemap(uriHandler);
    }

    @Test
    public void url() {

        // given
        MasterSitemapNode grandparent = new MasterSitemapNode(1, "public", PublicHomeView.class, TestLabelKey.Home, -1, PUBLIC, null);
        MasterSitemapNode parent = new MasterSitemapNode(2, "home", PublicHomeView.class, TestLabelKey.Home, -1, PUBLIC, null);
        MasterSitemapNode child = new MasterSitemapNode(3, "login", LoginView.class, TestLabelKey.Login, -1, PUBLIC, null);
        sitemap.addChild(grandparent, parent);
        sitemap.addChild(parent, child);
        // when

        // then
        assertThat(sitemap.uri(grandparent)).isEqualTo("public");
        assertThat(sitemap.uri(parent)).isEqualTo("public/home");
        assertThat(sitemap.uri(child)).isEqualTo("public/home/login");
    }

    @Test
    public void append() {

        // given
        NodeRecord nr = new NodeRecord("public/home");
        // when

        MasterSitemapNode node = sitemap.append(nr);
        // then
        assertThat(node).isNotNull();
        assertThat(node.getUriSegment()).isEqualTo("home");
        assertThat(sitemap.getNodeCount()).isEqualTo(2);
        assertThat(sitemap.getParent(node)
                          .getUriSegment()).isEqualTo("public");

        // when
        nr = new NodeRecord("public/home/account");
        node = sitemap.append(nr);

        // then
        assertThat(node).isNotNull();
        assertThat(node.getUriSegment()).isEqualTo("account");
        assertThat(sitemap.getNodeCount()).isEqualTo(3);
        assertThat(sitemap.getParent(node)
                          .getUriSegment()).isEqualTo("home");
        assertThat(sitemap.getParent(sitemap.getParent(node))
                          .getUriSegment()).isEqualTo("public");

        // when
        nr = new NodeRecord("public/home/transfer");
        node = sitemap.append(nr);

        // then
        assertThat(node).isNotNull();
        assertThat(node.getUriSegment()).isEqualTo("transfer");
        assertThat(sitemap.getNodeCount()).isEqualTo(4);
        assertThat(sitemap.getParent(node)
                          .getUriSegment()).isEqualTo("home");
        assertThat(sitemap.getParent(sitemap.getParent(node))
                          .getUriSegment()).isEqualTo("public");

        // when
        nr = new NodeRecord("");
        node = sitemap.append(nr);

        // then
        assertThat(node).isNotNull();
        assertThat(node.getUriSegment()).isEqualTo("");
        assertThat(sitemap.getNodeCount()).isEqualTo(5);
        assertThat(sitemap.getRoots()).contains(node);
    }

    @Test
    public void nodeChainForSegments() {

        // given
        sitemap.append(new NodeRecord("public/home/view1"));
        sitemap.append(new NodeRecord("public/home/view2"));
        sitemap.append(new NodeRecord("private/home/wiggly"));
        List<String> segments = new ArrayList<>();
        segments.add("public");
        segments.add("home");
        segments.add("view1");

        // when
        List<MasterSitemapNode> result = sitemap.nodeChainForSegments(segments, true);
        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0)
                         .getUriSegment()).isEqualTo("public");
        assertThat(result.get(1)
                         .getUriSegment()).isEqualTo("home");
        assertThat(result.get(2)
                         .getUriSegment()).isEqualTo("view1");

        // given
        segments.remove(1);

        // when
        result = sitemap.nodeChainForSegments(segments, true);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)
                         .getUriSegment()).isEqualTo("public");

        // when
        result = sitemap.nodeChainForSegments(segments, false);

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void nodeChainForSegments_partial() {

        // given
        sitemap.append(new NodeRecord("public/home/view1"));
        sitemap.append(new NodeRecord("public/home/view2"));
        sitemap.append(new NodeRecord("private/home/wiggly"));
        List<String> segments = new ArrayList<>();
        segments.add("public");
        segments.add("home");
        segments.add("viewx");

        // when
        List<MasterSitemapNode> result = sitemap.nodeChainForSegments(segments, true);
        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)
                         .getUriSegment()).isEqualTo("public");
        assertThat(result.get(1)
                         .getUriSegment()).isEqualTo("home");

    }

    @Test
    public void getRedirectFor() {

        // given
        sitemap.append(new NodeRecord("public/home/view1"));
        sitemap.append(new NodeRecord("public/home/view2"));
        sitemap.append(new NodeRecord("private/home/wiggly"));
        sitemap.addRedirect("home", "public/home");
        // when redirect exists
        String page = sitemap.getRedirectPageFor("home");
        // then
        assertThat(page).isEqualTo("public/home");
        // when redirect does not exist
        page = sitemap.getRedirectPageFor("wiggly");
        assertThat(page).isEqualTo("wiggly");
    }

    @Test
    public void uris() {

        // given
        sitemap.append(new NodeRecord("public/home/view1"));
        sitemap.append(new NodeRecord("public/home/view2"));
        sitemap.append(new NodeRecord("private/home/wiggly"));

        // when

        // then
        assertThat(sitemap.uris()).containsOnly("public/home/view1", "public/home/view2", "private/home/wiggly", "private/home", "private", "public/home",
                "public");

    }

    @Test
    public void hasUri() {

        // given
        sitemap.append(new NodeRecord("public/home/view1"));
        sitemap.append(new NodeRecord("public/home/view2"));
        sitemap.append(new NodeRecord("private/home/wiggly"));

        // when

        // then
        assertThat(sitemap.hasUri("public/home")).isTrue();
        assertThat(sitemap.hasUri("private/home")).isTrue();
        assertThat(sitemap.hasUri("private/home/wiggly")).isTrue();

    }

    @Test
    public void hasURINavState() {

        // given
        sitemap.append(new NodeRecord("public/home/view1"));
        sitemap.append(new NodeRecord("public/home/view2"));
        sitemap.append(new NodeRecord("private/home/wiggly"));
        // when
        NavigationState navigationState1 = uriHandler.navigationState("public/home/view1");
        NavigationState navigationState3 = uriHandler.navigationState("public/home/view3");
        // then

        assertThat(sitemap.hasUri(navigationState1)).isTrue();
        assertThat(sitemap.hasUri(navigationState3)).isFalse();
    }

    @Test
    public void redirectFor() {

        // given
        sitemap.append(new NodeRecord("public/home/view1"));
        sitemap.append(new NodeRecord("public/home/view2"));
        sitemap.append(new NodeRecord("private/home/wiggly"));
        sitemap.addRedirect("public/home/view1", "public/home/view2");
        // when
        MasterSitemapNode node1 = sitemap.nodeFor("public/home/view1");
        SitemapNode node2 = sitemap.nodeFor("public/home/view2");
        // then
        assertThat(sitemap.getRedirectNodeFor(node1)).isEqualTo(node2);

    }

    /**
     *
     */

    @Test
    public void nodeFor_uri() {

        // given
        sitemap.append(new NodeRecord("public/home/view1"));
        sitemap.append(new NodeRecord("public/home/view2"));
        sitemap.append(new NodeRecord("private/home/wiggly"));
        // when
        MasterSitemapNode node1 = sitemap.nodeFor("public/home/view1");
        SitemapNode node2 = sitemap.nodeFor("public/home/view2");
        // then
        assertThat(node1.getUriSegment()).isEqualTo("view1");
        assertThat(sitemap.getParent(node1)
                          .getUriSegment()).isEqualTo("home");
        assertThat(node2.getUriSegment()).isEqualTo("view2");
    }

    @Test
    public void nodeFor_navState() {
        // given
        sitemap.append(new NodeRecord("public/home/view1"));
        sitemap.append(new NodeRecord("public/home/view2"));
        sitemap.append(new NodeRecord("private/home/wiggly"));
        // when
        NavigationState navigationState = uriHandler.navigationState("public/home/view2");
        SitemapNode node1 = sitemap.nodeFor(navigationState);
        // then
        assertThat(node1.getUriSegment()).isEqualTo("view2");
    }

    @Test
    public void nodeFor_emptyString() {
        // given
        sitemap.append(new NodeRecord("public/home/view2"));
        sitemap.append(new NodeRecord("private/home/wiggly"));
        sitemap.append(new NodeRecord(""));
        // when
        MasterSitemapNode node1 = sitemap.nodeFor("");
        // then
        assertThat(node1.getUriSegment()).isEqualTo("");
        assertThat(sitemap.getParent(node1)).isNull();

    }

    @Test
    public void nodeNearestFor() {

        // given
        sitemap.append(new NodeRecord("public/home/view1"));
        sitemap.append(new NodeRecord("public/home/view2"));
        sitemap.append(new NodeRecord("private/home/wiggly"));
        // when
        MasterSitemapNode node1 = sitemap.nodeNearestFor(uriHandler.navigationState("public/home/view3"));
        SitemapNode node2 = sitemap.nodeNearestFor("public/home/view3");
        SitemapNode node3 = sitemap.nodeNearestFor("public/home");

        // then

        assertThat(node1).isEqualTo(node3);
        assertThat(node1).isEqualTo(node2);
    }

    @Test
    public void multiLevelRedirect() {

        // given
        sitemap.append(new NodeRecord("public/home/view1"));
        sitemap.append(new NodeRecord("public/home/view2"));
        sitemap.append(new NodeRecord("public/home/view3"));
        sitemap.append(new NodeRecord("public/home/view4"));
        sitemap.addRedirect("public/home/view1", "public/home/view2");
        sitemap.addRedirect("public/home/view2", "public/home/view3");
        // when

        // then
        assertThat(sitemap.getRedirectPageFor("public/home/view1")).isEqualTo("public/home/view3");
    }

    @Test
    public void replaceNode() {
        //given
        MasterSitemapNode grandparent = new MasterSitemapNode(1, "public", PublicHomeView.class, TestLabelKey.Home, -1, PUBLIC, null);
        MasterSitemapNode parent = new MasterSitemapNode(2, "home", PublicHomeView.class, TestLabelKey.Home, -1, PUBLIC, null);
        MasterSitemapNode child = new MasterSitemapNode(3, "login", LoginView.class, TestLabelKey.Login, -1, PUBLIC, null);
        MasterSitemapNode newParent = new MasterSitemapNode(4, "home", PublicHomeView.class, TestLabelKey.Home, -1, AUTHENTICATION, null);
        sitemap.addChild(grandparent, parent);
        sitemap.addChild(parent, child);
        //when
        sitemap.replaceNode(parent, newParent);
        //then
        assertThat(sitemap.getParent(child)).isEqualTo(newParent);
        assertThat(sitemap.getChildren(grandparent)).containsOnly(newParent);
        assertThat(sitemap.getNodeCount()).isEqualTo(3);
        assertThat(sitemap.uriMap.get("public/home")).isEqualTo(newParent);
        assertThat(sitemap.nodeFor("public/home")).isEqualTo(newParent);

    }

    @Test
    public void replaceNodeAtRoot() {
        //given
        MasterSitemapNode grandparent = new MasterSitemapNode(1, "public", PublicHomeView.class, TestLabelKey.Home, -1, PUBLIC, null);
        MasterSitemapNode parent1 = new MasterSitemapNode(2, "home1", PublicHomeView.class, TestLabelKey.Home, -1, PUBLIC, null);
        MasterSitemapNode parent2 = new MasterSitemapNode(3, "home2", PublicHomeView.class, TestLabelKey.Home, -1, PUBLIC, null);
        MasterSitemapNode child1 = new MasterSitemapNode(4, "login", LoginView.class, TestLabelKey.Login, -1, PUBLIC, null);
        MasterSitemapNode child2 = new MasterSitemapNode(5, "login", LoginView.class, TestLabelKey.Login, -1, PUBLIC, null);
        MasterSitemapNode newGrandParent = new MasterSitemapNode(6, "public", PublicHomeView.class, TestLabelKey.Home, -1, PUBLIC, null);
        sitemap.addChild(grandparent, parent1);
        sitemap.addChild(grandparent, parent2);
        sitemap.addChild(parent1, child1);
        sitemap.addChild(parent1, child2);
        //when
        sitemap.replaceNode(grandparent, newGrandParent);
        //then
        assertThat(sitemap.getParent(newGrandParent)).isNull();
        assertThat(sitemap.getChildren(newGrandParent)).containsOnly(parent1, parent2);
        assertThat(sitemap.getChildren(parent1)).containsOnly(child1, child2);
        assertThat(sitemap.getChildren(parent2)).containsOnly();
        assertThat(sitemap.getChildren(child1)).containsOnly();
        assertThat(sitemap.getChildren(child2)).containsOnly();


    }


    @Test
    public void replace_standardKey_change() {
        //given
        MasterSitemapNode grandparent = new MasterSitemapNode(1, "public", PublicHomeView.class, TestLabelKey.Home, -1, PUBLIC, null);
        MasterSitemapNode parent = new MasterSitemapNode(2, "home", PublicHomeView.class, Public_Home, -1, PUBLIC, null);
        MasterSitemapNode child = new MasterSitemapNode(3, "login", LoginView.class, TestLabelKey.Login, -1, PUBLIC, null);
        MasterSitemapNode newParent = new MasterSitemapNode(2, "home", PublicHomeView.class, Public_Home, -1, AUTHENTICATION, null);
        sitemap.addChild(grandparent, parent);
        sitemap.addChild(parent, child);
        //        sitemap.addStandardPage(Public_Home, parent);
        //when
        sitemap.replaceNode(parent, newParent);
        //then
        assertThat(sitemap.getParent(child)).isEqualTo(newParent);
        assertThat(sitemap.getChildren(grandparent)).containsOnly(newParent);
        assertThat(sitemap.getNodeCount()).isEqualTo(3);
        assertThat(sitemap.standardPageNode(Public_Home)
                          .getPageAccessControl()).isEqualTo(AUTHENTICATION);
    }

    @Test
    public void replace_only_old_is_standard_page() {
        //given
        MasterSitemapNode grandparent = new MasterSitemapNode(1, "public", PublicHomeView.class, TestLabelKey.Home, -1, PUBLIC, null);
        MasterSitemapNode parent = new MasterSitemapNode(2, "home", PublicHomeView.class, Public_Home, -1, PUBLIC, null);
        MasterSitemapNode child = new MasterSitemapNode(3, "login", LoginView.class, TestLabelKey.Login, -1, PUBLIC, null);
        MasterSitemapNode newParent = new MasterSitemapNode(4, "home", PublicHomeView.class, TestLabelKey.Yes, -1, AUTHENTICATION, null);
        sitemap.addChild(grandparent, parent);
        sitemap.addChild(parent, child);
        //        sitemap.addStandardPage(Public_Home, parent);
        //when
        sitemap.replaceNode(parent, newParent);

        //then
        assertThat(sitemap.getParent(child)).isEqualTo(newParent);
        assertThat(sitemap.getChildren(grandparent)).containsOnly(newParent);
        assertThat(sitemap.getNodeCount()).isEqualTo(3);
        assertThat(sitemap.standardPageNode(Public_Home)).isNull();
    }

    @Test
    public void replace_only_new_is_standard_page() {
        //given
        MasterSitemapNode grandparent = new MasterSitemapNode(1, "public", PublicHomeView.class, TestLabelKey.Home, -1, PUBLIC, null);
        MasterSitemapNode parent = new MasterSitemapNode(2, "home", PublicHomeView.class, TestLabelKey.Yes, -1, PUBLIC, null);
        MasterSitemapNode child = new MasterSitemapNode(3, "login", LoginView.class, TestLabelKey.Login, -1, PUBLIC, null);
        MasterSitemapNode newParent = new MasterSitemapNode(2, "home", PublicHomeView.class, Public_Home, -1, AUTHENTICATION, null);
        sitemap.addChild(grandparent, parent);
        sitemap.addChild(parent, child);
        //        sitemap.addStandardPage(Public_Home, parent);
        //when
        sitemap.replaceNode(parent, newParent);
        //then
        assertThat(sitemap.getParent(child)).isEqualTo(newParent);
        assertThat(sitemap.getChildren(grandparent)).containsOnly(newParent);
        assertThat(sitemap.getNodeCount()).isEqualTo(3);
        assertThat(sitemap.standardPageNode(Public_Home)
                          .getPageAccessControl()).isEqualTo(AUTHENTICATION);
    }

    /**
     * AddChild should add standard page if it is one
     */
    @Test
    public void add_child_standard_page_parent() {
        //given
        when(navState.getVirtualPage()).thenReturn("public");
        MasterSitemapNode parent = new MasterSitemapNode(1, "public", PublicHomeView.class, StandardPageKey.Public_Home, -1, PUBLIC, null);
        MasterSitemapNode child = new MasterSitemapNode(2, "home", PublicHomeView.class, TestLabelKey.Yes, -1, PUBLIC, null);
        //when
        sitemap.addChild(parent, child);
        //then
        assertThat(sitemap.getStandardPages()).containsKey(StandardPageKey.Public_Home);
        assertThat(sitemap.standardPageNode(StandardPageKey.Public_Home)).isEqualTo(parent);
        assertThat(sitemap.isPublicHomeUri(navState)).isTrue();
        assertThat(sitemap.isPrivateHomeUri(navState)).isFalse();
    }


    @Test
    public void add_child_standard_page_child() {
        //given
        when(navState.getVirtualPage()).thenReturn("public/home");
        MasterSitemapNode parent = new MasterSitemapNode(1, "public", PublicHomeView.class, TestLabelKey.Yes, -1, PUBLIC, null);
        MasterSitemapNode child = new MasterSitemapNode(2, "home", PublicHomeView.class, StandardPageKey.Public_Home, -1, PUBLIC, null);
        //when
        sitemap.addChild(parent, child);
        //then
        assertThat(sitemap.getStandardPages()).containsKey(StandardPageKey.Public_Home);
        assertThat(sitemap.standardPageNode(StandardPageKey.Public_Home)).isEqualTo(child);
        assertThat(sitemap.isPublicHomeUri(navState)).isTrue();
        assertThat(sitemap.isPrivateHomeUri(navState)).isFalse();
    }

    @Test
    public void remove_node_removes_standard_page() {
        when(navState.getVirtualPage()).thenReturn("public/home");
        MasterSitemapNode parent = new MasterSitemapNode(1, "public", PublicHomeView.class, TestLabelKey.Yes, -1, PUBLIC, null);
        MasterSitemapNode child = new MasterSitemapNode(2, "home", PublicHomeView.class, StandardPageKey.Public_Home, -1, PUBLIC, null);
        //when
        sitemap.addChild(parent, child);
        //then
        assertThat(sitemap.getStandardPages()).containsKey(StandardPageKey.Public_Home);
        assertThat(sitemap.getStandardPageUris()).containsKey("public/home");
        //when
        sitemap.removeNode(child);
        //then
        assertThat(sitemap.getStandardPages()).doesNotContainKey(StandardPageKey.Public_Home);
        assertThat(sitemap.getStandardPageUris()).doesNotContainKey("public/home");
    }

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(Option.class).toInstance(option);
                bind(InMemoryOptionStore.class).to(DefaultInMemoryOptionStore.class);
            }

        };
    }
}
