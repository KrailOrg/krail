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

package uk.q3c.krail.option.persist;

import uk.q3c.krail.persist.PersistenceInfo;

/**
 * Interface which may be applied to Guice modules or configuration objects, to provide facility for
 * enabling / disabling the provision of an OptionDaoDelegate
 * <p>
 *
 * @param <M> the implementation object, returned for fluency
 *            Created by David Sowerby on 25/06/15.
 */
public interface OptionPersistenceEnabler<M> extends PersistenceInfo<M> {

    /**
     * Binds {@link OptionDaoDelegate} to an implementation, uniquely annotated for that implementation
     *
     * @return this for fluency
     */
    M provideOptionDao();


}
