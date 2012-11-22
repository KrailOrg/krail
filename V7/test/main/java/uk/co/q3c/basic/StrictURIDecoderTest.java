package uk.co.q3c.basic;

import static org.fest.assertions.Assertions.*;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.q3c.basic.BasicModule;
import uk.co.q3c.basic.StrictURIDecoder;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ BasicModule.class })
public class StrictURIDecoderTest {

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

	@Inject
	StrictURIDecoder decoder;

	@Test
	public void virtualPageAndParameters() {

		// given

		// when

		// then
		assertThat(decoder.setNavigationState(view).virtualPage()).isEqualTo(view);
		assertThat(decoder.parameters()).isEmpty();

		assertThat(decoder.setNavigationState(view_).virtualPage()).isEqualTo(view);
		assertThat(decoder.parameters()).isEmpty();

		assertThat(decoder.setNavigationState(view_p).virtualPage()).isEqualTo(view);
		assertThat(decoder.parameters()).containsOnly("a=b");

		assertThat(decoder.setNavigationState(view_p2).virtualPage()).isEqualTo(view);
		assertThat(decoder.parameters()).containsOnly("a=b", "year=1970");

		assertThat(decoder.setNavigationState(view_p2m1).virtualPage()).isEqualTo(view);
		assertThat(decoder.parameters()).containsOnly("a=b", "year=");

		assertThat(decoder.setNavigationState(view_p2m2).virtualPage()).isEqualTo(view);
		assertThat(decoder.parameters()).containsOnly("a=b", "=1970");

		assertThat(decoder.setNavigationState(view_p2m3).virtualPage()).isEqualTo(view);
		assertThat(decoder.parameters()).containsOnly("a=b", "1970");

		assertThat(decoder.setNavigationState(view_p2m5).virtualPage()).isEqualTo(view);
		assertThat(decoder.parameters()).containsOnly("year=1970", "=b");

		assertThat(decoder.setNavigationState(view_p2m6).virtualPage()).isEqualTo(view);
		assertThat(decoder.parameters()).containsOnly("a=", "year=1970");

		assertThat(decoder.setNavigationState(subView).virtualPage()).isEqualTo(subView);
		assertThat(decoder.parameters()).isEmpty();

		assertThat(decoder.setNavigationState(subView_).virtualPage()).isEqualTo(subView);
		assertThat(decoder.parameters()).isEmpty();

		assertThat(decoder.setNavigationState(subView_p).virtualPage()).isEqualTo(subView);
		assertThat(decoder.parameters()).containsOnly("a=b");

		assertThat(decoder.setNavigationState(subView_p2).virtualPage()).isEqualTo(subView);
		assertThat(decoder.parameters()).containsOnly("a=b", "year=1970");

		assertThat(decoder.setNavigationState(dbl).virtualPage()).isEqualTo(dbl);
		assertThat(decoder.parameters()).isEmpty();

		assertThat(decoder.setNavigationState(home).virtualPage()).isEqualTo("");
		assertThat(decoder.parameters()).isEmpty();

		assertThat(decoder.setNavigationState(home_p).virtualPage()).isEqualTo("");
		assertThat(decoder.parameters()).containsOnly("a=b");

		assertThat(decoder.setNavigationState(home_p2).virtualPage()).isEqualTo("");
		assertThat(decoder.parameters()).containsOnly("a=b", "year=1970");

	}

}
