package uk.co.q3c.basic.guice.uiscope;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.basic.BasicModule;
import uk.co.q3c.basic.BasicProvider;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BasicModule.class, UIScopeModule.class })
public class UIScopeTest {

	@Inject
	BasicProvider provider;

	TestUI uib;

	TestUI uia;

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
	}
}
