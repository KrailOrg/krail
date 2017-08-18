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

package uk.q3c.krail.config.bind

import com.google.common.collect.Lists
import com.google.inject.*
import spock.lang.Specification
import uk.q3c.krail.config.config.IniFileConfig
import uk.q3c.krail.core.guice.uiscope.UIScopeModule
import uk.q3c.krail.core.vaadin.DataModule
import uk.q3c.krail.i18n.test.TestI18NModule
import uk.q3c.krail.option.test.TestOptionModule
import uk.q3c.krail.persist.inmemory.InMemoryModule
import uk.q3c.krail.service.bind.ServicesModule
import uk.q3c.krail.testutil.eventbus.TestEventBusModule
import uk.q3c.krail.testutil.guice.vsscope.TestVaadinSessionScopeModule
import uk.q3c.krail.util.UtilsModule
import uk.q3c.util.UtilModule

/**
 * Created by David Sowerby on 15 Jan 2016
 */

class ApplicationConfigurationModuleTest extends Specification {


    List<Module> modules

    def setup() {
        modules = Lists.newArrayList(new TestI18NModule(), new ServicesModule(), new UtilsModule(), new UtilModule(), new TestOptionModule(), new TestEventBusModule(), new DataModule(), new TestVaadinSessionScopeModule(), new UIScopeModule(), new InMemoryModule())


    }


    def "configs"() {
        given:
        modules.add(new ApplicationConfigurationModule().addConfig("/home/x", 5, false).addConfig("/home/y", 6, true))
        when:
        Injector injector = Guice.createInjector(modules)
        TypeLiteral<Map<Integer, IniFileConfig>> mapLiteral = new TypeLiteral<Map<Integer, IniFileConfig>>() {}
        Key<Map<Integer, IniFileConfig>> key = Key.get(mapLiteral)
        Map<Integer, IniFileConfig> map = injector.getInstance(key)

        then:
        (map.get(5)).filename == "/home/x"
        !(map.get(5)).optional
        (map.get(6)).filename == "/home/y"
        (map.get(6)).optional

    }
}
