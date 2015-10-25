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

/**
 * Used to identify a {@link Service} which uses the {@link Dependency} annotation.  Annotation scanning is applied in
 * the {@link ServicesModule}.  Scanning is applied only to instances which implement this interface - it could be
 * applied to all {@link Service} instances, but that would incur unnecessary overhead for developers who prefer to use
 * Guice to configure dependencies
 * <p>
 * Created by David Sowerby on 12/11/15.
 */
public interface ServiceUsingDependencyAnnotation extends Service {
}
