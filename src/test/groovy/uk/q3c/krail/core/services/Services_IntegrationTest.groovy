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

import com.google.inject.Guice
import com.google.inject.Injector
import spock.lang.Specification
import uk.q3c.krail.core.eventbus.EventBusModule
import uk.q3c.krail.core.guice.uiscope.UIScopeModule
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.testutil.i18n.TestI18NModule
import uk.q3c.krail.testutil.option.TestOptionModule
import uk.q3c.krail.testutil.persist.TestPersistenceModule
import uk.q3c.krail.util.UtilsModule

import static uk.q3c.krail.testutil.i18n.TestLabelKey.*

/**
 *
 *
 *
 * Created by David Sowerby on 07 Dec 2015
 */
//@UseModules([UtilsModule])
class Services_IntegrationTest extends Specification {

    class TestServiceModule extends AbstractServiceModule {


        @Override
        protected void registerServices() {
            registerService(ServiceA, TestServiceA.class)
            registerService(ServiceB, TestServiceB.class)
            registerService(ServiceC, TestServiceC.class)
            registerService(ServiceD, TestServiceD.class)
            registerService(ServiceE, TestServiceE.class)
        }

        @Override
        protected void defineDependencies() {
            this.addDependency(ServiceB, ServiceC, Dependency.Type.ALWAYS_REQUIRED)
            this.addDependency(ServiceD, ServiceC, Dependency.Type.ALWAYS_REQUIRED)
        }
    }


    def setup() {

    }

    /**
     *
     * All dependencies are "alwaysRequired"
     * TestServiceA to E are set up with fields and annotations to match this test
     * B is unscoped, others are singletons
     * A->B'->C
     * D->C
     * E->B''->C
     *
     */
    def "service has no dependencies, added to model from injection interception"() {
        given:


        Injector injector = createInjector()
        ServicesModel model = injector.getInstance(ServicesModel)


        when:

        TestServiceC serviceC = injector.getInstance(TestServiceC)

        then:

        serviceC != null
        model.contains(serviceC)
    }

    def "service has dependencies, which are created automatically in model when dependant injected "() {
        Injector injector = createInjector()
        ServicesModel model = injector.getInstance(ServicesModel)


        when:

        TestServiceD serviceD = injector.getInstance(TestServiceD)
        then:
        serviceD != null
        model.contains(serviceD)
        model.contains(injector.getInstance(TestServiceC))
        model.getInstanceGraph().findDependencies(serviceD, ServicesGraph.Selection.ALL).contains(injector.getInstance(TestServiceC))
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "stop service is cascaded"() {
        given:
        Injector injector = createInjector()
        ServicesModel model = injector.getInstance(ServicesModel)
        TestServiceD serviceD = injector.getInstance(TestServiceD)
        TestServiceC serviceC = injector.getInstance(TestServiceC)


        when:

        serviceD.start();

        then:

        serviceD.isStarted();
        serviceC.isStarted();

        when:

        serviceC.stop()

        then:
        serviceD.isStopped();
        serviceC.isStopped();

    }


    def "instance definitions"() {
        given:

        Injector injector = createInjector()
        ServicesModel model = injector.getInstance(ServicesModel)
        TestServiceD serviceD = injector.getInstance(TestServiceD)
        TestServiceC serviceC = injector.getInstance(TestServiceC)


        when:

        serviceD.start();
        List<DependencyInstanceDefinition> dependencies = model.findInstanceDependencyDefinitions(serviceD)

        then:
        dependencies.size() == 1
        dependencies.get(0).getDependency().equals(serviceC)
        dependencies.get(0).getType().equals(Dependency.Type.ALWAYS_REQUIRED)

    }

    def "add service instance a second time is ignored"() {

        given:

        Injector injector = createInjector()
        ServicesModel model = injector.getInstance(ServicesModel)



        when:

        TestServiceD serviceD = injector.getInstance(TestServiceD)
        TestServiceC serviceC = injector.getInstance(TestServiceC)



        then:

        model.contains(serviceC)
        model.contains(serviceD)

        when:

        boolean result = model.addService(serviceD)

        then:

        !result
    }

    private Injector createInjector() {
        return Guice.createInjector([new UtilsModule(), new TestServiceModule(), new ServicesModule(), new TestI18NModule(), new TestOptionModule(), new TestPersistenceModule(), new EventBusModule(), new VaadinSessionScopeModule(), new UIScopeModule()])
    }

}
