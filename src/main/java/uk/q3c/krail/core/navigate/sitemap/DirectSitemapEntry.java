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

import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.i18n.I18NKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A simple data class to hold an entry for the Sitemap for use with a {@link DirectSitemapModule}. Note that if {@link #pageAccessControl} is {@link
 * PageAccessControl#ROLES}, then roles must be set to a non-empty value, but there is no check for this until the SitemapChecker is invoked. This allows a
 * set of Sitemap errors to be captured at once rather than one at a time.
 *
 * @author David Sowerby
 */
public class DirectSitemapEntry {
    private I18NKey labelKey;
    private String moduleName;
    private PageAccessControl pageAccessControl;
    private int positionIndex;
    private String roles;
    private Class<? extends KrailView> viewClass;
    /**
     * @param moduleName
     * the name of the Guice module the entry was made in
     * @param viewClass
     *         the class of KrailView used to display the page
     * @param labelKey
     *         the I18Nkey used to describe the node, typically in a navigation component
     * @param pageAccessControl
     *         the type of page control to use
     * @param positionIndex
     *         the position of a page in relation to its siblings.  Used as a sort order, relative numbering does not need to be sequential. A positionIndex < 0
     *         indicates that the page should not be displayed in a navigation component
     */
    public DirectSitemapEntry(@Nonnull String moduleName, @Nonnull Class<? extends KrailView> viewClass, @Nonnull I18NKey labelKey, @Nonnull
    PageAccessControl pageAccessControl, int
            positionIndex) {
        super();
        this.moduleName = moduleName;
        this.pageAccessControl = pageAccessControl;
        this.viewClass = viewClass;
        this.labelKey = labelKey;
        this.positionIndex = positionIndex;
    }

    /**
     * Roles are only used if {@link #pageAccessControl} is {@link PageAccessControl#ROLES}, so if you don't need them you can use the other constructor.
     ** @param moduleName
     * the name of the Guice module the entry was made in
     * @param viewClass
     *         the class of KrailView used to display the page
     * @param labelKey
     *         the I18Nkey used to describe the node, typically in a navigation component
     * @param pageAccessControl
     *         the type of page control to use
     * @param roles
     *         a comma separated list of roles, used only if pageAccessControl is {@link PageAccessControl#ROLES}
     * @param positionIndex
     *         the position of a page in relation to its siblings.  Used as a sort order, relative numbering does not need to be sequential. A positionIndex < 0
     *         indicates that the page should not be displayed in a navigation component
     */
    public DirectSitemapEntry(String moduleName, @Nonnull Class<? extends KrailView> viewClass, @Nonnull I18NKey labelKey, @Nonnull PageAccessControl
            pageAccessControl,
                              @Nullable String roles, int positionIndex) {
        super();
        this.moduleName = moduleName;
        this.pageAccessControl = pageAccessControl;
        this.viewClass = viewClass;
        this.labelKey = labelKey;
        this.roles = roles;
        this.positionIndex = positionIndex;
    }

    public String getModuleName() {
        return moduleName;
    }

    public int getPositionIndex() {
        return positionIndex;
    }

    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

    public Class<? extends KrailView> getViewClass() {
        return viewClass;
    }

    public void setViewClass(@Nonnull Class<? extends KrailView> viewClass) {
        this.viewClass = viewClass;
    }

    public I18NKey getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(@Nonnull I18NKey labelKey) {
        this.labelKey = labelKey;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(@Nullable String roles) {
        this.roles = roles;
    }

    public PageAccessControl getPageAccessControl() {
        return pageAccessControl;
    }

    public void setPageAccessControl(@Nonnull PageAccessControl pageAccessControl) {
        this.pageAccessControl = pageAccessControl;
    }

}
