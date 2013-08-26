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
package uk.co.q3c.v7.i18n;

import static org.fest.assertions.Assertions.*;

import java.util.Locale;

import org.junit.Test;

/**
 * General I18N testing
 * 
 * @author David Sowerby 9 Feb 2013
 * 
 */
public class I18NTest {
	@Test
	public void getValue() {

		// given
		Locale germanSwitzerland = new Locale("de", "CH");
		// when

		// then
		assertThat(LabelKey.Cancel.getValue(Locale.UK)).isEqualTo("Cancel");
		assertThat(LabelKey.Ok.getValue(Locale.UK)).isEqualTo("Ok");
		assertThat(LabelKey.Cancel.getValue(Locale.GERMAN)).isEqualTo("Stornieren");
		assertThat(LabelKey.Ok.getValue(Locale.GERMAN)).isEqualTo("Ok");

		// this in inherited from Labels_de
		assertThat(LabelKey.Cancel.getValue(germanSwitzerland)).isEqualTo("Stornieren");
		// this is inherited from Labels (2 levels of inheritance)
		assertThat(LabelKey.Ok.getValue(germanSwitzerland)).isEqualTo("Ok");
	}

	public void getMissingValue() {

		// given

		// when
		String value = LabelKey._nullkey_.getValue(Locale.GERMAN);
		// then
		assertThat(value).isEqualTo("x");
	}

}
