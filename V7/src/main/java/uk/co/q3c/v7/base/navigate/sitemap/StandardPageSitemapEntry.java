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
package uk.co.q3c.v7.base.navigate.sitemap;

import uk.co.q3c.v7.base.navigate.StandardPageKey;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;
import uk.co.q3c.v7.i18n.I18NKey;

/**
 * A simple data class to hold an standard page entry for the Sitemap for use with a {@link DirectSitemapModule}. Note
 * that if {@link #pageAccessControl} is {@link PageAccessControl#ROLES}, then roles must be set to a non-empty value,
 * but there is no check for this until the SitemapChecker is invoked. This allows a of Sitemap errors to be captured at
 * once rather than one at a time.
 * <p>
 * The labelKey cannot be of type {@link StandardPageKey}, because there may be intermediate pages - for example
 * 'private/home' may be assigned to the {@link StandardPageKey#Private_Home}, but the intermediate page 'private' needs
 * a key to display in the {@link UserNavigationTree}. That key cannot be a {@link StandardPageKey}
 * 
 * @author David Sowerby
 * 
 */
public class StandardPageSitemapEntry extends DirectSitemapEntry {

	protected StandardPageSitemapEntry(Class<? extends V7View> viewClass, I18NKey<?> labelKey,
			PageAccessControl pageAccessControl, String roles) {
		super(viewClass, labelKey, pageAccessControl, roles);
	}

	protected StandardPageSitemapEntry(Class<? extends V7View> viewClass, I18NKey<?> labelKey,
			PageAccessControl pageAccessControl) {
		super(viewClass, labelKey, pageAccessControl);
	}

}
