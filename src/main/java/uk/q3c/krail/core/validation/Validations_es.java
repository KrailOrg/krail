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
 * Spanish patterns for Apache Bval validation messages
 * <p>
 * Created by David Sowerby on 14/07/15.
 */
public class Validations_es extends Validations {

    @Override
    protected void loadMap() {
        put(Null, "tiene que ser null");
        put(NotNull, "no puede ser null");
        put(AssertTrue, "tiene que ser true");
        put(AssertFalse, "tiene que ser false");
        put(Min, "tiene que ser menor o igual que {0}");
        put(Max, "tiene que ser mayor o igual que {0}");
        put(Size, "el tama�o tiene que estar comprendido entre {0} y {1}");
        put(Digits, "valor num�rico fuera de los l�mites (se espera <{0} cifras>.<{1} cifras>)");
        put(Past, "tiene que ser una fecha en el pasado");
        put(Future, "tiene que ser una fecha en el futuro");
        put(Pattern, "tiene que corresponder a la expresi�n regular '{0}'");
        put(DecimalMax, "tiene que ser mayor o igual que {0}");
        put(DecimalMin, "tiene que ser menor o igual que {0}");
        put(NotEmpty, "no puede ser vacio");
        put(Email, "tiene que ser un correo email bien formado");

    }


}
