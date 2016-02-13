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

package uk.q3c.krail.core.navigate.sitemap

import spock.lang.Ignore
import spock.lang.Specification
import uk.q3c.krail.core.navigate.NavigationState
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler
import uk.q3c.krail.core.navigate.URIFragmentHandler

/**
 * Created by David Sowerby on 07 Jan 2016
 */
class DefaultMasterSitemapTest_Lock extends Specification {

    DefaultMasterSitemap sitemap

    URIFragmentHandler fragmentHandler = new StrictURIFragmentHandler()

    String uri = '/home/david'

    def setup() {
        sitemap = new DefaultMasterSitemap(fragmentHandler)
        sitemap.append(new NodeRecord(uri))
        NodeRecord nr = new NodeRecord("login")
        nr.setLabelKey(StandardPageKey.Log_In)
        sitemap.append(nr)
        sitemap.lock()
    }

    def "Append"() {
        when:
        sitemap.append(new NodeRecord(uri))

        then:
        thrown(SitemapLockedException)
    }

    def "CreateNode"() {
        when:
        sitemap.createNode(uri)

        then:
        thrown(SitemapLockedException)
    }

    def "GetReport"() {

        expect:
        sitemap.getReport() == null

    }

    def "SetReport"() {
        when:
        sitemap.setReport('report')

        then:
        thrown(SitemapLockedException)
    }

    def "IsLocked"() {
        expect:
        sitemap.isLocked()
    }

    def "lock"() {
        when:
        sitemap.lock()

        then:
        thrown(SitemapLockedException)
    }

    def "GetRootFor"() {
        expect:
        sitemap.getRootFor(new MasterSitemapNode(1, uri)) != null
    }

    def "ContainsNode"() {
        expect:
        !sitemap.containsNode(new MasterSitemapNode(5, uri))
    }

    def "Uri"() {
        expect:
        sitemap.uri(new MasterSitemapNode(1, uri)) != null
    }

    def "GetAllNodes"() {
        expect:
        !sitemap.getAllNodes().isEmpty()
    }

    def "GetRoots"() {
        expect:
        !sitemap.getRoots().isEmpty()
    }

    def "GetChildCount"() {
        when:
        sitemap.getChildCount(new MasterSitemapNode(5, uri))

        then:
        thrown(SitemapException)

    }

    def "HasUri"() {
        expect:
        sitemap.hasUri(uri)
    }

    @Ignore("Groovy cannot cope with method overloading ")
    def "HasUri with nav state"() {

        given:
        NavigationState ns = new NavigationState().setFragment(uri)

        expect:
        sitemap.hasUri((NavigationState) ns)
    }

    def "NavigationState"() {
        expect:
        sitemap.navigationState(new MasterSitemapNode(2, uri))
    }

    def "ToString"() {
        expect:
        sitemap.toString()
    }

    def "PagePermission"() {
        expect:
        sitemap.pagePermission(new MasterSitemapNode(2, uri))
    }

    def "AddNode"() {
        when:
        sitemap.addNode(new MasterSitemapNode(2, uri))

        then:
        thrown(SitemapLockedException)
    }

    def "RemoveNode"() {
        when:
        sitemap.removeNode(new MasterSitemapNode(2, uri))

        then:
        thrown(SitemapLockedException)
    }

    def "NodeNearestFor"() {
        expect:
        sitemap.nodeChainFor(new NavigationState().setFragment(uri))
    }

    def "NodeNearestFor1"() {
        expect:
        sitemap.nodeChainFor(new MasterSitemapNode(2, uri))
    }

    def "StandardPageURI"() {
        expect:
        sitemap.standardPageURI(StandardPageKey.Log_In)
    }

    def "GetStandardPages"() {
        expect:
        sitemap.getStandardPages()
    }

    def "GetChildren"() {
        expect:
        sitemap.getChildren(new MasterSitemapNode(2, uri)).isEmpty()
    }

    def "NodeFor"() {
        expect:
        sitemap.nodeFor(uri)
    }

    @Ignore("Groovy cannot cope with method overloading ")
    def "NodeFor navigation state"() {
        given:
        NavigationState ns = new NavigationState().setFragment(uri)
        expect:
        sitemap.nodeFor(ns as NavigationState)
    }

    def "GetRedirectNodeFor"() {
        expect:
        sitemap.getRedirectNodeFor(new MasterSitemapNode(5, uri))
    }

    def "GetRedirects"() {
        expect:
        sitemap.getRedirects().isEmpty()
    }

    def "AddRedirect"() {
        when:
        sitemap.addRedirect("a", "b")

        then:
        thrown(SitemapLockedException)
    }

    def "Uris"() {
        expect:
        sitemap.uris()
    }

    def "GetNodeCount"() {
        expect:
        sitemap.getNodeCount() > 0
    }

    def "NodeChainForUri"() {
        expect:
        sitemap.nodeChainForUri(uri, false)
    }

    def "NodeChainFor MasterSitemapNode"() {
        expect:
        sitemap.nodeChainFor(new MasterSitemapNode(2, uri))
    }

    def "NodeChainForSegments"() {
        expect:
        sitemap.nodeChainForSegments(new ArrayList<String>(), true).isEmpty()
    }

    def "FindNodeBySegment"() {
        expect:
        sitemap.findNodeBySegment(new ArrayList<MasterSitemapNode>(), "a", false) == null
    }

    def "CreateNode1"() {
        when:
        sitemap.createNode("a")
        then:
        thrown(SitemapLockedException)
    }

    @Ignore("Groovy cannot cope with method overloading ")
    def "NodeChainFor navigation state"() {

        given:
        NavigationState ns = new NavigationState().setFragment(uri)
        expect:
        sitemap.nodeChainFor(ns, false)
    }

    def "GetParent"() {
        expect:
        sitemap.getParent(new MasterSitemapNode(5, uri)) == null
    }

    def "GetRedirectPageFor"() {
        expect:
        sitemap.getRedirectPageFor('a')
    }

    @Ignore("Groovy cannot cope with method overloading ")
    def "GetRedirectPageFor1"() {
        given:
        NavigationState ns = new NavigationState().setFragment(uri)

        expect:
        sitemap.getRedirectPageFor(ns)
    }

    def "AddChild"() {
        when:
        sitemap.addChild(new MasterSitemapNode(2, uri), new MasterSitemapNode(2, uri))

        then:
        thrown(SitemapLockedException)
    }

    def "GetForest"() {
        expect:
        sitemap.getForest()
    }

    def "CheckForStandardPage"() {
        given:
        MasterSitemapNode msn = new MasterSitemapNode(2, uri)
        msn = msn.modifyLabelKey(StandardPageKey.Log_In)

        when:
        sitemap.checkForStandardPage(msn)

        then:
        thrown(SitemapLockedException)

    }

    def "AddStandardPage"() {
        given:
        MasterSitemapNode msn = new MasterSitemapNode(2, uri)
        msn = msn.modifyLabelKey(StandardPageKey.Log_In)

        when:
        sitemap.addStandardPage(msn, 'b')

        then:
        thrown(SitemapLockedException)
    }

    def "Clear"() {
        when:
        sitemap.clear()

        then:
        thrown(SitemapLockedException)
    }

    def "IsLoaded"() {
        expect:
        !sitemap.isLoaded()
    }

    def "SetLoaded"() {
        when:
        sitemap.setLoaded(false)

        then:
        thrown(SitemapLockedException)
    }

    def "GetUriMap"() {
        expect:
        sitemap.getUriMap()
    }

    def "AddOrReplaceChild"() {
        given:
        MasterSitemapNode msn = new MasterSitemapNode(2, uri)
        msn = msn.modifyLabelKey(StandardPageKey.Log_In)

        when:
        sitemap.addOrReplaceChild(msn, msn)

        then:
        thrown(SitemapLockedException)
    }

    def "ReplaceNode"() {
        given:
        MasterSitemapNode msn = new MasterSitemapNode(2, uri)
        msn = msn.modifyLabelKey(StandardPageKey.Log_In)

        when:
        sitemap.replaceNode(msn, msn)

        then:
        thrown(SitemapLockedException)
    }

    def "IsLoginUri"() {
        given:
        NavigationState ns = new NavigationState()
        ns.fragment('b').update(fragmentHandler)

        expect:
        !sitemap.isLoginUri(ns)
    }

    def "StandardPageNode"() {
        expect:
        sitemap.standardPageNode(StandardPageKey.Log_In)
    }

    def "IsLogoutUri"() {
        given:
        NavigationState ns = new NavigationState()
        ns.fragment('b').update(fragmentHandler)

        expect:
        !sitemap.isLogoutUri(ns)
    }

    def "IsPrivateHomeUri"() {
        NavigationState ns = new NavigationState()
        ns.fragment('b').update(fragmentHandler)

        expect:
        !sitemap.isPrivateHomeUri(ns)
    }

    def "IsPublicHomeUri"() {
        NavigationState ns = new NavigationState()
        ns.fragment('b').update(fragmentHandler)

        expect:
        !sitemap.isPublicHomeUri(ns)
    }

    def "GetStandardPageUris"() {
        expect:
        sitemap.getStandardPageUris()
    }
}
