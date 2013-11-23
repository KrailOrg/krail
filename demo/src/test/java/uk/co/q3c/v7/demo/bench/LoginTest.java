package uk.co.q3c.v7.demo.bench;

import static org.assertj.core.api.Assertions.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import uk.co.q3c.v7.base.view.DefaultLoginView;
import uk.co.q3c.v7.base.view.component.DefaultLoginStatusPanel;
import uk.co.q3c.v7.test.bench.V7TestBenchTestCase;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class LoginTest extends V7TestBenchTestCase {

	@Rule
	public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);

	@Before
	public void setUp() throws Exception {
		baseUrl = "http://localhost:8080/";
		driver.get(concatUrl(baseUrl, "/?restartApplication#public"));
	}

	@Test
	public void testLogin() {
		// given

		navTree().index(0).expand().get().click();
		String startFragment = "public/system-account";
		navigateTo(startFragment);

		pause(1000);

		// when
		// then initial state
		assertThat(loginButton().getText()).isEqualTo("log in");
		assertThat(loginLabel().getText()).isEqualTo("Guest");

		// when LoginStatusPanel button clicked
		loginButton().click();
		// then
		verifyUrl("public/login");

		// when username and password entered
		login();
		// then correct url and status panel updated
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

	public LoginTest open() {
		navigateTo("public/login");
		pause(100);
		return this;
	}

	public void login() {
		login("ds", "password");
	}

	public void login(String username, String password) {
		usernameBox().clear();
		usernameBox().sendKeys(username);
		passwordBox().clear();
		passwordBox().sendKeys(password);
		submitButton().click();
	}

	protected WebElement loginButton() {
		return element(DefaultLoginStatusPanel.class, Button.class);
	}

	protected WebElement loginLabel() {
		return element(DefaultLoginStatusPanel.class, Label.class);
	}

	protected WebElement usernameBox() {
		return element("username", DefaultLoginView.class, TextField.class);
	}

	protected WebElement passwordBox() {
		return element("password", DefaultLoginView.class, PasswordField.class);
	}

	protected WebElement submitButton() {
		return element(DefaultLoginView.class, Button.class);
	}

	public void init(WebDriver driver, String baseUrl) {
		this.setDriver(driver);
		this.setBaseUrl(baseUrl);
	}

}
