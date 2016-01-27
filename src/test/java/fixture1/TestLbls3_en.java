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

package fixture1;

import uk.q3c.krail.core.i18n.TestLabelKey3;

/**
 * Created by David Sowerby on 10/12/14.
 */
public class TestLbls3_en extends TestLbls3 {

    public TestLbls3_en() {
        super();
    }

    @Override
    protected void loadMap() {
        put(TestLabelKey3.Key1, "key number 1");
    }
}
