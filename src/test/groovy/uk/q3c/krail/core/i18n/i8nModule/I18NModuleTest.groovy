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

package uk.q3c.krail.core.i18n.i8nModule

import com.google.inject.Module
import com.google.inject.Provider
import com.google.inject.TypeLiteral
import uk.q3c.krail.core.eventbus.EventBusModule
import uk.q3c.krail.core.guice.GuiceModuleTestBase
import uk.q3c.krail.core.guice.uiscope.UIScopeModule
import uk.q3c.krail.core.guice.vsscope.VaadinSessionScopeModule
import uk.q3c.krail.core.i18n.*
import uk.q3c.krail.core.i18n.i18nModule.TestPatternSource
import uk.q3c.krail.core.option.InMemory
import uk.q3c.krail.core.persist.clazz.i18n.ClassPatternDao
import uk.q3c.krail.core.persist.clazz.i18n.ClassPatternSource
import uk.q3c.krail.core.persist.common.i18n.PatternDao
import uk.q3c.krail.core.persist.inmemory.common.InMemoryModule
import uk.q3c.krail.core.persist.inmemory.i18n.InMemoryPatternDao
import uk.q3c.krail.core.shiro.DefaultShiroModule
import uk.q3c.krail.core.vaadin.DataModule
import uk.q3c.krail.testutil.option.TestOptionModule
import uk.q3c.util.UtilModule

import java.lang.annotation.Annotation

/**
 * Unit tests for {@link I18NModule}
 *
 * Created by David Sowerby on 21/07/15.
 */
class I18NModuleTest extends GuiceModuleTestBase {




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

    def "I18N targets explicitly set"() {
        when:
        injector = createInjector(new I18NModule().target(ClassPatternSource.class).target(InMemory.class))

        then:

        targets().size() == 2
        targets().get(InMemory.class).get() instanceof InMemoryPatternDao
        targets().get(ClassPatternSource.class).get() instanceof ClassPatternDao
    }

    def "I18N targets not set, should be an empty map"() {
        when:
        injector = createInjector(new I18NModule())

        then:

        targets().size() == 0
    }


    def "I18N sources explicitly set"() {
        when:
        injector = createInjector(new I18NModule().source(ClassPatternSource.class).source(InMemory.class))

        then:

        sources().size() == 2
        sources().get(InMemory.class).get() instanceof InMemoryPatternDao
        sources().get(ClassPatternSource.class).get() instanceof ClassPatternDao
        sourcesOrderByBundle().isEmpty()
    }

    def "I18N sources not set, default should be 'class'"() {
        when:
        injector = createInjector(new I18NModule())

        then:

        sources().size() == 1
        sources().get(ClassPatternSource.class).get() instanceof ClassPatternDao
        sourcesOrderByBundle().isEmpty()
    }

    def "I18N sources default order explicitly set"() {
        when:
        injector = createInjector(new I18NModule().sourcesDefaultOrder(TestPatternSource.class, ClassPatternSource.class, InMemory.class))

        then:

        sourcesDefaultOrder().size() == 3
        Iterator<Class<? extends Annotation>> iterator = sourcesDefaultOrder().iterator()
        iterator.next().equals(TestPatternSource.class)
        iterator.next().equals(ClassPatternSource.class)
        iterator.next().equals(InMemory.class)
    }


    def "I18N source order set for individual bundles (key classes)"() {
        when:
        injector = createInjector(new I18NModule().sourcesOrderByBundle(LabelKey.class, TestPatternSource.class, ClassPatternSource.class).sourcesOrderByBundle(DescriptionKey.class, InMemory.class, TestPatternSource.class, ClassPatternSource.class))

        then:

        sourcesOrderByBundle().get(LabelKey.class).size() == 2
        sourcesOrderByBundle().get(DescriptionKey.class).size() == 3
        Iterator<Class<? extends Annotation>> iterator = sourcesOrderByBundle().get(LabelKey.class).iterator()
        iterator.next().equals(TestPatternSource.class)
        iterator.next().equals(ClassPatternSource.class)
        Iterator<Class<? extends Annotation>> iterator2 = sourcesOrderByBundle().get(DescriptionKey.class).iterator()
        iterator2.next().equals(InMemory.class)
        iterator2.next().equals(TestPatternSource.class)
        iterator2.next().equals(ClassPatternSource.class)
    }


    Set<Class<? extends Annotation>> sourcesDefaultOrder() {
        getBinding new TypeLiteral<Set<Class<? extends Annotation>>>() {}, PatternSourceOrderDefault.class
    }

    Map<Class<? extends I18NKey>, Set<Class<? extends Annotation>>> sourcesOrderByBundle() {
        getBinding new TypeLiteral<Map<Class<? extends I18NKey>, LinkedHashSet<Class<? extends Annotation>>>>() {}, PatternSourceOrderByBundle.class
    }

    Map<Class<? extends Annotation>, Provider<PatternDao>> sources() {
        getBinding new TypeLiteral<Map<Class<? extends Annotation>, Provider<PatternDao>>>() {}, PatternSources.class
    }

    Map<Class<? extends Annotation>, Provider<PatternDao>> targets() {
        getBinding new TypeLiteral<Map<Class<? extends Annotation>, Provider<PatternDao>>>() {}, PatternTargets.class
    }


    Locale defaultLocale() {
        getBinding new TypeLiteral<Locale>() {}, DefaultLocale.class
    }

    Set<Locale> supportedLocales() {
        getBinding new TypeLiteral<Set<Locale>>() {}, SupportedLocales.class
    }


    List<Module> addSupportingModules(List<Module> modules) {
        modules.add(new TestOptionModule())
        modules.add(new VaadinSessionScopeModule())
        modules.add(new UIScopeModule())
        modules.add(new DataModule())
        modules.add(new DefaultShiroModule())
        modules.add(new InMemoryModule().providePatternDao())
        modules.add(new EventBusModule())
        modules.add(new UtilModule())

        return modules
    }


}
