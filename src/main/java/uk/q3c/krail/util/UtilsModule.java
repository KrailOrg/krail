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

package uk.q3c.krail.util;

import com.google.inject.AbstractModule;
import uk.q3c.krail.core.view.component.ComponentIdGenerator;
import uk.q3c.krail.core.view.component.DefaultComponentIdGenerator;

/**
 * Bindings fo utility classes
 * <p>
 * Created by David Sowerby on 03 Jan 2016
 */
public class UtilsModule extends AbstractModule {

    @Override
    protected void configure() {
        bindResourceUtils();
        bindIdGenerator();
    }

    protected void bindIdGenerator() {
        bind(ComponentIdGenerator.class).to(DefaultComponentIdGenerator.class);
    }


    /**
     * Override this method to provide your own {@link ResourceUtils} binding
     */
    protected void bindResourceUtils() {
        bind(ResourceUtils.class).to(DefaultResourceUtils.class);
    }


}
