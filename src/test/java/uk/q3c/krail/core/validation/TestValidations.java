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

package uk.q3c.krail.core.validation;

import uk.q3c.krail.core.i18n.EnumResourceBundle;

/**
 * Created by David Sowerby on 06/02/15.
 */
public class TestValidations extends EnumResourceBundle<TestValidationKey> {


    @Override
    protected void loadMap() {
        put(TestValidationKey.Too_Big, "is far too big, it should be less than {0}");
    }
}
