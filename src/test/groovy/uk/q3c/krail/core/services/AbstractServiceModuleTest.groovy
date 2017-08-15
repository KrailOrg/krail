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

package uk.q3c.krail.core.services

import com.google.inject.*
import spock.lang.Specification
import uk.q3c.krail.core.eventbus.EventBusModule
import uk.q3c.krail.i18n.test.TestI18NModule
import uk.q3c.krail.option.test.TestOptionModule
import uk.q3c.krail.persist.inmemory.InMemoryModule
import uk.q3c.krail.testutil.guice.uiscope.TestUIScopeModule
import uk.q3c.krail.testutil.guice.vsscope.TestVaadinSessionScopeModule
import uk.q3c.util.UtilModule

class AbstractServiceModuleTest extends Specification {

    ServiceKey serviceKeyA = Mock(ServiceKey)
    ServiceKey serviceKeyB = Mock(ServiceKey)
    ServiceKey serviceKeyC = Mock(ServiceKey)


    class TestServiceModule extends AbstractServiceModule {

        @Override
        protected void registerServices() {
            registerService(serviceKeyA, MockServiceA)
            registerService(serviceKeyB, MockServiceB)
        }

        @Override
        protected void defineDependencies() {
            addDependency(serviceKeyA, serviceKeyB, Dependency.Type.REQUIRED_ONLY_AT_START)
            addDependency(serviceKeyA, serviceKeyC, Dependency.Type.OPTIONAL)
        }
    }


    def "services registered"() {
        given:

        TypeLiteral<Map<ServiceKey, Service>> mapTypeLiteral = new TypeLiteral<Map<ServiceKey, Service>>() {}
        Key key = Key.get(mapTypeLiteral)

        when:
        Injector injector = createInjector()

        then:

        injector.getBinding(key) != null

        Binding<Map<ServiceKey, Service>> x = injector.getBinding(key)
        Map<ServiceKey, Service> s = injector.getProvider(key).get()

        s.size() == 2
    }


    def "dependencies bound"() {
        given:

        TypeLiteral<Set<DependencyDefinition>> lit = new TypeLiteral<Set<DependencyDefinition>>() {}
        Key key = Key.get(lit)

        when:
        Injector injector = createInjector()

        then:

        injector.getBinding(key) != null

        Binding<Set<DependencyDefinition>> x = injector.getBinding(key)
        Set<DependencyDefinition> s = injector.getProvider(key).get()

        s.size() == 2

    }

    private Injector createInjector() {
        Guice.createInjector(new TestServiceModule(), new EventBusModule(), new ServicesModule(), new TestI18NModule(), new UtilModule(), new TestOptionModule(), new TestVaadinSessionScopeModule(), new TestUIScopeModule(), new InMemoryModule())
    }


}
