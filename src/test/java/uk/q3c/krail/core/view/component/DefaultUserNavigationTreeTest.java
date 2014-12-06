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
package uk.q3c.krail.core.view.component;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import fixture.ReferenceUserSitemap;
import fixture.TestI18NModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters;
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType;
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters;
import uk.q3c.krail.core.user.opt.*;
import uk.q3c.krail.i18n.CurrentLocale;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({UIScopeModule.class, TestI18NModule.class, TestUserOptionModule.class})
public class DefaultUserNavigationTreeTest {

    @Inject
    ReferenceUserSitemap userSitemap;

    @Inject
    CurrentLocale currentLocale;

    @Inject
    UserSitemapSorters sorters;

    @Mock
    Navigator navigator;

    @Inject
    UserOption userOption;

    DefaultUserNavigationTreeBuilder builder;

    private DefaultUserNavigationTree userNavigationTree;

    @Before
    public void setUp() throws Exception {
        Locale.setDefault(Locale.UK);
        currentLocale.setLocale(Locale.UK);
        userSitemap.clear();
        userSitemap.populate();
        builder = new DefaultUserNavigationTreeBuilder(userSitemap);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void build() {
        // given

        userNavigationTree = newTree();
        List<UserSitemapNode> expectedNodes = new ArrayList<>(userSitemap.getAllNodes());

        // don't want the logout node
        expectedNodes.remove(userSitemap.logoutNode);

        // when
        userNavigationTree.setOptionMaxDepth(1000);
        // then
        @SuppressWarnings("unchecked") List<UserSitemapNode> itemIds = (List<UserSitemapNode>) userNavigationTree
                .getItemIds();
        assertThat(itemIds).containsAll(expectedNodes);
        // ensure no extra ones, there isn't a containsOnly for a list
        assertThat(itemIds).hasSize(expectedNodes.size());
        assertThat(userNavigationTree.getParent(userSitemap.a11Node)).isEqualTo(userSitemap.a1Node);
        assertThat(userNavigationTree.getItemCaption(userSitemap.a11Node)).isEqualTo("ViewA11");
        assertThat(userNavigationTree.getItemCaption(userSitemap.publicHomeNode)).isEqualTo("Public Home");
        assertThat(userNavigationTree.areChildrenAllowed(userSitemap.a11Node)).isFalse();
        assertThat(userNavigationTree.areChildrenAllowed(userSitemap.a1Node)).isTrue();
    }

    private DefaultUserNavigationTree newTree() {
        return new DefaultUserNavigationTree(userSitemap, navigator, userOption, builder, sorters);
    }

    @Test
    public void build_depthLimited() {
        // given
        userNavigationTree = newTree();
        List<UserSitemapNode> expectedNodes = new ArrayList<>(userSitemap.getAllNodes());

        // don't want the logout node
        expectedNodes.remove(userSitemap.logoutNode);
        // these beyond required depth
        expectedNodes.remove(userSitemap.a11Node);
        expectedNodes.remove(userSitemap.b11Node);
        expectedNodes.remove(userSitemap.a1Node);
        expectedNodes.remove(userSitemap.b1Node);

        // when
        userNavigationTree.setOptionMaxDepth(2); // will cause rebuild
        // then
        @SuppressWarnings("unchecked") List<UserSitemapNode> itemIds = (List<UserSitemapNode>) userNavigationTree
                .getItemIds();
        assertThat(itemIds).containsAll(expectedNodes);
        // ensure no extra ones, there isn't a containsOnly for a list
        assertThat(itemIds).hasSize(expectedNodes.size());
    }

    @Test
    public void setMaxDepth() {

        // given
        userNavigationTree = newTree();

        // when
        userNavigationTree.setOptionMaxDepth(3);
        // then
        assertThat(userNavigationTree.getOptionMaxDepth()).isEqualTo(3);
        // userOption has been set
        int result = userNavigationTree.getUserOption().get(-1, DefaultUserNavigationTree.UserOptionProperty.MAX_DEPTH);
        assertThat(result).isEqualTo(3);
    }

    @Test
    public void setMaxDepth_noRebuild() {

        // given
        userNavigationTree = newTree();

        // when
        userNavigationTree.setOptionMaxDepth(2);
        // then
        assertThat(userNavigationTree.getOptionMaxDepth()).isEqualTo(2);
        // userOption has been set
        int result = userNavigationTree.getUserOption().get(-1, DefaultUserNavigationTree.UserOptionProperty.MAX_DEPTH);
        assertThat(result).isEqualTo(2);
    }

    @Test
    public void requiresRebuild() {

        // given
        when(navigator.getCurrentNode()).thenReturn(userSitemap.publicNode);
        userNavigationTree = newTree();
        userNavigationTree.build();
        // when
        userNavigationTree.setOptionSortAscending(false);
        // then build has happened
        assertThat(userNavigationTree.isRebuildRequired()).isFalse();

        // when
        userNavigationTree.setOptionSortAscending(true, false);
        userNavigationTree.setOptionSortType(SortType.INSERTION, false);
        // then build has not happened
        assertThat(userNavigationTree.isRebuildRequired()).isTrue();
    }

    @Test
    public void localeChange() {

        // given
        userNavigationTree = newTree();
        userNavigationTree.build();

        // when
        currentLocale.setLocale(Locale.GERMANY);
        // then
        assertThat(userNavigationTree.getItemCaption(userSitemap.aNode)).isEqualTo("DE_ViewA");
    }

    @Test
    public void structureChange() {

        // given
        userNavigationTree = newTree();
        userNavigationTree.build();
        userNavigationTree.setOptionSortAscending(false, false);
        // when
        userNavigationTree.structureChanged();
        // then make sure build has been called
        assertThat(userNavigationTree.isRebuildRequired()).isFalse();
    }

    @Test
    public void defaults() {

        // given

        // when
        userNavigationTree = newTree();
        // then
        assertThat(userNavigationTree.isImmediate()).isTrue();
        assertThat(userNavigationTree.getOptionMaxDepth()).isEqualTo(10);
        assertThat(userNavigationTree.isRebuildRequired()).isTrue();

    }

    @Test
    public void userSelection() {

        // given
        userNavigationTree = newTree();
        userNavigationTree.build();
        // when
        userNavigationTree.setValue(userSitemap.a1Node);
        // then
        verify(navigator).navigateTo("public/a/a1");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void sorted() {

        // given
        userNavigationTree = newTree();

        // when
        userNavigationTree.build();
        // then
        Collection<UserSitemapNode> roots = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                                            .rootItemIds();
        assertThat(roots).containsExactly(userSitemap.privateNode, userSitemap.publicNode);
        Collection<UserSitemapNode> children = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                                               .getChildren
                                                                                                       (userSitemap
                                                                                                               .publicNode);
        assertThat(children).containsExactly(userSitemap.loginNode, userSitemap.publicHomeNode, userSitemap.aNode);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void sortSelection() {

        // given
        userNavigationTree = newTree();

        // when alpha ascending (default)
        userNavigationTree.build();

        Collection<UserSitemapNode> roots = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                                            .rootItemIds();
        assertThat(roots).containsExactly(userSitemap.privateNode, userSitemap.publicNode);
        Collection<UserSitemapNode> children = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                                               .getChildren
                                                                                                       (userSitemap
                                                                                                               .publicNode);
        assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedAlphaAscending());

        // when
        userNavigationTree.setOptionSortAscending(false);
        // then
        roots = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                .rootItemIds();
        assertThat(roots).containsExactly(userSitemap.publicNode, userSitemap.privateNode);
        children = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                   .getChildren(userSitemap.publicNode);
        assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedAlphaDescending());

        // when
        userNavigationTree.setOptionSortAscending(true);
        userNavigationTree.setOptionSortType(SortType.INSERTION);
        // then
        roots = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                .rootItemIds();
        assertThat(roots).containsExactly(userSitemap.privateNode, userSitemap.publicNode);
        children = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                   .getChildren(userSitemap.publicNode);
        assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedInsertionAscending());

        // when
        userNavigationTree.setOptionSortAscending(false);
        userNavigationTree.setOptionSortType(SortType.POSITION);
        // then
        roots = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                .rootItemIds();
        assertThat(roots).containsExactly(userSitemap.privateNode, userSitemap.publicNode);
        children = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                   .getChildren(userSitemap.publicNode);
        assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedPositionDescending());

        // when
        userNavigationTree.setOptionSortAscending(false);
        userNavigationTree.setOptionSortType(SortType.INSERTION);
        // then
        roots = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                .rootItemIds();
        assertThat(roots).containsExactly(userSitemap.publicNode, userSitemap.privateNode);
        children = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                   .getChildren(userSitemap.publicNode);
        assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedInsertionDescending());

        // when
        userNavigationTree.setOptionSortAscending(true);
        userNavigationTree.setOptionSortType(SortType.POSITION);
        // then
        roots = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                .rootItemIds();
        assertThat(roots).containsExactly(userSitemap.publicNode, userSitemap.privateNode);
        children = (Collection<UserSitemapNode>) userNavigationTree.getTree()
                                                                   .getChildren(userSitemap.publicNode);
        assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedPositionAscending());
    }

    @Test
    public void options() {

        // given
        userNavigationTree = newTree();
        userNavigationTree.build();
        // when
        userNavigationTree.setOptionSortAscending(true);
        userNavigationTree.setOptionSortType(SortType.INSERTION);
        // then
        assertThat(userNavigationTree.getUserOption().get(false, DefaultUserNavigationTree.UserOptionProperty
                .SORT_ASCENDING)).isTrue();
        assertThat(userNavigationTree.getUserOption().get(SortType.ALPHA,DefaultUserNavigationTree.UserOptionProperty.SORT_TYPE)).isEqualTo(SortType.INSERTION);

    }

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {

                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(UserSitemapSorters.class).to(DefaultUserSitemapSorters.class);

            }

        };
    }

}
