package uk.q3c.krail

import com.vaadin.data.Binder
import com.vaadin.ui.TextField

/**
 * Created by David Sowerby on 14 Jun 2018
 */
class Wiggly {

    fun doit() {
        val binder = Binder<Person>()

        val titleField = TextField()

        // Start by defining the Field instance to use
        binder.forField(titleField)
                // Finalize by doing the actual binding to the Person class
                .bind(
                        // Callback that loads the title from a person instance
                        { it.title },
                        // Callback that saves the title in a person instance
                        { obj, title -> obj.title = title })

        val nameField = TextField()

        // Shorthand for cases without extra configuration
        binder.bind(nameField, { it.name }, { obj, name -> obj.name = name })
    }

}
