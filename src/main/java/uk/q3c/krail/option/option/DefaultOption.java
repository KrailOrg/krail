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

package uk.q3c.krail.option.option;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.q3c.krail.option.Option;
import uk.q3c.krail.option.OptionPermissionVerifier;
import uk.q3c.krail.option.UserHierarchy;
import uk.q3c.krail.option.UserHierarchyDefault;
import uk.q3c.krail.option.persist.OptionCache;
import uk.q3c.krail.option.persist.OptionDaoDelegate;
import uk.q3c.krail.option.persist.cache.DefaultOptionCacheLoader;

/**
 * * <b>NOTE:</b> All values to and from {@link Option} are natively typed.  All values to and from {@link OptionCache}, {@link DefaultOptionCacheLoader} and
 * {@link OptionDaoDelegate} are wrapped in Optional.
 */

public class DefaultOption extends OptionBase {
    private static Logger log = LoggerFactory.getLogger(DefaultOption.class);

    @Inject
    public DefaultOption(OptionCache optionCache, @UserHierarchyDefault UserHierarchy hierarchy, OptionPermissionVerifier permissionVerifier) {
        super(optionCache, hierarchy, permissionVerifier);
    }
}
