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

package uk.q3c.krail.core.i18n;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.vaadin.data.Property;
import uk.q3c.krail.core.user.opt.AnnotationOptionList;
import uk.q3c.krail.core.user.opt.Option;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation for {@link PatternSourceProvider}
 * <p>
 * Created by David Sowerby on 01/08/15.
 */
public class DefaultPatternSourceProvider implements PatternSourceProvider {


    private final Map<Class<? extends Annotation>, Provider<PatternDao>> sources;
    private final Map<Class<? extends Annotation>, Provider<PatternDao>> targets;
    private final Option option;
    private final Map<Class<? extends I18NKey>, LinkedHashSet<Class<? extends Annotation>>> sourceOrderByBundle;
    private final ImmutableSet<Class<? extends Annotation>> sourceOrderDefault;

    @Inject
    public DefaultPatternSourceProvider(@PatternSources Map<Class<? extends Annotation>, Provider<PatternDao>> sources, @PatternTargets Map<Class<? extends
            Annotation>, Provider<PatternDao>> targets, Option option, @PatternSourceOrderByBundle Map<Class<?
            extends I18NKey>, LinkedHashSet<Class<? extends Annotation>>> sourceOrderByBundle, @PatternSourceOrderDefault Set<Class<? extends Annotation>>
            sourceOrderDefault) {
        this.sources = sources;
        this.targets = targets;
        this.option = option;
        this.sourceOrderByBundle = sourceOrderByBundle;
        this.sourceOrderDefault = ImmutableSet.copyOf(sourceOrderDefault);
    }

    @Override
    @Nonnull
    public Optional<PatternDao> sourceFor(@Nonnull Class<? extends Annotation> sourceAnnotation) {
        Provider<PatternDao> provider = sources.get(sourceAnnotation);
        return (provider == null) ? Optional.empty() : Optional.of(provider.get());
    }

    @Override
    @Nonnull
    public Optional<PatternDao> targetFor(@Nonnull Class<? extends Annotation> targetAnnotation) {
        Provider<PatternDao> provider = targets.get(targetAnnotation);
        return (provider == null) ? Optional.empty() : Optional.of(provider.get());
    }


    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Option getOption() {
        return option;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void optionValueChanged(Property.ValueChangeEvent event) {
        //do nothing options used as required
    }

    /**
     * Returns the order in which sources are processed.  The first non-null of the following is used:
     * <ol>
     * <li>the order returned by{@link #getSourceOrderFromOption(String)} (a value from {@link Option}</li>
     * <li>the order returned by {@link #getSourceOrderDefaultFromOption()}  (a value from {@link Option}</li>
     * <li>{@link #sourceOrderByBundle}, which is defined by {@link I18NModule#sourcesOrderByBundle}</li>
     * <li>{@link #sourceOrderDefault}, which is defined by {@link I18NModule#sourcesDefaultOrder} </li>
     * <li>the keys from {@link #sources} - note that the order for this will be unreliable if PatternDaos have
     * been defined by multiple Guice modules</li>
     * <p>
     * <p>If the source order contains less elements than the number of sources, missing elements are added in the order declared in {@link #sources}<br>
     * If the source order contains more elements than the number of sources, any elements not in {@link #sources} are removed and a warning logged
     * </ol>
     *
     * @param key
     *         used to identify the bundle, from {@link I18NKey#bundleName()}
     *
     * @return a list containing the sources to be processed, in the order that they should be processed
     */
    @Override
    @Nonnull
    public ImmutableSet<Class<? extends Annotation>> orderedSources(@Nonnull I18NKey key) {
        checkNotNull(key);
        AnnotationOptionList sourceOrder = getSourceOrderFromOption(key.bundleName());
        if (!sourceOrder.isEmpty()) {
            return verifySourceOrder(sourceOrder.getList());
        }

        sourceOrder = getSourceOrderDefaultFromOption();
        if (!sourceOrder.isEmpty()) {
            return verifySourceOrder(sourceOrder.getList());
        }

        LinkedHashSet<Class<? extends Annotation>> order = this.sourceOrderByBundle.get(key.getClass());
        if (order != null) {
            return verifySourceOrder(order);
        }

        if (!sourceOrderDefault.isEmpty()) {
            return verifySourceOrder(sourceOrderDefault);
        }

        // just return as defined in sources
        return ImmutableSet.copyOf(sources.keySet());

    }

    /**
     * Checks that source order has the correct number of elements as described in javadoc for {@link #orderedSources}
     *
     * @param sourceOrder
     *         the order as retrieved from configuration
     *
     * @return the finals source order, adjusted if necessary, as described in javadoc for {@link #orderedSources}
     */
    private ImmutableSet<Class<? extends Annotation>> verifySourceOrder(@Nonnull Collection<Class<? extends Annotation>> sourceOrder) {
        // if all and only sources contained, just return
        if (sourceOrder.size() == sources.size() && sourceOrder.containsAll(sources.keySet())) {
            return ImmutableSet.copyOf(sourceOrder);
        }
        LinkedHashSet<Class<? extends Annotation>> newOrder = new LinkedHashSet<>();
        // iterate source order and add to new order only if in sources
        sourceOrder.forEach(a -> {
            if (sources.containsKey(a)) {
                newOrder.add(a);
            }
        });

        // if sizes the same we must have all source elements
        if ((newOrder.size()) == sources.size()) {
            return ImmutableSet.copyOf(newOrder);
        }

        //we have missing sources, so add them to new order
        sources.keySet()
               .forEach(a -> {
                   if (!newOrder.contains(a)) {
                       newOrder.add(a);
                   }
               });

        return ImmutableSet.copyOf(newOrder);
    }

    @Nonnull
    private AnnotationOptionList getSourceOrderFromOption(@Nonnull String bundleName) {
        checkNotNull(bundleName);
        return option.get(optionKeySourceOrder.qualifiedWith(bundleName));
    }

    @Nonnull
    private AnnotationOptionList getSourceOrderDefaultFromOption() {
        return option.get(optionKeySourceOrderDefault);
    }

    @Override
    public AnnotationOptionList selectedTargets() {
        AnnotationOptionList optionTargets = option.get(optionKeySelectedTargets);
        //use a copy to iterate over, otherwise removing from iterated set
        if (!optionTargets.isEmpty()) {
            List<Class<? extends Annotation>> copyTargets = new ArrayList<>(optionTargets.getList());
            optionTargets.getList()
                         .forEach(t -> {
                if (!targets.containsKey(t)) {
                    copyTargets.remove(t);
                }
                         });
            return new AnnotationOptionList(copyTargets);
        }
        return new AnnotationOptionList(Lists.newArrayList(targets.keySet()));
    }


}
