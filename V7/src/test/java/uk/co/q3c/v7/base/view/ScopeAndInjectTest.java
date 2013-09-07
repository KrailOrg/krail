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
package uk.co.q3c.v7.base.view;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;

/**
 * Checks for correct use of UIScope and Inject annotations
 * 
 * @author David Sowerby 7 Sep 2013
 * 
 */
public class ScopeAndInjectTest {
	/**
	 * All implementations of V7View should be have a scope of {@link UIScoped} to avoid issues when navigating and
	 * going back to existing pages.
	 * <p>
	 * They should also have an injected constructor. V7 standardises on the javax.Inject rather than the
	 * com.google.inject.Inject, and this test will identify any implementations which do not have a constructor with a
	 * javax.Inject annotation (it will also accept a no-args public constructor as Guice will accept that)
	 * 
	 * 
	 */
	@Test
	public void confirmUIScope() {

		// given
		Reflections reflections = new Reflections("");

		Set<Class<? extends V7View>> subTypes = reflections.getSubTypesOf(V7View.class);
		Set<Class<? extends V7View>> concreteTypes = new HashSet<>();

		Set<Class<? extends V7View>> noInject = new HashSet<>();
		Set<Class<? extends V7View>> noScope = new HashSet<>();

		// when
		// remove interfaces, abstract classes and inner classes
		// inner classes are the responsibility of the test
		for (Class<? extends V7View> clazz : subTypes) {
			if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()) && !clazz.isMemberClass()) {
				concreteTypes.add(clazz);
			}
		}

		for (Class<? extends V7View> clazz : concreteTypes) {
			if (!clazz.isAnnotationPresent(UIScoped.class)) {
				noScope.add(clazz);
			}
			if (!hasConstructorWithInject(clazz)) {
				noInject.add(clazz);
			}

		}

		if (noScope.size() > 0) {
			System.out
					.println("\n\n-------- The following V7View implementations are missing @UIScoped annotation -------------\n");
			for (Class<? extends V7View> clazz : noScope) {
				System.out.println(clazz.getName());
			}
		}

		if (noInject.size() > 0) {
			System.out
					.println("\n\n------ The following V7View implementation has no constructor with a javax.Inject annotation (if have you used the com.google.Inject, change it to javax.Inject ------\n");
			for (Class<? extends V7View> clazz : noInject) {
				System.out.println(clazz.getName());
			}
		}

		// then
		assertThat(noScope.size(), is(0));
		assertThat(noInject.size(), is(0));

	}

	/**
	 * Looks for any classes using com.google.inject.Inject - they should be using javax.inecjt.Inject
	 */
	@Test
	public void googleInject() {

		// given
		SubTypesScanner scanner = new SubTypesScanner(false);
		Reflections reflections = new Reflections("", scanner);
		// when
		// This requires MethodAnnotationsScanner to be set up according to the javadoc, but it is not clear how that
		// should be done
		// Set<Constructor> googleInjects = reflections.getConstructorsAnnotatedWith(com.google.inject.Inject.class);
		// Use a big hammer instead
		Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
		Set<Class<?>> googleInjects = new HashSet<>();
		// then
		for (Class<? extends Object> clazz : allClasses) {

			if (classHasGoogleInjectedConstructor(clazz)) {
				googleInjects.add(clazz);
			}
		}
		assertThat(googleInjects.size(), is(0));
	}

	private boolean classHasGoogleInjectedConstructor(Class<?> clazz) {
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor : constructors) {
			if (constructor.getAnnotation(com.google.inject.Inject.class) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * returns true if there is a constructor for {@code clazz} with either a {@link Inject} annotation or a
	 * parameterless public constructor (as required by Guice)
	 * 
	 * @param clazz
	 * @return
	 */
	protected boolean hasConstructorWithInject(Class<? extends V7View> clazz) {
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor : constructors) {
			if (constructor.getAnnotation(Inject.class) != null) {
				return true;
			}
			if ((constructor.getParameterTypes().length == 0) && (Modifier.isPublic(constructor.getModifiers()))) {
				return true;
			}
		}
		return false;
	}

	protected boolean classHasTestAnnotatedMethods(Class<? extends V7View> clazz) {
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.getAnnotation(Test.class) != null) {
				return true;
			}
		}
		return false;
	}
}
