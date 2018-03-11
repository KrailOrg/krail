# Release Notes for krail 0.15.1.0

The main purpose of this release is to make correct use of VaadinSession by ensuring that anything which may need to be stored in the session is Serializable.

This is needed in any environment where a session could be moved, for example in a clustered environment, and depending on how integration with Vert.x is implemented, may also be used in Vert.x local sessions.


# Language Change

A number of classes, and test classes have been converted to Kotlin.

# Units

Unit tests relating to these changes are written in Spek, and are held in a separate repo, krail-kotlin, pending update of Spek to 2.0, (which will hopefully resolve clashes between Spek and JUnit)


# Subject and Sessions

The mechanism for storing the `Subject` in `VaadinSession` has been revised

## SubjectProvider and Subject

`SubjectProvider` assumes a greater role, wrapping the `Subject` login/logout to manage the serialization of a `Subject` to `VaadinSession`.  

`Subject` was previously stored as a native object, but is not `Serializable`.  The `Subject` is now transformed to a [JWT](https://jwt.io/) for serialisation to the session.


## DefaultLoginView

Now just captures user input, the rest of the login logic moved to `SubjectProvider` 

## LoginExceptionHandler

Has been removed.  Login success and failures messages are sent via the `SessionBus`.  The `LoginView` handles those for giving feedback to the user

## DescriptionKey

Login related I18NKeys have moved to `LoginDescriptionKey` 

# Deprecated

See javadoc for replacements

- UserStatusBusMessage

 