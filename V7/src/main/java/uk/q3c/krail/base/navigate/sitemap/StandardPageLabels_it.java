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

import com.google.common.collect.ImmutableMap;

/**
 * The base for the resource bundle of Labels for Locale de. Entry is purely for testing
 *
 * @author David Sowerby 9 Feb 2013
 */
public class StandardPageLabels_it extends StandardPageLabels {

    private static final ImmutableMap<StandardPageKey, String> map;

    static {
        map = new ImmutableMap.Builder<StandardPageKey, String>()
                // @formatter:off

			.put(StandardPageKey.Public_Home, "Public Pagina")
			.build();

// @formatter:on
    }

    @Override
    public ImmutableMap<StandardPageKey, String> getMap() {
        return map;
    }

}
