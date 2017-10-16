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

package uk.q3c.krail.core.ui;

import com.vaadin.ui.AbstractField;

/**
 * Maps data types to the UI components to use to display them.  Note that the {@link #componentFor} methods are untyped.
 * This is because converters are used between the data type and the presentation type of the Field
 * <p>
 * Created by David Sowerby on 28/05/15.
 */
public interface DataTypeToUI {
    AbstractField componentFor(Class<?> dataType);

    AbstractField componentFor(Object dataObject);

    <T> T zeroValue(Class<T> aClass);
}
