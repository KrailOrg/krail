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
package uk.co.q3c.v7.base.guice.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A utility class for {@link Service} implementations
 * 
 * @author David Sowerby
 * 
 */
public class ServiceUtils {

	/**
	 * Extracts dependencies from the {@link DependsOnServices} annotation, and the {@link Service#getDependencies()}
	 * method, and combines the two into one Set.
	 * 
	 * @param service
	 * @return
	 */
	public static Set<Class<? extends Service>> extractDependencies(Service service) {
		Set<Class<? extends Service>> methodDependencies = extractMethodDependencies(service);
		List<Class<? extends Service>> annotationDependencies = extractAnnotationDependencies(service);

		// merge them - as this is a Set, duplicates will be rejected
		if (annotationDependencies != null) {
			methodDependencies.addAll(annotationDependencies);
		}
		return methodDependencies;
	}

	/**
	 * Extracts service ids of dependencies from the {@link DependsOnServices} annotation, and the
	 * {@link Service#getDependencies()} method, and combines the two into one Set.
	 * 
	 * @param service
	 * @return
	 */
	public static Set<String> extractDependenciesServiceIds(Service service) {
		Set<Class<? extends Service>> dependencies = extractDependencies(service);
		Set<String> dependencyIds = new HashSet<>();
		for (Class<? extends Service> dependency : dependencies) {
			String id = serviceId(dependency);
			dependencyIds.add(id);
		}
		return dependencyIds;
	}

	/**
	 * Retrieves and returns dependencies from the Service. If the service returns null, returns an empty Set;
	 * 
	 * @param service
	 * @return
	 */
	public static Set<Class<? extends Service>> extractMethodDependencies(Service service) {
		Set<Class<? extends Service>> dependencies = service.getDependencies();
		if (dependencies == null) {
			dependencies = new HashSet<>();
		}
		return dependencies;
	}

	/**
	 * Returns the dependencies specified by {@code Service} in a {@link DependsOnServices} annotation, or null if none
	 * are specified. Annotation dependencies are inherited.
	 * 
	 * @param service
	 * @return
	 */
	public static List<Class<? extends Service>> extractAnnotationDependencies(Service service) {
		Class<?> clazz = unenhancedClass(service.getClass());
		DependsOnServices annotation = clazz.getAnnotation(DependsOnServices.class);
		if (annotation != null) {
			List<Class<? extends Service>> dependencies = Arrays.asList(annotation.services());
			return dependencies;
		}
		return null;
	}

	/**
	 * Extracts an identifier for the service. This is used primarily because it has not yet been decided whether to
	 * employ an explicitly declared service id, or use the class name. This makes it easier to change
	 * 
	 * @param service
	 * @return
	 */
	public static String serviceId(Service service) {
		return serviceId(service.getClass());
	}

	/**
	 * Extracts an identifier for the service. This is used primarily because it has not yet been decided whether to
	 * employ an explicitly declared service id, or use the class name. This makes it easier to change
	 * 
	 * @param service
	 * @return
	 */
	public static String serviceId(Class<? extends Service> serviceClass) {
		Class<?> clazz = unenhancedClass(serviceClass);
		return clazz.getName();
	}

	/**
	 * Returns the underlying class un-enhanced by Guice, needed to identify annotations
	 * 
	 * @param serviceClass
	 */
	public static Class<?> unenhancedClass(Class<? extends Service> serviceClass) {
		Class<?> clazz = serviceClass;
		while (clazz.getName().contains("EnhancerByGuice")) {
			clazz = clazz.getSuperclass();
		}
		return clazz;
	}
}
