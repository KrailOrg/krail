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

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class StandardPagesTest {

	String testFilepath = "C:/Users/SowerbyD/git/v7/V7/test/";
	String test0 = "test0.V7.ini"; // should be non-existent
	String test1 = "test1.V7.ini";
	String test2 = "test2.V7.ini";

	StandardPages pages;

	@Before
	public void setup() {
		pages = new StandardPages();
	}

	@Test()
	public void missingSection() {

		// given

		pages.setFilepath("file:" + testFilepath + test1);
		// when
		pages.load();
		// then
		assertThat(pages.isLoaded()).isFalse();
		assertThat(allPropertiesHaveAValue(pages)).isTrue();
	}

	@Test()
	public void missingFile() {

		// given
		pages.setFilepath("file:" + testFilepath + test0);
		// when
		pages.load();
		// then
		assertThat(pages.isLoaded()).isFalse();
		assertThat(allPropertiesHaveAValue(pages)).isTrue();
	}

	@Test()
	public void missingProperties() {

		// given
		pages.setFilepath("file:" + testFilepath + test2);
		// when
		pages.load();
		// then
		assertThat(pages.isLoaded()).isTrue();
		assertThat(allPropertiesHaveAValue(pages)).isTrue();
	}

	private boolean allPropertiesHaveAValue(StandardPages pages) {
		// TODO
		return true;

	}

}
