/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package fixture;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.*;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.q3c.krail.base.guice.BaseServletModule;
import uk.q3c.krail.base.guice.uiscope.UIKey;
import uk.q3c.krail.base.guice.uiscope.UIScope;
import uk.q3c.krail.base.navigate.V7Navigator;
import uk.q3c.krail.base.shiro.ShiroIntegrationTestBase;
import uk.q3c.krail.base.ui.BasicUI;
import uk.q3c.krail.base.ui.ScopedUI;
import uk.q3c.krail.base.ui.ScopedUIProvider;
import uk.q3c.krail.base.ui.TestUI;
import uk.q3c.krail.base.view.V7View;
import uk.q3c.krail.base.view.V7ViewChangeEvent;
import uk.q3c.krail.base.view.V7ViewChangeListener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * THIS IS NOT IN USE - AND PROBABLY WON'T WORK. I HAVE KEPT IT ONLY BECAUSE THERE MAY BE SOME USEFUL IDEAS IN HERE
 * <p/>
 * <p/>
 * Extend this class to test anything related to a Vaadin UI (or in the case of V7, as {@link ScopedUI}. Note that the
 * {@link UIScope} is not prepared until the {@link #uiSetup()} method is called, so subclasses must use providers if
 * they want to inject UIScoped objects - otherwise the injection happens before the UIScope context is ready.
 * <p/>
 * A number of providers are made available by the class
 * <p/>
 * ConnectorIDAnswer added to enable the use of the mocked session. When the session was not used there was no problem
 * with having no connector ids, but the call to setConverterFactory in the UI.ini method changed that. Mocking the
 * session requires provision of unique Ids for all connectors
 *
 * @author David Sowerby 18 Jan 2013
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({BaseServletModule.class})
public abstract class UITestBase extends ShiroIntegrationTestBase implements V7ViewChangeListener {
    protected static Class<? extends ScopedUI> uiClass;
    // this is static to ensure count remains unique across all method calls
    private static int connectCount = 1;
    protected final String baseUri = "http://example.com";

    protected VaadinRequest mockedRequest = mock(VaadinRequest.class);
    protected VaadinSession mockedSession = mock(VaadinSession.class);
    protected V7View currentView;

    @Inject
    protected Injector injector;

    @Mock
    protected ErrorHandler errorHandler;

    protected ScopedUI ui;

    @Inject
    protected Provider<V7Navigator> navigatorPro;

    @Before
    public void setup() {
        if (uiClass != null) {
            createUI(uiClass);
        }
    }

    @SuppressWarnings("deprecation")
    protected ScopedUI createUI(final Class<? extends ScopedUI> clazz) {
        CurrentInstance.set(UI.class, null);
        CurrentInstance.set(UIKey.class, null);
        UICreateEvent event = mock(UICreateEvent.class);
        // when(event.getSource()).thenReturn(vaadinService);

        Answer<Class<? extends ScopedUI>> answer = new Answer<Class<? extends ScopedUI>>() {

            @Override
            public Class<? extends ScopedUI> answer(InvocationOnMock invocation) throws Throwable {
                return clazz;
            }
        };
        when(event.getUIClass()).thenAnswer(answer);
        ui = (ScopedUI) getUIProvider().createInstance(event);
        CurrentInstance.set(UI.class, ui);
        when(mockedRequest.getParameter("v-loc")).thenReturn(baseUri + "/");
        when(mockedSession.createConnectorId(Matchers.any(ClientConnector.class))).thenAnswer(new ConnectorIdAnswer());
        ui.setSession(mockedSession);
        ui.getV7Navigator()
          .addViewChangeListener(this);
        // ui.doInit(mockedRequest, 23);
        return ui;
    }

    /**
     * Override to define your UIProvider
     */
    protected abstract ScopedUIProvider getUIProvider();

    @After
    public void teardown() {
        currentView = null;
    }

    @Override
    public void beforeViewChange(V7ViewChangeEvent event) {
    }

    @Override
    public void afterViewChange(V7ViewChangeEvent event) {
    }

    /**
     * Use this method to create TestUI instances, rather than the UIProvider It simulates the creation of a new
     * CurrentInstance (which happens for each request)
     *
     * @return
     */
    protected TestUI createTestUI() {
        return (TestUI) createUI(TestUI.class);
    }

    /**
     * Use this method to create BasicUI instances, rather than the UIProvider It simulates the creation of a new
     * CurrentInstance (which happens for each request)
     *
     * @return
     */
    protected BasicUI createBasicUI() {
        return (BasicUI) createUI(BasicUI.class);
    }

    public class ConnectorIdAnswer implements Answer<String> {

        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
            connectCount++;
            return Integer.toString(connectCount);
        }

    }

}
