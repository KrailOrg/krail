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

package uk.q3c.krail.core.persist.inmemory.option

import uk.q3c.krail.core.persist.common.option.OptionDaoTestBase

/**
 * Created by David Sowerby on 21 Jan 2016
 */
class InMemoryOptionDaoTest extends OptionDaoTestBase {

    def setup() {
        dao = new InMemoryOptionDao(new DefaultInMemoryOptionStore(), optionStringConverter)
        expectedConnectionUrl = "In Memory Store"
    }

}
