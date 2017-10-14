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

package uk.q3c.krail.core.ui;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by David Sowerby on 28/05/15.
 */
public class DefaultDataTypeToUI implements DataTypeToUI {

    @Override
    public AbstractField componentFor(Object dataObject) {
        Class modelClass = dataObject.getClass();
        return componentFor(modelClass);
    }

    @Override
    public AbstractField componentFor(Class<?> dataType) {
        if (dataType.equals(LocalDate.class)) {
            return new DateField();
        }
        if (dataType.equals(boolean.class) || dataType.equals(Boolean.class)) {
            return new CheckBox();
        }
        return new TextField();
    }

    @SuppressFBWarnings("URV_INHERITED_METHOD_WITH_RELATED_TYPES") // cannot really change this
    @SuppressWarnings("unchecked")
    @Override
    public <T> T zeroValue(Class<T> dataType) {
        if (dataType.equals(LocalDate.class)) {
            return (T) LocalDate.now();
        }
        if (dataType.equals(boolean.class) || dataType.equals(Boolean.class)) {
            return (T) Boolean.FALSE;
        }
        if (dataType.equals(Integer.class) || dataType.equals(int.class)) {
            return (T) Integer.valueOf(0);
        }
        if (dataType.equals(LocalDateTime.class)) {
            return (T) LocalDateTime.now();
        }
        throw new UnsupportedOperationException("data type not supported: " + dataType);
    }
}
