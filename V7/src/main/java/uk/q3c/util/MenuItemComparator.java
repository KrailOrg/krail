/*
 * Copyright (c) 2014 David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package uk.q3c.util;

import com.vaadin.ui.MenuBar.MenuItem;

import java.util.Comparator;

public class MenuItemComparator implements Comparator<MenuItem> {

    @Override
    public int compare(MenuItem arg0, MenuItem arg1) {
        return arg0.getText()
                   .compareTo(arg1.getText());
    }

}
