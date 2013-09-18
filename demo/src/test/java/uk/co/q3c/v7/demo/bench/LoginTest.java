package uk.co.q3c.v7.demo.bench;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import uk.co.q3c.v7.test.bench.V7TestBenchTestCase;

import com.vaadin.testbench.By;
import com.vaadin.testbench.ScreenshotOnFailureRule;

public class LoginTest extends V7TestBenchTestCase {

	@Rule
	public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);

	@Before
	public void setUp() throws Exception {
		baseUrl = "http://localhost:8080/";
		driver.get(concatUrl(baseUrl, "/?restartApplication#public"));
	}

	@Test
	public void login() throws Exception {
		// given
		navTree().index(0).expand().get().click(8, 7);
		String startFragment = "public/system-account";
		navigateTo(startFragment);

		assertThat(navTreeSelection(), is("System Account"));

		verifyUrl(startFragment);
		// driver.get("http://google.com");
		verifyUrl(startFragment);

		// when
		// then initial state
		assertThat(loginButtonText(), is("log in"));
		assertThat(loginLabelText(), is("Guest"));

		// when LoginStatusPanel button clicked
		loginButton().click();
		// then
		verifyUrl("public/login");

		// when username and password entered
		usernameBox().clear();
		usernameBox().sendKeys("ds");
		passwordBox().clear();
		passwordBox().sendKeys("password");
		submitButton().click();
		// then correct url and status panel updated
		verifyUrl(startFragment);
		assertThat(loginButtonText(), is("log out"));
		assertThat(loginLabelText(), is("ds"));
	}

	@After
	public void tearDown() throws Exception {
		String verificationErrorString = verificationErrors.toString();
		System.out.println(verificationErrorString);
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	protected WebElement loginButton() {
		return driver.findElement(By.vaadin("ROOT::PID_SDefaultLoginStatusPanel-Button/domChild[0]/domChild[0]"));
	}

	protected WebElement loginLabel() {
		return driver.findElement(By.vaadin("ROOT::PID_SDefaultLoginStatusPanel-Label"));
	}

	protected String loginButtonText() {
		return loginButton().getText();
	}

	protected String loginLabelText() {
		return loginLabel().getText();
	}

	protected WebElement usernameBox() {
		return driver.findElement(By.vaadin("ROOT::PID_SDefaultLoginView-TextField-username"));
	}

	protected WebElement passwordBox() {
		return driver.findElement(By.vaadin("ROOT::PID_SDefaultLoginView-PasswordField-password"));
	}

	protected WebElement submitButton() {
		return driver.findElement(By.vaadin("ROOT::PID_SDefaultLoginView-Button"));
	}
}
