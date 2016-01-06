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
package uk.q3c.krail.core.navigate.sitemap.comparator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import uk.q3c.krail.core.navigate.sitemap.MasterSitemapNode;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapNode;
import uk.q3c.krail.i18n.I18NKey;
import uk.q3c.krail.i18n.LabelKey;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Comparator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Comparator which can be used to sort SitemapNode by insertion order, based on {@link MasterSitemapNode#getId()}
 */
@SuppressFBWarnings("OCP_OVERLY_CONCRETE_PARAMETER")
public class PositionIndexDescending implements Comparator<UserSitemapNode>, UserSitemapSorter, Serializable {
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(@Nonnull UserSitemapNode o1, @Nonnull UserSitemapNode o2) {
        checkNotNull(o1);
        checkNotNull(o2);
        return o2.getPositionIndex() - o1.getPositionIndex();
    }

    @Override
    public I18NKey nameKey() {
        return LabelKey.Position_Index_Descending;
    }

}
