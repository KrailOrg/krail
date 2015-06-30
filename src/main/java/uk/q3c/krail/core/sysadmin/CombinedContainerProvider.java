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

import com.vaadin.data.Container;
import uk.q3c.krail.core.persist.VaadinContainerProvider;
import uk.q3c.krail.core.user.opt.InMemory;

import java.lang.annotation.Annotation;

/**
 * Provides a single point from which to obtain a Vaadin {@link Container} for Option or Pattern regardless of which persistence source(s) is in use.
 * However, implementations are likely to need to to use a Guice Injector directly, and this class should not be used in a general way - that is why it is in
 * the sys admin package.  Generally you should inject {@link VaadinContainerProvider} annotated to identify the persistence source.
 * <p>
 * Created by David Sowerby on 30/06/15.
 */
public interface CombinedContainerProvider {

    /**
     * Return a container for the persistence source identified by {@code annotationClass}, for the provided {@code entityClass}
     *
     * @param annotationClass
     *         the annotationClaass which identifies the persistence source, for example {@link InMemory}
     * @param entityClass
     *         the class of the entity the container should manage - either PatternEntity or OptionEntity only
     * @param <E>
     *         the entity type
     *
     * @return a container instance
     *
     * @throws UnsupportedOperationException
     *         if called with an entityClass which is not PatternEntity or OptionEntity
     * @throws @OptionException
     *         if there is no Option support for {@code annotationClass}
     * @throws @I18NException
     *         if there is no Pattern support for {@code annotationClass}
     */
    <E> Container getContainer(Class<? extends Annotation> annotationClass, Class<E> entityClass);
}
