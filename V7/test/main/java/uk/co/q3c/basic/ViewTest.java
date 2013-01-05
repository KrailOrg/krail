package uk.co.q3c.basic;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.shiro.V7ShiroModule;
import uk.co.q3c.v7.demo.shiro.DemoShiroModule;
import uk.co.q3c.v7.demo.view.DemoViewBase;
import uk.co.q3c.v7.demo.view.DemoViewModule;
import uk.co.q3c.v7.demo.view.View2;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class, UIScopeModule.class, TestModule.class, DemoViewModule.class, V7ShiroModule.class,
		DemoShiroModule.class })
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
