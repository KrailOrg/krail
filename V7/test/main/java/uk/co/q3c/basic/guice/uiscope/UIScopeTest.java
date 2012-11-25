package uk.co.q3c.basic.guice.uiscope;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.basic.BasicModule;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BasicModule.class, UIScopeModule.class })
public class UIScopeTest {

	@Inject
	TestUI uib;

	@Inject
	TestUI uia;

	@Test
	public void uiScope() {

		// given

		// when

		// then
		Assert.assertNotEquals("Different ui instances", uia, uib);
		Assert.assertNotEquals(uia.getFooterBar(), uib.getFooterBar());
		Assert.assertNotEquals(uia.getHeaderBar(), uia.getExtraHeaderBar());
		Assert.assertNotEquals(uib.getHeaderBar(), uib.getExtraHeaderBar());
	}
}
