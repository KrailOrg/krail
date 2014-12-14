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

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Enumeration;
import java.util.ResourceBundle;

public abstract class EnumResourceBundle<E extends Enum<E>> extends ResourceBundle {
    public enum UserOptionProperty {AUTO_STUB, GENERATE_STUB_WITH_NAME, SOURCE_ORDER, SOURCE_ORDER_DEFAULT}

    private static Logger log = LoggerFactory.getLogger(EnumResourceBundle.class);

    private Class<E> keyClass;
    private boolean loaded = false;
    private EnumMap<E, String> map;

    public EnumResourceBundle() {

    }

    /**
     * Loads data into the map but only if {@link #loaded} is false.  The bundle may have been returned from the
     * ResourceBundle cache, and may therefore not need reloading
     */
    public void load() {
        Preconditions.checkNotNull(keyClass, "setKeyClass must be called before calling load()");
        if (!loaded) {
            this.map = new EnumMap<E, String>(keyClass);
            loadMap();
            loaded = true;
        }

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

    /**
     * Returns the value for {@code key}, but ONLY from this map - the usual lookup rules for Java's {@link
     * ResourceBundle} are not used.
     *
     * @param key
     *
     * @return the value for the key, or null if the key is not in the map for this instance
     */
    public String getValue(E key) {
        if (key == null) {
            return null;
        }
        String value = getMap().get(key);
        return value;
    }

    public EnumMap<E, String> getMap() {
        return map;
    }

    /**
     * Puts the key and value into the map (standard map behaviour).
     *
     * @param key
     * @param value
     */
    public void put(E key, String value) {
        map.put(key, value);
    }

    public Class<E> getKeyClass() {
        return keyClass;
    }

    public void setKeyClass(Class<E> keyClass) {
        this.keyClass = keyClass;
    }

    public void reset() {
        map.clear();
        loaded = false;
        loadMap();
        log.debug("Values reset from persistence");
    }

}