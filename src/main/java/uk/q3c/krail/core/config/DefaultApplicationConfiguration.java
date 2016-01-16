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
package uk.q3c.krail.core.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * As this is a Singleton, it should be threadsafe.  However, the work to make it so has not been done as version 2.0 of Apache Commons Configuration
 * provides that facility - but has not yet been released.  At the time of writing this it was at 2.0-beta2, but not published to Maven central.  See:
 * <p>
 * https://commons.apache.org/proper/commons-configuration/userguide/howto_concurrency.html
 */
@NotThreadSafe
@Singleton
public class DefaultApplicationConfiguration extends InheritingConfiguration implements ApplicationConfiguration {
    @SuppressFBWarnings("SCII_SPOILED_CHILD_INTERFACE_IMPLEMENTOR")
    @Inject
    protected DefaultApplicationConfiguration() {
        super();
    }

}
