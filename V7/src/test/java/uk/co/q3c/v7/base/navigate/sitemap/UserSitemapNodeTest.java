/*
 * Copyright (C) 2014 David Sowerby
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
package uk.co.q3c.v7.base.navigate.sitemap;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import fixture.testviews2.View1;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.i18n.CurrentLocale;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.Translate;

import java.text.Collator;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class UserSitemapNodeTest {

	private UserSitemapNode userNode;

	@Inject
	Translate translate;

	@Inject
	CurrentLocale currentLocale;

	@Test
	public void translate() {
		// given
		MasterSitemapNode masterNode = new MasterSitemapNode("a", View1.class, LabelKey.Home_Page);
		userNode = new UserSitemapNode(masterNode);
		currentLocale.setLocale(Locale.GERMANY);
		Collator collator = Collator.getInstance(Locale.GERMANY);
		// when
		userNode.translate(translate, Locale.GERMANY, collator);
		// then
		assertThat(userNode.getLabel()).isEqualTo("Startseite");
		assertThat(userNode.getCollationKey()).isEqualTo(collator.getCollationKey("Startseite"));
	}
}
