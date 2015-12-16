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

package uk.q3c.util
import com.google.inject.Inject
import spock.guice.UseModules
import spock.lang.Specification
import uk.q3c.krail.UnitTestFor
import uk.q3c.krail.core.services.Service
import uk.q3c.krail.core.services.ServiceKey
import uk.q3c.krail.core.services.ServiceStatus
import uk.q3c.krail.i18n.I18NKey
/**

 * Created by David on 22/10/15.
 */
@UnitTestFor(ClassNameUtils)
@UseModules([AOPTestModule])
class ClassNameUtilsTest extends Specification {

    def doubleD = '$'

    static class TestClass implements Service {

        @Inject
        protected TestClass() {

        }

        @Override
        ServiceStatus start() throws Exception {
            return null
        }



        @Override
        ServiceStatus stop() throws Exception {
            return null
        }


        @Override
        ServiceStatus fail() {
            return null
        }

        @Override
        ServiceStatus stop(Service.State reasonForStop) {
            return null
        }

        @Override
        String getName() {
            return super.getName()
        }

        @Override
        String getDescription() {
            return null
        }

        @Override
        Service.State getState() {
            return null
        }

        @Override
        boolean isStarted() {
            return false
        }

        @Override
        boolean isStopped() {
            return false
        }



        @Override
        I18NKey getNameKey() {
            return null
        }



        @Override
        void setInstance(int instance) {

        }

        @Override
        I18NKey getDescriptionKey() {
            return null
        }

        @Override
        void setDescriptionKey(I18NKey descriptionKey) {

        }

        @Override
        ServiceKey getServiceKey() {
            return super.getServiceKey()
        }

        @Override
        int getInstance() {
            return 0
        }
    }

    @Inject
    TestClass testClass

    def "SimpleNameWithoutEnhance"() {
        expect:
        testClass.getClass().getName().contains('uk.q3c.util.ClassNameUtilsTest$TestClass$$EnhancerByGuice')

        and:
        ClassNameUtils.simpleClassNameEnhanceRemoved(TestClass.class).equals('TestClass')

        and:
        ClassNameUtils.simpleClassNameEnhanceRemoved(testClass.getClass()).equals('TestClass')
    }

    def "classNameEnhanceRemoved"() {
        expect:
        testClass.getClass().getName().contains('uk.q3c.util.ClassNameUtilsTest$TestClass$$EnhancerByGuice')


        and:
        ClassNameUtils.classWithEnhanceRemoved(testClass.getClass()).equals(TestClass.class)
    }


}
