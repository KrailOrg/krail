/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.services;

import uk.q3c.krail.i18n.I18NKey;

import javax.annotation.concurrent.Immutable;

/**
 * A ky object to uniquely identify a {@link Service} instance
 *
 * Created by David Sowerby on 24/10/15.
 */
@Immutable
public class ServiceKey {

    private final I18NKey key;
    private final int instance;

    public ServiceKey(I18NKey key, int instance) {
        this.key = key;
        this.instance = instance;
    }

    public ServiceKey(I18NKey key) {
        this.key = key;
        this.instance = 0;
    }

    /**
     * Returns true only if o is another ServiceKey and has the same key and instance
     *
     * @param o the other ServiceKey
     * @return true only if o is another ServiceKey and has the same key and instance
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceKey that = (ServiceKey) o;

        if (instance != that.instance) return false;
        return !(key != null ? !key.equals(that.key) : that.key != null);

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + instance;
        return result;
    }

    @Override
    public String toString() {
        return ((Enum) key).name() + ":" + instance;
    }
}
