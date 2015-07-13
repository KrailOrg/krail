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
 * German patterns for Apache Bval validation messages
 * <p>
 * Created by David Sowerby on 14/07/15.
 */
public class Validations_de extends Validations {

    @Override
    protected void loadMap() {
        put(Null, "muss'null'sein");
        put(NotNull, "darf nicht'null'sein");
        put(AssertTrue, "muss'true'sein");
        put(AssertFalse, "muss'false'sein ");
        put(Min, "muss gr\u00F6\u00DFer oder gleich {0} sein");
        put(Max, "muss kleiner oder gleich {0} sein ");
        put(Size, "Gr\u00F6\u00DFe muss zwischen {0} und {1} liegen");
        put(Digits, "numerischer Wert au\u00DFerhalb des G\u00FCltigkeitsbereiches(erwarte:<{0} digits >.<{1} digits >)");
        put(Past, "muss Datum in der Vergangenheit sein ");
        put(Future, "muss Datum in der Zukunft sein ");
        put(Pattern, "Muss mit regul\u00E4rem Ausdruck \u00FCbereinstimmen: {0}");
        put(DecimalMax, "muss kleiner oder gleich {0} sein ");
        put(DecimalMin, "muss gr\u00F6\u00DFer oder gleich {0} sein ");
        put(NotEmpty, "darf nicht leer sein ");
        put(Email, "muss g\u00FCltiges Format einer EMail - Adresse sein");
    }
}
