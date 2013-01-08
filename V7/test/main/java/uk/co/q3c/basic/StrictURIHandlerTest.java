package uk.co.q3c.basic;

import static org.fest.assertions.Assertions.*;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.navigate.StrictURIFragmentHandler;
import uk.co.q3c.v7.base.shiro.V7ShiroModule;
import uk.co.q3c.v7.base.ui.UIModule;
import uk.co.q3c.v7.demo.ui.BasicUI;
import uk.co.q3c.v7.demo.ui.DemoUIProvider;
import uk.co.q3c.v7.demo.view.DemoViewModule;

import com.google.inject.Injector;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BaseModule.class, UIScopeModule.class, DemoViewModule.class, V7ShiroModule.class, UIModule.class })
public class StrictURIHandlerTest {

	final String view = "view1";
	final String view_ = "view1/";
	final String view_p = "view1/a=b";
	final String view_p2 = "view1/a=b/year=1970";
	final String view_p2m1 = "view1/a=b/year=";
	final String view_p2m2 = "view1/a=b/=1970";
	final String view_p2m3 = "view1/a=b/1970";
	final String view_p2m5 = "view1/=b/year=1970";
	final String view_p2m6 = "view1/a=/year=1970";

	final String subView = "view1/subView";
	final String subView_ = "view1/subView/";
	final String subView_p = "view1/subView/a=b";
	final String subView_p2 = "view1/subView/a=b/year=1970";
	final String dbl = "view//subView";

	final String home = "";
	final String home_p = "a=b";
	final String home_p2 = "a=b/year=1970";

	final String subView_p2_bang = "!view1/subView/a=b/year=1970";

	@Inject
	DemoUIProvider provider;

	@Inject
	Injector injector;

	StrictURIFragmentHandler handler;

	@Before
	public void setup() {
		provider.createInstance(BasicUI.class);
		handler = injector.getInstance(StrictURIFragmentHandler.class);

	}

	@Test
	public void readVirtualPageAndparameterList() {

		// given

		// when

		// then
		assertThat(handler.setFragment(view).virtualPage()).isEqualTo(view);
		assertThat(handler.parameterList()).isEmpty();

		assertThat(handler.setFragment(view_).virtualPage()).isEqualTo(view);
		assertThat(handler.parameterList()).isEmpty();

		assertThat(handler.setFragment(view_p).virtualPage()).isEqualTo(view);
		assertThat(handler.parameterList()).containsOnly("a=b");
		assertThat(handler.parameterValue("a")).isEqualTo("b");

		assertThat(handler.setFragment(view_p2).virtualPage()).isEqualTo(view);
		assertThat(handler.parameterList()).containsOnly("a=b", "year=1970");
		assertThat(handler.parameterValue("a")).isEqualTo("b");
		assertThat(handler.parameterValue("year")).isEqualTo("1970");

		assertThat(handler.setFragment(view_p2m1).virtualPage()).isEqualTo(view);
		assertThat(handler.parameterList()).containsOnly("a=b");
		assertThat(handler.parameterValue("a")).isEqualTo("b");
		assertThat(handler.parameterValue("year")).isEqualTo(null);

		assertThat(handler.setFragment(view_p2m2).virtualPage()).isEqualTo(view);
		assertThat(handler.parameterList()).containsOnly("a=b");

		assertThat(handler.setFragment(view_p2m3).virtualPage()).isEqualTo(view);
		assertThat(handler.parameterList()).containsOnly("a=b");

		assertThat(handler.setFragment(view_p2m5).virtualPage()).isEqualTo(view);
		assertThat(handler.parameterList()).containsOnly("year=1970");

		assertThat(handler.setFragment(view_p2m6).virtualPage()).isEqualTo(view);
		assertThat(handler.parameterList()).containsOnly("year=1970");

		assertThat(handler.setFragment(subView).virtualPage()).isEqualTo(subView);
		assertThat(handler.parameterList()).isEmpty();

		assertThat(handler.setFragment(subView_).virtualPage()).isEqualTo(subView);
		assertThat(handler.parameterList()).isEmpty();

		assertThat(handler.setFragment(subView_p).virtualPage()).isEqualTo(subView);
		assertThat(handler.parameterList()).containsOnly("a=b");

		assertThat(handler.setFragment(subView_p2).virtualPage()).isEqualTo(subView);
		assertThat(handler.parameterList()).containsOnly("a=b", "year=1970");

		assertThat(handler.setFragment(dbl).virtualPage()).isEqualTo(dbl);
		assertThat(handler.parameterList()).isEmpty();

		assertThat(handler.setFragment(home).virtualPage()).isEqualTo("");
		assertThat(handler.parameterList()).isEmpty();

		assertThat(handler.setFragment(home_p).virtualPage()).isEqualTo("");
		assertThat(handler.parameterList()).containsOnly("a=b");

		assertThat(handler.setFragment(home_p2).virtualPage()).isEqualTo("");
		assertThat(handler.parameterList()).containsOnly("a=b", "year=1970");
	}

	@Test
	public void setVirtualPage() {

		// given
		handler.setUseBang(false);
		handler.setFragment(home_p2);
		// when
		handler.setVirtualPage("view2");
		// then
		assertThat(handler.virtualPage()).isEqualTo("view2");
		assertThat(handler.fragment()).isEqualTo("view2/a=b/year=1970");
		assertThat(handler.parameterList()).containsOnly("a=b", "year=1970");
	}

	@Test
	public void setParameter() {

		// given
		handler.setUseBang(false);
		handler.setFragment(view_p2);
		// when
		handler.setParameterValue("a", "23"); // update
		handler.setParameterValue("id", "111"); // new
		// then
		assertThat(handler.parameterList()).containsOnly("a=23", "year=1970", "id=111");
		assertThat(handler.fragment()).isEqualTo("view1/a=23/id=111/year=1970");

	}

	@Test
	public void removeParameter() {

		// given
		handler.setFragment(view_p2);
		// when
		handler.removeParameter("a");
		// then
		assertThat(handler.fragment()).isEqualTo("view1/year=1970");

	}

	@Test
	public void hashBang() {

		// given
		handler.setFragment(view_p2);
		// when
		handler.setUseBang(true);
		// then
		assertThat(handler.fragment()).isEqualTo("!" + view_p2);
		// when
		handler.setFragment(subView_p2_bang);
		// then
		assertThat(handler.fragment()).isEqualTo(subView_p2_bang);
		// when missing bang
		handler.setFragment(view_p2m1);
		// then
		assertThat(handler.fragment()).isEqualTo("!" + "view1/a=b");
	}

	@Test
	public void BangFragmentWhenNotExpected() {

		// given
		handler.setUseBang(false);
		// when
		handler.setFragment(subView_p2_bang);
		// then
		assertThat(handler.fragment()).isEqualTo(subView_p2);

	}
}
