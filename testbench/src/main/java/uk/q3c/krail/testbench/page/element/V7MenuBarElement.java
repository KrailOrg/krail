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

package uk.q3c.krail.testbench.page.element;

import com.vaadin.testbench.elements.AbstractComponentElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.elementsbase.ServerClass;
import com.vaadin.testbench.exceptions.LowVaadinVersionException;
import com.vaadin.testbench.exceptions.MenuItemNotAvailableException;
import com.vaadin.testbench.util.VersionUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * THIS IS A VERY BADLY HACKED VERSION FOR USE ONLY UNTIL I FIND OUT WHAT IS WRONG WITH {@link MenuBarElement}.  DO NOT
 * USE THIS CLASS IF YOU CAN AVOID IT - IT WILL BE REMOVED
 * <p/>
 * Created by David Sowerby on 19/10/14.
 */
@ServerClass("com.vaadin.ui.MenuBar")
public class V7MenuBarElement extends AbstractComponentElement {

    private final int vaadinMajorVersionRequired = 7;
    private final int vaadinMinorVersionRequired = 3;
    private final int vaadinRevisionRequired = 1;
    private Point lastItemLocationMovedTo = null;

    /**
     * Clicks the item specified by a full path given as variable arguments.<br>
     * Fails if path given is not full (ie: last submenu is already opened, and
     * path given is last item only).
     * <p>
     * Example:<br>
     * <p/>
     * <pre>
     * // clicks on &quot;File&quot; item
     * menuBarElement.click(&quot;File&quot;);
     * // clicks on &quot;Copy&quot; item in &quot;File&quot; top level menu.
     * menuBarElement.click(&quot;File&quot;, &quot;Copy&quot;);
     * </pre>
     * <p/>
     * </p>
     *
     * @param path
     *         Array of items to click through, starting at the root
     */
    public void clickItem(String... path) {
        checkVersion();
        closeAll();
        ArrayList<String> segments = new ArrayList(Arrays.asList(path));

        WebElement rootElement = getItem(segments.get(0));
        activateOrOpenSubmenu(rootElement, true);

        segments.remove(0);

        for (String segment : segments) {
            WebElement item = getItem(segment);
            item.click();
        }
    }

    private WebElement getItem(String item) {
        List<WebElement> elements = getDriver().findElements(By.className("v-menubar-menuitem-caption"));
        WebElement selectedElement = null;
        for (WebElement element : elements) {
            if (element.getText()
                       .equals(item)) {
                selectedElement = element;
                break;
            }
        }


        if (selectedElement == null) {
            throw new MenuItemNotAvailableException(item);
        }

        return selectedElement;
    }

    private void checkVersion() {
        if (!isMenuBarApiSupported()) {
            throw new LowVaadinVersionException(String.format("Vaadin version required: %d.%d.%d",
                    vaadinMajorVersionRequired, vaadinMinorVersionRequired, vaadinRevisionRequired));
        }
    }

    /**
     * Closes all submenus, if any is open.<br>
     * This is done by clicking on the currently selected top level item.
     */
    public void closeAll() {
        checkVersion();

        lastItemLocationMovedTo = null;
        WebElement selectedItem = getSelectedTopLevelItem();
        if (selectedItem != null) {
            activateOrOpenSubmenu(selectedItem, true);
        }
    }

    private WebElement getSelectedTopLevelItem() {
        List<WebElement> selectedItems = findElements(By.className("v-menubar-menuitem-selected"));
        if (selectedItems.size() == 0) {
            return null;
        }
        return selectedItems.get(0);
    }

    private WebElement getVisibleItem(String item) {
        return findElement(com.vaadin.testbench.By.vaadin(item));
    }

    private void activateOrOpenSubmenu(WebElement item, boolean alwaysClick) {

        if (lastItemLocationMovedTo == null || !isAnySubmenuVisible()) {
            item.click();
            if (hasSubmenu(item)) {
                lastItemLocationMovedTo = item.getLocation();
            }
            return;
        }

        // Assumes mouse is still at position of last clicked element
        Actions action = new Actions(getDriver());
        action.moveToElement(item);
        action.build()
              .perform();

        if (isLeaf(item) || isSelectedTopLevelItem(item)) {
            lastItemLocationMovedTo = null;
        } else {
            lastItemLocationMovedTo = item.getLocation();
        }

        if (alwaysClick || isLeaf(item) || !isAnySubmenuVisible()) {
            action = new Actions(getDriver());
            action.click();
            action.build()
                  .perform();
        }
    }

    private boolean isSelectedTopLevelItem(WebElement item) {
        WebElement selectedItem = getSelectedTopLevelItem();
        if (selectedItem == null) {
            return false;
        }

        String itemCaption = item.findElements(By.className("v-menubar-menuitem-caption"))
                                 .get(0)
                                 .getAttribute("innerHTML");
        String selectedItemCaption = selectedItem.findElements(By.className("v-menubar-menuitem-caption"))
                                                 .get(0)
                                                 .getAttribute("innerHTML");
        return itemCaption.equals(selectedItemCaption);
    }

    private boolean isAnySubmenuVisible() {
        WebElement selectedItem = getSelectedTopLevelItem();
        if (selectedItem == null) {
            return false;
        }
        return hasSubmenu(selectedItem);
    }

    private boolean hasSubmenu(WebElement item) {
        List<WebElement> submenuIndicatorElements = item.findElements(By.className("v-menubar-submenu-indicator"));
        return submenuIndicatorElements.size() != 0;
    }

    private boolean isLeaf(WebElement item) {
        return !hasSubmenu(item);
    }

    /**
     * @return true if Vaadin version is high enough to have VMenuBar update
     * needed in order to use this API. False otherwise.
     */
    private boolean isMenuBarApiSupported() {

        return VersionUtil.isAtLeast(vaadinMajorVersionRequired, vaadinMinorVersionRequired, vaadinRevisionRequired,
                getDriver());
    }

}
