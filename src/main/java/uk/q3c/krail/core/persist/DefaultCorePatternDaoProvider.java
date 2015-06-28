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
import uk.q3c.krail.core.config.ConfigurationException;
import uk.q3c.krail.i18n.PatternDao;
import uk.q3c.util.MessageFormat;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Default implementation for {@link CorePatternDaoProvider}.
 * <p>
 * Created by David Sowerby on 26/06/15.
 */
public class DefaultCorePatternDaoProvider implements CorePatternDaoProvider {

    private Class<? extends Annotation> activeDaoAnnotation;
    private Injector injector;
    private Set<Class<? extends Annotation>> patternDaoProviders;

    @Inject
    protected DefaultCorePatternDaoProvider(Injector injector, @PatternDaoProviders Set<Class<? extends Annotation>> patternDaoProviders, @ActivePatternDao
    Class<? extends Annotation> activeDaoAnnotation) {
        this.injector = injector;
        this.patternDaoProviders = patternDaoProviders;
        this.activeDaoAnnotation = activeDaoAnnotation;
    }

    @Override
    public PatternDao get() {
        if (!patternDaoProviders.contains(activeDaoAnnotation)) {
            String msg = MessageFormat.format("The default PatternDao annotation of '{0}' does not match any of the providers.  Do you need to change " +
                    "I18NModule.activeDao() ?", activeDaoAnnotation.getSimpleName());
            throw new ConfigurationException(msg);
        }
        Key<PatternDao> activeDaoKey = Key.get(PatternDao.class, activeDaoAnnotation);
        return injector.getInstance(activeDaoKey);
    }
}
