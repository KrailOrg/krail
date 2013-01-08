package uk.co.q3c.basic;

import static org.mockito.Mockito.*;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import uk.co.q3c.basic.guice.uiscope.TestUI;
import uk.co.q3c.v7.A;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIKey;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.V7View;
import uk.co.q3c.v7.base.navigate.V7ViewChangeEvent;
import uk.co.q3c.v7.base.navigate.V7ViewChangeListener;
import uk.co.q3c.v7.demo.ui.BasicUI;
import uk.co.q3c.v7.demo.ui.DemoUIProvider;
import uk.co.q3c.v7.demo.view.components.HeaderBar;

import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class })
public abstract class UITestBase implements V7ViewChangeListener {

	protected static final String view1 = "secure/view1";
	protected static final String view2 = "public/view2";

	@Inject
	@Named(A.baseUri)
	String baseUri;

	VaadinRequest mockedRequest = mock(VaadinRequest.class);

	V7View currentView;

	@Inject
	private SecurityManager securityManager;

	@Inject
	protected DemoUIProvider provider;

	@Inject
	Injector injector;

	protected HeaderBar headerBar;

	protected BasicUI ui;

	protected V7Navigator navigator;

	@Before
	public void uiSetup() {

		ui = createBasicUI();
		navigator = injector.getInstance(V7Navigator.class);
		headerBar = injector.getInstance(HeaderBar.class);
		// VaadinRequest vr = new VaadinServletRequest(null, null);
		System.out.println("initialising test");
		CurrentInstance.set(UI.class, ui);
		when(mockedRequest.getParameter("v-loc")).thenReturn(baseUri + "/");
		ui.getGuiceNavigator().addViewChangeListener(this);

		SecurityUtils.setSecurityManager(securityManager);

		ui.doInit(mockedRequest, 1);

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

	protected TestUI createTestUI() {
		// simulates the creation of a new current instance (happens for each request)
		CurrentInstance.set(UI.class, null);
		CurrentInstance.set(UIKey.class, null);
		return (TestUI) provider.createInstance(TestUI.class);
	}

	protected BasicUI createBasicUI() {
		// simulates the creation of a new current instance (happens for each request)
		CurrentInstance.set(UI.class, null);
		CurrentInstance.set(UIKey.class, null);
		return (BasicUI) provider.createInstance(BasicUI.class);
	}

	// protected void clickButton(Button button) {
	// Map<String, Object> variables = new HashMap<String, Object>();
	// variables.put("state", true);
	// button.changeVariables(null, variables);
	// }

}
