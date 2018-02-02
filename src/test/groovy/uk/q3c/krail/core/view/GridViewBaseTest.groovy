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

package uk.q3c.krail.core.view

import com.vaadin.ui.Label
import uk.q3c.krail.core.view.component.ViewChangeBusMessage
import uk.q3c.krail.i18n.Translate

/**
 * Created by David Sowerby on 08 Feb 2016
 */
class GridViewBaseTest extends ViewTest {

    class GridTest extends GridViewBase {

        protected GridTest(Translate translate) {
            super(translate)
        }

        @Override
        protected void doBuild(ViewChangeBusMessage busMessage) {
            gridLayout.addComponent(new Label())
        }
    }

    GridTest gridTest


    def setup() {
        view = new GridTest(translate)
        gridTest = view
    }


}
