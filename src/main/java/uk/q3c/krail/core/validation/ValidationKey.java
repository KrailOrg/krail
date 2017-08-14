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

import uk.q3c.krail.i18n.I18NKey;

/**
 * {@link I18NKey} for Apache Bval validation messages.  The messages themselves have been transposed from BVal to {@link Validations}, with some
 * translations (See sub-classes of {@link Validations}}
 * <p>
 * Created by David Sowerby on 14/07/15.
 */
public enum ValidationKey implements I18NKey {

    Null,
    NotNull,
    AssertTrue,
    AssertFalse,
    Min,
    Max,
    Size,
    Digits,
    Past,
    Future,
    Pattern,
    DecimalMax,
    DecimalMin,
    NotEmpty,
    Email

}
