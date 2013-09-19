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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 * The native Java {@link java.text.MessageFormat} has some quirky behaviour especially when using the apostrophe
 * (single quote) character. The {@link MessageFormatter} from sl4j claims much faster performance, but expects the
 * argument parameters and arguments to be in the same order (this is perfectly reasonable for logging, but different
 * languages will require substitution in different orders).
 * <p>
 * Neither is completely suited to I18N translation
 * <p>
 * 
 * 
 * @author David Sowerby 10 Feb 2013
 * 
 */
public class MessageFormat {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageFormat.class);

	/**
	 * This method uses {@link MessageFormatter} for speed and resilience, but acts as an intermediary and takes a
	 * pattern string of the format:
	 * <p>
	 * <ul>
	 * <em>this is a {1} pattern where the {0} can be in any {2}</i>
	 * </ul>
	 * and arguments
	 * <ul>
	 * <i>parameters, simple, order</em>
	 * </ul>
	 * will result in
	 * <ul>
	 * <i>this is a simple pattern where the parameters can be in any order</i>
	 * </ul>
	 * This is done by sorting the arguments into the same order as they are required by the pattern. No claims are made
	 * for efficiency or performance - {@link MessageFormatter} is fast, but this utility has not been optimised. This
	 * method is deliberately not tolerant of errors in the pattern structure - substitution will simply not occur, and
	 * the unmodified pattern returned.
	 * <p>
	 * If you want to include a "{" in the output, simply escape it "\\{". This will escape the whole placeholder
	 * <p>
	 * You can have any number of parameters, provided the numbering sequence is continuous, starts from zero, and is
	 * matched by the same number of arguments.
	 */
	public static String format(String pattern, Object... arguments) {
		List<Integer> parameters = new ArrayList<>();
		try {
			String strippedPattern = scanForParameters(pattern, parameters);
			Object[] sortedArguments = sortArguments(parameters, arguments, pattern);
			return MessageFormatter.arrayFormat(strippedPattern, sortedArguments).getMessage();
		} catch (Exception e) {
			return pattern;
		}
	}

	private static String scanForParameters(String pattern, List<Integer> parameters) {
		int i = 0;
		StringBuilder strippedPattern = new StringBuilder();
		while (i < pattern.length()) {
			char c = pattern.charAt(i);
			// if the '{' has been escaped this moves the scan beyond it, thereby ignoring it
			if (c == '\\') {
				i++;
				c = pattern.charAt(i);
				if (c == '{') {
					strippedPattern.append('{');
					i++;
					c = pattern.charAt(i);
					strippedPattern.append(c);
				}
			} else {
				strippedPattern.append(c);
			}
			// find an opening brace
			if (c == '{') {
				// find the closing '}' and extract
				StringBuilder placeholder = new StringBuilder();
				boolean done = false;

				while (!done) {
					i++;
					c = pattern.charAt(i);

					if (c == '}') {
						parameters.add(Integer.valueOf(placeholder.toString()));
						strippedPattern.append(c);
						done = true;
					} else {
						placeholder.append(c);
					}
				}
			}

			i++;
		}
		return strippedPattern.toString();
	}

	private static Object[] sortArguments(List<Integer> parameters, Object[] arguments, String pattern) {
		if (parameters.size() != arguments.length) {
			Object[] args = new Object[] { parameters.size(), arguments.length, pattern };
			LOGGER.warn(
					"Message pattern and arguments do not match, there are {} parameters in the pattern, and {} arguments. The pattern is: '{}'",
					args);
			throw new RuntimeException();
		}
		List<Object> sortedArguments = new ArrayList<>();
		for (Integer i : parameters) {
			sortedArguments.add(arguments[i]);
		}
		return sortedArguments.toArray();
	}
}
