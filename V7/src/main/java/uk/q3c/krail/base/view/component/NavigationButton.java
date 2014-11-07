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
package uk.q3c.krail.base.view.component;

import com.vaadin.ui.Button;
import uk.q3c.krail.base.navigate.sitemap.UserSitemapNode;

/**
 * Simply a Vaadin Button encapsulating a SitemapNode
 *
 * @author David Sowerby
 */
public class NavigationButton extends Button {

    private UserSitemapNode node;
    private String params;

    protected NavigationButton() {
        super();
    }

    public UserSitemapNode getNode() {
        return node;
    }

    public void setNode(UserSitemapNode node) {
        this.node = node;
        this.setCaption(node.getLabel());
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

}
