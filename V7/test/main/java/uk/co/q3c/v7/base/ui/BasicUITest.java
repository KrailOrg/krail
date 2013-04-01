package uk.co.q3c.v7.base.ui;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

import uk.co.q3c.v7.base.data.V7DefaultConverterFactory;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.shiro.V7ShiroVaadinModule;
import uk.co.q3c.v7.base.view.DefaultErrorView;
import uk.co.q3c.v7.demo.view.DemoViewModule;
import uk.co.q3c.v7.demo.view.PublicHomeView;
import uk.co.q3c.v7.demo.view.View1;
import uk.co.q3c.v7.demo.view.View2;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import fixture.TestIniModule;
import fixture.TestUIModule;
import fixture.UITestBase;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class, UIScopeModule.class, TestUIModule.class, DemoViewModule.class,
		V7ShiroVaadinModule.class, TestIniModule.class })
public class BasicUITest extends UITestBase {

	@BeforeClass
	public static void setupClass() {
		uiClass = BasicUI.class;
	}

	@Test
	public void headerBar() {

		// given

		// when

		// then
		assertThat(basicUI().getHeaderBar()).isNotNull();

	}

	@Test
	public void footerBar() {

		// given

		// when

		// then
		assertThat(basicUI().getFooterBar()).isNotNull();

	}

	@Test
	public void initialPosition() {

		// given
		// when

		// then
		assertThat(ui).isNotNull();
		assertThat(ui.getPage().getLocation().toString()).isEqualTo(baseUri + "/#public/home");
		// cannot do this, there is no way that I can see to get the title back
		// assertThat(basicUI().getPage().getTitle()).isEqualTo();

	}

	@SuppressWarnings("deprecation")
	@Test
	public void changePage() {

		// given

		// when
		ui.getV7Navigator().navigateTo(view2);
		// then
		assertThat(currentView).isInstanceOf(View2.class);
		assertThat(ui.getPage().getLocation().toString()).isEqualTo(uri(view2));
		// when
		ui.getV7Navigator().navigateTo("");
		// then
		assertThat(currentView).isInstanceOf(PublicHomeView.class);
		assertThat(ui.getPage().getLocation().toString()).isEqualTo(baseUri + "/#public/home");
		// when
		ui.getV7Navigator().navigateTo(view1);
		// then
		assertThat(currentView).isInstanceOf(View1.class);
		assertThat(ui.getPage().getLocation().toString()).isEqualTo(uri(view1));

	}

	@Test
	public void errorView() {

		// given
		ui.getV7Navigator().navigateTo(view1);
		// when
		ui.getV7Navigator().navigateTo("viewx2");
		// then
		assertThat(currentView).isInstanceOf(DefaultErrorView.class);
		// uri stays where it was
		assertThat(ui.getPage().getLocation().toString()).isEqualTo(uri("viewx2"));

	}

	@Test
	public void changePageWithParams() {

		// given

		// when
		ui.getV7Navigator().navigateTo(view2 + "/a=b");
		// then
		assertThat(currentView).isInstanceOf(View2.class);
		assertThat(ui.getPage().getLocation().toString()).isEqualTo(uri(view2 + "/a=b"));

	}

	@Test
	public void converterFactorySet() {

		// given

		// when

		// then
		verify(mockedSession).setConverterFactory(Matchers.any(V7DefaultConverterFactory.class));

	}

	private BasicUI basicUI() {
		return (BasicUI) ui;
	}

	private String uri(String pageName) {
		return baseUri + "/#" + pageName;
	}

}
