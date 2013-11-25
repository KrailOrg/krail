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
package uk.co.q3c.util;

import java.util.HashSet;
import java.util.Set;

public class ReflectionUtils {

	public static Set<Class<?>> allInterfaces(Class<?> clazz) {
		Set<Class<?>> allInterfaces = new HashSet<>();
		Class<?> classToCheck = clazz;
		while (classToCheck != null) {
			Class<?>[] interfaces = classToCheck.getInterfaces();
			for (Class<?> intf : interfaces) {
				allInterfaces.add(intf);
			}
			classToCheck = classToCheck.getSuperclass();
		}
		return allInterfaces;
	}
}
