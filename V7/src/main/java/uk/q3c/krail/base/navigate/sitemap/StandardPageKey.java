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

import uk.q3c.krail.i18n.I18NKey;

/**
 * @author David Sowerby 24 Mar 2013
 * @see StandardPageLabels
 */
public enum StandardPageKey implements I18NKey<StandardPageLabels> {
    Public_Home, // The home page for non-authenticated users
    Private_Home, // The home page for authenticated users
    Log_In, // the login page
    Log_Out; // the page to go to after logging out


}
