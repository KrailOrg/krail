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

import uk.q3c.krail.core.i18n.I18NKey;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.view.KrailView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder for {@link MasterSitemapNode} information, prior to creation of the immutable {@link MasterSitemapNode}
 * <p>
 * Created by David Sowerby on 03/03/15.
 */
public class NodeRecord {
    private I18NKey labelKey;
    private PageAccessControl pageAccessControl;
    private int positionIndex = 1; // visible by default
    private List<String> roles = new ArrayList<>();
    private String uri;
    private String uriSegment;
    private Class<? extends KrailView> viewClass;

    public NodeRecord(String uri) {

        this.uri = uri;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public int getPositionIndex() {
        return positionIndex;
    }

    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUriSegment() {
        return uriSegment;
    }

    public void setUriSegment(String uriSegment) {
        this.uriSegment = uriSegment;
    }

    public Class<? extends KrailView> getViewClass() {
        return viewClass;
    }

    public void setViewClass(Class<? extends KrailView> viewClass) {
        this.viewClass = viewClass;
    }

    public I18NKey getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(I18NKey labelKey) {
        this.labelKey = labelKey;
    }

    public void addRole(String role) {
        roles.add(role);
    }

    public PageAccessControl getPageAccessControl() {
        return pageAccessControl;
    }

    public void setPageAccessControl(PageAccessControl pageAccessControl) {
        this.pageAccessControl = pageAccessControl;
    }
}
