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
package uk.q3c.krail.core.navigate.sitemap;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import fixture.TestI18NModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.user.opt.DefaultUserOption;
import uk.q3c.krail.core.user.opt.DefaultUserOptionStore;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionStore;
import uk.q3c.krail.core.view.PublicHomeView;
import uk.q3c.krail.i18n.TestLabelKey;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, VaadinSessionScopeModule.class})
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

    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(UserOption.class).to(DefaultUserOption.class);
                bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
            }

        };
    }

}
