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

package uk.q3c.util;

/**
 * Created by David Sowerby on 03 Jan 2016
 */
public interface ClassNameUtils {

    /**
     * Returns a "pure" class name, ignoring any appended enhancement
     *
     * @param clazz the class to return the simple name of
     *
     * @return a "pure" class name, ignoring any appended enhancement
     */

    String simpleClassNameEnhanceRemoved(Class<?> clazz);

    /**
     * Returns the underlying class (effectively removing enhancement by Guice), often needed to identify annotations
     *
     * @param clazz the original, possibly enhanced class
     */

    Class<?> classWithEnhanceRemoved(Class<?> clazz);
}
