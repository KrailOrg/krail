package uk.co.q3c.basic.guice.uiscope;

import static org.mockito.Mockito.*;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.basic.A;
import uk.co.q3c.basic.BasicModule;
import uk.co.q3c.basic.BasicProvider;
import uk.co.q3c.basic.demo.HeaderBar;
import uk.co.q3c.basic.demo.ViewModule;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BasicModule.class, ViewModule.class })
public class UIScopeTest {
	@Inject
	@Named(A.baseUri)
	String baseUri;
	@Inject
	BasicProvider provider;

	TestUI uib;

	TestUI uia;

	UIScope scope;

	@Test
	public void uiScope() {
		// given
		uib = (TestUI) provider.createInstance(TestUI.class);
		uia = (TestUI) provider.createInstance(TestUI.class);
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
		VaadinRequest mockedRequest = mock(VaadinRequest.class);
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

	@ModuleProvider
	protected UIScopeModule uiModuleProvider() {
		UIScopeModule module = new UIScopeModule();
		scope = module.getUiScope();
		return module;
	}

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
