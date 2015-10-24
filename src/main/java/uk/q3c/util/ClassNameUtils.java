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
package uk.q3c.util;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public class ClassNameUtils {

    /**
     * Returns a "pure" class name, ignoring any appended enhancement
     *
     * @param clazz
     *
     * @return
     */
    public static String simpleClassNameEnhanceRemoved(@Nonnull Class<?> clazz) {
        checkNotNull(clazz);
        String simpleName = clazz.getSimpleName();
        if (simpleName.contains("$$Enhancer")) {
            simpleName = simpleName.substring(0, simpleName.indexOf("$$Enhancer"));

            // could be an inner class
            if (simpleName.contains("$")) {
                int index = simpleName.indexOf("$");
                simpleName = simpleName.substring(index + 1, simpleName.length());
            }

        }

        return simpleName;
    }


    /**
     * Returns the underlying class (effectively removing enhancement by Guice), often needed to identify annotations
     *
     * @param clazz
     */
    public static Class<?> classWithEnhanceRemoved(@Nonnull Class<?> clazz) {
        checkNotNull(clazz);
        while (clazz.getName()
                    .contains("$$EnhancerByGuice")) {
            clazz = clazz.getSuperclass();
        }
        return clazz;
    }

}