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

package uk.q3c.krail.testutil.view

import com.vaadin.ui.Component
import uk.q3c.krail.core.i18n.Caption
import uk.q3c.krail.core.user.LoginCaption
import uk.q3c.krail.core.view.ViewBase
import java.lang.reflect.Field
import java.util.*

/**
 * Checks a Vaadin component container (for example a KrailView) to ensure that all fields have a @Caption annotation
 *
 *
 * Created by David Sowerby on 19 Jan 2016
 */
class ViewFieldChecker(private val view: ViewBase, private val fieldsWithoutI18N: Set<String>, private val fieldsWithoutId: Set<String>) {
    private val fieldsMissingAnnotation: MutableSet<String>
    private val fieldsMissingId: Set<String>
    private val fieldsNotConstructed: MutableSet<String>

    init {
        fieldsMissingAnnotation = mutableSetOf()
        fieldsMissingId = HashSet()
        fieldsNotConstructed = HashSet()
    }

    /**
     * Returns true only if all of the component fields of `out` have a @Caption annotation
     *
     * @return true only if all of the component fields of `out` have a @Caption annotation
     */
    @Throws(IllegalAccessException::class)
    fun check(): Boolean {
        val cut = view.javaClass
        println("Checking $cut for I18N annotated component fields")
        val declaredFields = cut.declaredFields
        for (i in declaredFields.indices) {
            val field = declaredFields[i]
            if (Component::class.java.isAssignableFrom(field.type)) {
                if (!(field.isAnnotationPresent(Caption::class.java) || field.isAnnotationPresent(LoginCaption::class.java))) {
                    annotationMissing(field)
                }
                field.isAccessible = true
                val component = field.get(view) as Component?
                if (component == null) {
                    println(field.name + "has not been constructed")
                    fieldsNotConstructed.add(field.name)
                }
            }
        }
        return fieldsMissingAnnotation.isEmpty() && fieldsMissingId.isEmpty() && fieldsNotConstructed.isEmpty()
    }

    private fun annotationMissing(field: Field) {
        if (!fieldsWithoutI18N.contains(field.name)) {
            println("Field does not have a caption annotation: " + field.name)
            fieldsMissingAnnotation.add(field.name)
        }
    }
}
