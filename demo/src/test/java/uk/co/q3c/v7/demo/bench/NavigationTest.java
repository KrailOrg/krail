package uk.co.q3c.v7.demo.bench;

import static org.assertj.core.api.Assertions.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.q3c.v7.test.bench.V7TestBenchTestCase;

import com.vaadin.testbench.ScreenshotOnFailureRule;

public class NavigationTest extends V7TestBenchTestCase {

	@Rule
	public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);

	@Before
	public void setUp() throws Exception {
		baseUrl = "http://localhost:8080/";
		driver.get(concatUrl(baseUrl, "/?restartApplication"));
	}

	@Test
	public void navigateFromTree() throws InterruptedException {

		// given

		// when

		// then
		verifyUrl("home");
		// // when
		navTree().select(0);
		// // then
		verifyUrl("system-account");
		// // when
		navTree().select(1);
		// // then
		verifyUrl("home");
		// // when
		navTree().select(2);
		// // then
		verifyUrl("login");

		// when
		navTree().expand(0);
		navTree().select(0, 1);
		// then
		verifyUrl("system-account/enable-account");

	}

	@After
	public void tearDown() throws Exception {
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

}
