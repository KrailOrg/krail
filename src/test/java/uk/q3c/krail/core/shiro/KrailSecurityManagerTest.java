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
package uk.q3c.krail.core.shiro;

import com.google.inject.AbstractModule;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import fixture.TestI18NModule;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.q3c.krail.core.guice.uiscope.UIKey;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.navigate.StrictURIFragmentHandler;
import uk.q3c.krail.core.navigate.URIFragmentHandler;
import uk.q3c.krail.core.ui.BasicUI;
import uk.q3c.krail.core.ui.ScopedUI;
import uk.q3c.krail.core.user.opt.DefaultUserOption;
import uk.q3c.krail.core.user.opt.DefaultUserOptionStore;
import uk.q3c.krail.core.user.opt.UserOption;
import uk.q3c.krail.core.user.opt.UserOptionStore;
import uk.q3c.krail.core.view.component.UserStatusPanel;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({UIScopeModule.class, TestI18NModule.class})
public class KrailSecurityManagerTest extends ShiroIntegrationTestBase {

    @Mock
    UserStatusPanel monitor1;

    @Mock
    UserStatusPanel monitor2;


    @Mock
    BasicUI ui;

    @Mock
    VaadinSessionProvider vsp;

    @Mock
    VaadinSession session;

    @Override
    @Before
    public void setupShiro() {

        super.setupShiro();

    }

    @Test
    public void login() {

        // given
        when(vsp.get()).thenReturn(session);
        KrailSecurityManager securityManager = (KrailSecurityManager) SecurityUtils.getSecurityManager();

        securityManager.setSessionProvider(vsp);
        UsernamePasswordToken token = new UsernamePasswordToken("xxx", "password");
        // when
        getSubject().login(token);
        // then stored in session
        verify(session).setAttribute(eq(Subject.class), any(Subject.class));

    }

    @ModuleProvider
    AbstractModule moduleProvider() {
        // creates the UIScope before injections
        createUI();
        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
                bind(UserOption.class).to(DefaultUserOption.class);
                bind(UserOptionStore.class).to(DefaultUserOptionStore.class);
            }
        };
    }

    protected ScopedUI createUI() {
        UIKey uiKey = new UIKey(3);
        CurrentInstance.set(UI.class, null);
        CurrentInstance.set(UIKey.class, uiKey);
        CurrentInstance.set(UI.class, ui);
        when(ui.getInstanceKey()).thenReturn(uiKey);

        return ui;
    }

}
