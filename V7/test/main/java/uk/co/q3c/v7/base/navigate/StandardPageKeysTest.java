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

import java.util.Locale;

import org.junit.Test;

public class StandardPageKeysTest {

	@Test
	public void keyLabels() {

		// given

		// when

		// then
		for (StandardPageKey key : StandardPageKey.values()) {
			assertThat(key.getValue(Locale.UK)).overridingErrorMessage(key.name()).isNotNull();

		}
	}

	@Test
	public void segment() {

		// given

		// when

		// then
		assertThat(StandardPageKey.defaultSegment(StandardPageKey.Public_Home)).isEqualTo("public-home");
		assertThat(StandardPageKey.defaultSegment(StandardPageKey.Secure_Home)).isEqualTo("secure-home");
		assertThat(StandardPageKey.defaultSegment(StandardPageKey.Login)).isEqualTo("login");
		assertThat(StandardPageKey.defaultSegment(StandardPageKey.Request_Account)).isEqualTo("request-account");
		assertThat(StandardPageKey.defaultSegment(StandardPageKey.Unlock_Account)).isEqualTo("unlock-account");
		assertThat(StandardPageKey.defaultSegment(StandardPageKey.System_Account)).isEqualTo("system-account");
	}

	@Test
	public void uri() {

		// given

		// when

		// then
		assertThat(StandardPageKey.defaultUri(StandardPageKey.Public_Home)).isEqualTo("public/public-home");
		assertThat(StandardPageKey.defaultUri(StandardPageKey.Secure_Home)).isEqualTo("secure/secure-home");
		assertThat(StandardPageKey.defaultUri(StandardPageKey.Login)).isEqualTo("public/login");
		assertThat(StandardPageKey.defaultUri(StandardPageKey.Request_Account)).isEqualTo(
				"public/system-account/request-account");
		assertThat(StandardPageKey.defaultUri(StandardPageKey.Unlock_Account)).isEqualTo(
				"public/system-account/unlock-account");
		assertThat(StandardPageKey.defaultUri(StandardPageKey.System_Account)).isEqualTo("public/system-account");
	}

}
