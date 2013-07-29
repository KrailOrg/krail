package fixture;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.config.V7IniProvider;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIKey;
import uk.co.q3c.v7.base.guice.uiscope.UIScope;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.shiro.ShiroIntegrationTestBase;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.ui.ScopedUIProvider;
import uk.co.q3c.v7.base.ui.TestUI;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;

import com.google.inject.Injector;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

/**
 * Extend this class to test anything related to a Vaadin UI (or in the case of
 * V7, as {@link ScopedUI}. Note that the {@link UIScope} is not prepared until
 * the {@link #uiSetup()} method is called, so subclasses must use providers if
 * they want to inject UIScoped objects - otherwise the injection happens before
 * the UIScope context is ready.
 * <p>
 * A number of providers are made available by the class
 * <p>
 * ConnectorIDAnswer added to enable the use of the mocked session. When the
 * session was not used there was no problem with having no connector ids, but
 * the call to setConverterFactory in the UI.ini method changed that. Mocking
 * the session requires provision of unique Ids for all connectors
 * 
 * @author David Sowerby 18 Jan 2013
 * 
 */
@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class })
public abstract class UITestBase extends ShiroIntegrationTestBase implements
		V7ViewChangeListener {
	// this is static to ensure count remains unique across all method calls
	private static int connectCount = 1;

	public class ConnectorIdAnswer implements Answer<String> {

		@Override
		public String answer(InvocationOnMock invocation) throws Throwable {
			connectCount++;
			return Integer.toString(connectCount);
		}

	}

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

	@Inject
	protected V7IniProvider iniPro;

	protected V7Ini ini;

	protected static Class<? extends ScopedUI> uiClass;

	@Before
	public void setup() {
		if (uiClass != null) {
			createUI(uiClass);
		}
	}

	@After
	public void teardown() {
		currentView = null;
	}

	@Override
	public boolean beforeViewChange(V7ViewChangeEvent event) {
		return true;
	}

	@Override
	public void afterViewChange(V7ViewChangeEvent event) {
		currentView = event.getNewView();
	}

	/**
	 * Use this method to create TestUI instances, rather than the UIProvider It
	 * simulates the creation of a new CurrentInstance (which happens for each
	 * request)
	 * 
	 * @return
	 */
	protected TestUI createTestUI() {
		return (TestUI) createUI(TestUI.class);
	}

	/**
	 * Use this method to create BasicUI instances, rather than the UIProvider
	 * It simulates the creation of a new CurrentInstance (which happens for
	 * each request)
	 * 
	 * @return
	 */
	protected BasicUI createBasicUI() {
		return (BasicUI) createUI(BasicUI.class);
	}

	@SuppressWarnings("deprecation")
	protected ScopedUI createUI(Class<? extends ScopedUI> clazz) {
		CurrentInstance.set(UI.class, null);
		CurrentInstance.set(UIKey.class, null);
		ui = (ScopedUI) getUIProvider().createInstance(clazz);
		CurrentInstance.set(UI.class, ui);
		when(mockedRequest.getParameter("v-loc")).thenReturn(baseUri + "/");
		when(
				mockedSession.createConnectorId(Matchers
						.any(ClientConnector.class))).thenAnswer(
				new ConnectorIdAnswer());
		ui.setSession(mockedSession);
		ui.getV7Navigator().addViewChangeListener(this);
		ui.doInit(mockedRequest, 23);
		ini = iniPro.get();
		return ui;
	}

	/**
	 * Override to define your UIProvider
	 */
	protected abstract ScopedUIProvider getUIProvider();

}
