package uk.co.q3c.v7.base.guice.uiscope;

import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.shiro.V7ShiroVaadinModule;
import uk.co.q3c.v7.base.view.components.HeaderBar;
import uk.co.q3c.v7.demo.view.DemoViewModule;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

import fixture.TestIniModule;
import fixture.TestUIModule;
import fixture.UITestBase;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ UIScopeModule.class, BaseModule.class, TestUIModule.class, DemoViewModule.class,
		V7ShiroVaadinModule.class, TestIniModule.class })
public class UIScopeTest extends UITestBase {

	TestUI uib;

	TestUI uia;

	@Test
	public void uiScope() {
		// given

		uia = getTestUI();
		uib = getTestUI();
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

		CurrentInstance.set(UI.class, null);
		CurrentInstance.set(UIKey.class, null);
		CurrentInstance.set(UI.class, uia);
		uia.doInit(mockedRequest, 1);

		HeaderBar originalHeader = uia.getHeaderBar();

		// when
		// simulates key being cleared by framework during navigation
		CurrentInstance.set(UIKey.class, null);
		uia.getV7Navigator().navigateTo("view2");

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

	/**
	 * Cannot use the inherited {@link #createTestUI()}, because that sets up the CurrentInstance. For this test we need
	 * more than one CurrentInstance
	 * 
	 * @return
	 */
	private TestUI getTestUI() {
		CurrentInstance.set(UI.class, null);
		CurrentInstance.set(UIKey.class, null);
		return (TestUI) provider.createInstance(TestUI.class);

	}

}
