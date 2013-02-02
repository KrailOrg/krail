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

import static org.fest.assertions.Assertions.*;

import org.apache.shiro.io.ResourceUtils;
import org.junit.Test;

public class ConfigUtilTest {

	private String resourcePath;

	@Test
	public void shiroFilePathWithExpandedVariable_fullSpec() {

		// given
		resourcePath = ResourceUtils.FILE_PREFIX + "$user.home/directory/filename.txt";
		String expected = "file:" + buildPath("directory", "filename.txt");
		// when
		String result = ConfigUtil.shiroFilePathWithExpandedVariable(resourcePath);
		// then
		assertThat(result).isEqualTo(expected);

	}

	@Test
	public void shiroFilePathWithExpandedVariable_notFileContext() {

		// given
		resourcePath = ResourceUtils.CLASSPATH_PREFIX + "$user.home/directory/filename.txt";
		// when
		String result = ConfigUtil.shiroFilePathWithExpandedVariable(resourcePath);
		// then
		assertThat(result).isEqualTo(resourcePath);
	}

	@Test
	public void shiroFilePathWithExpandedVariable_nobase_noLeadingSlash() {

		// given
		resourcePath = ResourceUtils.FILE_PREFIX + "directory/filename.txt";
		// when
		String result = ConfigUtil.shiroFilePathWithExpandedVariable(resourcePath);
		// then
		assertThat(result).isEqualTo(resourcePath);
	}

	@Test
	public void shiroFilePathWithExpandedVariable_nobase_withLeadingSlash() {

		// given
		resourcePath = ResourceUtils.FILE_PREFIX + "/directory/filename.txt";
		// when
		String result = ConfigUtil.shiroFilePathWithExpandedVariable(resourcePath);
		// then
		assertThat(result).isEqualTo(resourcePath);
	}

	@Test
	public void shiroFilePathWithExpandedVariable_noDirectory() {

		// given
		resourcePath = ResourceUtils.FILE_PREFIX + "$user.home/filename.txt";
		String expected = "file:" + buildPath("filename.txt");
		// when
		String result = ConfigUtil.shiroFilePathWithExpandedVariable(resourcePath);
		// then
		assertThat(result).isEqualTo(expected);

	}

	private String buildPath(String directory, String filename) {
		return System.getProperty("user.home") + "/" + directory + "/" + filename;
	}

	private String buildPath(String filename) {
		return System.getProperty("user.home") + "/" + filename;
	}
}
