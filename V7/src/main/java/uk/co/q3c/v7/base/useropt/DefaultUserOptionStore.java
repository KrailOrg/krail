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
package uk.co.q3c.v7.base.useropt;

import java.util.Map;
import java.util.TreeMap;

import javax.inject.Singleton;

/**
 * A volatile storage mechanism for {@link UserOption} implementations. The option values are simply stored in a map and
 * will therefore disappear when out of scope. To use persistent storage, provide your own implementation
 * 
 * @author David Sowerby 16 Jul 2013
 * 
 */
@Singleton
public class DefaultUserOptionStore implements UserOptionStore {

	private final Map<String, Map<String, String>> groupMap = new TreeMap<>();

	@Override
	public Map<String, String> optionMap(String optionGroup, String option) {
		Map<String, String> optionMap = groupMap.get(optionGroup);
		if (optionMap == null) {
			optionMap = new TreeMap<>();
			groupMap.put(optionGroup, optionMap);
		}
		return optionMap;
	}

	@Override
	public String getOptionValue(String optionGroup, String option) {
		Map<String, String> optionMap = groupMap.get(optionGroup);
		if (optionMap == null) {
			return null;
		}
		return optionMap.get(option);
	}
}
