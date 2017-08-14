/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.core.services;

import com.google.common.base.Preconditions;
import uk.q3c.krail.i18n.I18NKey;

import javax.annotation.concurrent.Immutable;

/**
 * A ky object to uniquely identify a {@link Service} class without needing to use the Class itself
 *
 * Created by David Sowerby on 24/10/15.
 */
@Immutable
public class ServiceKey {

    private final I18NKey key;

    public ServiceKey(I18NKey key) {
        Preconditions.checkNotNull(key);
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceKey that = (ServiceKey) o;

        return key.equals(that.key);

    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return ((Enum) key).name();
    }
}
