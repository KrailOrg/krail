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
package uk.q3c.krail.core.navigate.sitemap;

import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.Translate;

import java.text.CollationKey;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

/**
 * To enable locale sensitive sorting of nodes - for example within a UserNavigationTree - a collation key from
 * {@link Collator} is added by the {@link #setLabelKey(I18NKey)} method. This means the collation key
 * is generally created only once, but is available for sorting as often as needed. The collation key will only need to
 * be updated if locale or labelKey changes. This approach also takes advantage of the improved performance of the
 * collation key sorting (http://docs.oracle.com/javase/tutorial/i18n/text/perform.html)
 */
public class UserSitemapNode implements SitemapNode, Comparable<UserSitemapNode> {

    private final MasterSitemapNode masterNode;
    private CollationKey collationKey;
    private String label;

    public UserSitemapNode(MasterSitemapNode masterNode) {
        super();
        this.masterNode = masterNode;
    }

    /**
     * Updates the {@link #label} and {@link #collationKey} for the {@code locale}
     */
    public void translate(Translate translate, Locale locale, Collator collator) {
        I18NKey key = masterNode.getLabelKey();
        label = translate.from(key);
        collationKey = collator.getCollationKey(label);
    }

    public MasterSitemapNode getMasterNode() {
        return masterNode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public CollationKey getCollationKey() {
        return collationKey;
    }

    public void setCollationKey(CollationKey collationKey) {
        this.collationKey = collationKey;
    }

    public int getId() {
        return masterNode.getId();
    }

    @Override
    public I18NKey getLabelKey() {
        return masterNode.getLabelKey();
    }

    @Override
    public String getUriSegment() {
        return masterNode.getUriSegment();
    }

    @Override
    public Class<? extends KrailView> getViewClass() {
        return masterNode.getViewClass();
    }

    @Override
    public PageAccessControl getPageAccessControl() {
        return masterNode.getPageAccessControl();
    }

    @Override
    public List<String> getRoles() {
        return masterNode.getRoles();
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public int compareTo(UserSitemapNode otherNode) {
        return collationKey.compareTo(otherNode.collationKey);
    }

    @Override
    public int getPositionIndex() {
        return masterNode.getPositionIndex();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserSitemapNode)) {
            return false;
        }

        UserSitemapNode that = (UserSitemapNode) o;

        return !(masterNode != null ? !masterNode.equals(that.masterNode) : that.masterNode != null);

    }


}
