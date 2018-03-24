# Release Notes for krail 0.16.0.0

The release has two purposes:

- make correct use of VaadinSession by ensuring that anything which may need to be stored in the session is Serializable. This is needed in any environment where a session could be moved, for example in a clustered environment, and depending on how integration with Vert.x is implemented, may also be used in Vert.x local sessions.
- enable the same application code to run on Vertx or a Servlet container with minimal (see the Bootstrap section) 


# Language Change

A number of classes, and test classes have been converted to Kotlin.

# Unit Tests

New unit tests, or thoses that have been modified significantly, are written in Spek, and are held in a separate repo, krail-kotlin, pending update of Spek to 2.0, (which will hopefully resolve clashes between Spek and JUnit)


# Subject and Sessions

## SubjectProvider and Subject

`SubjectProvider` assumes a greater role, wrapping the `Subject` login/logout to manage the serialization of a `Subject` to `VaadinSession`.  

`Subject` was previously stored as a native object, but is not `Serializable`.  The `Subject` is now transformed to a [JWT](https://jwt.io/) for serialisation to the session.


## DefaultLoginView

Now just captures user input, the rest of the login logic moved to `SubjectProvider` 

## LoginExceptionHandler

Has been removed.  Login success and failures messages are sent via the `SessionBus`.  The `LoginView` handles those for giving feedback to the user

## DescriptionKey

Login related I18NKeys have moved to `LoginDescriptionKey` 


# Bootstrap

In order to support both Vertx and Servlet environments, a new approach to start up has been introduced.  A bootstrap file 'krail-bootstrap.yml' is required to select a slightly different set of modules depending on the environment.
More information can be found in the [developer guide](https://davidsowerby.gitbooks.io/krail-user-guide/content/devguide/bootstrap.html)

# Deprecated

See javadoc for replacements

- UserStatusBusMessage

 