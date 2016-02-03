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
package testutil;

import uk.q3c.krail.core.ui.BasicUI;
import uk.q3c.krail.core.ui.DefaultUIModule;

/**
 * Extends the default UIModule to provide instances of TestUI
 *
 * @author David Sowerby 12 Jan 2013
 */
public class TestUIModule extends DefaultUIModule {


    /**
     * {@inheritDoc}
     */
    @Override
    protected void define() {
        addUIBinding(BasicUI.class);
    }


}
