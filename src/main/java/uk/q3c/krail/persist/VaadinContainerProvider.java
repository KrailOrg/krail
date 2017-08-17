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

package uk.q3c.krail.persist;

/**
 * Common interface for all providers of Vaadin Container instances - note that param <C> is not typed - we want to avoid a dependency on Vaadin directly
 *
 * Although this is Vaadin specific, it resides in the persist package to enable various persistence implementations to work with Krail.  There is no need to implement this interface
 * if there is no requirement for use with Vaadin, and the need for it will disappear when Krail is updated to Vaadin 8
 *
 * <p>
 * Created by David Sowerby on 29/06/15.
 */
public interface VaadinContainerProvider<C> {

    <E> C get(Class<E> entityClass, ContainerType containerType);
}
