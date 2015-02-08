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
 * Created by David Sowerby on 05/02/15.
 */

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element must be a number whose value must be lower or
 * equal to the specified maximum.
 * <p>
 * Supported types are:
 * <ul>
 * <li><code>BigDecimal</code></li>
 * <li><code>BigInteger</code></li>
 * <li><code>byte</code>, <code>short</code>, <code>int</code>, <code>long</code>,
 * and their respective wrappers</li>
 * </ul>
 * Note that <code>double</code> and <code>float</code> are not supported due to rounding errors
 * (some providers might provide some approximative support)
 * <p>
 * <code>null</code> elements are considered valid
 *
 * @author Emmanuel Bernard
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {MaxValidatorForNumber.class})
public @interface Max {
    TestValidationKey messageKey() default TestValidationKey.Too_Big;

    String message() default "krail";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return value the element must be lower or equal to
     */
    long value();

    /**
     * Defines several <code>@Max</code> annotations on the same element
     *
     * @author Emmanuel Bernard
     * @see Max
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        Max[] value();
    }
}
