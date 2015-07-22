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

package uk.q3c.krail.i18n.i8nModule

import com.google.inject.*
import spock.lang.Specification
import uk.q3c.krail.core.data.DataModule
import uk.q3c.krail.core.eventbus.EventBusModule
import uk.q3c.krail.core.eventbus.SessionBus
import uk.q3c.krail.core.guice.uiscope.UIScopeModule
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.core.persist.DefaultActivePatternDao
import uk.q3c.krail.core.persist.InMemoryBundleReader
import uk.q3c.krail.core.persist.InMemoryModule
import uk.q3c.krail.core.shiro.DefaultShiroModule
import uk.q3c.krail.core.user.opt.InMemory
import uk.q3c.krail.i18n.*
import uk.q3c.krail.testutil.TestOptionModule

import java.lang.annotation.Annotation

/**
 * Unit tests for {@link I18NModule}
 *
 * Created by David Sowerby on 21/07/15.
 */
class I18NModuleTest extends Specification {

    Injector injector


    def "in memory database reader bound by call to inMemory()"() {
        when:

        injector = createInjector(new I18NModule().inMemory())


        then:
        def Map<String, BundleReader> x = getBinding(new TypeLiteral<Map<String, BundleReader>>() {})
        x.get("in memory") != null
    }

    def " Using Locale objects, supported locales set, setting defaultLocale also adds to supported locales"() {
        when:

        injector = createInjector(new I18NModule().defaultLocale(Locale.ITALY).supportedLocales(Locale.GERMANY))


        then:

        defaultLocale().equals(Locale.ITALY)
        supportedLocales().contains(Locale.ITALY)
        supportedLocales().contains(Locale.GERMANY)
        supportedLocales().size() == 2

    }


    def "Using Locale language tags, supported locales set, setting defaultLocale also adds to supported locales"() {
        when:

        injector = createInjector(new I18NModule().defaultLocale("it_IT").supportedLocales("de_DE"))


        then:

        defaultLocale().equals(Locale.ITALY)
        supportedLocales().contains(Locale.ITALY)
        supportedLocales().contains(Locale.GERMANY)
        supportedLocales().size() == 2

    }

    def "default to Locale.UK if no locales set"() {
        when:

        injector = createInjector(new I18NModule())


        then:

        defaultLocale().equals(Locale.UK)
        supportedLocales().contains(Locale.UK)
        supportedLocales().size() == 1

    }

    def "Throw an IllegalArgumentException for an invalid Locale language tag in defaultLocale()"() {

        when:
        injector = createInjector(new I18NModule().defaultLocale("rubbish"))

        then:

        thrown IllegalArgumentException
    }

    def "Throw an IllegalArgumentException for an invalid Locale language tag in supportedLocales()"() {

        when:
        injector = createInjector(new I18NModule().supportedLocales("rubbish"))

        then:

        thrown IllegalArgumentException
    }

    def "activeDao not defined, defaults to InMemory"() {
        when:
        injector = createInjector(new I18NModule())

        then:
        activeDao().equals(InMemory.class)
    }

    def "activeDao explicitly set"() {
        when:
        injector = createInjector(new I18NModule().activeDao(SessionBus.class))

        then:
        activeDao().equals(SessionBus.class)
    }

    def "bundle sources explicitly set"() {
        when:
        injector = createInjector(new I18NModule().bundleSource("database", InMemoryBundleReader.class).bundleSource("p", PropertiesFromClasspathBundleReader))

        then:

        bundleSources().size() == 2
        bundleSources().get("database") instanceof InMemoryBundleReader
        bundleSources().get("p") instanceof PropertiesFromClasspathBundleReader
        bundleSourcesOrder().isEmpty()
    }

    def "bundle sources not set, default should be 'class'"() {
        when:
        injector = createInjector(new I18NModule())

        then:

        bundleSources().size() == 1
        bundleSources().get("class") instanceof ClassBundleReader
        bundleSourcesOrder().isEmpty()
    }

    def "set bundles sources default order explicitly"() {
        when:
        injector = createInjector(new I18NModule().bundleSourcesOrderDefault("a", "c", "b"))

        then:

        bundleSourcesOrderDefault().size() == 3
        Iterator<String> iterator = bundleSourcesOrderDefault().iterator()
        iterator.next().equals("a")
        iterator.next().equals("c")
        iterator.next().equals("b")
    }


    def "bundles source order set for individual bundles"() {
        when:
        //noinspection GroovyAssignabilityCheck
        injector = createInjector(new I18NModule().bundleSourcesOrder("Labels", "c", "b").bundleSourcesOrder("Descriptions", "f", "g", "a"))

        then:

        bundleSourcesOrder().get("Labels").size() == 2
        bundleSourcesOrder().get("Descriptions").size() == 3
        Iterator<String> iterator = bundleSourcesOrder().get("Labels").iterator()
        iterator.next().equals("c")
        iterator.next().equals("b")
        Iterator<String> iterator2 = bundleSourcesOrder().get("Descriptions").iterator()
        iterator2.next().equals("f")
        iterator2.next().equals("g")
        iterator2.next().equals("a")
    }

    def Set<String> bundleSourcesOrderDefault() {
        getBinding new TypeLiteral<Set<String>>() {}, BundleSourcesOrderDefault.class
    }

    def Map<String, Set<String>> bundleSourcesOrder() {
        getBinding new TypeLiteral<Map<String, Set<String>>>() {}, BundleSourcesOrder.class
    }


    def Map<String, BundleReader> bundleSources() {
        getBinding new TypeLiteral<Map<String, BundleReader>>() {}
    }


    def Class<? extends Annotation> activeDao() {
        getBinding new TypeLiteral<Class<? extends Annotation>>() {}, DefaultActivePatternDao.class
    }


    def Locale defaultLocale() {
        getBinding new TypeLiteral<Locale>() {}, DefaultLocale.class
    }

    def Set<Locale> supportedLocales() {
        getBinding new TypeLiteral<Set<Locale>>() {}, SupportedLocales.class
    }

    def getBinding(typeLiteral) {
        //noinspection GroovyAssignabilityCheck
        Key<?> key = Key.get(typeLiteral)
        def binding = injector.getBinding(key)
        binding.provider.get()
    }

    def getBinding(typeLiteral, annotationClass) {
        def key = Key.get(typeLiteral, annotationClass)
        def binding = injector.getBinding(key)
        binding.provider.get()
    }


    private Injector createInjector(Module moduleUnderTest) {
        List<Module> modules = new ArrayList<>()
        addSupportingModules(modules);
        modules.add(moduleUnderTest);
        return Guice.createInjector(modules);

    }

    private List<Module> addSupportingModules(List<Module> modules) {
        modules.add(new TestOptionModule())
        modules.add(new VaadinSessionScopeModule())
        modules.add(new UIScopeModule())
        modules.add(new DataModule())
        modules.add(new DefaultShiroModule())
        modules.add(new InMemoryModule().providePatternDao())
        modules.add(new EventBusModule())
        return modules;
    }


}
