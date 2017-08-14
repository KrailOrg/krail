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

package uk.q3c.krail.core.validation;

import uk.q3c.krail.i18n.EnumResourceBundle;

import static uk.q3c.krail.core.validation.ValidationKey.*;

/**
 * English patterns for Apache Bval validation messages
 * <p>
 * Created by David Sowerby on 14/07/15.
 */
public class Validations extends EnumResourceBundle<ValidationKey> {


    @Override
    protected void loadMap() {
        put(Null, "must be null");
        put(NotNull, "may not be null");
        put(AssertTrue, "must be true");
        put(AssertFalse, "must be false");
        put(Min, "must be greater than or equal to {0}");
        put(Max, "must be less than or equal to {0}");
        put(Size, "size must be between {0} and {1}");
        put(Digits, "numeric value out of bounds (<{0} digits>.<{1} digits> expected)");
        put(Past, "must be a past date");
        put(Future, "must be a future date");
        put(Pattern, "must match the following regular expression: {0}");
        put(DecimalMax, "must be less than or equal to {0}");
        put(DecimalMin, "must be greater than or equal to {0}");
        put(NotEmpty, "may not be empty");
        put(Email, "not a well-formed email address");
    }
}

    