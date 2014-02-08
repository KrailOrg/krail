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
		assertThat(navTreeSelection()).isEqualTo("System Account");
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

	// @Ignore("Testing with notifications not working")
	/**
	 * The notification does not actually appear when the test is run, but does if you step through the test in debug
	 * (although that breaks the test!)
	 */
	@Test
	public void authorisationFailure() {

		// given
		// when

		navigateTo("private/home");
		// then
		verifyNotUrl("private/home");
		navigateTo("system-account");
		// when
		login();

		// then
		verifyUrl("system-account");
		// when
		navigateTo("private/home");
		// then
		verifyUrl("private/home");

	}

	@Test
	public void redirectFromPrivate() {

		// given
		login();
		logout();
		login();
		// when
		navigateTo("private");
		pause(500);
		// then
		verifyUrl("private/home");
		assertThat(navTreeSelection()).isEqualTo("Private Home");

	}

	@Test
	public void browserBackForward() {

		// given

		// when
		navTree().select(0);
		// then
		verifyUrl("system-account");
		assertThat(navTreeSelection()).isEqualTo("System Account");
		// when
		login();
		// then
		verifyUrl("system-account");
		assertThat(navTreeSelection()).isEqualTo("System Account");
		// when
		navigateTo("private/home");
		// then
		verifyUrl("private/home");
		assertThat(navTreeSelection()).isEqualTo("Private Home");
		// when
		navigateTo("system-account/enable-account");
		// then
		verifyUrl("system-account/enable-account");
		assertThat(navTreeSelection()).isEqualTo("Enable Account");
		// when
		navigateBack();
		// then
		verifyUrl("private/home");
		assertThat(navTreeSelection()).isEqualTo("Private Home");
		// when
		navigateForward();
		verifyUrl("system-account/enable-account");
		assertThat(navTreeSelection()).isEqualTo("Enable Account");
	}

	@Test
	public void breadcrumb_navigate() {

		// given
		pause(1000);
		// when
		navigateTo("system-account/reset-account");
		assertThat(breadcrumb(0)).isNotNull();
		breadcrumb(0).get().click();
		// then
		verifyUrl("system-account");
	}

	@Test
	public void subpage_navigate() {

		// given
		pause(1000);
		// when
		navigateTo("system-account");
		assertThat(subpagepanel(0)).isNotNull();
		subpagepanel(0).get().click();
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
