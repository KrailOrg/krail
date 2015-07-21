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

package uk.q3c.krail.core.persist;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.vaadin.data.Container;
import uk.q3c.krail.core.config.ConfigurationException;
import uk.q3c.krail.core.user.opt.OptionDao;
import uk.q3c.util.MessageFormat;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation for {@link OptionSource}.
 * <p>
 * Created by David Sowerby on 26/06/15.
 */
public class DefaultOptionSource implements OptionSource {

    private Class<? extends Annotation> activeSource;
    private Injector injector;
    private Map<Class<? extends Annotation>, PersistenceInfo<?>> optionDaoProviders;

    @Inject
    protected DefaultOptionSource(Injector injector, @OptionDaoProviders Map<Class<? extends Annotation>, PersistenceInfo<?>> optionDaoProviders,
                                  @DefaultActiveOptionSource Class<? extends Annotation> activeSource) {
        this.injector = injector;
        this.optionDaoProviders = optionDaoProviders;
        this.activeSource = activeSource;
    }

    @Override
    public OptionDao getActiveDao() {
        return getDao(activeSource);
    }

    @Override
    @Nonnull
    public OptionDao getDao(@Nonnull Class<? extends Annotation> annotationClass) {
        checkAnnotationKey(annotationClass);
        Key<OptionDao> activeDaoKey = Key.get(OptionDao.class, annotationClass);
        return injector.getInstance(activeDaoKey);
    }

    protected void checkAnnotationKey(Class<? extends Annotation> annotationClass) {
        checkNotNull(annotationClass);
        if (!optionDaoProviders.keySet()
                               .contains(annotationClass)) {
            String msg = MessageFormat.format("The OptionDao annotation of '{0}' does not match any of the providers.", annotationClass.getSimpleName());
            throw new ConfigurationException(msg);
        }
    }

    @Override
    public PersistenceInfo getActivePersistenceInfo() {
        return getPersistenceInfo(activeSource);
    }

    @Override
    @Nonnull
    public PersistenceInfo getPersistenceInfo(@Nonnull Class<? extends Annotation> annotationClass) {
        checkAnnotationKey(annotationClass);
        return optionDaoProviders.get(annotationClass);
    }

    @Override
    public Class<? extends Annotation> getActiveSource() {
        return activeSource;
    }

    @Override
    public void setActiveSource(@Nonnull Class<? extends Annotation> activeSource) {
        this.activeSource = activeSource;
    }

    @Override
    public Container getContainer(@Nonnull Class<? extends Annotation> annotationClass) {
        checkAnnotationKey(annotationClass);
        Key<OptionContainerProvider> containerProviderKey = Key.get(OptionContainerProvider.class, annotationClass);
        OptionContainerProvider provider = injector.getInstance(containerProviderKey);
        return provider.get();
    }


}
