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

package fixture

import com.google.common.collect.ImmutableList
import com.google.inject.Inject
import fixture.testviews2.TestLoginView
import fixture.testviews2.TestLogoutView
import fixture.testviews2.TestPrivateHomeView
import fixture.testviews2.TestPublicHomeView
import fixture.testviews2.ViewA
import fixture.testviews2.ViewA1
import fixture.testviews2.ViewA11
import fixture.testviews2.ViewA111
import fixture.testviews2.ViewB
import fixture.testviews2.ViewB1
import fixture.testviews2.ViewB11
import fixture.testviews2.ViewB12
import fixture.testviews2.ViewB121
import fixture.testviews2.ViewB122
import uk.q3c.krail.core.eventbus.SessionBusProvider
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.navigate.URIFragmentHandler
import uk.q3c.krail.core.navigate.sitemap.DefaultUserSitemap
import uk.q3c.krail.core.navigate.sitemap.EmptyView
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode
import uk.q3c.krail.core.navigate.sitemap.StandardPageKey
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode
import uk.q3c.krail.core.shiro.PageAccessControl
import uk.q3c.krail.core.view.EmptyViewConfiguration
import uk.q3c.krail.core.view.KrailView
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.test.TestLabelKey
import uk.q3c.util.guice.SerializationSupport
import java.text.Collator
import java.util.*

/**
 * Provides a user sitemap with page layout:
 *
 *
 * -Public<br></br>
 * --Logout<br></br>
 * --ViewA<br></br>
 * ---ViewA1<br></br>
 * ----ViewA11<br></br>
 * -----ViewA111<br></br> excluded
 * -----ViewA112<br></br>
 * -----ViewA113<br></br>
 * -----ViewA114<br></br>
 * --Login<br></br>
 * --Public Home
 *
 *
 * -Private<br></br>
 * --Private Home<br></br>
 * --ViewB<br></br>
 * ---ViewB1<br></br>
 * ----ViewB11<br></br> excluded
 * ----ViewB12<br></br>
 * -----ViewB121<br></br>
 * -----ViewB122<br></br> excluded
 * <br></br>
 * Insertion order ascending is set to be the same as UK alpha ascending <br></br>
 * <br></br>
 * Position index is set to be the reverse of alphabetic order
 *
 * @author dsowerby
 */
class ReferenceUserSitemap
/**
 * Insertion order ascending is set to be the same as UK alpha ascending <br></br>
 * <br></br>
 * Position index is set to be the reverse of alphabetic order
 */
@Inject
constructor(translate: Translate, uriHandler: URIFragmentHandler, sessionBusProvider: SessionBusProvider, serializationSupport: SerializationSupport) : DefaultUserSitemap(translate, uriHandler, sessionBusProvider, serializationSupport) {

    var aFragment = "public/a"
    var aViewClass: Class<out KrailView> = ViewA::class.java
    var a1Fragment = "public/a/a1"
    var a1ViewClass: Class<out KrailView> = ViewA1::class.java
    var a11Fragment = "public/a/a1/a11"
    var a11ViewClass: Class<out KrailView> = ViewA11::class.java
    var a111Fragment = "public/a/a1/a11/a111"
    var a111ViewClass: Class<out KrailView> = ViewA111::class.java // excluded
    var a112Fragment = "public/a/a1/a11/a112"
    var a112ViewClass: Class<out KrailView> = ViewA111::class.java
    var a113Fragment = "public/a/a1/a11/a113"
    var a113ViewClass: Class<out KrailView> = ViewA111::class.java
    var a114Fragment = "public/a/a1/a11/a114"
    var a114ViewClass: Class<out KrailView> = ViewA111::class.java

    var bFragment = "private/b"
    var bViewClass: Class<out KrailView> = ViewB::class.java
    var b1Fragment = "private/b/b1"
    var b1ViewClass: Class<out KrailView> = ViewB1::class.java
    var b11Fragment = "private/b/b1/b11"
    var b11ViewClass: Class<out KrailView> = ViewB11::class.java // excluded
    var b12Fragment = "private/b/b1/b12"
    var b12ViewClass: Class<out KrailView> = ViewB12::class.java


    var b121Fragment = "private/b/b1/b12/b121"
    var b121ViewClass: Class<out KrailView> = ViewB121::class.java
    var b122Fragment = "private/b/b1/b12/b122"
    var b122ViewClass: Class<out KrailView> = ViewB122::class.java // excluded


    var loginFragment = "public/login"
    var logoutFragment = "public/logout"
    var privateFragment = "private"
    var publicFragment = "public"
    var privateHomeFragment = "private/home"
    var publicHomeFragment = "public/home"
    var loginViewClass: Class<out KrailView> = TestLoginView::class.java
    var logoutViewClass: Class<out KrailView> = TestLogoutView::class.java
    var privateHomeViewClass: Class<out KrailView> = TestPrivateHomeView::class.java
    var publicHomeViewClass: Class<out KrailView> = TestPublicHomeView::class.java
    var insertionOrder: LinkedList<String>
    var positionIndexes: MutableMap<String, Int>
    var a11Node: UserSitemapNode? = null
    var a111Node: UserSitemapNode? = null
    var a112Node: UserSitemapNode? = null
    var a113Node: UserSitemapNode? = null
    var a114Node: UserSitemapNode? = null
    var a1Node: UserSitemapNode? = null
    var aNode: UserSitemapNode? = null
    var b11Node: UserSitemapNode? = null
    var b12Node: UserSitemapNode? = null
    var b121Node: UserSitemapNode? = null
    var b122Node: UserSitemapNode? = null

    var b1Node: UserSitemapNode? = null
    var bNode: UserSitemapNode? = null
    var loginNode: UserSitemapNode? = null
    var logoutNode: UserSitemapNode? = null
    var privateHomeNode: UserSitemapNode? = null
    var privateNode: UserSitemapNode? = null
    var publicHomeNode: UserSitemapNode? = null
    var publicNode: UserSitemapNode? = null


    init {

        insertionOrder = LinkedList()
        positionIndexes = HashMap()


        insertionOrder.add(privateFragment)
        positionIndexes[privateFragment] = 4

        insertionOrder.add(publicFragment)
        positionIndexes[publicFragment] = 2

        insertionOrder.add(loginFragment)
        positionIndexes[loginFragment] = 8

        insertionOrder.add(logoutFragment)
        positionIndexes[logoutFragment] = 7

        insertionOrder.add(publicHomeFragment)
        positionIndexes[publicHomeFragment] = 6

        insertionOrder.add(bFragment)
        positionIndexes[bFragment] = 3


        insertionOrder.add(b1Fragment)
        positionIndexes[b1Fragment] = 3

        insertionOrder.add(b11Fragment)
        positionIndexes[b11Fragment] = -1 //This one should not be in nav components

        insertionOrder.add(b12Fragment)
        positionIndexes[b12Fragment] = 2

        insertionOrder.add(b121Fragment)
        positionIndexes[b121Fragment] = 1

        insertionOrder.add(b122Fragment)
        positionIndexes[b122Fragment] = -1//This one should not be in nav components


        insertionOrder.add(aFragment)
        positionIndexes[aFragment] = 5

        insertionOrder.add(a1Fragment)
        positionIndexes[a1Fragment] = 5

        insertionOrder.add(a11Fragment)
        positionIndexes[a11Fragment] = 5

        insertionOrder.add(a111Fragment)
        positionIndexes[a111Fragment] = -1//This one should not be in nav components

        insertionOrder.add(privateHomeFragment)
        positionIndexes[privateHomeFragment] = 13

        insertionOrder.add(a112Fragment)
        positionIndexes[a112Fragment] = 15

        insertionOrder.add(a113Fragment)
        positionIndexes[a113Fragment] = 16

        insertionOrder.add(a114Fragment)
        positionIndexes[a114Fragment] = 17


    }

    fun populate() {
        createStandardPages()
        createPages()
    }

    /**
     * Creates the nodes and pages for standard pages, including intermediate (public and private) pages.
     */
    private fun createStandardPages() {
        loginNode = createNode(loginFragment, "login", loginViewClass, StandardPageKey.Log_In, PageAccessControl.PUBLIC)
        logoutNode = createNode(logoutFragment, "logout", logoutViewClass, StandardPageKey.Log_Out, PageAccessControl.PUBLIC)
        privateHomeNode = createNode(privateHomeFragment, "home", privateHomeViewClass, StandardPageKey.Private_Home, PageAccessControl.PUBLIC)
        publicHomeNode = createNode(publicHomeFragment, "home", publicHomeViewClass, StandardPageKey.Public_Home, PageAccessControl.PUBLIC)

        publicNode = createNode(publicFragment, "public", EmptyView::class.java, LabelKey.Public, PageAccessControl.PUBLIC)
        privateNode = createNode(privateFragment, "private", EmptyView::class.java, LabelKey.Private, PageAccessControl.PERMISSION)

        addChild(publicNode, publicHomeNode!!)
        addChild(publicNode, loginNode!!)
        addChild(publicNode, logoutNode!!)
        addChild(privateNode, privateHomeNode!!)

        //        addStandardPage(StandardPageKey.Log_In, loginNode);
        //        addStandardPage(StandardPageKey.Log_Out, logoutNode);
        //        addStandardPage(StandardPageKey.Public_Home, publicHomeNode);
        //        addStandardPage(StandardPageKey.Private_Home, privateHomeNode);
    }

    fun createNode(fullURI: String, uriSegment: String, viewClass: Class<out KrailView>, labelKey: I18NKey, pageAccessControl: PageAccessControl, roles: List<String> = listOf()): UserSitemapNode {

        val collator = Collator.getInstance()

        val r: ImmutableList<String> = ImmutableList.copyOf(roles)
        val id = insertionOrder.indexOf(fullURI)
        val positionIndex = positionIndexes[fullURI]!!
        val masterNode = MasterSitemapNode(id = id, uriSegment = uriSegment, viewClass = viewClass, labelKey = labelKey, positionIndex = positionIndex, pageAccessControl = pageAccessControl, roles = r, viewConfiguration = EmptyViewConfiguration::class.java)

        val node = UserSitemapNode(masterNode)
        node.label = translate.from(labelKey)
        val collationKey = collator.getCollationKey(node.label)
        node.collationKey = collationKey

        return node
    }

    private fun createPages() {
        aNode = createNode(aFragment, "a", aViewClass, TestLabelKey.ViewA, PageAccessControl.PUBLIC)
        a1Node = createNode(a1Fragment, "a1", a1ViewClass, TestLabelKey.ViewA1, PageAccessControl.PUBLIC)
        a11Node = createNode(a11Fragment, "a11", a11ViewClass, TestLabelKey.ViewA11, PageAccessControl.PUBLIC)
        a111Node = createNode(a111Fragment, "a111", a111ViewClass, TestLabelKey.ViewA111, PageAccessControl.USER)
        a112Node = createNode(a112Fragment, "a112", a112ViewClass, OtherLabelKey.ViewA112, PageAccessControl.GUEST)
        a113Node = createNode(a113Fragment, "a113", a113ViewClass, OtherLabelKey.ViewA113, PageAccessControl.AUTHENTICATION)
        a114Node = createNode(a114Fragment, "a114", a114ViewClass, OtherLabelKey.ViewA114, PageAccessControl.ROLES)

        addChild(publicNode, aNode!!)
        addChild(aNode, a1Node!!)
        addChild(a1Node, a11Node!!)
        addChild(a11Node, a111Node!!)
        addChild(a11Node, a112Node!!)
        addChild(a11Node, a113Node!!)
        addChild(a11Node, a114Node!!)

        bNode = createNode(bFragment, "b", bViewClass, TestLabelKey.ViewB, PageAccessControl.PERMISSION)
        b1Node = createNode(b1Fragment, "b1", b1ViewClass, TestLabelKey.ViewB1, PageAccessControl.PERMISSION)
        b11Node = createNode(b11Fragment, "b11", b1ViewClass, TestLabelKey.ViewB11, PageAccessControl.PERMISSION)
        b12Node = createNode(b12Fragment, "b12", b12ViewClass, TestLabelKey.ViewB12, PageAccessControl.PERMISSION)
        b121Node = createNode(b121Fragment, "b121", b121ViewClass, TestLabelKey.ViewB121, PageAccessControl.PERMISSION)
        b122Node = createNode(b122Fragment, "b122", b122ViewClass, TestLabelKey.ViewB122, PageAccessControl.PERMISSION)

        addChild(privateNode, bNode!!)
        addChild(bNode, b1Node!!)
        addChild(b1Node, b11Node!!)
        addChild(b1Node, b12Node!!)
        addChild(b12Node, b121Node!!)
        addChild(b12Node, b122Node!!)
    }


    fun publicSortedAlphaAscending(): List<UserSitemapNode> {
        val list = LinkedList<UserSitemapNode>()
        list.add(loginNode())
        list.add(publicHomeNode())
        list.add(aNode())
        return list
    }

    fun loginNode(): UserSitemapNode {
        return nodeFor(loginFragment)
    }

    fun publicHomeNode(): UserSitemapNode {
        return nodeFor(publicHomeFragment)
    }

    fun aNode(): UserSitemapNode {
        return nodeFor(aFragment)
    }

    fun publicSortedAlphaDescending(): List<UserSitemapNode> {
        val list = LinkedList<UserSitemapNode>()
        list.add(aNode())
        list.add(publicHomeNode())
        list.add(loginNode())
        return list
    }

    fun publicSortedInsertionAscending(): List<UserSitemapNode> {
        val list = LinkedList<UserSitemapNode>()
        list.add(loginNode())
        list.add(publicHomeNode())
        list.add(aNode())
        return list
    }

    fun publicSortedInsertionDescending(): List<UserSitemapNode> {
        val list = LinkedList<UserSitemapNode>()
        list.add(aNode())
        list.add(publicHomeNode())
        list.add(loginNode())
        return list
    }

    fun publicSortedPositionAscending(): List<UserSitemapNode> {
        val list = LinkedList<UserSitemapNode>()
        list.add(aNode())
        list.add(publicHomeNode())
        list.add(loginNode())
        return list
    }

    fun publicSortedPositionDescending(): List<UserSitemapNode> {
        val list = LinkedList<UserSitemapNode>()
        list.add(loginNode())
        list.add(publicHomeNode())
        list.add(aNode())
        return list
    }

    fun logoutNode(): UserSitemapNode {
        return nodeFor(logoutFragment)
    }

    fun privateHomeNode(): UserSitemapNode {
        return nodeFor(privateHomeFragment)
    }

    fun publicNode(): UserSitemapNode {
        return nodeFor(publicFragment)
    }

    fun privateNode(): UserSitemapNode {
        return nodeFor(privateFragment)
    }

    fun a1Node(): UserSitemapNode {
        return nodeFor(a1Fragment)
    }

    fun a11Node(): UserSitemapNode {
        return nodeFor(a11Fragment)
    }

    fun bNode(): UserSitemapNode {
        return nodeFor(bFragment)
    }

    fun b1Node(): UserSitemapNode {
        return nodeFor(b1Fragment)
    }

    fun b11Node(): UserSitemapNode {
        return nodeFor(b11Fragment)
    }

    fun b121Node(): UserSitemapNode {
        return nodeFor(b121Fragment)
    }

    fun b122Node(): UserSitemapNode {
        return nodeFor(b122Fragment)
    }

    fun a111Node(): UserSitemapNode {
        return nodeFor(a111Fragment)
    }


    fun b12Node(): UserSitemapNode {
        return nodeFor(b12Fragment)
    }
}


enum class OtherLabelKey : I18NKey {
    ViewA112, ViewA113, ViewA114
}