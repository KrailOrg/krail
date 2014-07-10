package uk.co.q3c.v7.testApp.test;

import com.vaadin.ui.ComboBox;
import org.junit.Before;
import org.junit.Test;
import uk.co.q3c.v7.base.view.component.DefaultLocaleSelector;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 09/07/14.
 */

public class LocaleTest extends V7TestBenchTestCase {

    @Before
    public void setUp() throws Exception {
        driver.get(rootUrl());
    }


    @Test
    public void defaultLocale() {

        // given

        // when

        // then
        String comboValue = comboValue(DefaultLocaleSelector.class, ComboBox.class);
        assertThat(comboValue).isEqualTo("English (United Kingdom)");
    }

    @Test
    public void switchToGerman() {

        // given

        // when
        localeSelect(2);

        // then
        String comboValue = comboValue(DefaultLocaleSelector.class, ComboBox.class);
        assertThat(comboValue).isEqualTo("German (Germany)");
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add(navTreeItemCaption(i));
        }
        assertThat(items).containsExactly("Benachrichtigungen", "Einloggen", "Nachricht Feld", "Öffentliche Startseite", "Systemkonto");

        items.clear();
        for (int i = 0; i < 5; i++) {
            String s = navMenuItem(i);
            items.add(s);
        }

        //this is in a different order to navtree. See https://github.com/davidsowerby/v7/issues/257
        assertThat(items).containsExactly("Benachrichtigungen", "Einloggen", "Nachricht Feld", "Systemkonto", "Öffentliche Startseite");
    }

}
