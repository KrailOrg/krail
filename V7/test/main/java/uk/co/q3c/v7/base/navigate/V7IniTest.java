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
package uk.co.q3c.v7.base.navigate;

import static org.fest.assertions.Assertions.*;

import java.io.File;

import org.apache.shiro.config.Ini.Section;
import org.junit.Before;
import org.junit.Test;

import uk.co.q3c.v7.base.navigate.V7Ini.StandardPageKey;

public class V7IniTest {

	private final String[] pages = new String[] { "publicHome", "secureHome", "login", "logout" };

	String test0 = "test0.V7.ini"; // should be non-existent
	String test1 = "test1.V7.ini";
	String test2 = "test2.V7.ini";

	V7Ini ini;

	@Before
	public void setup() {
		ini = new V7Ini();
	}

	@Test()
	public void missingSection() {

		// given
		String testFile = test1;
		// when
		ini.loadFromPath(filepath(testFile));
		ini.validate();
		// then
		allPropertiesHaveAValue("pages", pages);
	}

	@Test()
	public void missingFile() {

		// given
		String testFile = test0;
		// when
		ini.loadFromPath(filepath(testFile));
		// then
		allPropertiesHaveAValue("pages", pages);
	}

	@Test()
	public void missingProperties() {

		// given
		String testFile = test2;
		// when
		ini.loadFromPath(filepath(testFile));
		// then
		allPropertiesHaveAValue("pages", pages);
	}

	@Test
	public void standardPage() {

		// given
		String testFile = test1;
		// when
		ini.loadFromPath(filepath(testFile));
		ini.validate();

		// then
		assertThat(ini.standardPageURI(StandardPageKey.secureHome)).isEqualTo("secure/home");
		assertThat(ini.standardPageURI(StandardPageKey.publicHome)).isEqualTo("public/home");

	}

	private void allPropertiesHaveAValue(String sectionName, String[] keys) {
		Section section = ini.getSection(sectionName);
		for (String key : keys) {
			assertThat(section.containsKey(key)).isTrue();
		}
	}

	private String filepath(String filename) {
		File f = new File("test");
		File f2 = new File(f, filename);
		String filepath = "file:" + f2.getAbsolutePath();
		System.out.println(filepath);
		return filepath;
	}

}
