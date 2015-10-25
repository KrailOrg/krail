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

import com.google.inject.Inject
import com.google.inject.Injector
import net.engio.mbassy.bus.common.PubSubSupport
import spock.guice.UseModules
import spock.lang.Specification
import uk.q3c.krail.UnitTestFor
import uk.q3c.krail.core.eventbus.BusMessage
import uk.q3c.krail.core.eventbus.EventBusModule
import uk.q3c.krail.core.eventbus.GlobalBus
import uk.q3c.krail.core.guice.uiscope.UIScopeModule
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.core.navigate.sitemap.SitemapService
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.LabelKey
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.testutil.TestI18NModule
import uk.q3c.krail.testutil.TestOptionModule
import uk.q3c.krail.testutil.TestPersistenceModule

/**
 *
 * Created by David Sowerby on 12/11/15.
 */
@UnitTestFor(ServicesModule)
@UseModules([ServicesModule, TestI18NModule, TestOptionModule, TestPersistenceModule, EventBusModule, VaadinSessionScopeModule, UIScopeModule])
class ServicesModuleTest extends Specification {

    Translate translate = Mock(Translate)
    ServicesController servicesController = Mock(ServicesController)
    ServiceDependencyScanner scanner = Mock(ServiceDependencyScanner)

    static class TestService extends AbstractService {

        @Dependency
        private SitemapService dependency;
        int initCalled
        int stopsCalled

        @Inject
        protected TestService(Translate translate, ServicesController servicesController) {
            super(translate, servicesController)
        }

        @Override
        protected void doStop() throws Exception {
            stopsCalled++;
        }

        @Override
        protected void doStart() throws Exception {

        }

        @Override
        void init(PubSubSupport<BusMessage> eventBus) {
            initCalled++
        }

        @Override
        I18NKey getNameKey() {
            return LabelKey.Active_Source
        }

    }

    static class TestService2 extends AbstractService {

        @Dependency
        private SitemapService dependency;
        int initCalled
        int stopsCalled

        @Inject
        protected TestService2(Translate translate, ServicesController servicesController) {
            super(translate, servicesController)
        }

        @Override
        protected void doStop() throws Exception {
            stopsCalled++;
        }

        @Override
        protected void doStart() throws Exception {

        }

        @Override
        void init(PubSubSupport<BusMessage> eventBus) {
            super.init(eventBus)
            initCalled++
        }

        @Override
        I18NKey getNameKey() {
            return LabelKey.Active_Source
        }

    }

    static class TestServiceAnnotated extends AbstractService implements ServiceUsingDependencyAnnotation {

        int initCalled


        @Dependency
        private TestService dependency;

        @Inject
        protected TestServiceAnnotated(Translate translate, ServicesController servicesController, TestService dependency) {
            super(translate, servicesController)
            this.dependency = dependency

        }

        @Override
        protected void doStop() throws Exception {

        }

        @Override
        protected void doStart() throws Exception {

        }

        @Override
        I18NKey getNameKey() {
            return LabelKey.Yes
        }

        TestService getDependency() {
            return dependency
        }

        @Override
        void init(PubSubSupport<BusMessage> eventBus) {
            initCalled++
        }

    }


    @Inject
    Injector injector

    @Inject
    ServicesGraph graph

    @Inject
    @GlobalBus
    PubSubSupport<BusMessage> globalBus

    def "Service is intercepted after construction and registered with ServicesGraph, no annotation scan is done"() {

        when:

        TestService service = injector.getInstance(TestService)

        then:

        graph.isRegistered(service)
        service.initCalled == 1
        // cannot actively check for no scan - but a scan would fail because dependency is null
    }

    def "ServiceUsingDependencyAnnotation is intercepted, registered with ServicesGraph and scanned for annotation"() {

        when:

        TestServiceAnnotated service = injector.getInstance(TestServiceAnnotated)

        then:

        graph.isRegistered(service)
        graph.isRegistered(service.getDependency())
        service.initCalled == 1
    }

    //unreliable - not surprising given that it tries to call finalize() directly.  How can this be tested?
//    def "finalize calls stop"(){
//
//        given:
//        PubSubSupport<BusMessage> globalBus = Mock(PubSubSupport)
//        TestService2   service = injector.getInstance(TestService2)
//        service.init(globalBus)
//        graph.addService(service.getServiceKey())
//        when:
//        service.finalize()
//        then:
//        service.stopsCalled==1
//    }


}
