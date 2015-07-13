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

import static uk.q3c.krail.core.validation.ValidationKey.*;

/**
 * Italian patterns for Apache Bval validation messages
 * <p>
 * Created by David Sowerby on 14/07/15.
 */
public class Validations_it extends Validations {

    @Override
    protected void loadMap() {

        put(Null, "deve essere null");
        put(NotNull, "non pu\u00F2 essere null");
        put(AssertTrue, "deve essere true");
        put(AssertFalse, "deve essere false");
        put(Min, "deve essere minore o uguale di {0}");
        put(Max, "deve essere maggiore o uguale di {0}");
        put(Size, "le dimensioni devono essere tra {0} e {1}");
        put(Digits, "valore numerico fuori dai limiti (atteso <{0} cifre>.<{1} cifre>)");
        put(Past, "deve essere una data nel passato");
        put(Future, "deve essere una data futura");
        put(Pattern, "deve corrispondere all'espressione regolare '{0}'");
        put(DecimalMax, "deve essere maggiore o uguale di {0}");
        put(DecimalMin, "deve essere minore o uguale di {0}");
        put(NotEmpty, "non pu\u00F2 essere vuoto");
        put(Email, "non \u00E8 un indirizzo email ben formato");
    }


}
