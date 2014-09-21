/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package uk.co.q3c.v7.testbench;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UITree {
    private static Logger log = LoggerFactory.getLogger(UITree.class);
    private final ElementLocator locator;

    public UITree(WebDriver driver, String context) {
        super();
        this.locator = new ElementLocator(driver, context);
    }

    public UITree id(String id) {
        locator.id(id);
        return this;
    }

    /**
     * tried to make this expand nodes first, but for some very strange reason I can't get that to work from here - yet
     * the same calls in the same order in the test case works. Must be something to do with the way Selenium calls are
     * handled. You will therefore need to ensure a node is expanded,using {@link #expand(int)} before selecting one of
     * its sub-nodes.
     *
     * @param index
     *
     * @return
     */
    public UITree select(int... index) {
        locator.index(index)
               .get()
               .click();
        return this;
    }

    public UITree expand(int index) {
        locator.index(index)
               .expand()
               .get()
               .click();
        return this;
    }

    public ElementLocator index(int... index) {
        return locator.index(index);
    }

    protected void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            log.error("Sleep was interrupted");
        }
    }

    public ElementLocator getLocator() {
        return locator;
    }

}
