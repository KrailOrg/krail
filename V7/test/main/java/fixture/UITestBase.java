package fixture;

import static org.mockito.Mockito.*;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import uk.co.q3c.base.guice.uiscope.TestUI;
import uk.co.q3c.base.shiro.ShiroIntegrationTestBase;
import uk.co.q3c.v7.A;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIKey;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.navigate.V7View;
import uk.co.q3c.v7.base.navigate.V7ViewChangeEvent;
import uk.co.q3c.v7.base.navigate.V7ViewChangeListener;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.base.view.components.HeaderBar;
import uk.co.q3c.v7.demo.shiro.DemoRealm;
import uk.co.q3c.v7.demo.ui.DemoUIProvider;

import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class })
public abstract class UITestBase extends ShiroIntegrationTestBase implements V7ViewChangeListener {

	protected static final String view1 = "secure/view1";
	protected static final String view2 = "public/view2";

	@Inject
	@Named(A.baseUri)
	protected String baseUri;

	VaadinRequest mockedRequest = mock(VaadinRequest.class);

	protected V7View currentView;

	@Inject
	protected DemoUIProvider provider;

	@Inject
	Injector injector;

	protected HeaderBar headerBar;

	protected BasicUI ui;

	protected V7Navigator navigator;

	@Before
	public void uiSetup() {
		RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
		rsm.setRealm(getRealm());
		ui = createBasicUI();
		navigator = injector.getInstance(V7Navigator.class);
		headerBar = injector.getInstance(HeaderBar.class);
		// VaadinRequest vr = new VaadinServletRequest(null, null);
		System.out.println("initialising test");
		CurrentInstance.set(UI.class, ui);
		when(mockedRequest.getParameter("v-loc")).thenReturn(baseUri + "/");
		ui.getGuiceNavigator().addViewChangeListener(this);
		ui.doInit(mockedRequest, 1);

	}

	protected Realm getRealm() {
		return new DemoRealm();
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
	 * Use this method to create TestUI instances, rather than the UIProvider It simulates the creation of a new
	 * CurrentInstance (which happens for each request)
	 * 
	 * @return
	 */
	protected TestUI createTestUI() {
		CurrentInstance.set(UI.class, null);
		CurrentInstance.set(UIKey.class, null);
		return (TestUI) provider.createInstance(TestUI.class);
	}

	/**
	 * Use this method to create BasicUI instances, rather than the UIProvider It simulates the creation of a new
	 * CurrentInstance (which happens for each request)
	 * 
	 * @return
	 */
	protected BasicUI createBasicUI() {
		CurrentInstance.set(UI.class, null);
		CurrentInstance.set(UIKey.class, null);
		return (BasicUI) provider.createInstance(BasicUI.class);
	}

}
