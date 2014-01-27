package uk.co.q3c.v7.demo.bench;

import static org.assertj.core.api.Assertions.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import uk.co.q3c.v7.test.bench.ElementLocator;
import uk.co.q3c.v7.test.bench.V7TestBenchTestCase;

import com.vaadin.testbench.ScreenshotOnFailureRule;

public class LoginTest extends V7TestBenchTestCase {

	@Rule
	public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);

	@Before
	public void setUp() throws Exception {
		baseUrl = "http://localhost:8080/";
		driver.get(concatUrl(baseUrl, "/?restartApplication#"));
	}

	@Test
	public void testLogin() {
		// given
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
		verifyUrl(startFragment);
		assertThat(loginButton().getText()).isEqualTo("log out");
		assertThat(loginLabel().getText()).isEqualTo("ds");
	}

	@Test
	public void loginFromLogout() {

		// given
		login();
		logout();
		verifyUrl("logout");
		// when
		login();
		// then
		verifyUrl("private/home");
	}

	//
	@Test
	public void authenticationFailure() {

		// given

		// when
		loginButton().click();
		pause(100);
		usernameBox().clear();
		usernameBox().sendKeys("ds");
		passwordBox().clear();
		passwordBox().sendKeys("rubbsih");

		submitButton().click();
		// fillLoginForm("ds", "rubbish");
		// then
		verifyUrl("login"); // has not moved
		assertThat(navTreeSelection()).isEqualTo("Login");
		WebElement label = new ElementLocator(driver).id("DefaultLoginView-Label-status").get();
		assertThat(label).isNotNull();
		String s = label.getText();
		assertThat(s).isEqualTo("That username or password was not recognised");
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
