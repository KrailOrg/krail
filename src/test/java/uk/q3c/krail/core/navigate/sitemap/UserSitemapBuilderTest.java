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

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.VaadinSession;
import net.engio.mbassy.bus.MBassador;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.config.KrailApplicationConfigurationModule;
import uk.q3c.krail.core.eventbus.EventBusModule;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.navigate.sitemap.UserSitemapBuilderTest.TestVaadinSessionScopeModule;
import uk.q3c.krail.core.shiro.VaadinSessionProvider;
import uk.q3c.krail.core.user.status.UserStatusBusMessage;
import uk.q3c.krail.core.user.status.UserStatusChangeSource;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.i18n.LocaleChangeBusMessage;
import uk.q3c.krail.i18n.test.TestI18NModule;
import uk.q3c.krail.option.test.TestOptionModule;
import uk.q3c.krail.service.bind.ServicesModule;
import uk.q3c.krail.testutil.persist.TestPersistenceModuleVaadin;
import uk.q3c.krail.util.UtilsModule;
import uk.q3c.util.UtilModule;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
@RunWith(MycilaJunitRunner.class)
@GuiceContext({TestI18NModule.class, TestVaadinSessionScopeModule.class, TestOptionModule.class, TestPersistenceModuleVaadin.class, EventBusModule.class,
        UIScopeModule.class, SitemapModule.class, ServicesModule.class, UtilsModule.class, UtilModule.class, KrailApplicationConfigurationModule.class})
public class UserSitemapBuilderTest extends TestWithSitemap {

    @Mock
    VaadinSessionProvider mockVaadinSessionProvider;

    @Mock
    UserSitemapNodeModifier nodeModifier;

    @Mock
    VaadinSession vaadinSession;
    @Mock
    MBassador<BusMessage> eventBus;
    @Mock
    private UserSitemapCopyExtension copyExtension;
    @Mock
    private UserStatusChangeSource userStatusChangeSource;


    @Override
    @Before
    public void setup() {
        super.setup();
        when(mockVaadinSessionProvider.get()).thenReturn(vaadinSession);
    }



    @Test
    public void pageNotAuthorised() {
        // given

        buildMasterSitemap(8);
        when(subject.isAuthenticated()).thenReturn(false);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode1)).thenReturn(true);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode2)).thenReturn(false);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode3)).thenReturn(true);

        // when
        createUserSitemap();

        // then
        assertThat(userSitemap.getAllNodes()).hasSize(2);
        assertThat(userSitemapContains(masterNode1)).isTrue();
        assertThat(userSitemapContains(masterNode2)).isFalse();
        assertThat(userSitemapContains(masterNode3)).isTrue();

        UserSitemapNode userNode3 = userSitemap.userNodeFor(masterNode3);
        assertThat(userNode3).isNotNull();
        assertThat(userSitemap.getParent(userNode3)).isNotNull();
        UserSitemapNode userNode3Parent = userSitemap.getParent(userNode3);
        assertThat(userNode3Parent.getMasterNode()).isEqualTo(masterNode1);
    }

    private boolean userSitemapContains(SitemapNode masterNode) {
        return userSitemap.userNodeFor(masterNode) != null;
    }

    @Test
    public void redirects() {
        // given
        buildMasterSitemap(8);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode1)).thenReturn(true);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode2)).thenReturn(false);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode3)).thenReturn(true);
        // when
        createUserSitemap();
        // then
        assertThat(userSitemap.getRedirects()
                              .keySet()).containsOnly("a");
    }

    @Test
    public void uriMap() {
        // given
        buildMasterSitemap(8);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode1)).thenReturn(true);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode2)).thenReturn(false);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode3)).thenReturn(true);
        createUserSitemap();
        // when

        // then
        assertThat(userSitemap.getUriMap()
                              .keySet()).containsOnly("1", "1/3");
    }

    @Test
    public void userStatusChanged() {
        // given
        buildMasterSitemap(8);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode1)).thenReturn(true);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode2)).thenReturn(false);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode3)).thenReturn(true);
        createUserSitemap();
        // when
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode2)).thenReturn(true);
        //it doesn't matter what the user status actually is, just that it has changed
        userSitemapBuilder.userStatusChanged(new UserStatusBusMessage(userStatusChangeSource,true));
        // then
        assertThat(userSitemap.getUriMap()
                              .keySet()).containsOnly("1", "1/3", "2");
        assertThat(userSitemap.getRedirects()
                              .keySet()).containsOnly("a");
    }

    @Test
    public void standardPages() {
        // given
        buildMasterSitemap(8);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode1)).thenReturn(true);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode2)).thenReturn(false);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode3)).thenReturn(true);
        when(pageAccessController.isAuthorised(subject, masterSitemap, privateHomeNode)).thenReturn(false);
        when(pageAccessController.isAuthorised(subject, masterSitemap, publicHomeNode)).thenReturn(true);
        when(pageAccessController.isAuthorised(subject, masterSitemap, loginNode)).thenReturn(true);
        when(pageAccessController.isAuthorised(subject, masterSitemap, logoutNode)).thenReturn(true);

        // when
        createUserSitemap();
        // then
        // never shown but needs to be in userSitemap to navigate to
        assertThat(userSitemap.standardPageNode(StandardPageKey.Log_Out)).isNotNull();
        assertThat(userSitemap.standardPageNode(StandardPageKey.Private_Home)).isNotNull();
        assertThat(userSitemap.standardPageNode(StandardPageKey.Public_Home)).isNotNull();
        assertThat(userSitemap.standardPageNode(StandardPageKey.Log_In)).isNotNull();

    }

    @Test
    public void translationAndLocaleChange() {
        // given
        buildMasterSitemap(8);
        currentLocale.setLocale(Locale.UK);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode1)).thenReturn(true);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode2)).thenReturn(false);
        when(pageAccessController.isAuthorised(subject, masterSitemap, masterNode3)).thenReturn(true);
        when(pageAccessController.isAuthorised(subject, masterSitemap, privateHomeNode)).thenReturn(false);
        when(pageAccessController.isAuthorised(subject, masterSitemap, publicHomeNode)).thenReturn(true);
        when(pageAccessController.isAuthorised(subject, masterSitemap, loginNode)).thenReturn(true);
        when(pageAccessController.isAuthorised(subject, masterSitemap, logoutNode)).thenReturn(true);

        // when
        createUserSitemap();
        // then
        assertThat(userNode1.getCollationKey()).isNotNull();
        assertThat(userNode1.getLabel()).isEqualTo("Yes");
        assertThat(userNode3.getCollationKey()).isNotNull();
        assertThat(userNode3.getLabel()).isEqualTo("Enable Account");

        // when
        currentLocale.setLocale(Locale.GERMANY);
        ((DefaultUserSitemap) userSitemap).localeChanged(new LocaleChangeBusMessage(this, Locale.GERMANY));
        assertThat(userNode1.getCollationKey()).isNotNull();
        assertThat(userNode1.getLabel()).isEqualTo("Ja");
        assertThat(userNode3.getCollationKey()).isNotNull();
        assertThat(userNode3.getLabel()).isEqualTo("Konto aktivieren");
    }

    @Override
    @ModuleProvider
    protected AbstractModule moduleProvider() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);

            }

        };
    }

    @ModuleProvider
    protected AbstractModule moduleProvider2() {
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(VaadinSessionProvider.class).toInstance(mockVaadinSessionProvider);
                bind(MasterSitemap.class).to(DefaultMasterSitemap.class);
                bind(UserSitemap.class).to(DefaultUserSitemap.class);
            }

        };
    }

    // Overrides the VaadinSeesionProvider so we can use a mock
    public static class TestVaadinSessionScopeModule extends VaadinSessionScopeModule {
        @Override
        protected void bindVaadinSessionProvider() {
        }
    }
}
