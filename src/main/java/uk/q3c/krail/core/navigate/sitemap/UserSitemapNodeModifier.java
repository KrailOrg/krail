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

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.core.i18n.CurrentLocale;
import uk.q3c.krail.core.i18n.Translate;
import uk.q3c.krail.core.shiro.PageAccessController;
import uk.q3c.krail.core.shiro.SubjectProvider;
import uk.q3c.util.NodeModifier;

import javax.annotation.Nonnull;
import java.text.Collator;
import java.util.Comparator;

import static com.google.common.base.Preconditions.checkNotNull;

public class UserSitemapNodeModifier implements NodeModifier<MasterSitemapNode, UserSitemapNode> {
    private static Logger log = LoggerFactory.getLogger(UserSitemapNodeModifier.class);

    private final SubjectProvider subjectProvider;
    private final PageAccessController pageAccessController;
    private final Collator collator;
    private final Translate translate;
    private MasterSitemap masterSitemap;

    @Inject
    public UserSitemapNodeModifier(SubjectProvider subjectProvider, CurrentLocale currentLocale,
                                   PageAccessController pageAccessController,
                                   Translate translate) {
        super();
        this.subjectProvider = subjectProvider;
        this.pageAccessController = pageAccessController;
        this.collator = Collator.getInstance(currentLocale.getLocale());
        this.translate = translate;
    }

    /**
     * * Checks each node to ensure that the Subject has permission to view, and if so, adds it to this tree. Note that
     * if a node is redirected, its pageAccessControl attribute will have been modified to be the same as the redirect
     * target by the SitemapChecker.
     * <p/>
     * Nodes which have a null label key are ignored, as they cannot be displayed. The logout page is never loaded. The
     * login page is only shown if the user is not authenticated.<br>
     * <br>
     * The label and collation key for the node are created using {@link CurrentLocale}, which may be different for
     * different users; so both this class and CurrentLocale are {@link VaadinSessionScoped}
     *
     * {@link #setMasterSitemap} must be called first
     *
     * @param masterNode the MasterSitemapNode that the UserSitemapNode must reference
     *
     * @return created UserSitemapNode with reference to {@code masterNode}
     */
    @Override
    public UserSitemapNode create(UserSitemapNode parentUserNode, @Nonnull MasterSitemapNode masterNode) {
        checkNotNull(masterNode);
        log.debug("creating a node for master node {}", masterNode);
        // if there is no labelKey (usually when page is redirected), cannot be shown
        if (masterNode.getLabelKey() == null) {
            return null;
        }

        // if the subject is already authenticated, don't show the login page
        if (subjectProvider.get()
                           .isAuthenticated()) {
            if (masterNode.equals(masterSitemap.standardPageNode(StandardPageKey.Log_In))) {
                log.debug("User has already authenticated, do not show the login node");
                return null;
            }
        }
        if (pageAccessController.isAuthorised(subjectProvider.get(), masterSitemap, masterNode)) {
            log.debug("User is authorised for page {}, creating a node for it", masterSitemap.uri(masterNode));
            UserSitemapNode userNode = new UserSitemapNode(masterNode);
            userNode.setLabel(translate.from(masterNode.getLabelKey()));
            userNode.setCollationKey(collator.getCollationKey(userNode.getLabel()));
            return userNode;
        } else {
            log.debug("User is NOT authorised for page {}, returning null", masterSitemap.uri(masterNode));
            return null;
        }
    }

    @Override
    public MasterSitemapNode sourceNodeFor(@Nonnull UserSitemapNode target) {
        return target.getMasterNode();
    }

    /**
     * Not used in this implementation
     */
    @Override
    public void setLeaf(@Nonnull UserSitemapNode targetNode, boolean isLeaf) {

    }

    /**
     * Not used in this implementation
     */

    @Override
    public void setCaption(@Nonnull UserSitemapNode targetNode, String caption) {

    }

    @Override
    public boolean attachOnCreate() {
        return false;
    }

    /**
     * Not used in this implementation
     */
    @Override
    public void sortChildren(UserSitemapNode parentNode, @Nonnull Comparator<UserSitemapNode> comparator) {

    }


    public void setMasterSitemap(MasterSitemap masterSitemap) {
        this.masterSitemap = masterSitemap;
    }

}
