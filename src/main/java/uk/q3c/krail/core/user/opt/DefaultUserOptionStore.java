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
package uk.q3c.krail.core.user.opt;

import com.google.inject.Singleton;

import java.util.Map;
import java.util.TreeMap;

/**
 * A volatile storage mechanism for {@link UserOption} implementations. The option values are simply stored in a map
 * and
 * will therefore disappear when out of scope. To use persistent storage, provide your own implementation - the
 * implementation of this is effectively a DAO.
 *
 * @author David Sowerby 16 Jul 2013
 */
@Singleton
public class DefaultUserOptionStore implements UserOptionStore {

    private final Map<String, Map<String, Object>> groupMap = new TreeMap<>();

    @Override
    public Object getOptionValue(String optionGroup, String option) {
        Map<String, Object> optionMap = optionMap(optionGroup);
        if (optionMap == null) {
            return null;
        }
        return optionMap.get(option);
    }

    /**
     * Used for setting option values, looks for the optionMap for the supplied group and option, and creates one if
     * none exists
     *
     * @param optionGroup
     * @param option
     *
     * @return
     */

    private Map<String, Object> optionMap(String optionGroup) {
        Map<String, Object> optionMap = groupMap.get(optionGroup);
        if (optionMap == null) {
            optionMap = new TreeMap<>();
            groupMap.put(optionGroup, optionMap);
        }
        return optionMap;
    }

    @Override
    public void setOptionValue(String optionGroup, String option, Object value) {
        Map<String, Object> optionMap = optionMap(optionGroup);
        optionMap.put(option, value);
    }

    @Override
    public void clear() {
        groupMap.clear();
    }

    // Keep these - might be useful for a persistent store

    // public void setOption(String optionGroup, String option, List<String> list) {
    // StringBuilder buf = new StringBuilder();
    // boolean first = true;
    // for (String s : list) {
    // if (!first) {
    // buf.append("|");
    // } else {
    // first = false;
    // }
    // buf.append(s);
    // }
    //
    // userOptionStore.optionMap(optionGroup, option).put(option, buf.toString());
    // }
    //
    // public void setOption(String optionGroup, String option, Map<String, String> map) {
    // StringBuilder buf = new StringBuilder();
    // boolean first = true;
    // for (Map.Entry<String, String> entry : map.entrySet()) {
    // if (!first) {
    // buf.append("|");
    // } else {
    // first = false;
    // }
    // buf.append(entry.getKey());
    // buf.append("=");
    // buf.append(entry.getValue());
    // }
    // userOptionStore.optionMap(optionGroup, option).put(option, buf.toString());
    // }

}
