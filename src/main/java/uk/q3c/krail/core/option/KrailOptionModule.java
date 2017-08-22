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

package uk.q3c.krail.core.option;

import uk.q3c.krail.core.guice.vsscope.VaadinSessionScoped;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.OptionPermissionVerifier;
import uk.q3c.krail.option.bind.OptionModule;
import uk.q3c.krail.option.persist.OptionCache;
import uk.q3c.krail.option.persist.cache.DefaultOptionCache;

/**
 * Configures the use of {@link Option}
 * <p>
 * Created by David Sowerby on 16/11/14.
 */
public class KrailOptionModule extends OptionModule {

    @Override
    protected void configure() {
        super.configure();
        bindVaadinOptionSource();
        bindOptionPopup();
    }

    @Override
    protected void bindPermissionVerifier() {
        bind(OptionPermissionVerifier.class).to(KrailOptionPermissionVerifier.class);

    }

    protected void bindVaadinOptionSource() {
        bind(VaadinOptionSource.class).to(DefaultVaadinOptionSource.class);
    }


    /**
     * Override this method to provide your own {@link OptionPopup} implementation
     */
    protected void bindOptionPopup() {
        bind(OptionPopup.class).to(DefaultOptionPopup.class);
    }

    @Override
    protected void bindOptionCache() {
        bind(OptionCache.class).to(DefaultOptionCache.class).in(VaadinSessionScoped.class);
    }
}
