package functional;

import static org.fest.assertions.Assertions.*;
import static org.junit.Assert.*;

import org.apache.shiro.authz.AuthorizationException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.config.BaseIniModule;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.shiro.V7ShiroVaadinModule;
import uk.co.q3c.v7.base.ui.BasicUI;
import uk.co.q3c.v7.base.view.ApplicationViewModule;
import uk.co.q3c.v7.base.view.StandardViewModule;
import uk.co.q3c.v7.demo.view.DemoModule;
import uk.co.q3c.v7.demo.view.DemoViewBase;
import uk.co.q3c.v7.demo.view.View2;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.mycila.testing.plugin.guice.ModuleProvider;
import com.vaadin.ui.Button.ClickEvent;

import fixture.TestHelper;
import fixture.TestUIModule;
import fixture.UITestBase;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class, UIScopeModule.class, TestUIModule.class, StandardViewModule.class, DemoModule.class,
		V7ShiroVaadinModule.class, BaseIniModule.class })
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

	@Test
	public void footerBar() {

		// given

		// when
		ui.getV7Navigator().navigateTo(view2 + "/id=1");
		// then
		assertThat(view().getFooterBar()).isNotNull();

	}

	@Test
	public void simpleClassName() {

		// given

		// when
		ui.getV7Navigator().navigateTo(view2 + "/id=1");
		// then
		assertThat(view().simpleClassName()).isEqualTo("View2");

	}

	@Test
	public void stripClassName() {

		// given
		String className1 = "View2$$enhance by Guice";
		String className2 = "View2$enhance by Guice";
		// when
		// when
		ui.getV7Navigator().navigateTo(view2 + "/id=1");
		// then
		assertThat(view().stripClassName(className1)).isEqualTo("View2");
		assertThat(view().stripClassName(className2)).isEqualTo("View2");

	}

	@Test(expected = AuthorizationException.class)
	public void doSecureThing() {

		// given
		ui.getV7Navigator().navigateTo(view2 + "/id=1");
		ClickEvent event = new ClickEvent(view().getAuthorisationButton());

		// when
		view().buttonClick(event);
		// then
		fail();

	}

	@Test
	public void doAuthenticationThing() {

		// given
		ui.getV7Navigator().navigateTo(view2 + "/id=1");
		ClickEvent event = new ClickEvent(view().getAuthenticationButton());

		// when
		view().buttonClick(event);

		// then
		assertThat(view().getLastNotification()).isEqualTo(
				"You got here because you have logged in. If you had not, Shiro would have raised an exception");

	}

	@Test
	public void userMessage() {

		// given
		String msg = "a squiggly message";
		ui.getV7Navigator().navigateTo(view2 + "/id=1");
		ClickEvent event = new ClickEvent(view().getSendMsgButton());
		view().getTextField().setValue(msg);
		// when
		view().buttonClick(event);

		// then
		assertThat(view().getFooterBar().getUserMessage()).isEqualTo(msg);

	}

	@ModuleProvider
	private ApplicationViewModule applicationViewModuleProvider() {
		return TestHelper.applicationViewModuleUsingSitemap();
	}

	private DemoViewBase view() {
		return (DemoViewBase) currentView;
	}

}
