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

public class Descriptions_de extends Descriptions {

	private static ImmutableMap<DescriptionKey, String> map;
	static {

		map = new ImmutableMap.Builder<DescriptionKey, String>()
				// @formatter:off
				.put(DescriptionKey.Last_Name,"die Nachname oder der Familienname")
				.put(DescriptionKey.Confirm_Ok, "Bestätigen, dass dieser Wert in Ordnung ist")
				.build();
				// @formatter:on

	}

	@Override
	public ImmutableMap<DescriptionKey, String> getMap() {
		return map;
	}

}
