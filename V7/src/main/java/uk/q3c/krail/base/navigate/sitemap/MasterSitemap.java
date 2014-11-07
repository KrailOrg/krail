/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.base.navigate.sitemap;

import uk.q3c.krail.base.navigate.NavigationState;

public interface MasterSitemap extends Sitemap<MasterSitemapNode> {

    public abstract String getReport();

    public abstract void setReport(String report);

    public abstract MasterSitemapNode append(String uri);

    public abstract MasterSitemapNode append(NavigationState navigationState);

}
