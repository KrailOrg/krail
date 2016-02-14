/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */
package uk.q3c.krail.testutil.guice.uiscope;

import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import uk.q3c.krail.core.guice.uiscope.UIKey;
import uk.q3c.krail.core.guice.uiscope.UIScope;
import uk.q3c.krail.core.guice.uiscope.UIScopeModule;
import uk.q3c.krail.core.ui.BasicUI;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * "Fudges" the UIScope for testing - also clears scope from previous test use"
 * <p>
 * Created by David Sowerby on 13/03/15.
 */
public class TestUIScopeModule extends UIScopeModule {

    public TestUIScopeModule() {
        super();
        setScopeKey(1);
    }

    public final void setScopeKey(int index) {
        UI.setCurrent(null); // overcomes the inheritable check in CurrentInstance.set
        //flush last test if there was one
        UIScope oldScope = UIScope.getCurrent();
        if (oldScope != null) {
            oldScope.flush();
        }
        UIKey uiKey = new UIKey(index);
        BasicUI ui = mock(BasicUI.class);
        when(ui.getInstanceKey()).thenReturn(uiKey);
        UI.setCurrent(ui);
        CurrentInstance.set(UIKey.class, uiKey);
        getUiScope().startScope(uiKey);
    }
}