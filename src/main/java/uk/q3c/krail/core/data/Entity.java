/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.data;

import java.beans.PropertyChangeListener;

/**
 * Created by david on 17/08/14.
 */
public interface Entity<T extends Dao> extends PropertyChangeListener {
    boolean isDirty();

    void setDirty(boolean dirty);

    void save();

    void delete() throws Exception;

    T getDao();


    void init(T Dao);
}
