package uk.co.q3c.basic;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.basic.BasicModule;
import uk.co.q3c.basic.BasicUI;
import uk.co.q3c.basic.HomeView;
import uk.co.q3c.basic.View1;
import uk.co.q3c.basic.View2;
import uk.co.q3c.basic.guice.uiscope.UIScopeModule;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BasicModule.class ,UIScopeModule.class,TestModule.class})
public class BasicUITest extends UITestBase {

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
		assertThat(ui.getPage().getLocation().toString()).isEqualTo(uri("view1"));
		// cannot do this, there is no way that I can see to get the title back
		// assertThat(basicUI().getPage().getTitle()).isEqualTo();

	}

	@Test
	public void changePage() {

		// given
		// when
		ui.getNavigator().navigateTo("view2");
		// then
		assertThat(currentView).isInstanceOf(View2.class);
		// when
		ui.getNavigator().navigateTo("");
		// then
		assertThat(currentView).isInstanceOf(HomeView.class);
		// when
		ui.getNavigator().navigateTo("view1");
		// then
		assertThat(currentView).isInstanceOf(View1.class);

	}

	@Test
	public void changePageWithParams() {

		// given

		// when
		ui.getNavigator().navigateTo("view2/a=b");
		// then
		assertThat(currentView).isInstanceOf(View2.class);

	}

	private BasicUI basicUI() {
		return (BasicUI) ui;
	}

	private String uri(String pageName) {
		return baseUri + "#!" + pageName;
	}

}
