/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.navigate.sitemap;

/**
 * Listeners are notified when changes either occur to labels (usually as a result of switching Locale) or because the
 * structure of the sitemap has changed. The structure may change as a result of the user logging in / out, or as a
 * result of permissions changes
 *
 * @author dsowerby
 */
public interface UserSitemapChangeListener {
    /**
     * Fired when only the labels have changed - this typically happens as a result of switching Locale.
     */
    void labelsChanged();

    /**
     * Fired when a page is added / removed, or its position has changed. This will happen as a result of permission
     * changes, logging in /out or potentially the dynamic addition / removal of pages (see
     * https://github.com/davidsowerby/krail/issues/254).
     */
    void structureChanged();
}
