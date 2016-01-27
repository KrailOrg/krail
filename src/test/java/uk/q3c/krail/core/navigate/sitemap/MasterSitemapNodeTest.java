/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.core.navigate.sitemap;

import com.google.common.collect.Lists;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.i18n.TestLabelKey;
import uk.q3c.krail.core.shiro.PageAccessControl;
import uk.q3c.krail.core.view.LoginView;
import uk.q3c.krail.testutil.TestI18NModule;
import uk.q3c.krail.testutil.TestOptionModule;
import uk.q3c.krail.testutil.TestPersistenceModule;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, VaadinSessionScopeModule.class, TestPersistenceModule.class, TestOptionModule.class, EventBusModule.class, UIScopeModule
        .class})
public class MasterSitemapNodeTest {

    @Test
    public void setLabelKey() {

        // given

        // when
        MasterSitemapNode node = new MasterSitemapNode(1, "a", LoginView.class, TestLabelKey.Yes, 3, PageAccessControl.PERMISSION, Lists.newArrayList("a", "b"));
        // then
        assertThat(node.getLabelKey()).isEqualTo(TestLabelKey.Yes);
        assertThat(node.getId()).isEqualTo(1);
        assertThat(node.getUriSegment()).isEqualTo("a");
        assertThat(node.getPageAccessControl()).isEqualTo(PageAccessControl.PERMISSION);
        assertThat(node.getRoles()).containsExactly("a", "b");
    }

    @Test
    public void modifyPageControl() {
        //given
        MasterSitemapNode node = new MasterSitemapNode(1, "a", LoginView.class, TestLabelKey.Yes, 3, PageAccessControl.PERMISSION, Lists.newArrayList("a",
                "b"));
        //when
        MasterSitemapNode modifiedNode = node.modifyPageAccessControl(PageAccessControl.AUTHENTICATION);
        //then
        assertThat(modifiedNode.getLabelKey()).isEqualTo(TestLabelKey.Yes);
        assertThat(modifiedNode.getId()).isEqualTo(1);
        assertThat(modifiedNode.getUriSegment()).isEqualTo("a");
        assertThat(modifiedNode.getPageAccessControl()).isEqualTo(PageAccessControl.AUTHENTICATION);
        assertThat(modifiedNode.getRoles()).containsExactly("a", "b");
    }
}
