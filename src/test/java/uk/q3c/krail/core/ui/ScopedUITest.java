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
package uk.q3c.krail.core.ui;

import com.github.mcollovati.vertx.vaadin.communication.SockJSPushConnection;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import net.engio.mbassy.bus.MBassador;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.q3c.krail.core.ConfigurationException;
import uk.q3c.krail.core.env.RuntimeEnvironment;
import uk.q3c.krail.core.eventbus.UIBusProvider;
import uk.q3c.krail.core.form.Form;
import uk.q3c.krail.core.guice.uiscope.UIKey;
import uk.q3c.krail.core.guice.uiscope.UIScope;
import uk.q3c.krail.core.i18n.I18NProcessor;
import uk.q3c.krail.core.i18n.LabelKey;
import uk.q3c.krail.core.navigate.Navigator;
import uk.q3c.krail.core.push.Broadcaster;
import uk.q3c.krail.core.push.DefaultBroadcaster;
import uk.q3c.krail.core.push.DefaultKrailPushConfiguration;
import uk.q3c.krail.core.push.DefaultPushMessageRouter;
import uk.q3c.krail.core.push.KrailPushConfiguration;
import uk.q3c.krail.core.push.PushMessageRouter;
import uk.q3c.krail.core.view.KrailView;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.i18n.CurrentLocale;
import uk.q3c.krail.i18n.Translate;
import uk.q3c.util.guice.SerializationSupport;
import uk.q3c.util.testutil.LogMonitor;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    Broadcaster broadcaster;
    PushMessageRouter pushMessageRouter;
    @Mock
    UIBusProvider uiBusProvider;
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
    KrailView toView;
    @Mock
    Component viewContent;
    @Mock
    SerializationSupport serializationSupport;
    LogMonitor logMonitor;
    UIKey uiKey;
    @Mock
    private MBassador<BusMessage> eventBus;

    private KrailPushConfiguration pushConfiguration;

    @Before
    public void setup() {
        Locale.setDefault(Locale.UK);
        when(uiBusProvider.get()).thenReturn(eventBus);
        pushMessageRouter = new DefaultPushMessageRouter(uiBusProvider);
        logMonitor = new LogMonitor();
        logMonitor.addClassFilter(ScopedUI.class);
        uiKey = new UIKey();
        pushConfiguration = new DefaultKrailPushConfiguration(RuntimeEnvironment.VERTX);
        ui = new BasicUI(navigator, errorHandler, broadcaster, pushMessageRouter, applicationTitle, translate, currentLocale, translator, serializationSupport, pushConfiguration);
        ui.setInstanceKey(uiKey);
    }

    @After
    public void teardown() {
        logMonitor.close();
    }

    @Test
    public void asListener() {
        // given

        // when

        // then
        verify(broadcaster).register(DefaultBroadcaster.ALL_MESSAGES, ui);
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

    @Test
    public void push_config() {
        // given
        prepAttach();
        ui.attach();
        VaadinService service = mock(VaadinService.class);
        when(service.ensurePushAvailable()).thenReturn(true);
        when(session.getService()).thenReturn(service);

        // when

        // then
        assertThat(ui.getPushConfiguration()).isInstanceOf(DefaultKrailPushConfiguration.class);
        assertThat(ui.getPushConnection()).isNull(); // push mode not enabled yet

        // when
        ui.getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
        assertThat(ui.getPushConnection()).isInstanceOf(SockJSPushConnection.class);

        //when disable and re-enabled push, connection type is still correct
        ui.getPushConfiguration().setPushMode(PushMode.DISABLED);
        ui.getPushConfiguration().setPushMode(PushMode.AUTOMATIC);

        //then
        assertThat(ui.getPushConnection()).isInstanceOf(SockJSPushConnection.class);

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
        ui.setInstanceKey(uiKey);
        // when
        ui.detach();
        // then
        verify(uiScope).releaseScope(ui.getInstanceKey());
        verify(broadcaster).unregister(Broadcaster.ALL_MESSAGES, ui);
    }

    @Test(expected = MethodReconfigured.class)
    public void methodReconfigured() {
        // given

        // when
        ui.setNavigator(vaadinNavigator);
        // then
    }

    @Test
    public void pageTitle() {
        //given
        when(applicationTitle.getTitleKey()).thenReturn(LabelKey.Yes);
        when(translate.from(LabelKey.Yes)).thenReturn("Title");
        //when
        assertThat(ui.pageTitle()).isEqualTo("Title");

    }

    @Test
    public void init() {
        // given
        prepAttach();
        // when
        ui.init(request);
        // then
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

    @Test(expected = ConfigurationException.class)
    public void init_with_viewDisplayPanel_parent_null() {
        // given
        ui = new DuffUI(navigator, errorHandler, broadcaster, pushMessageRouter, applicationTitle, translate, currentLocale, translator, serializationSupport, pushConfiguration);
        prepAttach();
        // when
        ui.init(request);
        // then

    }

    @Test
    public void changeView() {
        // given
        when(toView.getRootComponent()).thenReturn(viewContent);
        when(toView.getName()).thenReturn("toView");
        // when
        ui.changeView(toView);
        // then
        verify(toView).getRootComponent();
        verify(translator).translate(toView);
        verify(viewContent).setSizeFull();
        assertThat(ui.getViewDisplayPanel()
                     .getContent()).isEqualTo(viewContent);
    }

    @Test
    public void changeViewToForm() {
        // given
        Form form = mock(Form.class);
        when(form.getRootComponent()).thenReturn(viewContent);
        when(form.getName()).thenReturn("toForm");
        // when
        ui.changeView(form);
        // then
        verify(form).getRootComponent();
        verify(translator, never()).translate(toView);
        verify(form).translate();
        verify(viewContent).setSizeFull();
        assertThat(ui.getViewDisplayPanel()
                .getContent()).isEqualTo(viewContent);
    }

    @Test(expected = ConfigurationException.class)
    public void changeView_RootComponentNotSet() {
        // given
        when(toView.getRootComponent()).thenReturn(null);
        when(toView.getName()).thenReturn("toView");
        // when
        ui.changeView(toView);
        // then
    }

    /**
     * There is a much better functional test
     */
    @Test
    public void receiveBroadcastMessage() {
        //given
        prepAttach();
        ui.attach();
        ui.setScope(uiScope);
        ui.setInstanceKey(uiKey);
        String keyId = uiKey.toString();

        //when
        ui.receiveBroadcast("group", "message", uiKey, 55);
        //then
        assertThat(logMonitor.debugLogs()).contains("UI instance " + keyId + " receiving message id: 55 from: " + keyId);
    }


    class DuffUI extends ScopedUI {
        protected DuffUI(Navigator navigator, ErrorHandler errorHandler, Broadcaster broadcaster, PushMessageRouter
                pushMessageRouter, ApplicationTitle applicationTitle, Translate translate, CurrentLocale currentLocale, I18NProcessor translator, SerializationSupport serializationSupport, KrailPushConfiguration pushConfiguration) {
            super(navigator, errorHandler, broadcaster, pushMessageRouter, applicationTitle, translate, currentLocale, translator, serializationSupport, pushConfiguration);
        }

        @Override
        public AbstractOrderedLayout screenLayout() {
            return new VerticalLayout();
        }

    }

    public class ConnectorIdAnswer implements Answer<String> {

        @Override
        public String answer(InvocationOnMock invocation) {
            connectCount++;
            return Integer.toString(connectCount);
        }

    }

}
