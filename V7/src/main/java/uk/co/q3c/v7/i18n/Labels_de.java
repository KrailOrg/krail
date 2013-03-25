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
 * The base for the resource bundle of Labels for Locale de. Entry is purely for testing
 * 
 * 
 * @author David Sowerby 9 Feb 2013
 * 
 */
public class Labels_de extends Labels {

	private static final EnumMap<LabelKeys, String> map = new EnumMap<LabelKeys, String>(LabelKeys.class);
	// TODO make map unmodifiable
	static {

		map.put(LabelKeys.cancel, "stornieren");
		map.put(LabelKeys.small, "klein");
		map.put(LabelKeys.first_name, "vorname");
		map.put(LabelKeys.last_name, "nachname");
	}

	@Override
	public EnumMap<LabelKeys, String> getMap() {
		return map;
	}

}
