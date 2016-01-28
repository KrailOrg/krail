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

import com.vaadin.data.Property;

import javax.annotation.Nonnull;

/**
 * Any context using {@link Option} is required to implement this interface, it is used to identify the class which is
 * using an option, and forms part of a key to define that option
 * <p>
 * Created by David Sowerby on 01/12/14.
 */
public interface OptionContext {

    /**
     * Returns the {@link Option} instance being used by this context
     *
     * @return the {@link Option} instance being used by this context
     */
    @Nonnull
    Option getOption();

    /**
     * Called when an option value is changed in response to a property change - usually a Field which has been modified by the user
     *
     * @param event the event representing the change
     */
    void optionValueChanged(Property.ValueChangeEvent event);


}
