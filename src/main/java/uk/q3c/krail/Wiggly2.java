package uk.q3c.krail;

import com.vaadin.data.Binder;
import com.vaadin.ui.TextField;

/**
 * Created by David Sowerby on 14 Jun 2018
 */
public class Wiggly2 {

    public void doit() {
        Binder<Person> binder = new Binder<>();

        TextField titleField = new TextField();

// Start by defining the Field instance to use
        binder.forField(titleField)
                // Finalize by doing the actual binding to the Person class
                .bind(
                        // Callback that loads the title from a person instance
                        Person::getTitle,
                        // Callback that saves the title in a person instance
                        Person::setTitle);

        TextField nameField = new TextField();

// Shorthand for cases without extra configuration
        binder.bind(nameField, Person::getName, Person::setName);
    }

}
