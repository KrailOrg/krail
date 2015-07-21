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
 * Simple implementation of MessageInterpolator.Context
 *
 * Created by David Sowerby on 06/02/15.
 */

import javax.validation.MessageInterpolator;
import javax.validation.ValidationException;
import javax.validation.metadata.ConstraintDescriptor;
import java.io.Serializable;

public class SimpleContext implements MessageInterpolator.Context, Serializable {

    private final Object value;
    private final ConstraintDescriptor<?> descriptor;
    private String propertyName;

    /**
     * Create a simple immutable message interpolator context.
     *
     * @param propertyName
     * @param value
     *         value being validated
     * @param descriptor
     *         ConstraintDescriptor corresponding to the constraint being
     */
    public SimpleContext(String propertyName, Object value, ConstraintDescriptor<?> descriptor) {
        this.propertyName = propertyName;
        this.value = value;
        this.descriptor = descriptor;
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

    /**
     * Returns an instance of the specified type allowing access to
     * provider-specific APIs. If the Bean Validation provider
     * implementation does not support the specified class,
     * {@link ValidationException} is thrown.
     *
     * @param type
     *         the class of the object to be returned
     *
     * @return an instance of the specified class
     *
     * @throws ValidationException
     *         if the provider does not support the call
     * @since 1.1
     */
    // TODO implement
    //    @Override
    //    public <T> T unwrap(Class<T> type) {
    //        throw new RuntimeException("Added by later version of validation  API, not yet implemented");
    //    }

}
