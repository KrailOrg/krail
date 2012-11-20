package basic;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BasicModule.class })
public class BasicUITest extends UITestBase {

	@Test
	public void headerBar() {

		// given

		// when

		// then
		assertThat(basicUI()).isNotNull();

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
		assertThat(ui.getPage().getLocation().toString()).isEqualTo(uri("view2"));

	}

	private BasicUI basicUI() {
		return (BasicUI) ui;
	}

	private String uri(String pageName) {
		return baseUri + "#!" + pageName;
	}

}
