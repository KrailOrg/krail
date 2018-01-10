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

package uk.q3c.krail.service

import com.google.inject.Inject
import spock.guice.UseModules
import spock.lang.Specification
import uk.q3c.krail.config.i18n.ConfigurationLabelKey
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.eventbus.MessageBus
import uk.q3c.krail.i18n.Translate
import uk.q3c.krail.i18n.i18n.PatternLabelKey
import uk.q3c.krail.service.model.DefaultServiceDependencyScanner
import uk.q3c.util.UtilModule
import uk.q3c.util.clazz.ClassNameUtils
import uk.q3c.util.testutil.LogMonitor
/**
 *
 * Created by David Sowerby on 11/11/15.
 */
@UseModules([UtilModule])
class DefaultServiceDependencyScannerTest extends Specification {

    @Inject
    ClassNameUtils classNameUtils

    Service mockA = Mock(Service)
    Service mockB = Mock(Service)
    Service mockC = Mock(Service)
    Service mockD = Mock(Service)

    def model = Mock(ServiceModel)
    def translate = Mock(Translate)
    MessageBus globalBusProvider = Mock(MessageBus)
    LogMonitor logMonitor
    RelatedServiceExecutor servicesExecutor = Mock(RelatedServiceExecutor)



    def setup() {
        mockA.getServiceKey() >> new ServiceKey(LabelKey.Authentication)
        mockB.getServiceKey() >> new ServiceKey(ConfigurationLabelKey.Application_Configuration_Service)
        mockC.getServiceKey() >> new ServiceKey(LabelKey.Active_Source)
        mockD.getServiceKey() >> new ServiceKey(PatternLabelKey.Auto_Stub)
        translate.from(LabelKey.Yes) >> "Yes"
        logMonitor = new LogMonitor()
        logMonitor.addClassFilter(DefaultServiceDependencyScanner.class)
    }

    def cleanup() {
        logMonitor.close()
    }

    def "scan valid dependencies, @Dependency on non-Service field ignored"() {
        given:

        def scanner = new DefaultServiceDependencyScanner(model, classNameUtils)
        def service = new TestService(translate, mockA, mockB, mockC, mockD, globalBusProvider, servicesExecutor)

        when:
        scanner.scan(service)

        then:
        1 * model.alwaysDependsOn(service.getServiceKey(), mockA.getServiceKey())
        1 * model.optionallyUses(service.getServiceKey(), mockB.getServiceKey())
        1 * model.requiresOnlyAtStart(service.getServiceKey(), mockC.getServiceKey())
        1 * model.optionallyUses(service.getServiceKey(), mockD.getServiceKey())
        logMonitor.warnLogs().isEmpty()

    }


    def "scan with Dependency field null"() {
        given:

        def scanner = new DefaultServiceDependencyScanner(model, classNameUtils)
        def service = new TestService(translate, null, mockB, mockC, mockD, globalBusProvider, servicesExecutor)

        when:
        scanner.scan(service)

        then:
//        1 * model.alwaysDependsOn(service.getServiceKey(), mockA.getServiceKey())
        1 * model.optionallyUses(service.getServiceKey(), mockB.getServiceKey())
        1 * model.requiresOnlyAtStart(service.getServiceKey(), mockC.getServiceKey())
        1 * model.optionallyUses(service.getServiceKey(), mockD.getServiceKey())
        logMonitor.warnLogs().contains("Field is annotated with @Dependency but is null, dependency not set")
    }


}
