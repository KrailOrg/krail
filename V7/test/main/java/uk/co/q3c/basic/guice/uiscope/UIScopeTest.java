package uk.co.q3c.basic.guice.uiscope;

import static org.mockito.Mockito.*;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.basic.TestModule;
import uk.co.q3c.basic.TestShiroModule;
import uk.co.q3c.v7.A;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIKey;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.shiro.V7ShiroModule;
import uk.co.q3c.v7.demo.shiro.DemoShiroModule;
import uk.co.q3c.v7.demo.ui.DemoUIProvider;
import uk.co.q3c.v7.demo.view.DemoViewModule;
import uk.co.q3c.v7.demo.view.components.HeaderBar;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ UIScopeModule.class, TestShiroModule.class, BaseModule.class, TestModule.class, DemoViewModule.class,
		V7ShiroModule.class, DemoShiroModule.class })
public class UIScopeTest {
	@Inject
	@Named(A.baseUri)
	String baseUri;
	@Inject
	DemoUIProvider provider;

	TestUI uib;

	TestUI uia;

	// UIScope scope;

	@Inject
	private SecurityManager securityManager;

	@Before
	public void setup() {
		SecurityUtils.setSecurityManager(securityManager);
	}

	@Test
	public void uiScope() {
		// given
		uia = (TestUI) provider.createInstance(TestUI.class);
		uib = (TestUI) provider.createInstance(TestUI.class);
		// when

		// then
		// Just to make sure we are not looking at the same instance
		Assert.assertNotEquals(uia, uib);

		// ui instances should have different header bars
		Assert.assertNotEquals(uia.getHeaderBar(), uib.getHeaderBar());
		Assert.assertNotEquals(uia.getHeaderBar(), uib.getExtraHeaderBar());
		Assert.assertNotEquals(uia.getExtraHeaderBar(), uib.getHeaderBar());
		Assert.assertNotEquals(uia.getExtraHeaderBar(), uib.getExtraHeaderBar());

		// but both header bars should be the same within a ui instance
		Assert.assertEquals(uia.getHeaderBar(), uia.getExtraHeaderBar());
		Assert.assertEquals(uib.getHeaderBar(), uib.getExtraHeaderBar());

		// given
		VaadinServletRequest mockedRequest = mock(VaadinServletRequest.class);
		when(mockedRequest.getParameter("v-loc")).thenReturn(baseUri + "/");
		CurrentInstance.set(UI.class, uia);
		uia.doInit(mockedRequest, 1);

		HeaderBar originalHeader = uia.getHeaderBar();

		// when
		// simulates key being cleared by framework during navigation
		CurrentInstance.set(UIKey.class, null);
		uia.getGuiceNavigator().navigateTo("view2");

		// then
		// this is not a good test do I need TestBench?
		Assert.assertEquals(uia.getHeaderBar(), originalHeader);

		// // when ui is closed
		// uia.detach();
		//
		// // then scope cache should have been cleared
		// assertThat(scope.cacheHasEntryFor(uia)).isFalse();
		// assertThat(scope.cacheHasEntryFor(uib)).isTrue();
	}

	// @ModuleProvider
	// protected UIScopeModule uiModuleProvider() {
	// UIScopeModule module = new UIScopeModule();
	// scope = module.getUiScope();
	// return module;
	// }

	@ModuleProvider
	protected AbstractModule uiTestModule() {
		return new AbstractModule() {

			@Override
			protected void configure() {
				MapBinder<String, UI> mapbinder = MapBinder.newMapBinder(binder(), String.class, UI.class);
				mapbinder.addBinding(TestUI.class.getName()).to(TestUI.class);
			}

		};
	}
}
