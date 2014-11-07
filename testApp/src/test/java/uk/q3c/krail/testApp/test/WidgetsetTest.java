/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.testApp.test;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.q3c.krail.testbench.KrailTestBenchTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class WidgetsetTest extends KrailTestBenchTestCase {

    private WidgetSetViewPageObject view = new WidgetSetViewPageObject(this);

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

        // then
        assertThat(view.stepper()).isNotNull();
        assertThat(view.stepper()
                       .isDisplayed()).isTrue();
    }

    @After
    public void tearDown2() throws Exception {
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
        //		// don't know why this is necessary in this test and no other?
        //		driver.close();
    }
}
