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
package uk.q3c.krail.base.view.layout;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import uk.q3c.krail.base.view.layout.DefaultViewConfig.Split;

public class ApplicationViewLayout1 extends ViewLayoutBase {
    private VerticalLayout baseLayout;

    protected ApplicationViewLayout1() {
        super();
    }

    @Override
    public boolean isValidSplit(Split split) {
        if (split.section1 == 4) {
            if (split.section2 == 5 || split.section2 == 6 || split.section2 == 7) {
                return true;
            }
        }
        if (split.section1 == 5 || split.section1 == 6 || split.section1 == 7) {
            if (split.section2 == 4) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doAssemble() {
        baseLayout = new VerticalLayout();
        layoutRoot = baseLayout;

        HorizontalLayout row0 = new HorizontalLayout(components.get(0), components.get(1), components.get(2));
        row0.setWidth("100%");
        add(row0);
        add(components.get(3));
        HorizontalSplitPanel row2 = new HorizontalSplitPanel();
        row2.setWidth("100%");
        components.get(4)
                  .setSizeFull();

        row2.setFirstComponent(components.get(4));

        VerticalLayout mainArea = new VerticalLayout(components.get(5), components.get(6), components.get(7));
        mainArea.setSizeFull();
        row2.setSecondComponent(mainArea);
        add(row2);
        add(components.get(8));
    }

    private void add(Component component) {
        baseLayout.addComponent(component);
    }
}
