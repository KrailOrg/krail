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
import java.util.MissingResourceException;

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
		assertThat(LabelKeys.cancel.getValue(Locale.UK)).isEqualTo("cancel");
		assertThat(LabelKeys.ok.getValue(Locale.UK)).isEqualTo("ok");
		assertThat(LabelKeys.cancel.getValue(Locale.GERMAN)).isEqualTo("stornieren");
		assertThat(LabelKeys.ok.getValue(Locale.GERMAN)).isEqualTo("ok");

		// this in inherited from Labels_de
		assertThat(LabelKeys.cancel.getValue(germanSwitzerland)).isEqualTo("stornieren");
		// this is inherited from Labels (2 levels of inheritance)
		assertThat(LabelKeys.ok.getValue(germanSwitzerland)).isEqualTo("ok");
	}

	@Test(expected = MissingResourceException.class)
	public void getMissingValue() {

		// given

		// when
		LabelKeys._notdefined_.getValue(Locale.GERMAN);
		// then
		// exception expected
	}

}
