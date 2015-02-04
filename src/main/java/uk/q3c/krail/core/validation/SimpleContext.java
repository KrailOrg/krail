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

package uk.q3c.krail.core.validation;

/**
 * Created by David Sowerby on 06/02/15.
 */

import javax.validation.MessageInterpolator;
import javax.validation.metadata.ConstraintDescriptor;
import java.io.Serializable;

public class SimpleContext implements MessageInterpolator.Context, Serializable {

    private final Object value;
    private final ConstraintDescriptor<?> descriptor;
    private String propertyName;
    private boolean useFieldNamesInMessages;

    /**
     * Create a simple immutable message interpolator context.
     *
     * @param propertyName
     * @param value
     *         value being validated
     * @param descriptor
     *         ConstraintDescriptor corresponding to the constraint being
     */
    public SimpleContext(String propertyName, Object value, ConstraintDescriptor<?> descriptor, boolean
            useFieldNamesInMessages) {
        this.propertyName = propertyName;
        this.value = value;
        this.descriptor = descriptor;
        this.useFieldNamesInMessages = useFieldNamesInMessages;
    }

    public boolean isUseFieldNamesInMessages() {
        return useFieldNamesInMessages;
    }

    public void setUseFieldNamesInMessages(boolean useFieldNamesInMessages) {
        this.useFieldNamesInMessages = useFieldNamesInMessages;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return descriptor;
    }

    @Override
    public Object getValidatedValue() {
        return value;
    }

}
