/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.core.services

import com.google.inject.*
import spock.lang.Specification
import uk.q3c.krail.UnitTestFor

@UnitTestFor(AbstractServiceModule)
//@UseModules([])
class AbstractServiceModuleTest extends Specification {

    ServiceKey serviceKeyA = Mock(ServiceKey)
    ServiceKey serviceKeyB = Mock(ServiceKey)
    ServiceKey serviceKeyC = Mock(ServiceKey)


    class TestServiceModule extends AbstractServiceModule {
        @Override
        protected void configure() {
            super.configure()
            addDependency(serviceKeyA, serviceKeyB, Dependency.Type.REQUIRED_ONLY_AT_START)
            addDependency(serviceKeyA, serviceKeyC, Dependency.Type.OPTIONAL)
        }
    }

    class TestServiceModule2 extends AbstractServiceModule {
        @Override
        protected void configure() {
            addDependency(serviceKeyA, serviceKeyB, Dependency.Type.REQUIRED_ONLY_AT_START)
            addDependency(serviceKeyA, serviceKeyC, Dependency.Type.OPTIONAL)
        }
    }


    def "properly configured"() {
        given:

        TypeLiteral<Set<DependencyDefinition>> lit = new TypeLiteral<Set<DependencyDefinition>>() {}
        Key key = Key.get(lit)

        when:
        Injector injector = Guice.createInjector(new TestServiceModule())

        then:

        injector.getBinding(key) != null

        Binding<Set<DependencyDefinition>> x = injector.getBinding(key)
        Set<DependencyDefinition> s = injector.getProvider(key).get()

        s.size() == 2

    }

    def "missed super.configure() "() {
        given:

        TypeLiteral<Set<DependencyDefinition>> lit = new TypeLiteral<Set<DependencyDefinition>>() {}
        Key key = Key.get(lit)

        when:
        Injector injector = Guice.createInjector(new TestServiceModule2())

        then:

        thrown(com.google.inject.CreationException)

    }

}
