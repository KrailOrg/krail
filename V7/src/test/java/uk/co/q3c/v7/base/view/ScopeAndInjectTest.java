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

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;

import com.google.inject.Inject;

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
	 * 
	 */
	@Test
	public void confirmUIScope() {

		// given
		Reflections reflections = new Reflections("");

		Set<Class<? extends V7View>> subTypes = reflections.getSubTypesOf(V7View.class);
		Set<Class<? extends V7View>> concreteTypes = new HashSet<>();

		Set<Class<? extends V7View>> noInject = new HashSet<>();
		// Set<Class<? extends V7View>> noScope = new HashSet<>();

		// when
		// remove interfaces, abstract classes and inner classes
		// inner classes are the responsibility of the test
		for (Class<? extends V7View> clazz : subTypes) {
			if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()) && !clazz.isMemberClass()) {
				concreteTypes.add(clazz);
			}
		}

		for (Class<? extends V7View> clazz : concreteTypes) {
			// if (!clazz.isAnnotationPresent(UIScoped.class)) {
			// noScope.add(clazz);
			// }
			if (!hasConstructorWithInject(clazz)) {
				noInject.add(clazz);
			}

		}
		// These are test classes which don't need to be UISCoped (and make test construction more difficult if they
		// are)
		// noScope.remove(fixture.testviews2.TestAnnotatedView.class);
		// noScope.remove(fixture1.TestAnnotatedView.class);

		// if (noScope.size() > 0) {
		// System.out
		// .println("\n\n-------- The following V7View implementations are missing @UIScoped annotation -------------\n");
		// for (Class<? extends V7View> clazz : noScope) {
		// System.out.println(clazz.getName());
		// }
		// }

		if (noInject.size() > 0) {
			System.out
					.println("\n\n------ The following V7View implementation has no constructor with a javax.Inject annotation (if have you used the com.google.Inject, change it to javax.Inject ------\n");
			for (Class<? extends V7View> clazz : noInject) {
				System.out.println(clazz.getName());
			}
		}

		// then

		// assertThat(noScope).isEmpty();
		assertThat(noInject).isEmpty();

	}

	/**
	 * Looks for any classes using javax.inject.* instead of the com.google.inject.* equivalent. Convention for this
	 * project is to use the Google annotations. Using mixed types can cause assignment incompatibility.
	 */

	@Test
	public void googleAnnotations() {

		// given
		// when
		testForJavaxAnnotation(javax.inject.Singleton.class);
		testForJavaxAnnotation(javax.inject.Inject.class);

		// then

		// report for test output

	}

	/**
	 * Looks for use of javax.inject.Provider (should be com.google.inject.Provider). For the scope classes, however,
	 * the Provider has to be of the Google type, and are therefore excluded from the check
	 */
	@Test
	public void testForGoogleClasses() {
		// given
		List<Class<?>> targetTypes = new ArrayList<>();
		targetTypes.add(javax.inject.Provider.class);

		Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(
				new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner()).setUrls(
				ClasspathHelper.forPackage("uk.co.q3c")));

		Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);
		// when
		boolean failed = false;
		for (Class<? extends Object> clazz : allClasses) {
			Field[] declaredFields = clazz.getDeclaredFields();
			for (Field field : declaredFields) {
				if (targetTypes.contains(field.getType())) {
					System.out.println("Found target type " + field.getType() + " in " + clazz.getName());
					failed = true;
				}
				;
			}
		}

		// then
		assertThat(failed).isFalse().overridingErrorMessage("See console output if this fails");
	}

	private void testForJavaxAnnotation(Class<? extends Annotation> annotation) {
		Reflections reflections = new Reflections("");
		Set<Class<?>> googleInjects = reflections.getTypesAnnotatedWith(annotation);
		String outputMsg = "Testing for incorrect use of " + annotation.getName();
		if (!googleInjects.isEmpty()) {
			StringBuilder buf = new StringBuilder();
			for (Class<?> clazz : googleInjects) {
				buf.append(clazz.getName());
				buf.append(";");
			}
			outputMsg = buf.toString();
		}
		assertThat(googleInjects).hasSize(0).overridingErrorMessage(outputMsg);

	}

	/**
	 * All View classes should have an injected constructor. V7 standardises on the com.google.inject.Inject rather than
	 * the javax.inject.Inject, and this test will identify any implementations which do not have a constructor with a
	 * Google Inject annotation (it will also accept a no-args public constructor as Guice will accept that)
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
		Set<Class<?>> javaxInjects = new HashSet<>();
		// then
		for (Class<? extends Object> clazz : allClasses) {

			if (classHasJavaxInjectedConstructor(clazz)) {
				javaxInjects.add(clazz);
			}
		}

		// report for test output
		String outputMsg = "none found";
		if (!javaxInjects.isEmpty()) {
			StringBuilder buf = new StringBuilder();
			for (Class<?> clazz : javaxInjects) {
				buf.append(clazz.getName());
				buf.append(";");
			}
			outputMsg = buf.toString();
		}
		assertThat(javaxInjects).hasSize(0).overridingErrorMessage(outputMsg);
	}

	private boolean classHasJavaxInjectedConstructor(Class<?> clazz) {
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor : constructors) {
			if (constructor.getAnnotation(javax.inject.Inject.class) != null) {
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
