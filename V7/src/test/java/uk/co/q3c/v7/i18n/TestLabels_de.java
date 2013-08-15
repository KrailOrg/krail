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

import java.util.EnumMap;

/**
 * 
 * 
 * @author David Sowerby 9 Feb 2013
 * 
 */
public class TestLabels_de extends TestLabels {

	private static final EnumMap<TestLabelKey, String> map = new EnumMap<TestLabelKey, String>(TestLabelKey.class);
	// TODO make map unmodifiable
	static {
		map.put(TestLabelKey.Home, "zu Hause");
		map.put(TestLabelKey.Yes, "Ja");
		map.put(TestLabelKey.No, "Nein");
	}

	@Override
	public EnumMap<TestLabelKey, String> getMap() {
		return map;
	}

}
