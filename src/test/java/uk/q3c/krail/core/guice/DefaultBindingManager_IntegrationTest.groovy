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

package uk.q3c.krail.core.guice

import com.google.inject.Module
import com.google.inject.TypeLiteral
import uk.q3c.krail.core.persist.DefaultActiveOptionSource
import uk.q3c.krail.core.persist.InMemoryOptionContainerProvider
import uk.q3c.krail.core.persist.KrailPersistenceUnitHelper
import uk.q3c.krail.core.persist.OptionContainerProvider
import uk.q3c.krail.core.user.opt.InMemory
import uk.q3c.krail.core.user.opt.InMemoryOptionDao
import uk.q3c.krail.core.user.opt.OptionDao
import uk.q3c.krail.core.validation.JavaxValidationSubstitutes
import uk.q3c.krail.core.validation.KrailInterpolator
import uk.q3c.krail.i18n.I18NKey

import javax.validation.MessageInterpolator
import java.lang.annotation.Annotation

/**
 * Integration test for {@link DefaultBindingManager}
 *
 * Created by David Sowerby on 22/07/15.
 */
class DefaultBindingManager_IntegrationTest extends GuiceModuleTestBase {


    class TestDefaultBindingManager extends DefaultBindingManager {

        @Override
        protected void addAppModules(List<Module> modules) {

        }
    }

    def "InMemory option dao is active dao by default"() {
        when:

        DefaultBindingManager bindingManager = new TestDefaultBindingManager()
        injector = bindingManager.getInjector()

        then:

        activeOptionDao().equals(InMemory.class)
    }

    def "OptionContainerProvider is bound to InMemoryOptionContainerProvider with InMemory annotation"() {
        when:

        DefaultBindingManager bindingManager = new TestDefaultBindingManager()
        injector = bindingManager.getInjector()

        then:

        optionContainerProvider() instanceof InMemoryOptionContainerProvider
    }

    def "OptionDao is bound to InMemoryOptionDao with InMemory annotation"() {
        when:

        DefaultBindingManager bindingManager = new TestDefaultBindingManager()
        injector = bindingManager.getInjector()

        then:

        optionDao() instanceof InMemoryOptionDao
    }


    def "KrailValidationModule overrides ValidationModule, provides I18NKey substitutes for JSR303 and replacement interpolator"() {
        when:

        DefaultBindingManager bindingManager = new TestDefaultBindingManager()
        injector = bindingManager.getInjector()

        then:

        javaxSubstitutes().size() == 15
        interpolator() instanceof KrailInterpolator
    }

    def "OptionModule active source is InMemory"() {

        when:

        DefaultBindingManager bindingManager = new TestDefaultBindingManager()
        injector = bindingManager.getInjector()

        then:

        activeOptionSource().equals(InMemory.class)
    }


    def OptionDao optionDao() {
        getBinding OptionDao, InMemory.class
    }

    def Class<? extends Annotation> activeOptionSource() {
        getBinding new TypeLiteral<Class<? extends Annotation>>() {}, DefaultActiveOptionSource
    }

    def MessageInterpolator interpolator() {
        getBinding MessageInterpolator.class
    }

    def Map<Class<? extends Annotation>, I18NKey> javaxSubstitutes() {
        getBinding new TypeLiteral<Map<Class<? extends Annotation>, I18NKey>>() {}, JavaxValidationSubstitutes
    }

    def Class<? extends Annotation> activeOptionDao() {
        getBinding KrailPersistenceUnitHelper.annotationClassLiteral(), DefaultActiveOptionSource.class
    }

    def OptionContainerProvider optionContainerProvider() {
        getBinding OptionContainerProvider, InMemory.class
    }
    @Override
    List<Module> addSupportingModules(List<Module> modules) {

        return modules;
    }
}
