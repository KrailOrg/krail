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
package uk.co.q3c.v7.base.config;

import java.io.File;

import org.apache.shiro.io.ResourceUtils;

import com.google.common.base.Strings;

/**
 * Helper methods for config manipulation
 * 
 * @author David Sowerby 2 Feb 2013
 * 
 */
public class ConfigUtil {

	/**
	 * 
	 * @param base
	 *            the base of the path, for example <code>$user.home</code>. Must be prefixed with $, and will be
	 *            expanded using {@link System#getProperty()}. Null or empty is ignored
	 * @param directory
	 *            the directory within base, or if base is not specified, within default directory. Can be null or
	 *            empty.
	 * @param filename
	 *            The filename. Cannot be null or empty
	 * @return
	 */
	public static File fileFromPathWithVariable(String base, String directory, String filename) {
		File d;
		if (!Strings.isNullOrEmpty(base)) {
			File b = new File(expandProperty(base));
			d = new File(b, directory);
		} else {
			d = new File(directory);
		}
		File f = new File(d, filename);
		return f;
	}

	private static String expandProperty(String s) {
		String s1 = s.replace("$", "");
		return System.getProperty(s1);
	}

	/**
	 * Shiro uses a context prefix with its resource path {@link ResourceUtils#getInputStreamForPath(String)}. This
	 * method allows the inclusion of a system variable within the file path specification, expands the variable, and
	 * returns the result in a format suitable for use with Shiro ResourceUtils
	 * 
	 * @param resourcePath
	 *            a resource path of the form "file:$user.home/directory/filename". If there is no "file:" prefix, or
	 *            variable specified, <code>resourcePath</code> is returned unmodified.
	 * @return the resourcePath with the system variable expanded. This "file:$user.home/directory/filename" will be
	 *         returned, for example as "file:/home/david/directory/filename"
	 */
	public static String shiroFilePathWithExpandedVariable(String resourcePath) {
		if (resourcePath.startsWith(ResourceUtils.FILE_PREFIX)) {
			int c1 = resourcePath.indexOf('$');
			int c2 = resourcePath.indexOf('/');
			int c3 = resourcePath.lastIndexOf('/');
			int c4 = resourcePath.length();
			String base = (c1 < 0) ? null : resourcePath.substring(c1, c2);
			if (Strings.isNullOrEmpty(base)) {
				return resourcePath;
			}
			String directory = resourcePath.substring(c2, c3);
			String filename = resourcePath.substring(c3, c4);
			return "file:" + fileFromPathWithVariable(base, directory, filename);
		} else {
			return resourcePath;
		}
	}

	/**
	 * OrientDB uses 'local', 'remote' and 'memory' contexts for its database path. See
	 * http://code.google.com/p/orient/wiki/Concepts#Database. This method will expand a system property in the file
	 * path for the local option only. If the context is 'remote' or 'memory', the input string is returned unmodified.
	 * 
	 * @param resourcePath
	 * @return
	 */
	public static String orientFilePathWithExpandedVariable(String resourcePath) {
		if (resourcePath.contains("local:")) {
			// use the #shiroFilePathWithExpandedVariable, but it needs 'file' not 'local'
			String s = resourcePath.replaceFirst("local:", "file:");
			return shiroFilePathWithExpandedVariable(s).replace("file:", "local:");
		} else {
			return resourcePath;
		}
	}
}
