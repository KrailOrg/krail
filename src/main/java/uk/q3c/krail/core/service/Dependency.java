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
package uk.q3c.krail.core.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation specifies dependencies between service instances and is valid only when applied to the field of Service
 *
 * <em>Note:</em>In order for @Dependency
 * The annotation It is captured by the {@link * ServiceDependencyScanner} and its values translated for use with the {@link ServicesGraph}
 * <p>
 *
 * @author David Sowerby
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Dependency {

    enum Type {ALWAYS_REQUIRED, REQUIRED_ONLY_AT_START, OPTIONAL}
    Type type = Type.ALWAYS_REQUIRED;

    /**
     * If false, this dependency is required to be running. Its full meaning qualified by {@link #always()}.  If
     * true, the dependency is optional.  See {@link ServicesModel#optionallyUses(ServiceKey, ServiceKey)}
     *
     * @return If false, this dependency is required to be running. Its full meaning qualified by {@link #always()}.  If
     * true, the dependency is optional.  See {@link ServicesModel#optionallyUses(ServiceKey, ServiceKey)}
     */
    boolean optional() default false;

    /**
     * This value is ignored if {@link #optional()} is true.<br><br>If {@link #optional()} is false, and this value is true, this dependency is required
     * to be running at all times in order for the declaring service to run.  See {@link
     * ServicesModel#alwaysDependsOn(ServiceKey, ServiceKey)}. If {@link #optional()} is false, and this value is false, this dependency is required only
     * in order to start the declaring service. See {@link ServicesModel#requiresOnlyAtStart(ServiceKey, ServiceKey)}.
     *
     * @return If true, the dependency is always required, otherwise it is required only to start the declaring service.
     */
    boolean always() default true;


}
