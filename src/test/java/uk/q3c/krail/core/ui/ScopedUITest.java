/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.q3c.krail.core.ui;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import net.engio.mbassy.bus.MBassador;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.q3c.krail.core.eventbus.BusMessage;
import uk.q3c.krail.core.guice.uiscope.UIKey;
import uk.q3c.krail.core.guice.uiscope.UIScope;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.push.Broadcaster;
import uk.q3c.krail.core.push.PushMessageRouter;
import uk.q3c.krail.core.user.opt.Option;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.I18NProcessor;
import uk.q3c.krail.i18n.Translate;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class ScopedUITest {

    static int connectCount;
    protected final String baseUri = "http://example.com";
    ScopedUI ui;
    @Mock
    Navigator navigator;
    @Mock
    ErrorHandler errorHandler;
    @Mock
    ConverterFactory converterFactory;
    @Mock
    Broadcaster broadcaster;
    @Mock
    PushMessageRouter pushMessageRouter;
    @Mock
    ApplicationTitle applicationTitle;
    @Mock
    Translate translate;
    @Mock
    CurrentLocale currentLocale;
    @Mock
    I18NProcessor translator;
    @Mock
    com.vaadin.navigator.Navigator vaadinNavigator;
    @Mock
    VaadinRequest request;
    @Mock
    VaadinSession session;
    @Mock
    UIScope uiScope;
    @Mock
    UIKey instanceKey;
    @Mock
    KrailView toView;
    @Mock
    Component viewContent;
    @Mock
    Option option;

    @Mock
    private MBassador<BusMessage> eventBus;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
        ui = new BasicUI(navigator, errorHandler, converterFactory, broadcaster, pushMessageRouter, applicationTitle, translate, currentLocale, translator,
                option);
    }

    @Test
    public void asListener() {
        // given

        // when

        // then
        verify(broadcaster).register(Broadcaster.ALL_MESSAGES, ui);
    }

    @Test
    public void detachNoScope() {
        // given
        prepAttach();
        ui.attach();
        // when
        ui.detach();
        // then
        // no exception
    }

    @SuppressWarnings("deprecation")
    private void prepAttach() {
        when(request.getParameter("v-loc")).thenReturn(baseUri + "/#home");
        ui.getPage()
          .init(request);
        when(session.createConnectorId(Matchers.any(ClientConnector.class))).thenAnswer(new ConnectorIdAnswer());
        when(session.getLocale()).thenReturn(Locale.FRANCE);

        when(session.hasLock()).thenReturn(true);
        ui.setSession(session);
    }

    @Test
    public void detachScopeNotNull() {
        // given
        prepAttach();
        ui.attach();
        ui.setScope(uiScope);
        ui.setInstanceKey(instanceKey);
        // when
        ui.detach();
        // then
        verify(uiScope).releaseScope(ui.getInstanceKey());
    }

    @Test(expected = MethodReconfigured.class)
    public void methodReconfigured() {
        // given

        // when
        ui.setNavigator(vaadinNavigator);
        // then
    }

    @Test
    public void init() {
        // given
        prepAttach();
        // when
        ui.init(request);
        // then
        verify(session).setConverterFactory(converterFactory);
        InOrder inOrder = inOrder(currentLocale, navigator, translator, navigator);
        //        inOrder.verify(currentLocale)
        //               .setLocale(Locale.FRANCE, false);
        inOrder.verify(navigator)
               .init();
        inOrder.verify(translator)
               .translate(ui);
        inOrder.verify(navigator)
               .navigateTo("home");
    }

    @Test
    public void changeView() {
        // given
        when(toView.getRootComponent()).thenReturn(viewContent);
        // when
        ui.changeView(toView);
        // then
        verify(toView).getRootComponent();
        verify(translator).translate(toView);
        verify(viewContent).setSizeFull();
        assertThat(ui.getViewDisplayPanel()
                     .getContent()).isEqualTo(viewContent);
    }

    public class ConnectorIdAnswer implements Answer<String> {

        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
            connectCount++;
            return Integer.toString(connectCount);
        }

    }

}
