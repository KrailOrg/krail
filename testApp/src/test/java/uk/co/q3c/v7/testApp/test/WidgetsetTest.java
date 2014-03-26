package uk.co.q3c.v7.testApp.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;
import org.vaadin.risto.stepper.IntStepper;

import uk.co.q3c.v7.testapp.view.WidgetsetView;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class WidgetsetTest extends V7TestBenchTestCase {

	/**
	 * We don't need to do much - we just want to make sure that the component is displayed; we are not testing the
	 * component, we just need to know that the widgetset has compiled and loaded.
	 */
	@Test
	public void stepper() {
		// given
		driver.get(rootUrl());
		pause(500);
		navigateTo("widgetset");
		pause(500);
		// when
		WebElement stepper = element(WidgetsetView.class, IntStepper.class);

		// then
		assertThat(stepper).isNotNull();
		assertThat(stepper.isDisplayed()).isTrue();
	}

	@After
	public void tearDown() throws Exception {
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
		// don't know why this is necessary in this test and no other?
		driver.close();
	}
}
