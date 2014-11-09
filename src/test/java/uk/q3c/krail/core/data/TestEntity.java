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
package uk.q3c.krail.core.data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;

public class TestEntity implements Entity {
    @NotNull
    @Size(min = 2, max = 14)
    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void setDirty(boolean dirty) {

    }

    @Override
    public void save() {

    }

    @Override
    public void delete() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {

    }

    @Override
    public Dao getDao() {
        return null;
    }

    @Override
    public void init(Dao Dao) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

    }
}
