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

package uk.co.q3c.v7.testbench.page.object;

import com.google.common.base.Optional;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.MenuBarElement;
import org.openqa.selenium.WebElement;
import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.view.component.DefaultUserNavigationMenu;
import uk.co.q3c.v7.testbench.V7TestBenchTestCase;
import uk.co.q3c.v7.testbench.page.element.V7MenuBarElement;

/**
 * Created by david on 04/10/14.
 */
public class NavMenuPageObject extends PageObject {
    /**
     * Initialises the PageObject with a reference to the parent test case, so that the PageObject can access a number
     * of variables from the parent, for example: drivers, baseUrl,
     * application appContext.
     * <p/>
     * <p/>
     * Note that all calls requiring eventual access to a WebDriver should be made via the parentCase, so that the
     * correct driver is acted on.
     *
     * @param parentCase
     */
    public NavMenuPageObject(V7TestBenchTestCase parentCase) {
        super(parentCase);
    }

    /**
     * reads the text (caption) of the top level navigation menu item, as selected by {@code index}
     *
     * @param index
     *
     * @return
     */
    public String item(int index) {
        ElementPath elementPath = new ElementPath(parentCase.getAppContext());
        ElementPath id = elementPath.id(ID.getIdc(Optional.absent(), DefaultUserNavigationMenu.class));
        String ids = id.get() + "#item" + index;
        WebElement element = parentCase.getDriver()
                                       .findElement(By.vaadin(ids));
        return element.getText();
    }

    /**
     * See {@link MenuBarElement#clickItem(String...)}
     *
     * @param path
     */
    public void clickItem(String... path) {
        V7MenuBarElement element = menuBar();
        element.clickItem(path);
    }

    public V7MenuBarElement menuBar() {
        V7MenuBarElement menuBar = parentCase.$(V7MenuBarElement.class)
                                             .first();
        return menuBar;
    }
}
