/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.view.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.util.forest.NodeFilter;

/**
 * Filters out nodes which have a position index of < 0 (this is used to indicate that the developer does not want this page to appear in a navigation
 * component)
 * <p>
 * Created by David Sowerby on 29/04/15.
 */
public class NoNavFilter implements NodeFilter<UserSitemapNode> {
    private static Logger log = LoggerFactory.getLogger(NoNavFilter.class);

    @Override
    public boolean accept(UserSitemapNode node) {
        boolean accept = node.getPositionIndex() >= 0;
        if (accept) {
            log.debug("accepted node: {} with position index of {}", node.toString(), node.getPositionIndex());
        } else {
            log.debug("rejected node: {} with position index of {}", node.toString(), node.getPositionIndex());
        }
        return accept;
    }
}
