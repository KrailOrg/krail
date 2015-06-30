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

package uk.q3c.krail.core.sysadmin;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.vaadin.data.Container;
import uk.q3c.krail.core.persist.ContainerType;
import uk.q3c.krail.core.persist.OptionDaoProviders;
import uk.q3c.krail.core.persist.PatternDaoProviders;
import uk.q3c.krail.core.persist.VaadinContainerProvider;
import uk.q3c.krail.core.user.opt.OptionEntity;
import uk.q3c.krail.core.user.opt.OptionException;
import uk.q3c.krail.i18n.I18NException;
import uk.q3c.krail.i18n.PatternEntity;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Default implementation for {@link CombinedContainerProvider}, using an injected {@link Injector}
 * <p>
 * Created by David Sowerby on 30/06/15.
 */
public class DefaultCombinedContainerProvider implements CombinedContainerProvider {


    private Injector injector;
    private Set<Class<? extends Annotation>> optionDaoProviders;
    private Set<Class<? extends Annotation>> patternDaoProviders;

    @Inject
    protected DefaultCombinedContainerProvider(Injector injector, @OptionDaoProviders Set<Class<? extends Annotation>> optionDaoProviders,
                                               @PatternDaoProviders Set<Class<? extends Annotation>> patternDaoProviders) {
        this.injector = injector;
        this.optionDaoProviders = optionDaoProviders;
        this.patternDaoProviders = patternDaoProviders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E> Container getContainer(@Nonnull Class<? extends Annotation> annotationClass, @Nonnull Class<E> entityClass) {
        if (!entityClass.equals(PatternEntity.class) && (!entityClass.equals(OptionEntity.class))) {
            throw new UnsupportedOperationException("Only OptionEntity or PatternEntity may be use with " + this.getClass()
                                                                                                                .getSimpleName());
        }
        if (entityClass.equals(PatternEntity.class)) {
            if (patternDaoProviders.contains(annotationClass)) {
                Key containerProviderKey = Key.get(VaadinContainerProvider.class, annotationClass);
                VaadinContainerProvider provider = (VaadinContainerProvider) injector.getInstance(containerProviderKey);
                return provider.get(entityClass, ContainerType.CACHED);
            } else {
                throw new I18NException("There is no I18N Pattern support with an annotation of " + annotationClass);
            }
        }

        //must be an OptionEntity
        if (optionDaoProviders.contains(annotationClass)) {
            Key containerProviderKey = Key.get(VaadinContainerProvider.class, annotationClass);
            VaadinContainerProvider provider = (VaadinContainerProvider) injector.getInstance(containerProviderKey);
            return provider.get(entityClass, ContainerType.CACHED);
        } else {
            throw new OptionException("There is no Option support with an annotation of " + annotationClass);
        }


    }
}
