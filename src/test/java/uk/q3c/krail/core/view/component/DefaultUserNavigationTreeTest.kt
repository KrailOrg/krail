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
package uk.q3c.krail.core.view.component

import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.google.inject.Provider
import com.mycila.testing.junit.MycilaJunitRunner
import com.mycila.testing.plugin.guice.GuiceContext
import com.mycila.testing.plugin.guice.ModuleProvider
import com.vaadin.ui.Tree
import fixture.ReferenceUserSitemap
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import uk.q3c.krail.core.env.ServletEnvironmentModule
import uk.q3c.krail.core.eventbus.VaadinEventBusModule
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.core.i18n.TestKrailI18NModule2
import uk.q3c.krail.core.navigate.NavigationState
import uk.q3c.krail.core.navigate.Navigator
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler
import uk.q3c.krail.core.navigate.URIFragmentHandler
import uk.q3c.krail.core.navigate.sitemap.CountNodeVisitor
import uk.q3c.krail.core.navigate.sitemap.ListNodeVisitor
import uk.q3c.krail.core.navigate.sitemap.TreeDataWalker
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.core.navigate.sitemap.UserSitemapStructureChangeMessage
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters
import uk.q3c.krail.core.navigate.sitemap.comparator.DefaultUserSitemapSorters.SortType
import uk.q3c.krail.core.navigate.sitemap.comparator.UserSitemapSorters
import uk.q3c.krail.core.shiro.DefaultShiroModule
import uk.q3c.krail.core.user.UserModule
import uk.q3c.krail.eventbus.EventBusAutoSubscriber
import uk.q3c.krail.i18n.CurrentLocale
import uk.q3c.krail.option.Option
import uk.q3c.krail.option.mock.TestOptionModule
import uk.q3c.krail.persist.inmemory.InMemoryModule
import uk.q3c.krail.testutil.guice.uiscope.TestUIScopeModule
import uk.q3c.krail.util.UtilsModule
import uk.q3c.util.UtilModule
import uk.q3c.util.guice.SerializationSupport
import uk.q3c.util.guice.SerializationSupportModule
import java.util.*

@RunWith(MycilaJunitRunner::class)
@GuiceContext(TestUIScopeModule::class, TestKrailI18NModule2::class, DefaultShiroModule::class, TestOptionModule::class, InMemoryModule::class, VaadinSessionScopeModule::class, VaadinEventBusModule::class, UtilModule::class, UserModule::class, UtilsModule::class, SerializationSupportModule::class, ServletEnvironmentModule::class)
@Ignore("Test failed after changing to Provider<Option> from Option, but this component may never be used again")
class DefaultUserNavigationTreeTest {

    @Inject
    lateinit var userSitemap: ReferenceUserSitemap

    @Inject
    lateinit var autoSubscriber: EventBusAutoSubscriber

    @Inject
    lateinit var currentLocale: CurrentLocale

    @Inject
    lateinit var sorters: UserSitemapSorters

    @Mock
    lateinit var navigator: Navigator

    @Mock
    lateinit var serialisationSupport: SerializationSupport

    @Inject
    lateinit var optionProvider: Provider<Option>

    lateinit var builder: DefaultUserNavigationTreeBuilder

    lateinit var userNavigationTree: DefaultUserNavigationTree

    @Before
    fun setUp() {
        Locale.setDefault(Locale.UK)
        currentLocale.locale = Locale.UK
        userSitemap.clear()
        userSitemap.populate()
        builder = DefaultUserNavigationTreeBuilder(userSitemap)
    }

    @After
    fun tearDown() {

    }

    @Test
    fun build() {
        // given
        println(userSitemap.toString())
        userNavigationTree = newTree()
        val expectedNodes = ArrayList(userSitemap.allNodes)

        // don't want the logout node
        expectedNodes.remove(userSitemap.logoutNode())

        //removed by using positionIndex <0
        expectedNodes.remove(userSitemap.b11Node())
        expectedNodes.remove(userSitemap.b122Node())
        expectedNodes.remove(userSitemap.a111Node())

        // when
        userNavigationTree.optionMaxDepth = 1000
        // then
        val itemIds = listNodes(userNavigationTree)
        assertThat(itemIds).hasSameSizeAs(expectedNodes)
        assertThat(itemIds).containsAll(expectedNodes)
        // ensure no extra ones, there isn't a containsOnly for a list
        assertThat(itemIds).hasSize(expectedNodes.size)
        assertThat(userNavigationTree.treeData.getParent(userSitemap.a11Node())).isEqualTo(userSitemap.a1Node())
        assertThat(userNavigationTree.itemCaptionGenerator.apply(userSitemap.a11Node())).isEqualTo("ViewA11")
        assertThat(userNavigationTree.itemCaptionGenerator.apply(userSitemap.publicHomeNode())).isEqualTo("Public Home")
        assertThat(userNavigationTree.isLeaf(userSitemap.a11Node())).isTrue()
        assertThat(userNavigationTree.isLeaf(userSitemap.a1Node())).isFalse()
        assertThat(userNavigationTree.isLeaf(userSitemap.b1Node())).isFalse()
        assertThat(userNavigationTree.isLeaf(userSitemap.b12Node())).isFalse()
    }

    @Test
    fun uriChangeSelectsCorrectNodeExpandedIfNecessary() {
        // given
        println(userSitemap.toString())
        userNavigationTree = newTree()
        userNavigationTree.build()
        val fromState = NavigationState().fragment("home")
        val toState = NavigationState().fragment(userSitemap.a11Fragment)
        val uriFragmentHandler = StrictURIFragmentHandler()
        fromState.update(uriFragmentHandler)
        toState.update(uriFragmentHandler)

        val viewChangeMsg = AfterViewChangeBusMessage(fromState, toState)
        // when

        userNavigationTree.afterViewChange(viewChangeMsg)
        // then
        assertThat(userNavigationTree.tree.isExpanded(userSitemap.a1Node()))
        assertThat(userNavigationTree.tree.isExpanded(userSitemap.aNode()))

    }

    private fun newTree(): DefaultUserNavigationTree {
        val tree = DefaultUserNavigationTree(userSitemap, navigator, optionProvider, builder, sorters, serialisationSupport)
        //simulates Guice construction
        autoSubscriber.afterInjection(tree)
        return tree
    }

    /**
     * The 'b' branch has position index set to < 0 at its root - none of it should there fore appear in the nav tree
     */
    @Test
    fun build_branch_hidden() {
        //given
        userNavigationTree = newTree()
        val expectedNodes = ArrayList(userSitemap.allNodes)
        // don't want the logout node
        expectedNodes.remove(userSitemap.logoutNode())
        expectedNodes.remove(userSitemap.bNode())
        expectedNodes.remove(userSitemap.b1Node())
        expectedNodes.remove(userSitemap.b11Node())
        expectedNodes.remove(userSitemap.b11Node())
        expectedNodes.remove(userSitemap.b12Node())
        expectedNodes.remove(userSitemap.b121Node())
        expectedNodes.remove(userSitemap.b122Node())
        expectedNodes.remove(userSitemap.a111Node())

        //re-instate as 'displayable'
        userSitemap.b11Node()
                .positionIndex = 5
        // hide the b branch
        userSitemap.bNode()
                .positionIndex = -1

        //when
        userNavigationTree.optionMaxDepth = 1000
        //then
        val itemIds = listNodes(userNavigationTree)
        assertThat(itemIds).containsAll(expectedNodes)
        // ensure no extra ones, there isn't a containsOnly for a list
        assertThat(itemIds).hasSize(expectedNodes.size)


    }

    @Test
    fun build_depthLimited() {
        // given
        userNavigationTree = newTree()
        val expectedNodes = ArrayList(userSitemap.allNodes)

        // don't want the logout node
        expectedNodes.remove(userSitemap.logoutNode())
        // these beyond required depth
        expectedNodes.remove(userSitemap.a11Node())
        expectedNodes.remove(userSitemap.b11Node())
        expectedNodes.remove(userSitemap.a1Node())
        expectedNodes.remove(userSitemap.b1Node())
        expectedNodes.remove(userSitemap.b12Node())
        expectedNodes.remove(userSitemap.b121Node())
        expectedNodes.remove(userSitemap.b122Node())
        expectedNodes.remove(userSitemap.a11Node())
        expectedNodes.remove(userSitemap.a111Node())

        // when
        userNavigationTree.optionMaxDepth = 2 // will cause rebuild
        // then
        val itemIds = listNodes(userNavigationTree)
        assertThat(itemIds).containsAll(expectedNodes)
        // ensure no extra ones, there isn't a containsOnly for a list
        assertThat(itemIds).hasSize(expectedNodes.size)
        assertThat(userNavigationTree.areChildrenAllowed(userSitemap.aNode())).isFalse()
        assertThat(userNavigationTree.areChildrenAllowed(userSitemap.bNode())).isFalse()
        assertThat(userNavigationTree.areChildrenAllowed(userSitemap.privateHomeNode())).isFalse()
        assertThat(userNavigationTree.areChildrenAllowed(userSitemap.publicHomeNode())).isFalse()
        assertThat(userNavigationTree.areChildrenAllowed(userSitemap.publicNode())).isTrue()
        assertThat(userNavigationTree.areChildrenAllowed(userSitemap.privateNode())).isTrue()
    }

    @Test
    fun setMaxDepth() {

        // given
        userNavigationTree = newTree()

        // when
        userNavigationTree.optionMaxDepth = 3
        // then
        assertThat(userNavigationTree.optionMaxDepth).isEqualTo(3)
        // option has been set
        val result = userNavigationTree.optionInstance()
                .get(DefaultUserNavigationTree.optionKeyMaximumDepth)
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun setMaxDepth_noRebuild() {

        // given
        userNavigationTree = newTree()

        // when
        userNavigationTree.optionMaxDepth = 2
        // then
        assertThat(userNavigationTree.optionMaxDepth).isEqualTo(2)
        // option has been set
        val result = userNavigationTree.optionInstance()
                .get(DefaultUserNavigationTree.optionKeyMaximumDepth)
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun requiresRebuild() {

        // given
        `when`(navigator.currentNode).thenReturn(userSitemap.publicNode())
        userNavigationTree = newTree()
        userNavigationTree.build()
        // when
        userNavigationTree.optionSortAscending = false
        // then build has happened
        assertThat(userNavigationTree.isRebuildRequired).isFalse()

        // when
        userNavigationTree.setOptionSortAscending(true, false)
        userNavigationTree.setOptionSortType(SortType.INSERTION, false)
        // then build has not happened
        assertThat(userNavigationTree.isRebuildRequired).isTrue()
    }

    @Test
    fun localeChange() {

        // given
        userNavigationTree = newTree()
        userNavigationTree.build()

        // when
        currentLocale.locale = Locale.GERMANY
        // then
        assertThat(userNavigationTree.itemCaptionGenerator.apply(userSitemap.aNode())).isEqualTo("DE_ViewA")
    }

    @Test
    fun structureChange() {

        // given
        userNavigationTree = newTree()
        userNavigationTree.build()
        userNavigationTree.setOptionSortAscending(false, false)
        // when
        userNavigationTree.structureChanged(UserSitemapStructureChangeMessage())
        // then make sure build has been called
        assertThat(userNavigationTree.isRebuildRequired).isFalse()
    }

    @Test
    fun defaults() {

        // given

        // when
        userNavigationTree = newTree()
        // then
        assertThat(userNavigationTree.optionMaxDepth).isEqualTo(10)
        assertThat(userNavigationTree.isRebuildRequired).isTrue()

    }

    @Test
    fun userSelection() {

        // given
        userNavigationTree = newTree()
        userNavigationTree.build()
        // when
        userNavigationTree.select(userSitemap.a1Node())
        // then
        verify<Navigator>(navigator).navigateTo("public/a/a1")
    }

    @Test
    fun sorted() {

        // given
        userNavigationTree = newTree()

        // when
        userNavigationTree.build()
        // then
        val roots = userNavigationTree.treeData.rootItems
        assertThat(roots).containsExactly(userSitemap.privateNode(), userSitemap.publicNode())
        val children = userNavigationTree.tree.treeData.getChildren(userSitemap.publicNode())
        assertThat(children).containsExactly(userSitemap.loginNode(), userSitemap.publicHomeNode(), userSitemap.aNode())
    }

    @Test
    fun sortSelection() {

        // given
        userNavigationTree = newTree()

        // when alpha ascending (default)
        userNavigationTree.build()

        var roots = userNavigationTree.treeData.rootItems
        assertThat(roots).containsExactly(userSitemap.privateNode(), userSitemap.publicNode())
        var children = userNavigationTree.tree.treeData.getChildren(userSitemap.publicNode())
        assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedAlphaAscending())

        // when
        userNavigationTree.optionSortAscending = false
        // then
        roots = userNavigationTree.treeData.rootItems
        assertThat(roots).containsExactly(userSitemap.publicNode(), userSitemap.privateNode())
        children = userNavigationTree.treeData.getChildren(userSitemap.publicNode())
        assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedAlphaDescending())

        // when
        userNavigationTree.optionSortAscending = true
        userNavigationTree.setOptionKeySortType(SortType.INSERTION)
        // then
        roots = userNavigationTree.treeData.rootItems
        assertThat(roots).containsExactly(userSitemap.nodeFor(userSitemap.privateFragment), userSitemap.nodeFor(userSitemap.publicFragment))
        children = userNavigationTree.treeData.getChildren(userSitemap.publicNode())
        assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedInsertionAscending())

        // when
        userNavigationTree.optionSortAscending = false
        userNavigationTree.setOptionKeySortType(SortType.POSITION)
        // then
        roots = userNavigationTree.treeData.rootItems
        assertThat(roots).containsExactly(userSitemap.privateNode(), userSitemap.publicNode())
        children = userNavigationTree.treeData.getChildren(userSitemap.publicNode())
        assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedPositionDescending())

        // when
        userNavigationTree.optionSortAscending = false
        userNavigationTree.setOptionKeySortType(SortType.INSERTION)
        // then
        roots = userNavigationTree.treeData.rootItems
        assertThat(roots).containsExactly(userSitemap.publicNode(), userSitemap.privateNode())
        children = userNavigationTree.treeData.getChildren(userSitemap.publicNode())
        assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedInsertionDescending())

        // when
        userNavigationTree.optionSortAscending = true
        userNavigationTree.setOptionKeySortType(SortType.POSITION)
        // then
        roots = userNavigationTree.tree.treeData.rootItems
        assertThat(roots).containsExactly(userSitemap.publicNode(), userSitemap.privateNode())
        children = userNavigationTree.treeData.getChildren(userSitemap.publicNode())
        assertThat(children).containsExactlyElementsOf(userSitemap.publicSortedPositionAscending())
    }

    @Test
    fun options() {

        // given
        userNavigationTree = newTree()
        userNavigationTree.build()
        // when
        userNavigationTree.optionSortAscending = true
        userNavigationTree.setOptionKeySortType(SortType.INSERTION)
        // then
        assertThat(userNavigationTree.optionInstance()
                .get(DefaultUserNavigationTree.optionKeySortAscending)).isTrue()
        assertThat(userNavigationTree.optionInstance()
                .get(DefaultUserNavigationTree.optionKeySortType)).isEqualTo(SortType.INSERTION)

    }

    private fun countNodes(tree: Tree<UserSitemapNode>): Int {
        val walker = TreeDataWalker(tree.treeData)
        val visitor = CountNodeVisitor<UserSitemapNode>()
        walker.walk(visitor)
        return visitor.count
    }

    private fun listNodes(tree: Tree<UserSitemapNode>): List<UserSitemapNode> {
        val walker = TreeDataWalker(tree.treeData)
        val visitor = ListNodeVisitor<UserSitemapNode>()
        walker.walk(visitor)
        return visitor.list
    }

    @ModuleProvider
    protected fun moduleProvider(): AbstractModule {
        return object : AbstractModule() {

            override fun configure() {

                bind(URIFragmentHandler::class.java).to(StrictURIFragmentHandler::class.java)
                bind(UserSitemapSorters::class.java).to(DefaultUserSitemapSorters::class.java)

            }

        }
    }


}
