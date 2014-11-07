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

package uk.co.q3c.v7.testbench;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.screenshot.ImageFileUtil;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import uk.co.q3c.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by David Sowerby on 07/11/14.
 */
public class BrowserSettingTest extends V7TestBenchTestCase {

    @Before
    @Override
    public void baseSetup() throws Exception {
        File errorDir = new File(ResourceUtils.userHomeDirectory(), "temp/screenshot-errors");
        Parameters.setScreenshotErrorDirectory(errorDir.getAbsolutePath());
        System.out.println(new File("."));
        File referenceDir = new File(ResourceUtils.userHomeDirectory(),
                "/home/david/git/v7/testbench/src/test/resources/screenshots");
        Parameters.setScreenshotReferenceDirectory("screenshots/reference");
        Parameters.setMaxScreenshotRetries(2);
        Parameters.setScreenshotComparisonTolerance(1.0);
        Parameters.setScreenshotRetryDelay(10);
        Parameters.setScreenshotComparisonCursorDetection(true);

    }

    @Test
    public void localeSetting_Germany() throws IOException {
        //given
        firefoxLocale = Locale.GERMANY;
        setDriver(createFirefoxDriver());
        driver.manage()
              .window()
              .setSize(new Dimension(1024, 768));
        //when
        driver.get("https://www.google.co.uk/");
        //then
        testBench(driver).compareScreen(ImageFileUtil.getReferenceScreenshotFile("germany.png"));
    }

    @Test
    public void localeSetting_Italy() throws IOException {
        //given
        firefoxLocale = Locale.ITALY;
        setDriver(createFirefoxDriver());
        driver.manage()
              .window()
              .setSize(new Dimension(1024, 768));
        //when
        driver.get("https://www.google.co.uk/");
        //then
        testBench(driver).compareScreen(ImageFileUtil.getReferenceScreenshotFile("italy.png"));
    }


}
