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

import com.google.common.base.Optional;
import com.vaadin.testbench.elements.TreeElement;
import uk.co.q3c.v7.base.view.component.DefaultUserNavigationTree;

/**
 * Created by david on 04/10/14.
 */
public class NavTreePageObject extends PageObject {
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
    public NavTreePageObject(V7TestBenchTestCase parentCase) {
        super(parentCase);
    }

    public String itemCaption(int index) {
        return navTree().index(index)
                        .get()
                        .getText();
    }

    protected UITree navTree() {
        return treeLocator().id("DefaultUserNavigationTree");
    }

    protected UITree treeLocator() {
        return new UITree(parentCase.getDriver(), parentCase.getAppContext());
    }

    public void expand(int index) {
        navTree().expand(index);
    }


    public void select(int... index) {
        navTree().select(index);
    }

    public String currentSelection() {
        return navTree2().getValue();
    }

    private TreeElement navTree2() {
        return parentCase.tree(Optional.absent(), DefaultUserNavigationTree.class);
    }
}
