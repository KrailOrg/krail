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

package uk.q3c.krail.core.eventbus;

import net.engio.mbassy.listener.Listener;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Overrides default behaviour for subscribing a {@link Listener} to an Event Bus of a specific scope (see {@link EventBusModule} for description of default
 * behaviour).  The only valid values are {@link UIBus}, {@link SessionBus} and {@link GlobalBus}.  Any other values are silently ignored.
 * <p>
 * Because this annotation overrides default behaviour, if this annotation has no values, then the {@link Listener} will not be subscribed to any bus.
 * <p>
 * Created by David Sowerby on 09/03/15.
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface SubscribeTo {

    Class<? extends Annotation>[] value() default {};
}
