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

package uk.q3c.krail.core.persist.common.common;

import uk.q3c.krail.core.i18n.PatternDao;
import uk.q3c.krail.core.user.opt.OptionDao;

/**
 * Interface which may be applied to Guice modules or configuration objects, to provide persistence to the Krail core.
 * <p>
 *
 * @param <M>
 *         the implementation object, returned for fluency
 *         Created by David Sowerby on 25/06/15.
 */
public interface KrailPersistenceUnit<M> extends PersistenceInfo<M> {

    //    /**
    //     * Binds {@link OptionDao} to an implementation, annotated with {@link CoreDao}.  This may occur only once per application
    //     *
    //     * @return this for fluency
    //     */
    //    M provideCoreOptionDao();

    /**
     * Binds {@link OptionDao} to an implementation, uniquely annotated for that implementation
     *
     * @return this for fluency
     */
    M provideOptionDao();

    //    /**
    //     * Binds {@link PatternDao} to an implementation, annotated with {@link CoreDao}. This may occur only once per application
    //     *
    //     * @return this for fluency
    //     */
    //    M provideCorePatternDao();

    /**
     * Binds {@link PatternDao} to an implementation, uniquely annotated for that implementation
     *
     * @return this for fluency
     */
    M providePatternDao();
}
