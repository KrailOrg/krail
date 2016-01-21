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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Identifies code which should be considered Experimental - it is not thoroughly tested, will probably change a lot, and may even disappear altogether.  On
 * the
 * other hand it may evolve into something really useful!
 * <p>
 * Created by David Sowerby on 13/07/15.
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Experimental {
}
