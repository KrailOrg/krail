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
package uk.q3c.krail.core.services;


/**
 * A utility class for {@link Service} implementations
 *
 * @author David Sowerby
 */
public class ServiceUtils {

    public static Class<?> unenhancedClass(Service service) {
        Class<?> serviceClass = service.getClass();
        return unenhancedClass(serviceClass);
    }

    /**
     * Returns the underlying class un-enhanced by Guice, needed to identify annotations
     *
     * @param serviceClass
     */
    public static Class<?> unenhancedClass(Class<?> serviceClass) {
        Class<?> clazz = serviceClass;
        while (clazz.getName()
                    .contains("EnhancerByGuice")) {
            clazz = clazz.getSuperclass();
        }
        return clazz;
    }
}
