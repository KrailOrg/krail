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
package uk.q3c.krail.persist.orient.custom;

import com.orientechnologies.orient.core.serialization.serializer.object.OObjectSerializer;

import java.util.Locale;

public class OrientCustomType_Locale implements OObjectSerializer<Locale, String> {

    @Override
    public Locale unserializeFieldValue(Class<?> iClass, String iFieldValue) {
        return new Locale(iFieldValue);
    }

    @Override
    public String serializeFieldValue(Class<?> iClass, Locale iFieldValue) {
        return iFieldValue.toString();
    }

}
