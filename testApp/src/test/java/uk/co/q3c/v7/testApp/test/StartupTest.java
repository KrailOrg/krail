package uk.co.q3c.v7.testApp.test;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import uk.co.q3c.v7.base.view.component.DefaultLocaleSelector;
import uk.co.q3c.v7.base.view.component.DefaultMessageBar;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 07/07/14.  Checks for correct conditions at application start up
 */


public class StartupTest extends V7TestBenchTestCase {

    @Rule
    public ScreenshotOnFailureRule screenshotOnFailureRule = new ScreenshotOnFailureRule(this, true);

    @Before
    public void setUp() throws Exception {
        driver.get(rootUrl());
    }

    @Test
    public void componentSettings() {

        // given

        // when

        // then
        // localSelector is UK
        String comboValue = comboValue(DefaultLocaleSelector.class, ComboBox.class);
        assertThat(comboValue).isEqualTo("English (United Kingdom)");

        WebElement messageBar = element(DefaultMessageBar.class, Label.class);
        assertThat(messageBar.getText()).isEqualTo("Message bar");
    }

    @Test
    public void navTreeOrder() {

        // given

        // when

        // then
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add(navTreeItemCaption(i));
        }
        assertThat(items).containsExactly("Log In", "Message Box", "Notifications", "Public Home", "System Account");
    }

    @Test
    public void navMenuOrder() {

        // given

        List<String> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String s = navMenuItem(i);
            items.add(s);
        }
        assertThat(items).containsExactly("Log In", "Message Box", "Notifications", "Public Home", "System Account");
    }


}
