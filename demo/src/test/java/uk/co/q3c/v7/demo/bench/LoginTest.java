package uk.co.q3c.v7.demo.bench;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import com.vaadin.testbench.ScreenshotOnFailureRule;

public class LoginTest extends V7TestBenchTestCase {

	@Rule
	public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);

	@Before
	public void setUp() throws Exception {
		context = "V7demo";
		driver.get(rootUrl());
	}

	@Test
	public void testLogin() {
		// given
		pause(1000);
		navTree().expand(0);
		// navTree().index(0).expand().get().click();
		String startFragment = "system-account";
		navigateTo(startFragment);

		pause(1000);

		// when
		// then initial state
		assertThat(loginButton().getText()).isEqualTo("log in");
		assertThat(loginLabel().getText()).isEqualTo("Guest");

		// when LoginStatusPanel button clicked
		loginButton().click();
		// then
		verifyUrl("login");

		// when username and password entered
		fillLoginForm();
		// then correct url and status panel updated
		pause(500);
		verifyUrl(startFragment);
		assertThat(loginButton().getText()).isEqualTo("log out");
		assertThat(loginLabel().getText()).isEqualTo("ds");
	}

	@After
	public void tearDown() throws Exception {
		String verificationErrorString = verificationErrors.toString();
		System.out.println(verificationErrorString);
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	public void init(WebDriver driver, String baseUrl) {
		this.setDriver(driver);
		this.setBaseUrl(baseUrl);
	}

}
