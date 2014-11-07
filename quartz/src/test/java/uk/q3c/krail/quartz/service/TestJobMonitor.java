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
package uk.q3c.krail.quartz.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class TestJobMonitor {

    private final Map<String, String> map;

    @Inject
    protected TestJobMonitor() {
        super();
        this.map = new HashMap<>();
    }

    public void clear() {
        map.clear();
    }

    public synchronized void add(String key, String value) {
        System.out.println("Setting property key=" + value);
        map.put(key, value);
    }

    public synchronized String getEntry(String key) {
        return map.get(key);
    }

}
