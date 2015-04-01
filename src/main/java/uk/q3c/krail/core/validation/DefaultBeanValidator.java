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

import com.google.inject.Inject;
import uk.q3c.krail.core.data.KrailEntity;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * An alternative implementation to the Vaadin {@link com.vaadin.data.validator.BeanValidator}, with some code re-used
 * from the original
 * <p>
 * Created by David Sowerby on 04/02/15.
 */
public class DefaultBeanValidator<T extends KrailEntity> implements BeanValidator<T> {

    private final Validator javaxValidator;
    private final KrailInterpolator krailInterpolator;
    private Class<T> beanClass;
    private String propertyName;
    private boolean useFieldNameInValidationMessage;

    @Inject
    public DefaultBeanValidator(Validator javaxValidator, KrailInterpolator krailInterpolator) {
        this.javaxValidator = javaxValidator;
        this.krailInterpolator = krailInterpolator;
    }


    /**
     * Checks the given value against this validator. If the value is valid the method does nothing. If the value is
     * invalid, an {@link InvalidValueException} is thrown.
     *
     * @param value
     *         the value to check
     *
     * @throws InvalidValueException
     *         if the value is invalid
     */
    @Override
    public void validate(Object value) throws InvalidValueException {
        Set<?> violations = javaxValidator.validateValue(beanClass, propertyName, value);
        if (violations.size() > 0) {
            InvalidValueException[] causes = new InvalidValueException[violations.size()];
            int i = 0;
            for (Object v : violations) {
                final ConstraintViolation<?> violation = (ConstraintViolation<?>) v;
                //interpolator will use CurrentLocale
                SimpleContext context = new SimpleContext(propertyName, value, violation.getConstraintDescriptor(),
                        useFieldNameInValidationMessage);
                String msg = krailInterpolator.interpolate(violation.getMessageTemplate(), context);
                causes[i] = new InvalidValueException(msg);
                ++i;
            }

            throw new InvalidValueException(null, causes);
        }
    }

    /**
     * Initialise the validator
     *
     * @param beanClass
     *         the bean class under validation
     * @param propertyName
     *         the property (field) name being validated
     * @param useFieldNameInValidationMessage
     *         if true, include the property name in the final message.  Only used by Krail messages
     */
    @Override
    public void init(Class<T> beanClass, String propertyName, boolean useFieldNameInValidationMessage) {
        this.beanClass = beanClass;
        this.propertyName = propertyName;
        this.useFieldNameInValidationMessage = useFieldNameInValidationMessage;
    }
}
