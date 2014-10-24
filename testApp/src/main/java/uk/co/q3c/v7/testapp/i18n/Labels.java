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
package uk.co.q3c.v7.testapp.i18n;

import com.google.common.collect.ImmutableMap;
import uk.co.q3c.v7.i18n.MapResourceBundle;

/**
 * The base for the resource bundle of Labels. This is an arbitrary division of i18N keys & values, but is loosely
 * defined as containing those value which are short, contain no parameters and are typically used for captions and
 * labels. They can of course be used anywhere.
 *
 * @author David Sowerby 9 Feb 2013
 */
public class Labels extends MapResourceBundle<TestAppLabelKey> {

    private static final ImmutableMap<uk.co.q3c.v7.testapp.i18n.TestAppLabelKey, String> map;

    static {
        map = new ImmutableMap.Builder<TestAppLabelKey, String>()
                // @formatter:off
				
		.put(TestAppLabelKey.Yes, "yes")
		.put(TestAppLabelKey.No, "no")
		.build();
		
// @formatter:on

    }

    @Override
    public ImmutableMap<TestAppLabelKey, String> getMap() {
        return map;
    }

}
