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
package uk.q3c.krail.core.navigate.sitemap;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import fixture.TestI18NModule;
import fixture.testviews2.ViewA;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.user.opt.DefaultUserOption;
import uk.q3c.krail.core.user.opt.DefaultUserOptionStore;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionStore;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.LabelKey;
import uk.q3c.krail.i18n.Translate;

import java.text.Collator;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, VaadinSessionScopeModule.class})
public class UserSitemapNodeTest {

    @Inject
    Translate translate;
    @Inject
    CurrentLocale currentLocale;
    private UserSitemapNode userNode;

    @Test
    public void translate() {
        // given
        MasterSitemapNode masterNode = new MasterSitemapNode("a", ViewA.class, LabelKey.Home_Page);
        userNode = new UserSitemapNode(masterNode);
        currentLocale.setLocale(Locale.GERMANY);
        Collator collator = Collator.getInstance(Locale.GERMANY);
        // when
        userNode.translate(translate, Locale.GERMANY, collator);
        // then
        assertThat(userNode.getLabel()).isEqualTo("Startseite");
        assertThat(userNode.getCollationKey()).isEqualTo(collator.getCollationKey("Startseite"));
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
