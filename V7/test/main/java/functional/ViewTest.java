package functional;

import static org.fest.assertions.Assertions.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.shiro.V7ShiroVaadinModule;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.base.ui.V7UIModule;
import uk.co.q3c.v7.demo.view.DemoViewBase;
import uk.co.q3c.v7.demo.view.DemoViewModule;
import uk.co.q3c.v7.demo.view.View2;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import fixture.TestIniModule;
import fixture.UITestBase;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class, UIScopeModule.class, V7UIModule.class, DemoViewModule.class,
		V7ShiroVaadinModule.class, TestIniModule.class })
public class ViewTest extends UITestBase {

	@BeforeClass
	public static void setupClass() {
		uiClass = BasicUI.class;
	}

	@Test
	public void captureParams() {

		// given

		// when
		ui.getV7Navigator().navigateTo(view2 + "/id=1");
		// then
		assertThat(currentView).isInstanceOf(View2.class);
		assertThat(((DemoViewBase) currentView).getParams()).contains("id=1");

	}

}
