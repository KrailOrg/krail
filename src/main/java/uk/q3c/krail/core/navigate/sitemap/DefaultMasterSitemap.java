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

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import uk.q3c.krail.core.navigate.NavigationState;
import uk.q3c.krail.core.navigate.URIFragmentHandler;

import java.util.ArrayList;
import java.util.List;


public class DefaultMasterSitemap extends DefaultSitemapBase<MasterSitemapNode> implements MasterSitemap {

    private int nextNodeId = 0;

    private String report;

    @Inject
    public DefaultMasterSitemap(URIFragmentHandler uriHandler) {
        super(uriHandler);

    }

    @Override
    public synchronized MasterSitemapNode append(NodeRecord nodeRecord) {
        checkLock();
        // take a copy to protect the parameter
        NavigationState navState = uriHandler.navigationState(nodeRecord.getUri());
        if (nodeRecord.getUriSegment() == null) {
            nodeRecord.setUriSegment(navState.getUriSegment());
        }
        // find the node (parent) to attach to, by looping and removing the trailing segment each
        // time until we find a matching node or run out of segments
        List<String> segments = new ArrayList(navState.getPathSegments());

        MasterSitemapNode node = null;
        while ((segments.size() > 0) && (node == null)) {
            segments.remove(segments.size() - 1);
            String path = Joiner.on("/")
                                .join(segments);
            node = nodeFor(path);
        }

        // if we never found a matching node, we must be starting a new root, parent will be null
        // and the start index will be 0
        int startIndex = segments.size();

        // reset the segments
        segments = new ArrayList(navState.getPathSegments());

        MasterSitemapNode parentNode = null;

        if (!segments.isEmpty()) {
            parentNode = node;
        }

        // create all the intermediate nodes needed to place the new child correctly
        // same idea as forceMkDir
        //intermediate nodes will only have the uri segment and id
        MasterSitemapNode childNode;
        for (int i = startIndex; i < segments.size() - 1; i++) {
            String segment = segments.get(i);
            childNode = new MasterSitemapNode(nextNodeId(), segment);
            addOrReplaceChild(parentNode, childNode);
            parentNode = childNode;
        }

        childNode = new MasterSitemapNode(nextNodeId(), nodeRecord);
        addOrReplaceChild(parentNode, childNode);

        if (childNode.getLabelKey() instanceof StandardPageKey) {
            StandardPageKey spk = (StandardPageKey) childNode.getLabelKey();
            standardPages.put(spk, childNode);
        }
        return childNode;
    }

    private int nextNodeId() {
        nextNodeId++;
        return nextNodeId;
    }

    @Override
    protected synchronized MasterSitemapNode createNode(String segment) {
        checkLock();
        return new MasterSitemapNode(nextNodeId(), segment);
    }

    @Override
    public synchronized String getReport() {
        return report;
    }

    @Override
    public synchronized void setReport(String report) {
        checkLock();
        this.report = report;
    }


}
