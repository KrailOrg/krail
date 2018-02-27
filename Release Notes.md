# Release Notes for krail 0.15.0.0

The main purpose of this release is to enable the use of [Eclipse Vert.x](http://vertx.io/).  This release includes breaking changes.

## Application start up
The intention is to enable the use of either a standard .war deployment or a Vert.x deployment with as little difference between the two as possible.

This required some breaking changes around the ServletContextListener

### DefaultBindingManager

The collation of Guice modules and the creation of the Guice Injector were combined into a single class, the `DefaultBindingManager`.

Responsibilities have now been split:
 
- the `BindingsCollator` collates the Guice modules
- the `DefaultServletContextListener` creates the Guice Injector in a web container environment.
- the `VertxThing` will create the Guice Injector in a Vert.x environment



## Tutorial

The Tutorial has not been updated - this will be done after the move to Vert.x succeeds or fails

