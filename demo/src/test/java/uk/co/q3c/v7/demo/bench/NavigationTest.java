package uk.co.q3c.v7.demo.bench;

import static org.assertj.core.api.Assertions.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.q3c.v7.test.bench.V7TestBenchTestCase;

import com.vaadin.testbench.By;
import com.vaadin.testbench.ScreenshotOnFailureRule;

public class NavigationTest extends V7TestBenchTestCase {

	@Rule
	public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);

	@Before
	public void setUp() throws Exception {
		driver.get(rootUrl());
	}

	@Test
	public void navigateFromTree() throws InterruptedException {

		// given

		// when

		// then
		verifyUrl("home");
		// // when
		navTree().select(2);
		// // then
		verifyUrl("system-account");
		assertThat(navTreeSelection()).isEqualTo("System Account");
		// // when
		navTree().select(1);
		// // then
		verifyUrl("home");
		// // when
		navTree().select(0);
		// // then
		verifyUrl("login");

		// when
		navTree().expand(2);
		navTree().select(2, 0);
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

		// pause(10000);
		// then
		// assertThat(notification()).isNotNull();
		// assertThat(notification().getText()).isEqualTo("You are not authorised for that action");
		// closeNotification();

		verifyNotUrl("private/home"); // not a valid test, but maybe it should be
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
		// then
		verifyUrl("private/home");
		assertThat(navTreeSelection()).isEqualTo("Private Home");

	}

	@Test
	public void browserBackForward() {

		// given
		pause(1000);
		// when
		navTree().select(2);
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

	@Test
	public void menuNavigate() {

		// given
		pause(1000);
		// when
		testBenchElement(driver.findElement(By.vaadin("V7demo::PID_SUserNavigationMenu#item1"))).click(43, 6);
		testBenchElement(driver.findElement(By.vaadin("V7demo::Root/VOverlay[0]/VMenuBar[0]#item0"))).click(44, 8);
		pause(500);
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
