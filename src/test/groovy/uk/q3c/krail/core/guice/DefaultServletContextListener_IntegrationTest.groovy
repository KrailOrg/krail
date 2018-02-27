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

package uk.q3c.krail.core.guice

import com.google.inject.Module
import com.google.inject.TypeLiteral
import uk.q3c.krail.core.validation.JavaxValidationSubstitutes
import uk.q3c.krail.core.validation.KrailInterpolator
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.persist.I18NPersistenceHelper
import uk.q3c.krail.option.persist.ActiveOptionSourceDefault
import uk.q3c.krail.option.persist.OptionDaoDelegate
import uk.q3c.krail.persist.InMemory
import uk.q3c.krail.persist.inmemory.dao.InMemoryOptionDaoDelegate
import uk.q3c.krail.testutil.dummy.Dummy
import uk.q3c.krail.testutil.dummy.DummyModule
import uk.q3c.util.testutil.LogMonitor

import javax.servlet.ServletContextEvent
import javax.validation.MessageInterpolator
import java.lang.annotation.Annotation

/**
 * Integration test for {@link DefaultServletContextListener}
 *
 * Created by David Sowerby on 22/07/15.
 */
class DefaultServletContextListener_IntegrationTest extends GuiceModuleTestBase {

    class TestDefaultServletContextListener extends DefaultServletContextListener {

        @Override
        protected BindingsCollator getBindingsCollator() {
            return new BindingsCollator(new DummyModule())
        }
    }

    def "destroy context with null injector"() {
        given:
        LogMonitor logMonitor = new LogMonitor()
        logMonitor.addClassFilter(DefaultServletContextListener.class)
        ServletContextEvent servletContextEvent = Mock(ServletContextEvent)
        DefaultServletContextListener contextListener = new TestDefaultServletContextListener()
        DefaultServletContextListener.injector = null

        when:
        contextListener.contextDestroyed(servletContextEvent)

        then:
        logMonitor.debugLogs().contains("Injector has not been constructed, no call made to stop service")
    }

    def "InMemory option dao is active dao by default"() {
        when:

        DefaultServletContextListener bindingManager = new TestDefaultServletContextListener()
        injector = bindingManager.getInjector()

        then:

        activeOptionDao().equals(InMemory.class)
    }


    def "OptionDao is bound to InMemoryOptionDao with InMemory annotation"() {
        when:

        DefaultServletContextListener bindingManager = new TestDefaultServletContextListener()
        injector = bindingManager.getInjector()

        then:

        optionDao() instanceof InMemoryOptionDaoDelegate
    }


    def "KrailValidationModule overrides ValidationModule, provides I18NKey substitutes for JSR303 and replacement interpolator"() {
        when:

        DefaultServletContextListener bindingManager = new TestDefaultServletContextListener()
        injector = bindingManager.getInjector()

        then:

        javaxSubstitutes().size() == 15
        interpolator() instanceof KrailInterpolator
    }

    def "OptionModule active source is InMemory"() {

        when:

        DefaultServletContextListener bindingManager = new TestDefaultServletContextListener()
        injector = bindingManager.getInjector()

        then:
        activeOptionSource().equals(InMemory.class)
    }

    def "app modules have been added"() {
        when:
        DefaultServletContextListener bindingManager = new TestDefaultServletContextListener()
        injector = bindingManager.getInjector()

        then:
        injector.getInstance(Dummy.class) != null
    }


    OptionDaoDelegate optionDao() {
        getBinding OptionDaoDelegate, InMemory.class
    }

    Class<? extends Annotation> activeOptionSource() {
        getBinding new TypeLiteral<Class<? extends Annotation>>() {}, ActiveOptionSourceDefault
    }

    MessageInterpolator interpolator() {
        getBinding MessageInterpolator.class
    }

    Map<Class<? extends Annotation>, I18NKey> javaxSubstitutes() {
        getBinding new TypeLiteral<Map<Class<? extends Annotation>, I18NKey>>() {}, JavaxValidationSubstitutes
    }

    Class<? extends Annotation> activeOptionDao() {
        getBinding I18NPersistenceHelper.annotationClassLiteral(), ActiveOptionSourceDefault.class
    }


    @Override
    List<Module> addSupportingModules(List<Module> modules) {
        return modules
    }
}
