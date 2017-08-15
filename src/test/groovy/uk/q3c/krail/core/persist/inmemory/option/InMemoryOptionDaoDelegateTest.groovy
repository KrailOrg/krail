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

import com.google.inject.Guice
import com.google.inject.Injector
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.core.persist.common.option.DefaultOptionDao
import uk.q3c.krail.option.test.TestOptionModule
import uk.q3c.krail.persist.inmemory.InMemoryModule
import uk.q3c.krail.testutil.option.OptionDaoTestBase
import uk.q3c.util.UtilModule
import uk.q3c.util.data.DataConverter

/**
 * Created by David Sowerby on 21 Jan 2016
 */

class InMemoryOptionDaoDelegateTest extends OptionDaoTestBase {


    def setup() {
        Injector injector = Guice.createInjector(new InMemoryModule().provideOptionDao(), new TestOptionModule(), new VaadinSessionScopeModule(), new UtilModule())
        InMemoryOptionDaoDelegate injectedDaoDelegate = injector.getInstance(InMemoryOptionDaoDelegate)
        optionElementConverter = injector.getInstance(DataConverter)

        optionSource.getActiveDao() >> injectedDaoDelegate
        dao = new DefaultOptionDao(optionElementConverter, optionSource)
        expectedConnectionUrl = "In Memory Store"
    }

}
