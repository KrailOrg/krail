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

package uk.q3c.krail.core.view

import spock.lang.Specification
import uk.q3c.krail.core.view.component.ViewChangeBusMessage

import java.lang.annotation.Annotation
import java.lang.reflect.Field

/**
 * Created by David Sowerby on 07 Feb 2016
 */
abstract class ViewTest extends Specification {

    KrailView view
    ViewChangeBusMessage busMessage = Mock()

    protected boolean fieldHasCaption(String fieldName, Class<? extends Annotation> annotation) {
        Field field = view.getClass().getDeclaredField(fieldName)
        return field.isAnnotationPresent(annotation)
    }

    def "root component set"() {
        given:
        view.buildView(busMessage)

        expect:
        view.getRootComponent() != null
    }
}
