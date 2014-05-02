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
package uk.co.q3c.v7.i18n;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author David Sowerby 9 Feb 2013
 * 
 */
public class TestLabels_de extends TestLabels {

	private static final ImmutableMap<TestLabelKey, String> map;
	static {

		map = new ImmutableMap.Builder<TestLabelKey, String>()
// @formatter:off	

			.put(TestLabelKey.Home, "zu Hause")
			.put(TestLabelKey.Yes, "Ja")
			.put(TestLabelKey.No, "Nein")
			.build();
		
// @formatter:on
	}

	@Override
	public ImmutableMap<TestLabelKey, String> getMap() {
		return map;
	}

}
