package uk.co.q3c.basic;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.basic.demo.DemoViewBase;
import uk.co.q3c.basic.demo.View2;
import uk.co.q3c.basic.demo.ViewModule;
import uk.co.q3c.basic.guice.uiscope.UIScopeModule;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BasicModule.class, UIScopeModule.class, TestModule.class, ViewModule.class })
public class ViewTest extends UITestBase {

	@Test
	public void captureParams() {

		// given

		// when
		ui.getGuiceNavigator().navigateTo("view2/id=1");
		// then
		assertThat(currentView).isInstanceOf(View2.class);
		assertThat(((DemoViewBase) currentView).getParams()).contains("id=1");

		// String fragment = Page.getCurrent().getUriFragment();
		// System.out.println("fragement=" + fragment);
		// then

	}

}
