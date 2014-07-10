package uk.co.q3c.v7.demo.bench;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class NavigationTest extends V7TestBenchTestCase {

	@Rule
	public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);

	@Before
	public void setUp() throws Exception {
		context = "V7demo";
	}

	@Test
	public void navigateFromTree() throws InterruptedException {

		// given
		driver.get(rootUrl());
		// when

		// then
		verifyUrl("home");
		// // when
		navTree().select(3);
		// // then
		verifyUrl("system-account");
		assertThat(navTreeSelection()).isEqualTo("System Account");
		// // when
		navTree().select(2);
		// // then
		verifyUrl("home");
		// // when
		navTree().select(0);
		// // then
		verifyUrl("login");

		// when
		navTree().expand(3);
		navTree().select(3, 0);
		// then
		verifyUrl("system-account/enable-account");

	}

	@After
    public void tearDown2() throws Exception {
        String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

}
