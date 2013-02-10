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
package uk.co.q3c.v7.util;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;

public class MessageFormatTest {
	@Test
	public void formatValid() {

		// given
		String pattern = "This is a {1} pattern where the {0} can be in any {2}";
		Object[] arguments = new Object[] { "parameters", "simple", "order" };
		// when
		String result = MessageFormat.format(pattern, arguments);
		// then
		assertThat(result).isEqualTo("This is a simple pattern where the parameters can be in any order");

	}

	@Test
	public void formatValidContiguous() {

		// given
		String pattern = "This is a {1} pattern where the {0} can be in any {2}{3}";
		Object[] arguments = new Object[] { "parameters", "simple", "order", " you like" };
		// when
		String result = MessageFormat.format(pattern, arguments);
		// then
		assertThat(result).isEqualTo("This is a simple pattern where the parameters can be in any order you like");

	}

	@Test
	public void formatValid0_9() {

		// given
		String pattern = "This is a {1} pattern where the {0} can be in any {2}{3}{4}{5}{6}{7}{8}{9}";
		Object[] arguments = new Object[] { "parameters", "simple", "order", "a", "b", "c", "d", "e", "f", "g" };
		// when
		String result = MessageFormat.format(pattern, arguments);
		// then
		assertThat(result).isEqualTo("This is a simple pattern where the parameters can be in any orderabcdefg");

	}

	@Test
	public void formatValidDoubleDigit() {

		// given
		String pattern = "This is a {1} pattern where the {0} can be in any {2}{3}{4}{5}{6}{7}{8}{9}{10}";
		Object[] arguments = new Object[] { "parameters", "simple", "order", "a", "b", "c", "d", "e", "f", "g", "h" };
		// when
		String result = MessageFormat.format(pattern, arguments);
		// then
		assertThat(result).isEqualTo("This is a simple pattern where the parameters can be in any orderabcdefgh");

	}

	@Test
	public void formatValidEscaped() {

		// given
		String pattern = "This is a {1} pattern where the {0} can be in any {2} ignoring \\{3}";
		Object[] arguments = new Object[] { "parameters", "simple", "order" };
		// when
		String result = MessageFormat.format(pattern, arguments);
		// then
		assertThat(result).isEqualTo("This is a simple pattern where the parameters can be in any order ignoring {3}");

	}

	@Test
	public void formatTooManyArguments() {

		// given
		String pattern = "This is a {1} pattern where the {0} can be in any {2}";
		Object[] arguments = new Object[] { "parameters", "simple", "order", "butty" };
		// when
		String result = MessageFormat.format(pattern, arguments);
		// then
		assertThat(result).isEqualTo(pattern);

	}

	@Test
	public void formatTooFewArguments() {

		// given
		String pattern = "This is a {1} pattern where the {0} can be in any {2}";
		Object[] arguments = new Object[] { "parameters", "simple" };
		// when
		String result = MessageFormat.format(pattern, arguments);
		// then
		assertThat(result).isEqualTo(pattern);

	}

}
