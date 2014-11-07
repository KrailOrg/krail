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
package uk.q3c.krail.base.navigate.sitemap;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import uk.q3c.krail.base.navigate.Navigator;
import uk.q3c.krail.base.shiro.PageAccessControl;
import uk.q3c.krail.base.view.KrailView;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LabelKey;

import java.text.Collator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Represents a node in the site map (equivalent to a web site 'page'). It contains a URI segment (this is just one
 * part
 * of the URI, so the node for the page at /private/account/open would contain just 'open'). To obtain the full URI,
 * use
 * {@link MasterSitemap#uri(MasterSitemapNode)}.
 * <p/>
 * {@link #viewClass} is the class of {@link KrailView} to be used in displaying the page, and the {@link
 * #getLabelKey()}
 * is an {@link I18NKey} key to a localised label for the page
 * <p/>
 * The {@link #id} is required because the URI segment alone may not be unique, and the view class and labelKey are
 * optional. For the node to be used in a graph, it needs a unique identifier. The id is provided by
 * {@link MasterSitemap#addChild(MasterSitemapNode, MasterSitemapNode)} and
 * {@link MasterSitemap#addNode(MasterSitemapNode)}. This field has an additional purpose in providing a record of
 * insertion order, so that nodes can be sorted by insertion order if required.
 * <p/>
 * To enable locale sensitive sorting of nodes - for example within a UserNavigationTree - a collation key from
 * {@link Collator} is added by the {@link #setLabelKey(I18NKey, Locale, Collator)} method. This means the collation
 * key
 * is generally created only once, but is available for sorting as often as needed. The collation key will only need to
 * be updated if locale or labelKey changes. This approach also takes advantage of the improved performance of the
 * collation key sorting (http://docs.oracle.com/javase/tutorial/i18n/text/perform.html)
 * <p/>
 * The type of user access control applied to the page is determined by {@link #pageAccessControl}. Note that these are
 * mutually exclusive, so a page cannot require both roles and permissions. This control is applied by the
 * {@link Navigator} during page changes, thereby disallowing access to an authorised page.
 *
 * @author David Sowerby 6 May 2013
 */
public class MasterSitemapNode implements SitemapNode {

    private int id;
    private I18NKey<?> labelKey;
    private PageAccessControl pageAccessControl;
    private int positionIndex;
    /**
     * Contains roles required to access this page, but is not used unless {@link #pageAccessControl} is
     * {@link PageAccessControl#ROLES}
     */
    private Set<String> roles = new HashSet<>();
    private String uriSegment;
    private Class<? extends KrailView> viewClass;

    public MasterSitemapNode(String uriSegment, Class<? extends KrailView> viewClass, I18NKey<?> labelKey) {
        super();
        this.uriSegment = uriSegment;
        this.viewClass = viewClass;
        setLabelKey(labelKey);
    }

    public MasterSitemapNode() {

    }

    @Override
    public String getUriSegment() {
        return uriSegment;
    }

    public void setUriSegment(String uriSegment) {
        this.uriSegment = uriSegment;
    }

    @Override
    public I18NKey<?> getLabelKey() {
        return labelKey;
    }

    /**
     * Sets {@link LabelKey}, may be null
     *
     * @param labelKey
     * @param locale
     */
    public void setLabelKey(I18NKey<?> labelKey) {
        this.labelKey = labelKey;
    }

    @Override
    public Class<? extends KrailView> getViewClass() {
        return viewClass;
    }

    public void setViewClass(Class<? extends KrailView> viewClass) {
        this.viewClass = viewClass;
    }

    public String toStringAsMapEntry() {
        StringBuilder buf = new StringBuilder();
        buf.append((uriSegment == null) ? "no segment given" : uriSegment);
        buf.append((viewClass == null) ? "" : "\t\t:  " + viewClass.getSimpleName());
        buf.append((labelKey == null) ? "" : "\t~  " + ((Enum<?>) labelKey).name());
        return buf.toString();

    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("id=");
        buf.append(Integer.toString(id));
        buf.append(", segment=");
        buf.append((uriSegment == null) ? "null" : uriSegment);
        buf.append(", viewClass=");
        buf.append((viewClass == null) ? "null" : viewClass.getName());
        buf.append(", labelKey=");
        buf.append((labelKey == null) ? "null" : ((Enum<?>) labelKey).name());
        buf.append(", roles=");
        if (roles.isEmpty()) {
            buf.append("none");
        } else {
            boolean first = true;
            for (String role : roles) {
                if (!first) {
                    buf.append(';');
                }
                buf.append('[');
                buf.append(role);
                buf.append(']');
                first = false;
            }
        }
        return buf.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPublicPage() {
        return pageAccessControl == PageAccessControl.PUBLIC;
    }

    /**
     * Adds a role. Only relevant if {@link #pageAccessControl} is {@link PageAccessControl#ROLES}
     */
    public void addRole(String role) {
        if (StringUtils.isNotEmpty(role)) {
            roles.add(role);
        }
    }

    public boolean hasRoles() {
        return !roles.isEmpty();
    }

    @Override
    public PageAccessControl getPageAccessControl() {
        return pageAccessControl;
    }

    public void setPageAccessControl(PageAccessControl pageAccessControl) {
        this.pageAccessControl = pageAccessControl;

    }

    @Override
    public List<String> getRoles() {
        return ImmutableList.copyOf(roles);
    }

    public void setRoles(List<String> list) {
        roles = new HashSet<>(list);

    }

    public int getPositionIndex() {
        return positionIndex;
    }

    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

}
