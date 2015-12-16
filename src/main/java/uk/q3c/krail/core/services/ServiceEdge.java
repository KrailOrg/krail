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

package uk.q3c.krail.core.services;

import javax.annotation.concurrent.Immutable;

/**
 * Graph edge describing the relationship between services.  An instance of this edge describes a dependency, and is
 * assumed to be directed from the dependant (the successor) to the service upon which it depends (the predecessor).
 * <p>
 * Created by David Sowerby on 25/10/15.
 */
@Immutable
public class ServiceEdge {

    private final Dependency.Type type;

    public ServiceEdge(Dependency.Type type) {
        this.type = type;
    }

    public Dependency.Type getType() {
        return type;
    }

    public boolean requiredOnlyAtStart() {
        return type == Dependency.Type.REQUIRED_ONLY_AT_START;
    }


    public boolean optional() {
        return type == Dependency.Type.OPTIONAL;
    }

    public boolean alwaysRequired() {
        return type == Dependency.Type.ALWAYS_REQUIRED;
    }
}