=========
Bootstrap
=========

In order to allow the same application code to run in both Vertx and Servlet environments, a bootstrap sequence is needed.

This was originally enables the Guice Injector to be held in a location
suitable to the environment, so that it can be retrieved during :doc:`deserialisation <../devguide/devguide-serialisation>`

It also sets the value of the @RuntimeEnvironment option, so that the application may make other adjustments if needed.

Injector Location
=================

In a standard Servlet environment, a static variable is the simplest way to hold a reference to the Guice Injector. This is provided by
``InjectorHolder``, but should usually only be accessed through ``InjectorLocator`` - this enables the code to be application portable,
because the ``InjectorLocator`` implementation is environment specific.


In a Vertx environment, the :doc:`injector instance <../devguide/devguide-vertx>` is held in ``Vertx.currentContext``. Again, to ensure portability, access should be
through ``InjectorLocator``

Guice Bindings
==============

Environment specific bindings are defined in ``ServletEnvironmentModule`` and ``VertxEnvironmentModule``, for:

-  ``InjectorLocator``

-  ``SerializationSupport``

Bootstrap file
==============

This is described in the :doc:`User Guide <../userguide/userguide-bootstrap>``
