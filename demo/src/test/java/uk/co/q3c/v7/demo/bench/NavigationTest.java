package uk.co.q3c.v7.demo.bench;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.q3c.v7.test.bench.V7TestBenchTestCase;

import com.vaadin.testbench.ScreenshotOnFailureRule;

public class NavigationTest extends V7TestBenchTestCase {

	private final StringBuffer verificationErrors = new StringBuffer();

	@Rule
	public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);

	@Before
	public void setUp() throws Exception {
		baseUrl = "http://localhost:8080/";
		driver.get(concatUrl(baseUrl, "/?restartApplication#public"));
	}

	@Test
	public void navigateFromTree() throws InterruptedException {

		// given

		// when

		// then
		verifyUrl("public");

		// when
		navTree().index(0).expand().get().click(8, 7);
		navTree().index(0, 1).expand().get().click(8, 10);
		navTree().index(0, 1, 0).get().click(25, 13);
		// then
		verifyUrl("public/system-account/request-account");

		// when
		navTree().index(0, 1, 1).get().click(31, 11);
		// then
		verifyUrl("public/system-account/unlock-account");

		// when
		driver.navigate().back();
		// then
		verifyUrl("public/system-account/request-account");

		// when
		driver.navigate().forward();
		// then
		verifyUrl("public/system-account/unlock-account");

		// when
		driver.navigate().to(url("public/system-account/request-account"));
		// then
		assertThat(navTreeSelection(), is("Request Account"));

	}

	@After
	public void tearDown() throws Exception {
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

}
