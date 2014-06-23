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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class TranslateTest {

	@Inject
	Translate translate;

	@Inject
	CurrentLocale currentLocale;

	@Before
	public void setup() {
		currentLocale.removeAllListeners();
		currentLocale.setLocale(Locale.UK);
	}

	@Test
	public void test() {

		Locale germanSwitzerland = new Locale("de", "CH");
		// when
		assertThat(translate.from(LabelKey.Cancel)).isEqualTo("Cancel");
		assertThat(translate.from(LabelKey.Ok)).isEqualTo("Ok");
		// then
		assertThat(translate.from(LabelKey.Cancel, Locale.GERMAN)).isEqualTo("Stornieren");
		// Ok is not redefined in _de
		assertThat(translate.from(LabelKey.Ok, Locale.GERMAN)).isEqualTo("Ok");

		// this in inherited from Labels_de
		assertThat(translate.from(LabelKey.Cancel, germanSwitzerland)).isEqualTo("Stornieren");
		// this is inherited from Labels (2 levels of inheritance)
		assertThat(translate.from(LabelKey.Ok, germanSwitzerland)).isEqualTo("Ok");
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
