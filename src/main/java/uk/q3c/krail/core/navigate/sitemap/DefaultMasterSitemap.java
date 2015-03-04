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
package uk.q3c.krail.core.navigate.sitemap;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.q3c.krail.core.navigate.NavigationState;
import uk.q3c.krail.core.navigate.URIFragmentHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the site layout. Individual "virtual pages" are represented by {@link MasterSitemapNode} instances.
 * This
 * map is built by one or more implementations of {@link SitemapLoader}, and is one of the fundamental building blocks
 * of the application, as it maps out pages, URIs and Views.
 * <p>
 * <p>
 * Because of it use as such a fundamental building block, an instance of this class has to be created early in the
 * application start up process. To avoid complex logic and dependencies within Guice modules, the building of the
 * {@link MasterSitemap} is managed by the {@link SitemapService}
 * <p>
 * Simple URI redirects can be added using {@link #addRedirect(String, String)}
 * <p>
 * If a duplicate entry is received (that is, a second entry for the same URI), the later entry will overwrite the
 * earlier entry
 * <p>
 * This MasterSitemap is complemented by instances of {@link UserSitemap}, which provides a user specific view of the
 * the {@link MasterSitemap}
 *
 * @author David Sowerby 19 May 2013
 */
@Singleton
public class DefaultMasterSitemap extends DefaultSitemapBase<MasterSitemapNode> implements MasterSitemap {

    private int nextNodeId = 0;

    private String report;

    @Inject
    public DefaultMasterSitemap(URIFragmentHandler uriHandler) {
        super(uriHandler);

    }

    @Override
    public synchronized MasterSitemapNode append(NodeRecord nodeRecord) {
        // take a copy to protect the parameter
        NavigationState navState = uriHandler.navigationState(nodeRecord.getUri());
        if (nodeRecord.getUriSegment() == null) {
            nodeRecord.setUriSegment(navState.getUriSegment());
        }
        // find the node (parent) to attach to, by looping and removing the trailing segment each
        // time until we find a matching node or run out of segments
        List<String> segments = new ArrayList(navState.getPathSegments());
//        segments.remove(segments.size() - 1);

        MasterSitemapNode node = null;
        while ((segments.size() > 0) && (node == null)) {
            segments.remove(segments.size() - 1);
            String path = Joiner.on("/")
                                .join(segments);
            // TODO should this be outside the loop?
            node = nodeFor(path);
        }

        // if we never found a matching node, we must be starting a new root, parent will be null
        // and the start index will be 0
        int startIndex = segments.size();

        // reset the segments
        segments = segments = new ArrayList(navState.getPathSegments());

        MasterSitemapNode parentNode = null;

        if (startIndex != 0) {
            parentNode = node;
        }

        // create all the intermediate nodes needed to place the new child correctly
        // same idea as forceMkDir
        //intermediate nodes will only have the uri segment and id
        MasterSitemapNode childNode = null;
        for (int i = startIndex; i < segments.size() - 1; i++) {
            String segment = segments.get(i);
            childNode = new MasterSitemapNode(nextNodeId(), segment);
            addOrReplaceChild(parentNode, childNode);// TODO Can't use addChild - we need to do a replace or add'
            parentNode = childNode;
        }

        childNode = new MasterSitemapNode(nextNodeId(), nodeRecord);
        addOrReplaceChild(parentNode, childNode);
        // TODO Can't use addChild - we need to do a replace or add'

        if (childNode.getLabelKey() instanceof StandardPageKey) {
            StandardPageKey spk = (StandardPageKey) childNode.getLabelKey();
            standardPages.put(spk, childNode);
        }
        return childNode;
        // TODO if node already exists replace it but also move children to the new one
        // TODO Standard page key detection - should check whenever a node is appended / added etc and add to standard page key map
        // TODO default views, keys, pageControl - are they needed and if so where to define? (removed from SitemapChecker)
    }

    private int nextNodeId() {
        nextNodeId++;
        return nextNodeId;
    }

    @Override
    protected synchronized MasterSitemapNode createNode(String segment) {
        MasterSitemapNode newNode = new MasterSitemapNode(nextNodeId(), segment);
        return newNode;
    }

    @Override
    public synchronized String getReport() {
        return report;
    }

    @Override
    public synchronized void setReport(String report) {
        this.report = report;
    }



}
