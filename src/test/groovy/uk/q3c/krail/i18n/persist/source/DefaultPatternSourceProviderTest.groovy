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

package uk.q3c.krail.i18n.persist.source

import com.google.inject.Provider
import org.apache.commons.collections15.ListUtils
import spock.lang.Specification
import uk.q3c.krail.core.i18n.LabelKey
import uk.q3c.krail.core.i18n.i18nModule.TestPatternSource
import uk.q3c.krail.core.i18n.i18nModule.TestPatternSource1
import uk.q3c.krail.i18n.I18NKey
import uk.q3c.krail.i18n.persist.PatternDao
import uk.q3c.krail.i18n.persist.clazz.ClassPatternSource
import uk.q3c.krail.option.Option
import uk.q3c.util.data.collection.AnnotationList

import java.lang.annotation.Annotation

@SuppressWarnings(["GroovyAssignabilityCheck", "GrEqualsBetweenInconvertibleTypes"])
class DefaultPatternSourceProviderTest extends Specification {

    DefaultPatternSourceProvider provider

    Set<Class<? extends Annotation>> sourceOrderDefault
    Map<Class<? extends I18NKey>, LinkedHashSet<Class<? extends Annotation>>> sourceOrderByBundle
    Option option = Mock()
    Map<Class<? extends Annotation>, Provider<PatternDao>> sources
    Map<Class<? extends Annotation>, Provider<PatternDao>> targets
    Provider<PatternDao> classPatternDaoProvider = Mock()
    Provider<PatternDao> testPatternDaoProvider = Mock()

    PatternDao classPatternDao = Mock()
    PatternDao testPatternDao = Mock()

    def setup() {
        sources = new LinkedHashMap<>()
        targets = new LinkedHashMap<>()
        sourceOrderByBundle = new HashMap<>()
        sourceOrderDefault = new LinkedHashSet<>()
        classPatternDaoProvider.get() >> classPatternDao
        testPatternDaoProvider.get() >> testPatternDao
    }


    def "no source order is specified, order should be the as when sources added"() {
        given:
        sources.put(TestPatternSource1.class, testPatternDaoProvider)
        sources.put(ClassPatternSource.class, classPatternDaoProvider)
        sources.put(TestPatternSource.class, testPatternDaoProvider)
        option.get(DefaultPatternSourceProvider.optionKeySourceOrder.qualifiedWith(LabelKey.Yes.bundleName())) >> new AnnotationList()
        option.get(DefaultPatternSourceProvider.optionKeySourceOrderDefault) >> new AnnotationList()
        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)
        LinkedHashSet<Class<? extends Annotation>> expected = new LinkedHashSet<>()
        expected.addAll(TestPatternSource1, ClassPatternSource, TestPatternSource)

        expect:
        provider.orderedSources(LabelKey.Yes) == expected
    }

    def "source order by bundle specified"() {
        sources.put(TestPatternSource1.class, testPatternDaoProvider)
        sources.put(ClassPatternSource.class, classPatternDaoProvider)
        sources.put(TestPatternSource.class, testPatternDaoProvider)

        option.get(DefaultPatternSourceProvider.optionKeySourceOrder.qualifiedWith(LabelKey.Yes.bundleName())) >> new AnnotationList()
        option.get(DefaultPatternSourceProvider.optionKeySourceOrderDefault) >> new AnnotationList()

        LinkedHashSet<Class<? extends Annotation>> expected = new LinkedHashSet<>()
        expected.addAll(ClassPatternSource, TestPatternSource1, TestPatternSource)

        sourceOrderByBundle.put(LabelKey.class, expected)

        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        expect:
        provider.orderedSources(LabelKey.Yes) == expected
    }

    def "default order is specified"() {
        given:
        sources.put(TestPatternSource1.class, testPatternDaoProvider)
        sources.put(ClassPatternSource.class, classPatternDaoProvider)
        sources.put(TestPatternSource.class, testPatternDaoProvider)

        sourceOrderDefault.add(ClassPatternSource)
        sourceOrderDefault.add(TestPatternSource)
        sourceOrderDefault.add(TestPatternSource1)

        option.get(DefaultPatternSourceProvider.optionKeySourceOrder.qualifiedWith(LabelKey.Yes.bundleName())) >> new AnnotationList()
        option.get(DefaultPatternSourceProvider.optionKeySourceOrderDefault) >> new AnnotationList()

        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        LinkedHashSet<Class<? extends Annotation>> expected = new LinkedHashSet<>()
        expected.addAll(ClassPatternSource, TestPatternSource, TestPatternSource1)


        expect:
        provider.orderedSources(LabelKey.Yes) == expected
    }

    def "order specified but not fully should have missing elements added at the end, in the order declared in sources"() {
        given:
        sources.put(TestPatternSource1.class, testPatternDaoProvider)
        sources.put(ClassPatternSource.class, classPatternDaoProvider)
        sources.put(TestPatternSource.class, testPatternDaoProvider)

        sourceOrderDefault.add(ClassPatternSource)

        option.get(DefaultPatternSourceProvider.optionKeySourceOrder.qualifiedWith(LabelKey.Yes.bundleName())) >> new AnnotationList()
        option.get(DefaultPatternSourceProvider.optionKeySourceOrderDefault) >> new AnnotationList()

        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        LinkedHashSet<Class<? extends Annotation>> expected = new LinkedHashSet<>()
        expected.addAll(ClassPatternSource, TestPatternSource, TestPatternSource1)


        expect:
        provider.orderedSources(LabelKey.Yes) == expected
    }

    def "order specified but too many elements, should have non-source elements removed "() {
        given:
        sources.put(TestPatternSource1.class, testPatternDaoProvider)

        sourceOrderDefault.add(ClassPatternSource)
        sourceOrderDefault.add(TestPatternSource)
        sourceOrderDefault.add(TestPatternSource1)

        option.get(DefaultPatternSourceProvider.optionKeySourceOrder.qualifiedWith(LabelKey.Yes.bundleName())) >> new AnnotationList()
        option.get(DefaultPatternSourceProvider.optionKeySourceOrderDefault) >> new AnnotationList()

        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        LinkedHashSet<Class<? extends Annotation>> expected = new LinkedHashSet<>()
        expected.addAll TestPatternSource1


        expect:
        provider.orderedSources(LabelKey.Yes) == expected
    }

    def "source order by bundle specified by option, but missing an element, has missing element added"() {
        given:
        sources.put(TestPatternSource1.class, testPatternDaoProvider)
        sources.put(ClassPatternSource.class, classPatternDaoProvider)
        sources.put(TestPatternSource.class, testPatternDaoProvider)

        AnnotationList fromOption = new AnnotationList(ClassPatternSource.class, TestPatternSource1.class)

        option.get(DefaultPatternSourceProvider.optionKeySourceOrder.qualifiedWith(LabelKey.Yes.bundleName())) >> fromOption
        option.get(DefaultPatternSourceProvider.optionKeySourceOrderDefault) >> new AnnotationList()

        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        LinkedHashSet<Class<? extends Annotation>> expected = new LinkedHashSet<>()
        expected.addAll(ClassPatternSource, TestPatternSource1, TestPatternSource)

        expect:
        provider.orderedSources(LabelKey.Yes) == expected
    }

    def "source order default specified by option, but missing an element, has missing element added"() {
        given:
        sources.put(TestPatternSource1.class, testPatternDaoProvider)
        sources.put(ClassPatternSource.class, classPatternDaoProvider)
        sources.put(TestPatternSource.class, testPatternDaoProvider)

        AnnotationList fromOption = new AnnotationList(ClassPatternSource.class, TestPatternSource1.class)

        option.get(DefaultPatternSourceProvider.optionKeySourceOrder.qualifiedWith(LabelKey.Yes.bundleName())) >> new AnnotationList()
        option.get(DefaultPatternSourceProvider.optionKeySourceOrderDefault) >> fromOption

        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        LinkedHashSet<Class<? extends Annotation>> expected = new LinkedHashSet<>()
        expected.addAll(ClassPatternSource, TestPatternSource1, TestPatternSource)

        expect:
        provider.orderedSources(LabelKey.Yes) == expected
    }

    def "source order by bundle specified by option, but has extra element, and missing element, extra element removed, missing element added"() {
        given:
        sources.put(TestPatternSource.class, testPatternDaoProvider)
        sources.put(TestPatternSource1.class, testPatternDaoProvider)


        AnnotationList fromOption = new AnnotationList(ClassPatternSource.class, TestPatternSource1.class)

        option.get(DefaultPatternSourceProvider.optionKeySourceOrder.qualifiedWith(LabelKey.Yes.bundleName())) >> fromOption
        option.get(DefaultPatternSourceProvider.optionKeySourceOrderDefault) >> new AnnotationList()

        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        LinkedHashSet<Class<? extends Annotation>> expected = new LinkedHashSet<>()
        expected.addAll(TestPatternSource1, TestPatternSource)

        expect:
        provider.orderedSources(LabelKey.Yes) == expected
    }

    def "source order by bundle specified by default, but has extra element, and missing element, extra element removed, missing element added"() {
        given:
        sources.put(TestPatternSource.class, testPatternDaoProvider)
        sources.put(TestPatternSource1.class, testPatternDaoProvider)


        AnnotationList fromOption = new AnnotationList(ClassPatternSource.class, TestPatternSource1.class)

        option.get(DefaultPatternSourceProvider.optionKeySourceOrder.qualifiedWith(LabelKey.Yes.bundleName())) >> new AnnotationList()
        option.get(DefaultPatternSourceProvider.optionKeySourceOrderDefault) >> fromOption

        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        LinkedHashSet<Class<? extends Annotation>> expected = new LinkedHashSet<>()
        expected.addAll(TestPatternSource1, TestPatternSource)

        expect:
        provider.orderedSources(LabelKey.Yes) == expected
    }

    def "targetFor with valid target returns Optional.of(valid dao), invalid target Returns optional.empty"() {

        given:
        targets.put(ClassPatternSource, classPatternDaoProvider)
        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        expect:
        provider.targetFor(ClassPatternSource.class).get() == classPatternDao
        provider.targetFor(TestPatternSource.class) == Optional.empty()
    }

    def "getOption()"() {

        given:
        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        expect:
        provider.optionInstance() == option
    }

    def "optionValueChanged does nothing but need to complete coverage"() {
        given:
        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        expect:
        provider.optionValueChanged(null)
    }

    def "no targets set by option, use those set by Guice"() {
        given:
        targets.put(ClassPatternSource, classPatternDaoProvider)
        targets.put(TestPatternSource, testPatternDaoProvider)
        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        AnnotationList fromOption = new AnnotationList()
        option.get(DefaultPatternSourceProvider.optionKeySelectedTargets) >> fromOption

        LinkedHashSet<Class<? extends Annotation>> expected = new LinkedHashSet<>()
        expected.addAll(ClassPatternSource, TestPatternSource)

        expect:

        ListUtils.isEqualList(provider.selectedTargets().getList(), expected)
    }

    def "targets set by option, override those set by Guice"() {
        given:
        targets.put(ClassPatternSource, classPatternDaoProvider)
        targets.put(TestPatternSource, testPatternDaoProvider)
        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        AnnotationList fromOption = new AnnotationList(TestPatternSource, ClassPatternSource)
        option.get(DefaultPatternSourceProvider.optionKeySelectedTargets) >> fromOption


        expect:

        provider.selectedTargets().size() == fromOption.size()
        provider.selectedTargets().getList().containsAll(fromOption.getList())
    }

    def "targets set by option, override those set by Guice, element not in Guice set is removed"() {
        given:
        targets.put(ClassPatternSource, classPatternDaoProvider)
        targets.put(TestPatternSource, testPatternDaoProvider)
        provider = new DefaultPatternSourceProvider(sources, targets, option, sourceOrderByBundle, sourceOrderDefault)

        AnnotationList fromOption = new AnnotationList(TestPatternSource.class, TestPatternSource1.class, ClassPatternSource.class)
        option.get(DefaultPatternSourceProvider.optionKeySelectedTargets) >> fromOption


        expect:
        provider.selectedTargets().size() == targets.size()
        provider.selectedTargets().getList().containsAll(targets.keySet())

    }
}
