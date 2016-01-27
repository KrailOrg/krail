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

package uk.q3c.krail.core.i18n;

/**
 * Implementations load the I18N pattern cache from potentially multiple sources.  Configuration of the order in which sources are accessed is held in {@link
 * I18NModule} and presented to consumers (such as implementations of this interface) via {@link PatternSourceProvider}
 *
 * <b>Note:</b>  A source is generally equivalent to a persistence unit (PU), although class and property file based sources are not conventional persistence
 * units.  A source is represented by an Annotation
 * <p>
 * Created by David Sowerby on 08/12/14.
 */
public interface PatternCacheLoader {


}
