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
package uk.q3c.krail.core.shiro;

import com.google.inject.Inject;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemap;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Delegate for user access control when relating specifically to pages.
 *
 * @author David Sowerby
 */
public class PageAccessController {
    private static Logger log = LoggerFactory.getLogger(PageAccessController.class);
    private final MasterSitemap sitemap;

    @Inject
    protected PageAccessController(MasterSitemap sitemap) {
        super();
        this.sitemap = sitemap;
    }

    public boolean isAuthorised(Subject subject, UserSitemapNode userNode) {
        return isAuthorised(subject, userNode.getMasterNode());
    }

    public boolean isAuthorised(Subject subject, MasterSitemapNode masterNode) {
        checkNotNull(masterNode, "node");
        checkNotNull(subject, "subject");
        String virtualPage = sitemap.navigationState(masterNode)
                                    .getVirtualPage();
        checkNotNull(virtualPage, "virtualPage");
        checkNotNull(masterNode.getPageAccessControl(), "node.getPageAccessControl(), " + masterNode.getUriSegment());
        log.debug("checking page access rights for {}", virtualPage);
        switch (masterNode.getPageAccessControl()) {
            case AUTHENTICATION:
                return subject.isAuthenticated();
            case GUEST:
                return (!subject.isAuthenticated()) && (!subject.isRemembered());
            case PERMISSION:
                return subject.isPermitted(new PagePermission(virtualPage));
            case PUBLIC:
                return true;
            case ROLES:
                return subject.hasAllRoles(masterNode.getRoles());
            case USER:
                return (subject.isAuthenticated()) || (subject.isRemembered());
        }
        return false;
    }

    public List<MasterSitemapNode> authorisedChildNodes(Subject subject, MasterSitemapNode parentNode) {
        checkNotNull(subject);
        if (parentNode == null) {
            return new ArrayList<>();
        }
        List<MasterSitemapNode> subnodes = sitemap.getChildren(parentNode);
        ArrayList<MasterSitemapNode> authorisedSubNodes = new ArrayList<MasterSitemapNode>();
        for (MasterSitemapNode node : subnodes) {
            if (isAuthorised(subject, node)) {
                authorisedSubNodes.add(node);
            }
        }
        return authorisedSubNodes;
    }
}
