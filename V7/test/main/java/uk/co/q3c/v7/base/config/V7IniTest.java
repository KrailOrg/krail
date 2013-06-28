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

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;

import org.apache.shiro.config.Ini.Section;
import org.junit.Before;
import org.junit.Test;

import uk.co.q3c.v7.base.config.V7Ini.DbParam;

public class V7IniTest {

	private final String[] pages = new String[] { "publicHome", "privateHome", "login", "logout" };
	private final String[] dbparams = new String[] { "dbURL", "dbUser", "dbPwd" };

	String test0 = "test0.V7.ini"; // should be non-existent
	String test1 = "test1.V7.ini";
	String test2 = "test2.V7.ini";
	String test3 = "test3.V7.ini";
	String test4 = "test4.V7.ini";
	String test5 = "test5.V7.ini";
	String test6 = "test6.V7.ini";
	V7Ini ini;

	@Before
	public void setup() {
		ini = new V7Ini();
	}

	@Test()
	public void missingFile() {

		// given
		String testFile = test0;
		// when
		ini.loadFromPath(filepath(testFile));
		// then
		allPropertiesHaveAValue("db", dbparams);
	}

	@Test()
	public void missingProperties() {

		// given
		String testFile = test2;
		// when
		ini.loadFromPath(filepath(testFile));
		// then
		allPropertiesHaveAValue("db", dbparams);
	}

	@Test
	public void dbParam() {

		// given
		String testFile = test1;
		// when
		ini.loadFromPath(filepath(testFile));
		// then
		assertThat(ini.dbParam(DbParam.dbURL)).isEqualTo("memory:scratchpad");
		assertThat(ini.dbParam(DbParam.dbUser)).isEqualTo("admin");
		assertThat(ini.dbParam(DbParam.dbPwd)).isEqualTo("admin");

	}

	@Test
	public void noOptionsSection() {

		// given
		String testFile = test3;
		// when
		ini.loadFromPath(filepath(testFile));

		// then
		assertThat(ini.optionReadSiteMap()).isTrue();
	}

	@Test
	public void noOptionValues() {

		// given
		String testFile = test4;
		// when
		ini.loadFromPath(filepath(testFile));

		// then
		assertThat(ini.optionReadSiteMap()).isTrue();
	}

	@Test
	public void optionValues1() {

		// given
		String testFile = test5;
		// when
		ini.loadFromPath(filepath(testFile));

		// then
		assertThat(ini.optionReadSiteMap()).isTrue();
	}

	@Test
	public void optionValues2() {

		// given
		String testFile = test6;
		// when
		ini.loadFromPath(filepath(testFile));

		// then
		assertThat(ini.optionReadSiteMap()).isFalse();
	}

	@Test
	public void save() {

		// given
		String testFile = test3; // make usre content is not default
		ini.loadFromPath(filepath(testFile));
		ini.load();
		// when
		ini.save("$user.home", "temp", "V7.ini");
		// then
		File home = new File(System.getProperty("user.home"));
		File temp = new File(home, "temp");
		File f = new File(temp, "V7.ini");
		assertThat(f.exists()).isTrue();
		// when
		V7Ini ini2 = new V7Ini();
		ini2.loadFromPath("file:" + f.getAbsolutePath());
		assertThat(ini2.getSectionNames()).isEqualTo(ini.getSectionNames());
		for (Section section : ini.getSections()) {
			assertThat(section.values().toString()).isEqualTo(ini2.get(section.getName()).values().toString());
		}

	}

	/**
	 * This is not really a test method, just a convenient way to save a file version of the default ini settings (to
	 * $user.home/temp/V7.ini.default)
	 */
	@Test
	public void saveDefaults() {

		// given
		File home = new File(System.getProperty("user.home"));
		File temp = new File(home, "temp");
		File f = new File(temp, "V7.ini.default");
		ini.load();
		// when
		ini.save("$user.home", "temp", "V7.ini.default");
		// then
		assertThat(f.exists()).isTrue();

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
		return filepath;
	}

}
