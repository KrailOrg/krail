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
package uk.co.q3c.v7.base.navigate.sitemap;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.vsscope.VaadinSessionScopeModule;
import uk.co.q3c.v7.base.view.PublicHomeView;
import uk.co.q3c.v7.i18n.I18NModule;
import uk.co.q3c.v7.i18n.TestLabelKey;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ I18NModule.class, VaadinSessionScopeModule.class })
public class MasterSitemapNodeTest {

	@Test
	public void setLabelKey() {

		// given
		MasterSitemapNode node = new MasterSitemapNode();
		// when
		node.setLabelKey(TestLabelKey.Yes);
		// then
		assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Yes);
	}

	@Test
	public void constructor() {

		// given

		// when
		SitemapNode node = new MasterSitemapNode("one", PublicHomeView.class, TestLabelKey.Yes);
		// then
		assertThat(node.getUriSegment()).isEqualTo("one");
		assertThat(node.getViewClass()).isEqualTo(PublicHomeView.class);
		assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Yes);

	}

}
