# Vert.x and Guice

There are a couple of libraries on Github which provide Guice integration with Vert.x, but don;t meet Krail's requirements.

## Injector scope

It would be entirely possible to have Singleton scope be equivalent to Vert.x instance scope.  At first sight, this looks attractive, but it would promote data sharing in a distributed application.

This would reduce the isolation / encapsulation between services and is therefore considered to be a "bad thing".

The Krail implementation considers a Verticle as Singleton scope (the Injector is created in the Verticle), encouraging the use of asynchronous events rather than sharing data.

 

