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
package uk.q3c.krail.i18n;

import com.google.common.collect.ImmutableMap;

import java.util.EnumMap;
import java.util.Enumeration;
import java.util.ResourceBundle;

public abstract class MapResourceBundle<E extends Enum<E>> extends ResourceBundle {


    private Class<E> clazz;
    private EnumMap<E, String> map ;

    public MapResourceBundle(Class<E> clazz) {
        this.clazz = clazz;
        this.map = new EnumMap<E, String>(clazz);
        loadMap();
    }

    protected abstract void loadMap();

    @Override
    public Enumeration<String> getKeys() {
        throw new RuntimeException("getKeys() replaced in Krail, use getMap() instead");
    }

    @Override
    protected Object handleGetObject(String arg0) {
        throw new RuntimeException("handleGetObject() replaced in Krail, use getValue() instead");
    }

    public String getValue(E key) {
        if (key == null) {
            return null;
        }
        String value = getMap().get(key);
        if (value != null) {
            return value;
        }
        @SuppressWarnings("unchecked") MapResourceBundle<E> enumparent = (MapResourceBundle<E>) parent;
        // returning null so that the enum name() can be used when there is no map entry
        if (enumparent == null) {
            return null;
        }
        return enumparent.getValue(key);

    }

    public  EnumMap<E, String> getMap() {
        return map;
    }

    public void put(E key, String value) {
        map.put(key, value);
    }

    public Class<E> getClazz() {
        return clazz;
    }

}