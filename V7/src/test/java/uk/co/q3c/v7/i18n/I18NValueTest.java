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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class I18NValueTest {

	@Inject
	Translate i18NValue;

	@Test
	public void value() {

		// given
		// current locale is UK
		// when
		String result = i18NValue.from(LabelKey.First_Name);
		// then
		assertThat(result).isEqualTo("First Name");

	}

	@Test
	public void valueFromName() {

		// given
		// current locale is UK

		// when
		String result = i18NValue.from(TestLabelKey.Login);

		// then
		assertThat(result).isEqualTo("Login");

	}

	@Test
	public void valueWithParams() {

		// given

		// when
		String result = i18NValue.from(MessageKey.invalidURI, "public/wiggly/id=3");
		// then
		assertThat(result).isEqualTo("public/wiggly/id=3 is not a valid page");

	}

	@ModuleProvider
	protected AbstractModule moduleProvider() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				bind(I18NProcessor.class).to(DefaultI18NProcessor.class);
			}

		};
	}

}
