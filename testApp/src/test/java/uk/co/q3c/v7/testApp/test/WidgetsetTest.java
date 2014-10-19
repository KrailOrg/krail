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

package uk.co.q3c.v7.testApp.test;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class WidgetsetTest extends V7TestBenchTestCase {

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
